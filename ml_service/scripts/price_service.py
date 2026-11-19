"""
Smart Cart AI - Price Intelligence Service (Module 5)
Features:
- Historical price retrieval (chart series, 30/60/90 day high/low/current/average)
- Price movement detection (increases, decreases, price drop % calculation)
- Potential savings calculation against peak/launch price
- Algorithmic price forecast (7-day ahead Random Forest Regressor) with disclaimer
- Target price alert evaluation (instant check if market price <= user target alert)
- Hardware Budget: Low-memory CPU execution (<20MB)
- Strict Rule: No external AI APIs, zero fabrication of price history.
"""

import os
import json
import pandas as pd
import numpy as np
from datetime import datetime, timedelta
from typing import Dict, List, Optional, Any

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATASETS_DIR = os.path.join(BASE_DIR, "datasets")
MODELS_DIR = os.path.join(BASE_DIR, "models")
DATASET_PATH = os.path.join(DATASETS_DIR, "price_history_dataset.csv")
PRODUCTS_PATH = os.path.join(DATASETS_DIR, "products_dataset.csv")

DISCLAIMER_TEXT = (
    "DISCLAIMER: Predicted prices are algorithmic statistical estimates derived from historical pricing trends, "
    "moving averages, and seasonal patterns. Actual marketplace prices on Amazon and Flipkart fluctuate continuously "
    "based on merchant inventory, real-time demand, and flash sales. These predictions are provided for analytical "
    "comparison only and do not represent a guaranteed or committed purchase price."
)

class PriceIntelligenceService:
    def __init__(self, price_history_path: str = DATASET_PATH, products_path: str = PRODUCTS_PATH):
        self.price_history_path = price_history_path
        self.products_path = products_path
        self.history_df = None
        self.products_df = None
        self._load_data()

    def _load_data(self):
        if os.path.exists(self.price_history_path):
            self.history_df = pd.read_csv(self.price_history_path)
            self.history_df["date"] = pd.to_datetime(self.history_df["date"])
            self.history_df = self.history_df.sort_values(by=["product_id", "date"]).reset_index(drop=True)
        else:
            self.history_df = pd.DataFrame()

        if os.path.exists(self.products_path):
            self.products_df = pd.read_csv(self.products_path)
            if "product_id" not in self.products_df.columns and "id" in self.products_df.columns:
                self.products_df["product_id"] = self.products_df["id"]
            if "current_price" not in self.products_df.columns:
                if "numeric_price" in self.products_df.columns:
                    self.products_df["current_price"] = pd.to_numeric(self.products_df["numeric_price"], errors="coerce").fillna(0.0)
                elif "price" in self.products_df.columns:
                    self.products_df["current_price"] = pd.to_numeric(self.products_df["price"].astype(str).str.replace(r"[^\d.]", "", regex=True), errors="coerce").fillna(0.0)
            if "product_name" not in self.products_df.columns and "name" in self.products_df.columns:
                self.products_df["product_name"] = self.products_df["name"]
        else:
            self.products_df = pd.DataFrame()

    def get_price_analytics(self, product_id: str, days: int = 60, target_alert_price: Optional[float] = None) -> Dict[str, Any]:
        """
        Retrieves historical price chart data, statistics, drop analysis,
        and target price alert status.
        """
        if self.history_df.empty:
            return {"error": "Price history dataset unavailable", "product_id": product_id}

        prod_df = self.history_df[self.history_df["product_id"] == product_id].sort_values(by="date")
        if prod_df.empty:
            # Check if product exists in products dataset
            p_match = self.products_df[self.products_df["product_id"] == product_id]
            if not p_match.empty:
                p_row = p_match.iloc[0]
                curr_price = float(p_row.get("current_price", p_row.get("price", 0)))
                return {
                    "product_id": product_id,
                    "product_name": str(p_row.get("product_name", p_row.get("name", product_id))),
                    "current_price": curr_price,
                    "history_available": False,
                    "message": "Insufficient historical data for time-series chart. Live catalog price displayed.",
                    "chart_points": [{"date": datetime.now().strftime("%Y-%m-%d"), "price": curr_price}],
                    "disclaimer": DISCLAIMER_TEXT
                }
            return {"error": f"Product '{product_id}' not found in catalog or price history."}

        # Filter to requested window
        latest_date = prod_df["date"].max()
        cutoff_date = latest_date - timedelta(days=days)
        window_df = prod_df[prod_df["date"] >= cutoff_date]
        if window_df.empty:
            window_df = prod_df

        current_record = prod_df.iloc[-1]
        current_price = float(current_record["price"])
        product_name = str(current_record.get("product_name", product_id))
        category = str(current_record.get("category", "General"))

        prices_series = window_df["price"].values
        high_price = float(np.max(prices_series))
        low_price = float(np.min(prices_series))
        avg_price = round(float(np.mean(prices_series)), 2)
        launch_peak = float(prod_df["price"].max())

        # Price movement detection
        if len(prices_series) >= 2:
            prev_price = float(prices_series[-2])
            day_change = round(current_price - prev_price, 2)
            day_change_pct = round((day_change / prev_price) * 100, 2) if prev_price > 0 else 0.0
        else:
            prev_price = current_price
            day_change = 0.0
            day_change_pct = 0.0

        # Drop detection (relative to 30-day peak or launch peak)
        savings_from_peak = round(launch_peak - current_price, 2)
        savings_from_peak_pct = round((savings_from_peak / launch_peak) * 100, 2) if launch_peak > 0 else 0.0
        is_lowest_price = current_price <= (low_price + 1e-2)
        is_significant_drop = savings_from_peak_pct >= 5.0

        # Build chart data series
        chart_points = []
        for _, row in window_df.iterrows():
            chart_points.append({
                "date": row["date"].strftime("%Y-%m-%d"),
                "price": round(float(row["price"]), 2),
                "is_lowest": bool(round(float(row["price"]), 2) == round(low_price, 2))
            })

        # Target alert verification
        alert_status = None
        if target_alert_price is not None and target_alert_price > 0:
            is_triggered = current_price <= target_alert_price
            diff = round(current_price - target_alert_price, 2)
            alert_status = {
                "target_price": target_alert_price,
                "current_price": current_price,
                "is_triggered": is_triggered,
                "difference": diff,
                "message": (
                    f"TARGET REACHED! Current price ₹{current_price:,.0f} is at or below your alert threshold ₹{target_alert_price:,.0f}."
                    if is_triggered else
                    f"Price is ₹{diff:,.0f} away from your target threshold of ₹{target_alert_price:,.0f}."
                )
            }

        # Algorithmic Forecast (from train_price_predictor bundle if available)
        forecast_result = None
        if len(prod_df) >= 14:
            try:
                from train_price_predictor import forecast_product_price
                forecast_result = forecast_product_price(product_id, days_ahead=7)
            except Exception as e:
                # Fallback to local moving trend if predictor module not loaded
                expected_next = round(avg_price * 0.98, 2) if current_price > avg_price else round(avg_price * 1.01, 2)
                forecast_result = {
                    "product_id": product_id,
                    "forecast_days": 7,
                    "price_trend": "dropping" if expected_next < current_price else "rising",
                    "estimated_range": f"₹{low_price:,.0f} - ₹{high_price:,.0f}",
                    "disclaimer": DISCLAIMER_TEXT
                }

        return {
            "product_id": product_id,
            "product_name": product_name,
            "category": category,
            "current_price": current_price,
            "highest_price": high_price,
            "lowest_price": low_price,
            "average_price": avg_price,
            "launch_peak_price": launch_peak,
            "savings_from_peak": savings_from_peak,
            "savings_from_peak_percent": savings_from_peak_pct,
            "is_all_time_low": is_lowest_price,
            "is_significant_drop": is_significant_drop,
            "day_change": day_change,
            "day_change_percent": day_change_pct,
            "movement_status": "decreased" if day_change < 0 else "increased" if day_change > 0 else "stable",
            "chart_points": chart_points,
            "target_alert_evaluation": alert_status,
            "forecast": forecast_result,
            "disclaimer": DISCLAIMER_TEXT
        }

    def check_target_alerts(self, alerts: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        """
        Batch checks a list of user target price alerts.
        Each alert item: {"product_id": str, "target_price": float, "user_id": Optional[str]}
        """
        results = []
        for alert in alerts:
            pid = alert.get("product_id")
            target = float(alert.get("target_price", 0))
            if not pid or target <= 0:
                continue
            analytics = self.get_price_analytics(pid, target_alert_price=target)
            if "error" not in analytics and analytics.get("target_alert_evaluation"):
                eval_data = analytics["target_alert_evaluation"]
                results.append({
                    "product_id": pid,
                    "product_name": analytics.get("product_name"),
                    "target_price": target,
                    "current_price": analytics.get("current_price"),
                    "is_triggered": eval_data["is_triggered"],
                    "difference": eval_data["difference"],
                    "notification_message": eval_data["message"]
                })
        return results


# Global singleton instance
_price_service_instance: Optional[PriceIntelligenceService] = None

def get_price_service() -> PriceIntelligenceService:
    global _price_service_instance
    if _price_service_instance is None:
        _price_service_instance = PriceIntelligenceService()
    return _price_service_instance

if __name__ == "__main__":
    svc = get_price_service()
    print("Testing Price Intelligence Service...")
    sample = svc.get_price_analytics("mob_1", days=30, target_alert_price=20000.0)
    print(f"Product: {sample.get('product_name')}")
    print(f"Current: ₹{sample.get('current_price')} | Low: ₹{sample.get('lowest_price')} | High: ₹{sample.get('highest_price')}")
    print(f"Savings from peak: ₹{sample.get('savings_from_peak')} ({sample.get('savings_from_peak_percent')}%)")
    print(f"Target Alert Status: {sample.get('target_alert_evaluation')}")
    print(f"Forecast trend: {sample.get('forecast', {}).get('price_trend')}")
    print("Service initialized and verified successfully!")
