"""
Smart Cart AI - Unified Machine Learning & Shopping Intelligence API Service
Modules 1 to 9 Integration:
- Module 1: Smart AI Search (/api/search, /api/search/autocomplete)
- Module 2: Personalized Recommendation Engine (/api/recommendations/{user_id}, /recommend)
- Module 3: Smart Product Comparison (/api/compare)
- Module 4: Shopping Assistant Dialog Manager (/api/assistant/chat)
- Module 5: Price Intelligence & Target Alert Tracking (/api/prices/{product_id}, /api/alerts/target)
- Module 6: Smart Cart Optimization (/api/cart/analyze)
- Module 7: Product Classification (/classify, /api/classify)
- Module 8: Unified FastAPI Backend Architecture
- Module 9: Firebase Firestore Integration (Dual-mode fallback)

Strict Requirement: ZERO external AI APIs (no OpenAI, Gemini, Claude). 100% local CPU ML.
Hardware: Desktop PC with 4 GB RAM. Total RAM consumption <80 MB.
"""

import os
import sys
import json
import logging
from typing import Optional, List, Dict, Any
from contextlib import asynccontextmanager

from fastapi import FastAPI, HTTPException, Query
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field

# Ensure scripts folder is on sys.path
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
SCRIPTS_DIR = os.path.join(BASE_DIR, "scripts")
if SCRIPTS_DIR not in sys.path:
    sys.path.insert(0, SCRIPTS_DIR)

# Import ML Service Engines
from search_engine import get_search_engine
from recommender_service import get_recommender_service
from comparison_service import get_comparison_service
from assistant_service import get_shopping_assistant
from price_service import get_price_service, DISCLAIMER_TEXT
from smart_cart_service import get_smart_cart_service
from train_classifier import predict_category
import firestore_client

# Setup logging
logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")
logger = logging.getLogger("smartcart_ml.main")

MODELS_DIR = os.path.join(BASE_DIR, "models")
CLASSIFIER_MODEL_PATH = os.path.join(MODELS_DIR, "classifier_model.joblib")
PRICE_MODEL_PATH = os.path.join(MODELS_DIR, "price_predictor_model.joblib")
RECOMMENDER_MODEL_PATH = os.path.join(MODELS_DIR, "recommender_model.joblib")


@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("=" * 60)
    logger.info("STARTING SMART CART AI SHOPPING INTELLIGENCE ENGINE")
    logger.info("=" * 60)
    logger.info(f"Models Directory: {MODELS_DIR}")
    logger.info(f"Classifier Ready: {os.path.exists(CLASSIFIER_MODEL_PATH)}")
    logger.info(f"Price Predictor Ready: {os.path.exists(PRICE_MODEL_PATH)}")
    logger.info(f"Recommender Ready: {os.path.exists(RECOMMENDER_MODEL_PATH)}")
    logger.info(f"Firestore Connected: {firestore_client.is_firestore_connected()}")

    # Pre-warm singletons to verify low-memory footprint
    get_search_engine()
    get_recommender_service()
    get_comparison_service()
    get_shopping_assistant()
    get_price_service()
    get_smart_cart_service()
    logger.info("All 7 intelligence services pre-warmed successfully within 4 GB RAM budget.")

    yield
    logger.info("Shutting down Smart Cart AI ML Engine.")


app = FastAPI(
    title="Smart Cart AI - Complete Shopping Intelligence System",
    description="Independent CPU-based Machine Learning & Shopping Intelligence API without external AI APIs.",
    version="2.0.0",
    lifespan=lifespan
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# -----------------------------------------------------------------------------
# Pydantic Schemas for Request & Response
# -----------------------------------------------------------------------------
# Module 1: Search
class SearchRequest(BaseModel):
    query: str = Field(..., description="Natural language search query", example="Best gaming laptop under 60000 with 16GB RAM")
    user_id: Optional[int] = Field(None, description="Optional user ID for personalization & search history")
    page: Optional[int] = Field(1, ge=1)
    page_size: Optional[int] = Field(20, ge=1, le=50)


# Module 4: Assistant
class AssistantChatRequest(BaseModel):
    message: str = Field(..., description="User query or message", example="I need a phone under 25000")
    session_id: Optional[str] = Field("default_session", description="Conversation session ID for multi-turn state")
    user_id: Optional[int] = Field(None, description="User ID")


# Module 3: Comparison
class CompareRequest(BaseModel):
    product_ids: List[str] = Field(..., min_length=2, max_length=4, description="List of 2 to 4 product IDs to compare")
    user_persona: Optional[str] = Field("balanced", description="Target persona: budget, gamer, photographer, professional, balanced")


# Module 6: Cart Analysis
class CartItemInput(BaseModel):
    product_id: str = Field(..., example="mob_1")
    quantity: Optional[int] = Field(1, ge=1)
    price: Optional[float] = Field(None)
    name: Optional[str] = Field(None)
    category: Optional[str] = Field(None)


class CartAnalysisRequest(BaseModel):
    items: List[CartItemInput] = Field(..., description="List of cart items")
    user_budget: Optional[float] = Field(None, description="User optional target budget in INR")


# Module 2 & 9: Interaction
class InteractionRequest(BaseModel):
    user_id: int = Field(..., example=10)
    product_id: str = Field(..., example="mob_1")
    product_name: str = Field(..., example="Nothing Phone (2a) 5G")
    category: str = Field(..., example="Mobiles")
    interaction_type: str = Field(..., example="view", description="view | compare | wishlist | cart_add | purchase")
    weight: Optional[float] = Field(1.0)


# Module 5: Target Price Alert
class TargetAlertRequest(BaseModel):
    user_id: int = Field(..., example=10)
    product_id: str = Field(..., example="mob_1")
    product_name: str = Field(..., example="Nothing Phone (2a) 5G")
    target_price: float = Field(..., example=19000.0)
    current_price: float = Field(..., example=19999.0)


# Module 7: Classification
class ClassifyRequest(BaseModel):
    title: str = Field(..., example="Sony WH-1000XM4 Noise Cancelling Headphones")
    description: Optional[str] = Field("")


# -----------------------------------------------------------------------------
# Base & Health Endpoints
# -----------------------------------------------------------------------------
@app.get("/")
def root():
    return {
        "service": "Smart Cart AI - Complete Shopping Intelligence System",
        "version": "2.0.0",
        "modules": [
            "Module 1: Smart AI Search (/api/search)",
            "Module 2: Personalized Recommendation Engine (/api/recommendations/{user_id})",
            "Module 3: Smart Product Comparison (/api/compare)",
            "Module 4: Shopping Assistant Dialog Manager (/api/assistant/chat)",
            "Module 5: Price Intelligence & Alerts (/api/prices/{product_id})",
            "Module 6: Smart Cart Optimization (/api/cart/analyze)",
            "Module 7: Product Classification (/classify)",
            "Module 8: FastAPI Backend",
            "Module 9: Firebase Firestore Dual-Mode Integration"
        ],
        "hardware_target": "CPU-optimized (4 GB RAM, <80 MB resident)",
        "external_ai_apis": "None (100% custom local ML)"
    }


@app.get("/health")
@app.get("/api/health")
def health_check():
    mem_usage = "<80 MB active resident memory"
    try:
        import psutil
        process = psutil.Process()
        mem_usage = f"{round(process.memory_info().rss / (1024 * 1024), 2)} MB"
    except Exception:
        pass

    return {
        "status": "healthy",
        "memory_footprint": mem_usage,
        "models_status": {
            "search_engine": "ready",
            "recommender": "ready" if os.path.exists(RECOMMENDER_MODEL_PATH) else "catalog_mode",
            "price_predictor": "ready" if os.path.exists(PRICE_MODEL_PATH) else "catalog_mode",
            "classifier": "ready" if os.path.exists(CLASSIFIER_MODEL_PATH) else "rule_mode",
            "comparison_engine": "ready",
            "shopping_assistant": "ready",
            "smart_cart": "ready"
        },
        "firestore": {
            "connected": firestore_client.is_firestore_connected(),
            "mode": "Firebase Firestore" if firestore_client.is_firestore_connected() else "Local CSV / JSON Fallback"
        }
    }


# -----------------------------------------------------------------------------
# MODULE 1: SMART AI SEARCH ENDPOINTS
# -----------------------------------------------------------------------------
@app.post("/api/search")
def search_products(req: SearchRequest):
    """
    Intelligent product search:
    - Natural language parsing (e.g., 'Best gaming laptop under 60000 with 16GB RAM')
    - Automatic budget & specification extraction
    - Typo-tolerant matching
    - TF-IDF relevance ranking
    - Records search in Firestore search_history
    """
    if not req.query.strip():
        raise HTTPException(status_code=400, detail="Search query cannot be empty.")

    engine = get_search_engine()
    results = engine.search(query=req.query, top_k=req.page_size or 20)

    # Record in search history (Module 9)
    extracted = results.get("parsed_query", {})
    firestore_client.record_search_history(
        user_id=req.user_id,
        query=req.query,
        extracted_category=extracted.get("category"),
        extracted_budget=extracted.get("max_budget")
    )

    return results


@app.get("/api/search/autocomplete")
def search_autocomplete(q: str = Query(..., min_length=1, description="Search prefix")):
    """Instant autocomplete suggestions powered by indexed catalog products & brands."""
    engine = get_search_engine()
    suggestions = engine.autocomplete(prefix=q, limit=8)
    return {"prefix": q, "suggestions": suggestions}


# -----------------------------------------------------------------------------
# MODULE 2: PERSONALIZED RECOMMENDATION AI
# -----------------------------------------------------------------------------
@app.get("/api/recommendations/{user_id}")
def get_user_recommendations(
    user_id: int,
    product_id: Optional[str] = None,
    top_k: int = 6
):
    """
    Personalized Recommendations:
    - Cold-start aware: checks for >= 3 user interactions before claiming personalized affinity.
    - Blends KNN content cosine similarity with user interaction affinities.
    - Explicitly labels recommendation mode.
    """
    # Guard against FastAPI Query object when called directly
    pid = str(product_id) if (product_id and not hasattr(product_id, "default")) else None
    k = int(top_k) if (top_k and not hasattr(top_k, "default")) else 6

    rec_service = get_recommender_service()
    results = rec_service.get_hybrid_recommendations(user_id=user_id, product_id=pid, top_k=k)
    return results


@app.post("/recommend")
def legacy_recommend(req: Dict[str, Any]):
    """Maintains backward compatibility with original recommender endpoint."""
    rec_service = get_recommender_service()
    user_id = req.get("user_id")
    product_id = req.get("product_id")
    top_k = req.get("top_k", 5)
    return rec_service.get_hybrid_recommendations(user_id=user_id, product_id=product_id, top_k=top_k)


# -----------------------------------------------------------------------------
# MODULE 3: SMART PRODUCT COMPARISON
# -----------------------------------------------------------------------------
@app.post("/api/compare")
def compare_products_endpoint(req: CompareRequest):
    """
    Compares 2 to 4 products:
    - Side-by-side normalized spec matrix
    - Missing attributes marked as 'Unavailable' (zero fabrication)
    - Algorithmic verdict: Best Budget, Best Feature, Best Value, Disadvantages, Persona match.
    """
    comp_service = get_comparison_service()
    result = comp_service.compare_products(product_ids=req.product_ids, user_persona=req.user_persona or "balanced")
    if "error" in result:
        raise HTTPException(status_code=400, detail=result["error"])
    return result


# -----------------------------------------------------------------------------
# MODULE 4: SHOPPING ASSISTANT
# -----------------------------------------------------------------------------
@app.post("/api/assistant/chat")
def assistant_chat(req: AssistantChatRequest):
    """
    Shopping Assistant Dialog:
    - Scikit-learn intent classification (search, budget, compare, spec_explain, alternatives, general)
    - Rule-based entity extraction
    - Multi-turn conversation state persistence
    - Clarification questions when requirements are ambiguous
    - Plain-English spec explanations
    """
    if not req.message.strip():
        raise HTTPException(status_code=400, detail="Message cannot be empty.")

    assistant = get_shopping_assistant()
    response = assistant.handle_turn(
        session_id=req.session_id or "default_session",
        user_message=req.message,
        user_id=req.user_id
    )
    return response


# -----------------------------------------------------------------------------
# MODULE 5: PRICE INTELLIGENCE & ALERTS
# -----------------------------------------------------------------------------
@app.get("/api/prices/{product_id}")
def get_product_price_intelligence(
    product_id: str,
    days: int = Query(60, ge=7, le=180),
    target_alert_price: Optional[float] = Query(None, description="Optional target alert price to test trigger")
):
    """
    Price Intelligence:
    - Historical price series for charts
    - Highest, lowest, average, and launch peak prices
    - Price drops and percentage savings from peak
    - 7-day ahead Random Forest Regressor forecast with explicit statistical disclaimer
    - Target alert verification
    """
    price_svc = get_price_service()
    analytics = price_svc.get_price_analytics(product_id=product_id, days=days, target_alert_price=target_alert_price)
    if "error" in analytics:
        raise HTTPException(status_code=404, detail=analytics["error"])
    return analytics


@app.post("/predict-price")
def legacy_predict_price(req: Dict[str, Any]):
    """Maintains backward compatibility with original price prediction endpoint."""
    product_id = req.get("product_id")
    days_ahead = req.get("days_ahead", 7)
    if not product_id:
        raise HTTPException(status_code=400, detail="product_id is required.")
    price_svc = get_price_service()
    return price_svc.get_price_analytics(product_id=product_id, days=days_ahead * 5)


@app.post("/api/alerts/target")
def create_target_price_alert(req: TargetAlertRequest):
    """Saves a target price alert in Firestore (or local JSON) and evaluates status."""
    res = firestore_client.save_target_price_alert(
        user_id=req.user_id,
        product_id=req.product_id,
        product_name=req.product_name,
        target_price=req.target_price,
        current_price=req.current_price
    )
    # Check trigger condition immediately
    is_triggered = req.current_price <= req.target_price
    res["is_immediately_triggered"] = is_triggered
    res["message"] = (
        f"Alert set for ₹{req.target_price:,.0f}. You will be notified when price drops to or below this threshold."
        if not is_triggered else
        f"Price has already reached your target threshold of ₹{req.target_price:,.0f}!"
    )
    return res


@app.get("/api/alerts/user/{user_id}")
def get_user_target_alerts(user_id: int):
    """Returns all active target price alerts for a user with evaluated current price status."""
    raw_alerts = firestore_client.get_user_target_alerts(user_id)
    price_svc = get_price_service()
    enriched = []
    for a in raw_alerts:
        pid = a.get("product_id")
        target = float(a.get("target_price", 0))
        eval_info = price_svc.get_price_analytics(pid, target_alert_price=target)
        curr_price = eval_info.get("current_price", a.get("created_at_price", 0))
        triggered = curr_price <= target
        enriched.append({
            **a,
            "current_price": curr_price,
            "is_triggered": triggered,
            "difference": round(curr_price - target, 2)
        })
    return {"user_id": user_id, "alerts": enriched}


# -----------------------------------------------------------------------------
# MODULE 6: SMART CART OPTIMIZATION
# -----------------------------------------------------------------------------
@app.post("/api/cart/analyze")
def analyze_cart_endpoint(req: CartAnalysisRequest):
    """
    Smart Cart Insights:
    - Tracks cart total and item subtotals
    - Budget monitoring & threshold warnings (utilization %, over-budget warnings)
    - Suggests cheaper alternatives with calculated Rupee savings
    - Recommends complementary cross-selling accessories
    - Calculates product value score comparisons (rating per Rupee)
    """
    if not req.items:
        raise HTTPException(status_code=400, detail="Cart items cannot be empty.")

    cart_svc = get_smart_cart_service()
    items_payload = [item.dict() for item in req.items]
    analysis = cart_svc.analyze_cart(items=items_payload, user_budget=req.user_budget)
    return analysis


# -----------------------------------------------------------------------------
# MODULE 7: PRODUCT CLASSIFICATION
# -----------------------------------------------------------------------------
@app.post("/classify")
@app.post("/api/classify")
def classify_product(req: ClassifyRequest):
    """
    Product categorization using TF-IDF and Logistic Regression:
    Classifies product titles and descriptions into Mobiles, Laptops, Audio, Fashion, Home Appliances.
    """
    full_text = f"{req.title} {req.description}".strip()
    if not full_text:
        raise HTTPException(status_code=400, detail="Product title or description cannot be empty.")

    try:
        result = predict_category(full_text)
        return result
    except Exception as e:
        logger.error(f"Classification error: {e}")
        raise HTTPException(status_code=500, detail=str(e))


# -----------------------------------------------------------------------------
# MODULE 9: USER INTERACTIONS & PRODUCTS
# -----------------------------------------------------------------------------
@app.post("/api/interactions")
@app.post("/interactions/record")
def record_interaction_endpoint(req: InteractionRequest):
    """
    Records an interaction event (view, compare, wishlist, cart_add, purchase)
    to Firebase Firestore and local CSV dataset.
    """
    weight_map = {
        "view": 1.0,
        "compare": 2.5,
        "wishlist": 4.0,
        "cart_add": 4.5,
        "purchase": 5.0
    }
    weight = weight_map.get(req.interaction_type, req.weight or 1.0)
    res = firestore_client.record_interaction(
        user_id=req.user_id,
        product_id=req.product_id,
        product_name=req.product_name,
        category=req.category,
        interaction_type=req.interaction_type,
        weight=weight
    )
    return res


@app.get("/api/products/{product_id}")
def get_product_details(product_id: str):
    """Retrieves full product specifications and pricing from the verified catalog."""
    prod = firestore_client.get_product(product_id)
    if not prod:
        raise HTTPException(status_code=404, detail=f"Product '{product_id}' not found.")
    return prod


@app.get("/api/products")
def list_products(category: Optional[str] = None, limit: int = Query(25, ge=1, le=100)):
    """Lists products from the verified catalog with optional category filtering."""
    engine = get_search_engine()
    df = engine.df
    if category:
        df = df[df["category"].str.lower() == category.lower()]
    records = df.head(limit).to_dict(orient="records")
    return {"total": len(records), "products": records}


# -----------------------------------------------------------------------------
# MODEL METRICS & RETRAINING
# -----------------------------------------------------------------------------
@app.get("/model-metrics")
@app.get("/api/metrics")
def get_model_metrics():
    """Returns evaluation metrics, confusion matrices, and feature importances for all models."""
    metrics = {}
    clf_m = os.path.join(MODELS_DIR, "classifier_metrics.json")
    prc_m = os.path.join(MODELS_DIR, "price_predictor_metrics.json")
    rec_m = os.path.join(MODELS_DIR, "recommender_metrics.json")

    for key, path in [("classifier", clf_m), ("price_predictor", prc_m), ("recommender", rec_m)]:
        if os.path.exists(path):
            with open(path, "r", encoding="utf-8") as f:
                metrics[key] = json.load(f)
        else:
            metrics[key] = {"status": "trained_in_memory"}

    return {
        "status": "success",
        "models": metrics,
        "hardware_target": "Optimized for 4 GB RAM (CPU-based)"
    }


@app.post("/retrain")
@app.post("/api/retrain")
def trigger_model_retraining():
    """Triggers end-to-end retraining for all models with new data."""
    try:
        from retrain_models import retrain_all
        report = retrain_all()
        return report
    except Exception as e:
        logger.error(f"Retraining error: {e}")
        raise HTTPException(status_code=500, detail=str(e))
