"""
Smart Cart AI - Automated Model Retraining Workflow
Runs when sufficient new interaction or price data is collected.
Retrains all 3 models:
1. Product Classifier (TF-IDF + Logistic Regression)
2. Price Predictor (Time-Series Random Forest Regressor)
3. Recommender (KNN Content + Hybrid Interaction Profiles)
Atomically updates Joblib model files and logs updated metrics.
"""

import os
import sys
import json
import logging
from datetime import datetime

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SCRIPTS_DIR = os.path.join(BASE_DIR, "scripts")
if SCRIPTS_DIR not in sys.path:
    sys.path.insert(0, SCRIPTS_DIR)

import train_classifier
import train_price_predictor
import train_recommender

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")
logger = logging.getLogger("smartcart_ml.retrain")

DATASETS_DIR = os.path.join(BASE_DIR, "datasets")
MODELS_DIR = os.path.join(BASE_DIR, "models")
RETRAIN_LOG_PATH = os.path.join(MODELS_DIR, "retraining_history.json")


def retrain_all() -> dict:
    start_time = datetime.now()
    logger.info("=== STARTING FULL RETRAINING WORKFLOW ===")
    
    results = {
        "timestamp": start_time.isoformat(),
        "status": "in_progress",
        "models_retrained": {}
    }

    # 1. Retrain Classifier
    try:
        logger.info("[1/3] Retraining Product Classification Model...")
        train_classifier.train()
        with open(os.path.join(MODELS_DIR, "classifier_metrics.json"), "r", encoding="utf-8") as f:
            clf_metrics = json.load(f)
        results["models_retrained"]["classifier"] = {
            "status": "success",
            "accuracy": clf_metrics["evaluation"]["accuracy"],
            "f1_weighted": clf_metrics["evaluation"]["f1_weighted"]
        }
    except Exception as e:
        logger.error(f"Classifier retraining failed: {e}")
        results["models_retrained"]["classifier"] = {"status": "failed", "error": str(e)}

    # 2. Retrain Price Predictor
    try:
        logger.info("[2/3] Retraining Price Predictor Model...")
        train_price_predictor.train()
        with open(os.path.join(MODELS_DIR, "price_predictor_metrics.json"), "r", encoding="utf-8") as f:
            prc_metrics = json.load(f)
        results["models_retrained"]["price_predictor"] = {
            "status": "success",
            "mae_inr": prc_metrics["evaluation"]["mae_inr"],
            "r2_score": prc_metrics["evaluation"]["r2_score"]
        }
    except Exception as e:
        logger.error(f"Price predictor retraining failed: {e}")
        results["models_retrained"]["price_predictor"] = {"status": "failed", "error": str(e)}

    # 3. Retrain Recommender
    try:
        logger.info("[3/3] Retraining Recommender (KNN + Hybrid User Affinities)...")
        train_recommender.train()
        with open(os.path.join(MODELS_DIR, "recommender_metrics.json"), "r", encoding="utf-8") as f:
            rec_metrics = json.load(f)
        results["models_retrained"]["recommender"] = {
            "status": "success",
            "category_cohesion": rec_metrics["evaluation"]["category_cohesion_top5"],
            "active_user_profiles": rec_metrics["evaluation"]["active_user_profiles"]
        }
    except Exception as e:
        logger.error(f"Recommender retraining failed: {e}")
        results["models_retrained"]["recommender"] = {"status": "failed", "error": str(e)}

    end_time = datetime.now()
    duration_sec = round((end_time - start_time).total_seconds(), 2)
    results["duration_seconds"] = duration_sec
    results["status"] = "completed"

    # Append to retraining history
    history = []
    if os.path.exists(RETRAIN_LOG_PATH):
        try:
            with open(RETRAIN_LOG_PATH, "r", encoding="utf-8") as f:
                history = json.load(f)
        except Exception:
            history = []
    
    history.append(results)
    with open(RETRAIN_LOG_PATH, "w", encoding="utf-8") as f:
        json.dump(history, f, indent=2)

    logger.info(f"=== RETRAINING COMPLETED IN {duration_sec}s ===")
    return results


if __name__ == "__main__":
    report = retrain_all()
    print("\nRetraining Summary:")
    print(json.dumps(report, indent=2))
