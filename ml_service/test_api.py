"""
Smart Cart AI - Comprehensive ML API Test Suite
Directly verifies all endpoint handlers:
- GET /
- GET /health
- POST /classify
- POST /predict-price
- POST /recommend
- POST /interactions/record
- GET /model-metrics
"""

import os
import sys
import json

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
if BASE_DIR not in sys.path:
    sys.path.insert(0, BASE_DIR)

import main
from main import (
    root,
    health_check,
    classify_product,
    predict_price,
    get_recommendations,
    record_user_interaction_event,
    get_model_metrics,
    ClassifyRequest,
    PricePredictRequest,
    RecommendRequest,
    InteractionRecordRequest
)


def run_tests():
    print("=" * 60)
    print("SMART CART AI - MACHINE LEARNING API VERIFICATION SUITE")
    print("=" * 60)

    # 1. Root
    print("\n[1/7] Testing GET / ...")
    r = root()
    assert r["status"] == "online"
    print(f"Status: OK | Service: {r['service']}")

    # 2. Health
    print("\n[2/7] Testing GET /health ...")
    health = health_check()
    assert health["status"] == "healthy"
    print(f"Status: OK | Memory: {health['memory_usage_mb']}")
    print(f"Models Ready: {health['models_status']}")
    print(f"Firestore Status: {health['firestore_status']}")

    # 3. Classify
    print("\n[3/7] Testing POST /classify ...")
    classify_samples = [
        ("Apple iPhone 15 Pro Max 256GB Natural Titanium", "A16 Bionic 48MP camera iOS smartphone", "Mobiles"),
        ("Sony WH-1000XM4 Noise Cancelling Wireless Headphones", "30-hour battery life LDAC audio", "Audio"),
        ("Dell Inspiron 15 Laptop Intel Core i5", "16GB RAM 512GB SSD 15.6 inch FHD Windows 11", "Laptops"),
        ("Levi's Men's Trucker Denim Jacket", "Classic regular fit cotton outerwear apparel", "Fashion"),
        ("LG 7 Kg Front Load Washing Machine", "AI Direct Drive steam wash 5 star inverter washer", "Home Appliances")
    ]
    for title, desc, expected in classify_samples:
        req = ClassifyRequest(title=title, description=desc)
        res = classify_product(req)
        print(f"  Query: '{title[:32]}...' -> {res['predicted_category']} ({res['confidence_percentage']})")
        assert res["predicted_category"] == expected, f"Expected {expected}, got {res['predicted_category']}"

    # 4. Price Predictor
    print("\n[4/7] Testing POST /predict-price ...")
    preq = PricePredictRequest(product_id="mob_1", days_ahead=7)
    pdata = predict_price(preq)
    print(f"  Product: {pdata['product_name']} ({pdata['category']})")
    print(f"  Current Price: ₹{pdata['current_price']}")
    print(f"  Trend: {pdata['price_trend'].upper()} | Best Day to Buy: {pdata['best_day_to_buy']} (₹{pdata['estimated_lowest_price']})")
    print(f"  Forecast Horizon: {len(pdata['forecast'])} days")
    print(f"  Disclaimer Attached: '{pdata['disclaimer'][:65]}...'")
    assert "DISCLAIMER" in pdata["disclaimer"]
    assert len(pdata["forecast"]) == 7

    # 5. Recommendation
    print("\n[5/7] Testing POST /recommend (Hybrid & Content KNN) ...")
    rreq = RecommendRequest(product_id="mob_1", user_id=10, top_k=4, alpha=0.65)
    rec_data = get_recommendations(rreq)
    print(f"  Mode: {rec_data['recommendation_mode']} | Returned: {rec_data['count']} items")
    for i, it in enumerate(rec_data["recommendations"], 1):
        print(f"    {i}. {it['name']} ({it['category']}) - {it['price']} | Score: {it['match_score']} ({it['match_reason']})")
    assert len(rec_data["recommendations"]) == 4

    # 6. Record Interaction
    print("\n[6/7] Testing POST /interactions/record ...")
    ireq = InteractionRecordRequest(
        user_id=10,
        product_id="mob_1",
        product_name="Nothing Phone (2a) 5G",
        category="Mobiles",
        interaction_type="wishlist",
        weight=4.0
    )
    ires = record_user_interaction_event(ireq)
    print(f"  Interaction status: {ires['status']} | Local storage synced: {ires['local_storage_synced']} | Firestore: {ires['firestore_synced']}")
    assert ires["status"] == "success"

    # 7. Model Metrics
    print("\n[7/7] Testing GET /model-metrics ...")
    mres = get_model_metrics()
    models = mres["models"]
    print("  Models verified:")
    print(f"    - Classifier Accuracy: {models['classifier']['evaluation']['accuracy'] * 100:.2f}% | F1: {models['classifier']['evaluation']['f1_weighted']}")
    print(f"    - Price Predictor MAE: ₹{models['price_predictor']['evaluation']['mae_inr']} | R²: {models['price_predictor']['evaluation']['r2_score']}")
    print(f"    - Recommender Cohesion: {models['recommender']['evaluation']['category_cohesion_top5'] * 100:.2f}% | Cosine Sim: {models['recommender']['evaluation']['average_cosine_similarity']}")

    print("\n" + "=" * 60)
    print("ALL ML API TESTS PASSED WITH 100% SUCCESS!")
    print("=" * 60)


if __name__ == "__main__":
    run_tests()
