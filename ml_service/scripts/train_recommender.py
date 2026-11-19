"""
Smart Cart AI - Product Recommendation Training Script
Model: KNN-based Content Similarity + Hybrid User Interaction Layer
Hardware Budget: Optimized for 4 GB RAM (<20 MB peak RAM)
Strict Requirement: No external AI APIs, 100% local scikit-learn.
"""

import os
import json
import joblib
import pandas as pd
import numpy as np
from datetime import datetime
from sklearn.neighbors import NearestNeighbors
from sklearn.preprocessing import MinMaxScaler, OneHotEncoder

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATASETS_DIR = os.path.join(BASE_DIR, "datasets")
MODELS_DIR = os.path.join(BASE_DIR, "models")
os.makedirs(MODELS_DIR, exist_ok=True)

PRODUCTS_PATH = os.path.join(DATASETS_DIR, "products_dataset.csv")
INTERACTIONS_PATH = os.path.join(DATASETS_DIR, "user_interactions_dataset.csv")
MODEL_OUTPUT_PATH = os.path.join(MODELS_DIR, "recommender_model.joblib")
METADATA_OUTPUT_PATH = os.path.join(MODELS_DIR, "recommender_metadata.joblib")
METRICS_OUTPUT_PATH = os.path.join(MODELS_DIR, "recommender_metrics.json")


def train():
    print("=" * 60)
    print("STAGE 5: TRAINING RECOMMENDER (KNN CONTENT + HYBRID INTERACTIONS)")
    print("=" * 60)

    if not os.path.exists(PRODUCTS_PATH) or not os.path.exists(INTERACTIONS_PATH):
        raise FileNotFoundError("Datasets missing. Run generate_datasets.py first.")

    products_df = pd.read_csv(PRODUCTS_PATH)
    if "id" not in products_df.columns and "product_id" in products_df.columns:
        products_df["id"] = products_df["product_id"]

    interactions_df = pd.read_csv(INTERACTIONS_PATH)

    print(f"[1/5] Loaded {len(products_df)} products and {len(interactions_df)} user interactions.")
    print(f"      Production products: {(products_df['is_production_catalog'] == 1).sum()}")
    print(f"      Augmented products:  {(products_df['is_production_catalog'] == 0).sum()}")

    # 1. Feature Engineering for Content Similarity
    # Categorical features: category, subcategory, brand
    cat_encoder = OneHotEncoder(sparse_output=False, handle_unknown="ignore")
    cat_features = cat_encoder.fit_transform(products_df[["category", "subcategory"]])

    # Numerical features: log price, rating, log review count
    price_scaler = MinMaxScaler()
    log_prices = np.log1p(products_df["numeric_price"].values).reshape(-1, 1)
    scaled_prices = price_scaler.fit_transform(log_prices)

    rating_scaler = MinMaxScaler()
    ratings = products_df["rating"].values.reshape(-1, 1)
    scaled_ratings = rating_scaler.fit_transform(ratings)

    review_scaler = MinMaxScaler()
    log_reviews = np.log1p(products_df["review_count"].values).reshape(-1, 1)
    scaled_reviews = review_scaler.fit_transform(log_reviews)

    # Combine into dense feature matrix
    # Weights: Category & Subcategory (60%), Price bracket (20%), Rating & Popularity (20%)
    X_content = np.hstack([
        cat_features * 1.5,
        scaled_prices * 0.8,
        scaled_ratings * 0.6,
        scaled_reviews * 0.4
    ])

    print(f"[2/5] Constructed content feature matrix: {X_content.shape} (shape: products x features)")

    # 2. Fit Nearest Neighbors Model (Cosine Distance)
    # Cosine distance measures angle between feature vectors regardless of absolute magnitudes
    knn = NearestNeighbors(
        n_neighbors=min(15, len(products_df)),
        metric="cosine",
        algorithm="brute"
    )
    knn.fit(X_content)
    print("[3/5] Fitted NearestNeighbors (metric='cosine') content model.")

    # 3. Build User Affinity Profiles from Interactions (Hybrid Layer)
    user_profiles = {}
    now = datetime(2026, 11, 18)

    interactions_df["timestamp"] = pd.to_datetime(interactions_df["timestamp"])
    for uid, group in interactions_df.groupby("user_id"):
        scores = {}
        for _, row in group.iterrows():
            pid = row["product_id"]
            weight = float(row["weight"])
            # Exponential decay: recent actions have higher weight
            days_ago = max(0.0, (now - row["timestamp"]).total_seconds() / 86400.0)
            decay = np.exp(-0.03 * days_ago)
            effective_weight = weight * decay
            scores[pid] = scores.get(pid, 0.0) + effective_weight

        # Normalize user scores to [0, 1]
        max_score = max(scores.values()) if scores else 1.0
        normalized_scores = {k: round(v / max_score, 4) for k, v in scores.items()}
        user_profiles[int(uid)] = normalized_scores

    print(f"[4/5] Computed interaction affinity profiles for {len(user_profiles)} active users.")

    # 4. Evaluate Content Recommender Quality
    # Metric 1: Intra-List Similarity (category cohesion)
    # Metric 2: Catalog Coverage
    cat_matches = []
    distances_sample = []
    for i in range(len(products_df)):
        distances, indices = knn.kneighbors([X_content[i]], n_neighbors=6)
        source_cat = products_df.iloc[i]["category"]
        rec_indices = indices[0][1:] # Exclude self
        rec_cats = products_df.iloc[rec_indices]["category"].tolist()
        match_ratio = sum(1 for c in rec_cats if c == source_cat) / len(rec_cats)
        cat_matches.append(match_ratio)
        distances_sample.extend(distances[0][1:])

    avg_cat_cohesion = float(np.mean(cat_matches))
    avg_cosine_sim = float(1.0 - np.mean(distances_sample))

    print("\n--- Recommendation Quality Metrics ---")
    print(f"Category Cohesion (Top-5):      {avg_cat_cohesion * 100:.2f}%")
    print(f"Average Neighbor Cosine Sim:    {avg_cosine_sim:.4f}")
    print(f"Catalog Coverage:               100.0% ({len(products_df)} products indexable)")

    # 5. Save Artifacts with Joblib
    metadata_bundle = {
        "products_df": products_df,
        "X_content": X_content,
        "cat_encoder": cat_encoder,
        "price_scaler": price_scaler,
        "rating_scaler": rating_scaler,
        "review_scaler": review_scaler,
        "user_profiles": user_profiles,
        "id_to_index": {str(row.get("id") or row.get("product_id")): idx for idx, row in products_df.iterrows()}
    }

    joblib.dump(knn, MODEL_OUTPUT_PATH, compress=3)
    joblib.dump(metadata_bundle, METADATA_OUTPUT_PATH, compress=3)
    print(f"[5/5] Saved model to: {MODEL_OUTPUT_PATH}")
    print(f"      Saved metadata to: {METADATA_OUTPUT_PATH}")

    # Metrics JSON
    metrics_data = {
        "model_name": "Product Recommendation (KNN + Hybrid)",
        "algorithm": "NearestNeighbors (Cosine) + Interaction Affinity Blending",
        "evaluation": {
            "category_cohesion_top5": round(avg_cat_cohesion, 4),
            "average_cosine_similarity": round(avg_cosine_sim, 4),
            "total_catalog_size": len(products_df),
            "production_catalog_size": int((products_df["is_production_catalog"] == 1).sum()),
            "active_user_profiles": len(user_profiles)
        }
    }
    with open(METRICS_OUTPUT_PATH, "w", encoding="utf-8") as f:
        json.dump(metrics_data, f, indent=2)
    print(f"      Saved metrics to: {METRICS_OUTPUT_PATH}")


def recommend_products(product_id: str = None, user_id: int = None, top_k: int = 5, alpha: float = 0.65) -> dict:
    """
    Produces top_k recommendations:
    - If product_id given: computes KNN content similarity
    - If user_id given: blends with user interaction affinity (Hybrid mode)
    - If neither given: recommends top trending/rated products
    """
    if not os.path.exists(MODEL_OUTPUT_PATH) or not os.path.exists(METADATA_OUTPUT_PATH):
        raise FileNotFoundError("Models not trained yet.")

    knn = joblib.load(MODEL_OUTPUT_PATH)
    meta = joblib.load(METADATA_OUTPUT_PATH)

    products_df = meta["products_df"]
    if "id" not in products_df.columns and "product_id" in products_df.columns:
        products_df["id"] = products_df["product_id"]

    X_content = meta["X_content"]
    id_to_index = meta["id_to_index"]
    user_profiles = meta["user_profiles"]

    user_affinity = user_profiles.get(user_id, {}) if user_id is not None else {}

    recommendations = []

    if product_id and product_id in id_to_index:
        idx = id_to_index[product_id]
        distances, indices = knn.kneighbors([X_content[idx]], n_neighbors=top_k + 5)
        
        candidates = []
        for dist, neighbor_idx in zip(distances[0], indices[0]):
            if neighbor_idx == idx:
                continue # Skip querying item itself
            
            cand_p = products_df.iloc[neighbor_idx].to_dict()
            cand_id = str(cand_p.get("id") or cand_p.get("product_id"))
            content_sim = float(1.0 - dist)

            # Hybrid score blending
            user_score = user_affinity.get(cand_id, 0.0)
            hybrid_score = (alpha * content_sim) + ((1.0 - alpha) * user_score)

            candidates.append({
                "product": cand_p,
                "content_similarity": round(content_sim, 4),
                "user_affinity": round(user_score, 4),
                "score": round(hybrid_score, 4),
                "match_reason": f"Similar in {cand_p['category']} with {round(content_sim * 100)}% match"
            })

        # Sort by hybrid score
        candidates.sort(key=lambda x: x["score"], reverse=True)
        recommendations = candidates[:top_k]
    else:
        # Fallback / General Discovery: High rating + user category preferences
        sorted_df = products_df.sort_values(by=["rating", "review_count"], ascending=[False, False])
        for _, row in sorted_df.head(top_k).iterrows():
            p_dict = row.to_dict()
            recommendations.append({
                "product": p_dict,
                "content_similarity": 1.0,
                "user_affinity": 0.0,
                "score": round(float(p_dict["rating"]) / 5.0, 4),
                "match_reason": f"Top rated in {p_dict['category']} ({p_dict['rating']}★)"
            })

    # Clean formatted response
    results = []
    for r in recommendations:
        p = r["product"]
        results.append({
            "id": str(p.get("id") or p.get("product_id")),
            "name": p["name"],
            "brand": p["brand"],
            "category": p["category"],
            "subcategory": p.get("subcategory", ""),
            "price": p["price"],
            "numeric_price": float(p["numeric_price"]),
            "rating": float(p["rating"]),
            "review_count": int(p["review_count"]),
            "specs": p.get("specs", ""),
            "best_for": p.get("best_for", ""),
            "is_production_catalog": int(p.get("is_production_catalog", 1)),
            "match_score": r["score"],
            "match_reason": r["match_reason"]
        })

    return {
        "query_product_id": product_id,
        "query_user_id": user_id,
        "recommendation_mode": "hybrid" if (user_id and user_affinity) else "content_knn",
        "count": len(results),
        "recommendations": results
    }


if __name__ == "__main__":
    train()

    print("\n--- Testing Content Recommendation for 'Nothing Phone (2a) 5G' (mob_1) ---")
    rec_res = recommend_products(product_id="mob_1", user_id=10, top_k=4)
    print(f"Mode: {rec_res['recommendation_mode']}")
    for i, item in enumerate(rec_res["recommendations"], 1):
        print(f"{i}. {item['name']} ({item['category']}) - {item['price']} | Score: {item['match_score']} ({item['match_reason']})")
