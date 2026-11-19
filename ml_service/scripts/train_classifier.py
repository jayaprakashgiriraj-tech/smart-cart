"""
Smart Cart AI - Product Classification Training Script
Model: TF-IDF Vectorizer + Logistic Regression
Hardware Budget: Optimized for 4 GB RAM (<25 MB peak RAM during training/inference)
Strict Requirement: No external AI APIs, 100% local scikit-learn.
"""

import os
import json
import re
import joblib
import pandas as pd
import numpy as np
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.model_selection import train_test_split, StratifiedKFold, cross_val_score
from sklearn.metrics import accuracy_score, precision_recall_fscore_support, classification_report, confusion_matrix

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATASETS_DIR = os.path.join(BASE_DIR, "datasets")
MODELS_DIR = os.path.join(BASE_DIR, "models")
os.makedirs(MODELS_DIR, exist_ok=True)

DATASET_PATH = os.path.join(DATASETS_DIR, "product_classification_dataset.csv")
MODEL_OUTPUT_PATH = os.path.join(MODELS_DIR, "classifier_model.joblib")
METRICS_OUTPUT_PATH = os.path.join(MODELS_DIR, "classifier_metrics.json")


def clean_text(text: str) -> str:
    """Preprocesses raw product text for clean n-gram extraction."""
    if not isinstance(text, str):
        return ""
    text = text.lower()
    text = re.sub(r"[^a-z0-9\s]", " ", text)
    text = re.sub(r"\s+", " ", text).strip()
    return text


def train():
    print("=" * 60)
    print("STAGE 3: TRAINING PRODUCT CLASSIFIER (TF-IDF + LOGISTIC REGRESSION)")
    print("=" * 60)

    if not os.path.exists(DATASET_PATH):
        raise FileNotFoundError(f"Dataset not found at {DATASET_PATH}. Run generate_datasets.py first.")

    df = pd.read_csv(DATASET_PATH)
    print(f"[1/5] Loaded dataset: {len(df)} total samples.")
    print(f"      Categories: {df['category'].unique().tolist()}")
    if "is_production_catalog" in df.columns:
        print(f"      Production vs Benchmark: {df['is_production_catalog'].value_counts().to_dict()}")

    # Prepare features
    df["full_text"] = df["title"].fillna("") + " " + df["description"].fillna("")
    df["cleaned_text"] = df["full_text"].apply(clean_text)

    X = df["cleaned_text"]
    y = df["category"]

    # Stratified Split (80% train, 20% test)
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.20, random_state=42, stratify=y
    )
    print(f"[2/5] Train set: {len(X_train)} samples | Test set: {len(X_test)} samples")

    # TF-IDF Vectorizer (max_features=500 keeps RAM minimal ~10MB)
    vectorizer = TfidfVectorizer(
        max_features=500,
        ngram_range=(1, 2),
        stop_words="english",
        sublinear_tf=True
    )
    X_train_vec = vectorizer.fit_transform(X_train)
    X_test_vec = vectorizer.transform(X_test)
    print(f"[3/5] TF-IDF extracted {X_train_vec.shape[1]} vocabulary features.")

    # Logistic Regression Classifier
    model = LogisticRegression(
        C=1.0,
        max_iter=1000,
        solver="lbfgs",
        random_state=42
    )
    model.fit(X_train_vec, y_train)

    # 5-Fold Stratified Cross Validation
    cv = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
    cv_scores = cross_val_score(model, X_train_vec, y_train, cv=cv, scoring="accuracy")
    print(f"[4/5] 5-Fold Cross Validation Accuracy: {cv_scores.mean() * 100:.2f}% (+/- {cv_scores.std() * 100:.2f}%)")

    # Evaluation on Held-out Test Set
    y_pred = model.predict(X_test_vec)
    y_prob = model.predict_proba(X_test_vec)

    acc = accuracy_score(y_test, y_pred)
    prec_weighted, rec_weighted, f1_weighted, _ = precision_recall_fscore_support(
        y_test, y_pred, average="weighted", zero_division=0
    )
    prec_macro, rec_macro, f1_macro, _ = precision_recall_fscore_support(
        y_test, y_pred, average="macro", zero_division=0
    )

    print("\n--- Model Performance Metrics ---")
    print(f"Accuracy:          {acc * 100:.2f}%")
    print(f"Weighted F1-Score: {f1_weighted:.4f}")
    print(f"Macro F1-Score:    {f1_macro:.4f}")
    print(f"Weighted Precision:{prec_weighted:.4f}")
    print(f"Weighted Recall:   {rec_weighted:.4f}")

    print("\nClassification Report:")
    report_dict = classification_report(y_test, y_pred, output_dict=True, zero_division=0)
    print(classification_report(y_test, y_pred, zero_division=0))

    # Save artifact bundle with Joblib
    pipeline_bundle = {
        "vectorizer": vectorizer,
        "model": model,
        "classes": list(model.classes_),
        "metadata": {
            "algorithm": "TF-IDF + Multinomial Logistic Regression",
            "vocab_size": len(vectorizer.vocabulary_),
            "trained_samples": len(X_train),
            "test_samples": len(X_test),
            "created_at": pd.Timestamp.now().isoformat(),
            "metrics": {
                "accuracy": round(float(acc), 4),
                "f1_weighted": round(float(f1_weighted), 4),
                "f1_macro": round(float(f1_macro), 4),
                "precision_weighted": round(float(prec_weighted), 4),
                "recall_weighted": round(float(rec_weighted), 4),
                "cv_accuracy_mean": round(float(cv_scores.mean()), 4),
                "cv_accuracy_std": round(float(cv_scores.std()), 4)
            }
        }
    }

    joblib.dump(pipeline_bundle, MODEL_OUTPUT_PATH, compress=3)
    print(f"[5/5] Model successfully saved to: {MODEL_OUTPUT_PATH}")

    # Save metrics JSON for API / health checks
    metrics_data = {
        "model_name": "Product Classification",
        "algorithm": "TF-IDF + Logistic Regression",
        "evaluation": pipeline_bundle["metadata"]["metrics"],
        "class_breakdown": report_dict,
        "categories": list(model.classes_)
    }
    with open(METRICS_OUTPUT_PATH, "w", encoding="utf-8") as f:
        json.dump(metrics_data, f, indent=2)
    print(f"      Metrics JSON saved to: {METRICS_OUTPUT_PATH}")


def predict_category(text: str) -> dict:
    """Helper function to test prediction on single text sample."""
    if not os.path.exists(MODEL_OUTPUT_PATH):
        raise FileNotFoundError("Model not trained yet.")
    bundle = joblib.load(MODEL_OUTPUT_PATH)
    vec = bundle["vectorizer"]
    clf = bundle["model"]
    classes = bundle["classes"]

    cleaned = clean_text(text)
    features = vec.transform([cleaned])
    probs = clf.predict_proba(features)[0]
    top_idx = np.argmax(probs)
    
    top_class = classes[top_idx]
    confidence = float(probs[top_idx])

    # Ranked distribution
    all_scores = {cls_name: round(float(p), 4) for cls_name, p in zip(classes, probs)}
    sorted_scores = sorted(all_scores.items(), key=lambda x: x[1], reverse=True)

    return {
        "input_text": text,
        "predicted_category": top_class,
        "confidence": round(confidence, 4),
        "confidence_percentage": f"{confidence * 100:.1f}%",
        "probabilities": dict(sorted_scores)
    }


if __name__ == "__main__":
    train()

    print("\n--- Testing Single Inference ---")
    test_queries = [
        "Apple iPhone 15 Pro Max 256GB Natural Titanium",
        "Sony WH-1000XM4 Wireless Noise Cancelling Headphones",
        "ASUS ROG Zephyrus G14 Gaming Laptop RTX 4060",
        "Levi's Men's Slim Fit Washed Denim Casual Jacket",
        "LG 7 Kg Inverter Front Load Washing Machine with Steam"
    ]
    for q in test_queries:
        res = predict_category(q)
        print(f"Query: '{q[:40]}...' -> {res['predicted_category']} ({res['confidence_percentage']})")
