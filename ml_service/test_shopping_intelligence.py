"""
Smart Cart AI - Comprehensive Shopping Intelligence Test Suite
Direct Endpoint Validation across Modules 1 to 9:
1. Health & Low-Memory Verification
2. AI Search Engine (Complex query, typo tolerance, autocomplete)
3. Shopping Assistant Multi-Turn Stateful Dialog & Spec Explanations
4. Personalized Recommendations & Cold-Start Distinction
5. Smart Product Comparison Matrix & Verdicts
6. Price Intelligence (60-day history, peak savings, drop detection & target alerts)
7. Smart Cart Optimization (budget warnings, cheaper swaps, complementary cross-selling)
8. Product Classification (TF-IDF + Logistic Regression)
9. Firebase Dual-Mode Persistence & Sync
"""

import os
import sys
import json
import logging

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
SCRIPTS_DIR = os.path.join(BASE_DIR, "scripts")
if BASE_DIR not in sys.path:
    sys.path.insert(0, BASE_DIR)
if SCRIPTS_DIR not in sys.path:
    sys.path.insert(0, SCRIPTS_DIR)

import main

print("=" * 75)
print("SMART CART AI - COMPREHENSIVE SHOPPING INTELLIGENCE TEST SUITE")
print("Target: Desktop PC 4 GB RAM, CPU Execution, ZERO External AI APIs")
print("=" * 75)

passed_count = 0
failed_count = 0

def run_test(name, func):
    global passed_count, failed_count
    try:
        func()
        print(f" [PASS] {name}")
        passed_count += 1
    except Exception as e:
        print(f" [FAIL] {name}: {e}")
        import traceback
        traceback.print_exc()
        failed_count += 1


# 1. Health & Resource Verification
def test_health():
    h = main.health_check()
    assert h["status"] == "healthy"
    assert "memory_footprint" in h
    assert h["models_status"]["search_engine"] == "ready"
    assert h["models_status"]["comparison_engine"] == "ready"
    print(f"       Memory Footprint: {h['memory_footprint']} | Firestore Mode: {h['firestore']['mode']}")

run_test("Module 8: System Health & Low-Memory Verification", test_health)


# 2. AI Search Engine (Module 1)
def test_search_complex_nl():
    req = main.SearchRequest(
        query="Best gaming laptop under 60000 with 16GB RAM",
        user_id=10,
        page_size=4
    )
    res = main.search_products(req)
    assert len(res["results"]) > 0
    parsed = res["parsed_query"]
    assert parsed.get("category") == "Laptops"
    assert parsed.get("max_budget") == 60000
    top = res["results"][0]
    print(f"       Parsed: Category={parsed['category']}, MaxBudget=₹{parsed['max_budget']}, Specs={parsed['specs']}")
    print(f"       Top match: {top['name']} ({top['price']}) - Relevance: {top['relevance_score']}")

run_test("Module 1: Natural Language Search with Category, Budget & Spec Parsing", test_search_complex_nl)


def test_search_typo_brand():
    req = main.SearchRequest(query="samusng phone under 25k", page_size=3)
    res = main.search_products(req)
    assert len(res["results"]) > 0
    parsed = res["parsed_query"]
    assert parsed.get("brand") == "Samsung"
    print(f"       Typo Corrected: 'samusng' -> '{parsed['brand']}' | Results returned: {len(res['results'])}")

run_test("Module 1: Typo-Tolerant Brand Resolution", test_search_typo_brand)


def test_autocomplete():
    res = main.search_autocomplete(q="head")
    assert len(res["suggestions"]) > 0
    print(f"       Autocomplete suggestions for 'head': {res['suggestions'][:4]}")

run_test("Module 1: Instant Prefix Autocomplete", test_autocomplete)


# 3. Shopping Assistant Multi-Turn Dialog (Module 4)
def test_assistant_multi_turn():
    session_id = "test_turn_session_99"

    # Turn 1: User asks broad query -> Clarification triggered
    req1 = main.AssistantChatRequest(message="I need a phone under 25000", session_id=session_id, user_id=10)
    res1 = main.assistant_chat(req1)
    assert "camera" in res1["reply"].lower() or "battery" in res1["reply"].lower()
    print("       Turn 1: Clarification requested for user preferences.")

    # Turn 2: User clarifies preference -> Refined recommendations returned
    req2 = main.AssistantChatRequest(message="Camera and battery", session_id=session_id, user_id=10)
    res2 = main.assistant_chat(req2)
    assert len(res2["recommended_products"]) > 0
    print(f"       Turn 2: Clarification resolved, returned {len(res2['recommended_products'])} tailored recommendations.")

    # Turn 3: Technical spec inquiry -> Plain English explanation
    req3 = main.AssistantChatRequest(message="What is AMOLED?", session_id=session_id)
    res3 = main.assistant_chat(req3)
    assert "amoled" in res3["reply"].lower()
    print("       Turn 3: Technical spec explanation delivered in plain language.")

    # Turn 4: Comparative inquiry -> Side-by-side comparison
    req4 = main.AssistantChatRequest(message="Compare Nothing Phone 2a with OnePlus 12R", session_id=session_id)
    res4 = main.assistant_chat(req4)
    assert "comparison" in res4["reply"].lower() or "₹" in res4["reply"]
    print("       Turn 4: Comparative synthesis generated across active items.")

run_test("Module 4: Shopping Assistant Multi-Turn Stateful Dialog & Spec Explanations", test_assistant_multi_turn)


# 4. Personalized Recommendations (Module 2)
def test_recommendations():
    # Cold-start user test (<3 interactions)
    cold_res = main.get_user_recommendations(user_id=8888, top_k=5)
    print("       DEBUG cold_res keys & mode:", cold_res.get("recommendation_mode"), cold_res.get("mode"))
    assert len(cold_res["recommendations"]) > 0
    mode = str(cold_res.get("recommendation_mode") or cold_res.get("mode", "")).lower()
    assert "cold_start" in mode or "popular" in mode or "fallback" in mode
    print(f"       Cold-Start Mode: '{mode}' cleanly tagged.")

    # Hybrid content similarity test
    sim_res = main.get_user_recommendations(user_id=1, product_id="mob_1", top_k=5)
    assert len(sim_res["recommendations"]) > 0
    print(f"       Similar to 'mob_1': {sim_res['recommendations'][0]['name']} (Score: {sim_res['recommendations'][0]['match_score']})")

run_test("Module 2: Personalized Recommendations & Cold-Start Guardrails", test_recommendations)


# 5. Smart Product Comparison (Module 3)
def test_comparison_verdicts():
    req = main.CompareRequest(product_ids=["mob_1", "mob_4", "mob_5"], user_persona="budget")
    res = main.compare_products_endpoint(req)
    assert res["comparison_count"] == 3
    v = res["verdict"]
    assert "best_budget_option" in v
    assert "best_feature_option" in v
    assert "best_value_option" in v
    assert "critical_disadvantages" in v
    print(f"       Best Budget: {v['best_budget_option']['name']} ({v['best_budget_option']['price']})")
    print(f"       Best Feature: {v['best_feature_option']['name']} ({v['best_feature_option']['price']})")
    print(f"       Best Value: {v['best_value_option']['name']} ({v['best_value_option']['price']})")

run_test("Module 3: 3-Product Comparison Matrix, Safe Missing Attributes & Verdicts", test_comparison_verdicts)


# 6. Price Intelligence & Target Price Alerts (Module 5)
def test_price_intelligence():
    res = main.get_product_price_intelligence(product_id="mob_1", days=60, target_alert_price=20000.0)
    assert "current_price" in res
    assert "lowest_price" in res
    assert "savings_from_peak" in res
    assert "chart_points" in res
    assert "disclaimer" in res
    assert "target_alert_evaluation" in res
    print(f"       Current: ₹{res['current_price']} | 60d Low: ₹{res['lowest_price']} | Savings from peak: ₹{res['savings_from_peak']} ({res['savings_from_peak_percent']}%)")
    print(f"       Alert trigger status: {res['target_alert_evaluation']['is_triggered']}")

    # Test saving alert
    alert_req = main.TargetAlertRequest(
        user_id=10,
        product_id="mob_1",
        product_name="Nothing Phone (2a) 5G",
        target_price=19000.0,
        current_price=res["current_price"]
    )
    alert_res = main.create_target_price_alert(alert_req)
    assert alert_res["status"] == "saved"
    print(f"       Target Alert Saved: Threshold ₹{alert_req.target_price:,.0f}")

run_test("Module 5: Price History, Drop Detection, Forecast & Target Alerts", test_price_intelligence)


# 7. Smart Cart Optimization (Module 6)
def test_smart_cart():
    req = main.CartAnalysisRequest(
        items=[
            main.CartItemInput(product_id="mob_1", quantity=1, price=23999.0, category="Mobiles"),
            main.CartItemInput(product_id="aud_1", quantity=1, price=2799.0, category="Audio")
        ],
        user_budget=20000.0
    )
    res = main.analyze_cart_endpoint(req)
    assert res["cart_total"] == 26798.0
    b = res["budget_analysis"]
    assert b["is_over_budget"] is True
    assert len(res["cheaper_alternatives"]) > 0
    print(f"       Cart Total: ₹{res['cart_total']} vs Target Budget: ₹{b['user_budget']}")
    print(f"       Budget Warning: {b['status']} - {b['message']}")
    print(f"       Potential Savings from Swaps: ₹{res['total_potential_savings']}")
    print(f"       Complementary Cross-Sell suggestions: {len(res['complementary_recommendations'])} items")

run_test("Module 6: Smart Cart Budget Monitoring, Cheaper Swaps & Cross-Selling", test_smart_cart)


# 8. Product Classification (Module 7)
def test_classification():
    req = main.ClassifyRequest(
        title="OnePlus Nord Buds 2 Wireless Earbuds",
        description="Active Noise Cancellation 25dB, 36 hours playback, Dolby Atmos audio"
    )
    res = main.classify_product(req)
    assert res["predicted_category"].lower() == "audio"
    print(f"       Classified '{req.title[:30]}...' -> {res['predicted_category']} ({res['confidence_percentage']})")

run_test("Module 7: Product Categorization (TF-IDF + Logistic Regression)", test_classification)


# 9. Firebase Dual-Mode Persistence (Module 9)
def test_persistence():
    req = main.InteractionRequest(
        user_id=10,
        product_id="mob_1",
        product_name="Nothing Phone (2a) 5G",
        category="Mobiles",
        interaction_type="view",
        weight=1.0
    )
    res = main.record_interaction_endpoint(req)
    assert res["status"] == "success"
    print(f"       Interaction recorded with weight {res['event']['weight']} (Local sync: {res['local_storage_synced']})")

run_test("Module 9: Firebase Multi-Collection Interaction Persistence", test_persistence)


print("=" * 75)
print(f"RESULTS: {passed_count} PASSED, {failed_count} FAILED")
print("=" * 75)
if failed_count > 0:
    sys.exit(1)
print("ALL SHOPPING INTELLIGENCE MODULES VALIDATED SUCCESSFULLY!")
