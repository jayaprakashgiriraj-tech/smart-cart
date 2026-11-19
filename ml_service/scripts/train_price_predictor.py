"""
Smart Cart AI - Price Prediction Training Script
Model: Random Forest Regressor on Historical Time-Series
Hardware Budget: Optimized for 4 GB RAM (<40 MB peak RAM)
Strict Requirement: No external AI APIs, 100% local scikit-learn.
Chronological validation (no future leakage). Explicit non-guaranteed disclaimer.
"""

import os
import json
import joblib
import pandas as pd
import numpy as np
from datetime import datetime, timedelta
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_absolute_error, mean_squared_error, r2_score

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATASETS_DIR = os.path.join(BASE_DIR, "datasets")
MODELS_DIR = os.path.join(BASE_DIR, "models")
os.makedirs(MODELS_DIR, exist_ok=True)

DATASET_PATH = os.path.join(DATASETS_DIR, "price_history_dataset.csv")
MODEL_OUTPUT_PATH = os.path.join(MODELS_DIR, "price_predictor_model.joblib")
METRICS_OUTPUT_PATH = os.path.join(MODELS_DIR, "price_predictor_metrics.json")

DISCLAIMER_TEXT = (
    "DISCLAIMER: Predicted prices are algorithmic statistical estimates derived from historical pricing trends, "
    "moving averages, and seasonal patterns. Actual marketplace prices on Amazon and Flipkart fluctuate continuously "
    "based on merchant inventory, real-time demand, and flash sales. These predictions are provided for analytical "
    "comparison only and do not represent a guaranteed or committed purchase price."
)

FEATURE_COLS = [
    "lag_price_1d",
    "lag_price_7d",
    "rolling_mean_7d",
    "rolling_mean_30d",
    "lowest_price_30d",
    "highest_price_30d",
    "day_of_week",
    "day_of_month",
    "month",
    "is_weekend",
    "discount_rate"
]


def train():
    print("=" * 60)
    print("STAGE 4: TRAINING PRICE PREDICTOR (RANDOM FOREST REGRESSOR)")
    print("=" * 60)

    if not os.path.exists(DATASET_PATH):
        raise FileNotFoundError(f"Dataset not found at {DATASET_PATH}. Run generate_datasets.py first.")

    df = pd.read_csv(DATASET_PATH)
    print(f"[1/5] Loaded price history: {len(df)} records across {df['product_id'].nunique()} products.")
    print(f"      Date range: {df['date'].min()} to {df['date'].max()}")

    # Ensure chronological order
    df["date"] = pd.to_datetime(df["date"])
    df = df.sort_values(by=["date", "product_id"]).reset_index(drop=True)

    # Chronological Split: 80% earlier days train, 20% latest days validation
    unique_dates = sorted(df["date"].unique())
    split_idx = int(len(unique_dates) * 0.80)
    split_date = unique_dates[split_idx]

    train_mask = df["date"] < split_date
    val_mask = df["date"] >= split_date

    train_df = df[train_mask]
    val_df = df[val_mask]

    print(f"[2/5] Chronological split at: {pd.to_datetime(split_date).strftime('%Y-%m-%d')}")
    print(f"      Train set: {len(train_df)} rows ({train_df['date'].min().strftime('%Y-%m-%d')} to {train_df['date'].max().strftime('%Y-%m-%d')})")
    print(f"      Validation set: {len(val_df)} rows ({val_df['date'].min().strftime('%Y-%m-%d')} to {val_df['date'].max().strftime('%Y-%m-%d')})")

    X_train = train_df[FEATURE_COLS]
    y_train = train_df["price"]

    X_val = val_df[FEATURE_COLS]
    y_val = val_df["price"]

    # Random Forest Regressor tuned for fast CPU & 4GB RAM
    model = RandomForestRegressor(
        n_estimators=60,
        max_depth=10,
        min_samples_split=4,
        min_samples_leaf=2,
        random_state=42,
        n_jobs=2
    )

    print("[3/5] Fitting Random Forest Regressor on time-series features...")
    model.fit(X_train, y_train)

    # Evaluate on chronological validation set
    y_pred = model.predict(X_val)

    mae = mean_absolute_error(y_val, y_pred)
    mse = mean_squared_error(y_val, y_pred)
    rmse = np.sqrt(mse)
    r2 = r2_score(y_val, y_pred)
    mape = np.mean(np.abs((y_val - y_pred) / y_val)) * 100

    print("\n--- Model Evaluation (Out-of-Time Chronological Validation) ---")
    print(f"Mean Absolute Error (MAE):     ₹{mae:.2f}")
    print(f"Root Mean Squared Error (RMSE): ₹{rmse:.2f}")
    print(f"Mean Absolute Percentage Error:{mape:.2f}%")
    print(f"R² Score:                      {r2:.4f}")

    # Feature importances
    importances = dict(zip(FEATURE_COLS, [round(float(imp), 4) for imp in model.feature_importances_]))
    sorted_importances = dict(sorted(importances.items(), key=lambda x: x[1], reverse=True))
    print(f"\nFeature Importances: {sorted_importances}")

    # Save artifact bundle with Joblib
    bundle = {
        "model": model,
        "features": FEATURE_COLS,
        "disclaimer": DISCLAIMER_TEXT,
        "metadata": {
            "algorithm": "Random Forest Regressor (Time-Series)",
            "n_estimators": 60,
            "max_depth": 10,
            "train_samples": len(train_df),
            "val_samples": len(val_df),
            "created_at": pd.Timestamp.now().isoformat(),
            "metrics": {
                "mae_inr": round(float(mae), 2),
                "rmse_inr": round(float(rmse), 2),
                "mape_percent": round(float(mape), 2),
                "r2_score": round(float(r2), 4)
            },
            "feature_importances": sorted_importances
        }
    }

    joblib.dump(bundle, MODEL_OUTPUT_PATH, compress=3)
    print(f"[4/5] Saved model bundle to: {MODEL_OUTPUT_PATH}")

    # Save metrics JSON
    metrics_data = {
        "model_name": "Price Predictor",
        "algorithm": "Random Forest Regressor",
        "evaluation": bundle["metadata"]["metrics"],
        "feature_importances": sorted_importances,
        "disclaimer": DISCLAIMER_TEXT
    }
    with open(METRICS_OUTPUT_PATH, "w", encoding="utf-8") as f:
        json.dump(metrics_data, f, indent=2)
    print(f"[5/5] Metrics JSON saved to: {METRICS_OUTPUT_PATH}")


def forecast_product_price(product_id: str, days_ahead: int = 7) -> dict:
    """Simulates multi-step autoregressive price forecast for a given product."""
    if not os.path.exists(MODEL_OUTPUT_PATH):
        raise FileNotFoundError("Model not trained yet.")
    if not os.path.exists(DATASET_PATH):
        raise FileNotFoundError("Dataset missing.")

    bundle = joblib.load(MODEL_OUTPUT_PATH)
    model = bundle["model"]

    df = pd.read_csv(DATASET_PATH)
    prod_history = df[df["product_id"] == product_id].sort_values(by="date").reset_index(drop=True)
    if prod_history.empty:
        # Fallback to general category stats if unseen product
        return {
            "error": f"Product ID {product_id} not found in history.",
            "disclaimer": DISCLAIMER_TEXT
        }

    latest = prod_history.iloc[-1].to_dict()
    current_price = float(latest["price"])
    product_name = latest["product_name"]
    category = latest["category"]

    recent_prices = prod_history["price"].tolist()[-30:] # Recent 30 days window
    last_date = pd.to_datetime(latest["date"])

    forecast_points = []
    simulated_prices = list(recent_prices)

    for step in range(1, days_ahead + 1):
        target_date = last_date + timedelta(days=step)
        dow = target_date.weekday()
        dom = target_date.day
        month = target_date.month
        is_wknd = 1 if dow in [5, 6] else 0

        lag_1 = simulated_prices[-1]
        lag_7 = simulated_prices[-7] if len(simulated_prices) >= 7 else simulated_prices[0]
        roll_7 = float(np.mean(simulated_prices[-7:]))
        roll_30 = float(np.mean(simulated_prices[-30:]))
        low_30 = float(np.min(simulated_prices[-30:]))
        high_30 = float(np.max(simulated_prices[-30:]))
        mrp = max(high_30, current_price * 1.05)
        disc = max(0.0, (mrp - lag_1) / mrp * 100)

        feature_vector = pd.DataFrame([{
            "lag_price_1d": lag_1,
            "lag_price_7d": lag_7,
            "rolling_mean_7d": roll_7,
            "rolling_mean_30d": roll_30,
            "lowest_price_30d": low_30,
            "highest_price_30d": high_30,
            "day_of_week": dow,
            "day_of_month": dom,
            "month": month,
            "is_weekend": is_wknd,
            "discount_rate": disc
        }])

        pred_price = round(float(model.predict(feature_vector)[0]), 2)
        # Apply gentle bounded dampening to prevent drift
        pred_price = max(low_30 * 0.95, min(high_30 * 1.05, pred_price))
        simulated_prices.append(pred_price)

        forecast_points.append({
            "date": target_date.strftime("%Y-%m-%d"),
            "predicted_price": pred_price,
            "expected_change": round(pred_price - current_price, 2),
            "expected_change_percent": round(((pred_price - current_price) / current_price) * 100, 2)
        })

    best_buy_day = min(forecast_points, key=lambda x: x["predicted_price"])
    price_trend = "dropping" if forecast_points[-1]["predicted_price"] < current_price else "rising" if forecast_points[-1]["predicted_price"] > current_price else "stable"

    return {
        "product_id": product_id,
        "product_name": product_name,
        "category": category,
        "current_price": current_price,
        "forecast_days": days_ahead,
        "price_trend": price_trend,
        "best_day_to_buy": best_buy_day["date"],
        "estimated_lowest_price": best_buy_day["predicted_price"],
        "forecast": forecast_points,
        "disclaimer": DISCLAIMER_TEXT
    }


if __name__ == "__main__":
    train()

    print("\n--- Testing Price Forecast ---")
    res = forecast_product_price("mob_1", days_ahead=7)
    print(f"Product: {res['product_name']}")
    print(f"Current Price: ₹{res['current_price']}")
    print(f"Trend: {res['price_trend'].upper()} | Best Day to Buy: {res['best_day_to_buy']} (₹{res['estimated_lowest_price']})")
    print(f"Disclaimer attached: {res['disclaimer'][:65]}...")
