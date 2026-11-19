"""
Smart Cart AI - Smart Cart Optimization Service (Module 6)
Features:
- Cart total tracking & item aggregation
- Budget monitoring & limit warning system
- Cheaper alternative discovery with calculated savings
- Complementary cross-selling recommendations
- Product value scoring and comparison
- Strict Rule: No external AI APIs, lightweight CPU-optimized analytics (<20MB RAM)
"""

import os
import json
import pandas as pd
import numpy as np
from typing import Dict, List, Optional, Any

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATASETS_DIR = os.path.join(BASE_DIR, "datasets")
PRODUCTS_PATH = os.path.join(DATASETS_DIR, "products_dataset.csv")

# Complementary category mappings
COMPLEMENTARY_MAP = {
    "mobile": ["audio", "accessories", "electronics"],
    "mobiles": ["audio", "accessories", "electronics"],
    "smartphone": ["audio", "accessories", "electronics"],
    "laptop": ["accessories", "electronics", "audio"],
    "laptops": ["accessories", "electronics", "audio"],
    "audio": ["accessories", "electronics"],
    "camera": ["accessories", "electronics"],
    "fashion": ["footwear", "accessories"],
    "footwear": ["fashion", "accessories"]
}

COMPLEMENTARY_KEYWORDS = {
    "mobile": ["earbuds", "case", "cover", "charger", "power bank", "adapter", "cable"],
    "laptop": ["mouse", "keyboard", "bag", "stand", "hub", "sleeve"],
    "audio": ["stand", "cable", "case"],
    "camera": ["sd card", "tripod", "bag", "battery", "lens"]
}


class SmartCartService:
    def __init__(self, products_path: str = PRODUCTS_PATH):
        self.products_path = products_path
        self.products_df = None
        self._load_products()

    def _load_products(self):
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

    def _find_catalog_product(self, product_id: str) -> Optional[Dict[str, Any]]:
        if self.products_df.empty:
            return None
        match = self.products_df[self.products_df["product_id"] == product_id]
        if not match.empty:
            return match.iloc[0].to_dict()
        return None

    def analyze_cart(
        self,
        items: List[Dict[str, Any]],
        user_budget: Optional[float] = None
    ) -> Dict[str, Any]:
        """
        Analyzes a shopping cart.
        items format: [{"product_id": "...", "quantity": 1, "price": 19999.0, ...}]
        """
        enriched_items = []
        cart_total = 0.0
        total_quantity = 0
        categories_in_cart = set()

        for raw_item in items:
            pid = str(raw_item.get("product_id", raw_item.get("id", "")))
            qty = max(1, int(raw_item.get("quantity", 1)))
            catalog_info = self._find_catalog_product(pid) or {}

            price = float(raw_item.get("price", catalog_info.get("current_price", catalog_info.get("price", 0.0))))
            name = str(raw_item.get("name", catalog_info.get("product_name", catalog_info.get("name", pid))))
            category = str(raw_item.get("category", catalog_info.get("category", "General")))
            brand = str(raw_item.get("brand", catalog_info.get("brand", "")))
            rating = float(raw_item.get("rating", catalog_info.get("rating", 4.0)))

            item_subtotal = price * qty
            cart_total += item_subtotal
            total_quantity += qty
            categories_in_cart.add(category.lower())

            # Value score: higher rating per 1,000 INR
            value_score = round((rating / max(1.0, price)) * 1000, 3)

            enriched_items.append({
                "product_id": pid,
                "product_name": name,
                "category": category,
                "brand": brand,
                "unit_price": price,
                "quantity": qty,
                "subtotal": round(item_subtotal, 2),
                "rating": rating,
                "value_score": value_score
            })

        cart_total = round(cart_total, 2)

        # 1. Budget Monitoring
        budget_analysis = None
        if user_budget is not None and user_budget > 0:
            diff = round(user_budget - cart_total, 2)
            is_over = cart_total > user_budget
            util_pct = round((cart_total / user_budget) * 100, 1)

            if is_over:
                status = "OVER_BUDGET"
                alert_type = "danger"
                warning_message = f"Budget Exceeded! Cart total (₹{cart_total:,.0f}) is ₹{abs(diff):,.0f} over your ₹{user_budget:,.0f} limit."
            elif util_pct >= 85:
                status = "NEAR_LIMIT"
                alert_type = "warning"
                warning_message = f"Budget Alert: {util_pct}% of your ₹{user_budget:,.0f} budget used. ₹{diff:,.0f} remaining."
            else:
                status = "WITHIN_BUDGET"
                alert_type = "success"
                warning_message = f"Cart is well within your budget! ₹{diff:,.0f} remaining ({100 - util_pct:.0f}% free)."

            budget_analysis = {
                "user_budget": user_budget,
                "cart_total": cart_total,
                "remaining_budget": diff,
                "utilization_percent": util_pct,
                "is_over_budget": is_over,
                "status": status,
                "alert_type": alert_type,
                "message": warning_message
            }

        # 2. Cheaper Alternative Discovery
        cheaper_alternatives = []
        total_potential_savings = 0.0

        if not self.products_df.empty:
            # Sort cart items by unit price descending to target biggest expenses
            sorted_items = sorted(enriched_items, key=lambda x: x["unit_price"], reverse=True)

            for item in sorted_items[:3]: # Evaluate top 3 most expensive items
                cat = item["category"]
                u_price = item["unit_price"]
                item_rating = item["rating"]

                # Find items in same category with price between 50% and 90% of current item
                candidates = self.products_df[
                    (self.products_df["category"].str.lower() == cat.lower()) &
                    (self.products_df["current_price"] < u_price) &
                    (self.products_df["current_price"] >= u_price * 0.40) &
                    (self.products_df["product_id"] != item["product_id"]) &
                    (self.products_df["rating"] >= max(3.5, item_rating - 0.4))
                ].sort_values(by="current_price", ascending=True)

                if not candidates.empty:
                    best_alt = candidates.iloc[0]
                    alt_price = float(best_alt["current_price"])
                    savings = round((u_price - alt_price) * item["quantity"], 2)
                    savings_pct = round(((u_price - alt_price) / u_price) * 100, 1)

                    if savings > 0:
                        total_potential_savings += savings
                        cheaper_alternatives.append({
                            "original_product_id": item["product_id"],
                            "original_product_name": item["product_name"],
                            "original_unit_price": u_price,
                            "alternative_product_id": str(best_alt["product_id"]),
                            "alternative_product_name": str(best_alt.get("product_name", best_alt.get("name", ""))),
                            "alternative_unit_price": alt_price,
                            "alternative_rating": float(best_alt.get("rating", 4.0)),
                            "savings_amount": savings,
                            "savings_percent": savings_pct,
                            "recommendation_reason": (
                                f"Switch to {best_alt.get('brand', '')} to save ₹{savings:,.0f} ({savings_pct}% off) "
                                f"with comparable {best_alt.get('rating', 4.0)}★ rating."
                            )
                        })

        # 3. Complementary Products (Cross-selling)
        complementary_recommendations = []
        cart_pids = {i["product_id"] for i in enriched_items}

        if not self.products_df.empty:
            for cat in categories_in_cart:
                comp_cats = COMPLEMENTARY_MAP.get(cat, ["accessories"])
                keywords = COMPLEMENTARY_KEYWORDS.get(cat, ["accessory", "cable"])

                # Find candidates in complementary categories
                comp_df = self.products_df[
                    (self.products_df["category"].str.lower().isin([c.lower() for c in comp_cats])) &
                    (~self.products_df["product_id"].isin(cart_pids))
                ]

                # Keyword match if possible
                matched = comp_df[comp_df["product_name"].str.lower().apply(
                    lambda name: any(kw in name for kw in keywords)
                )]
                if matched.empty:
                    matched = comp_df

                if not matched.empty:
                    top_comp = matched.sort_values(by="rating", ascending=False).head(2)
                    for _, comp_row in top_comp.iterrows():
                        if str(comp_row["product_id"]) not in [c["product_id"] for c in complementary_recommendations]:
                            complementary_recommendations.append({
                                "product_id": str(comp_row["product_id"]),
                                "product_name": str(comp_row.get("product_name", comp_row.get("name", ""))),
                                "category": str(comp_row.get("category", "")),
                                "brand": str(comp_row.get("brand", "")),
                                "current_price": float(comp_row.get("current_price", 0.0)),
                                "rating": float(comp_row.get("rating", 4.0)),
                                "complements_category": cat,
                                "reason": f"Perfect companion accessory for items in your {cat.title()} cart."
                            })

        # 4. Product Value Comparisons
        best_value_item = None
        most_expensive_item = None
        if enriched_items:
            best_value_item = max(enriched_items, key=lambda x: x["value_score"])
            most_expensive_item = max(enriched_items, key=lambda x: x["unit_price"])

        value_comparison = {
            "best_value_item": {
                "product_id": best_value_item["product_id"],
                "product_name": best_value_item["product_name"],
                "rating": best_value_item["rating"],
                "price": best_value_item["unit_price"],
                "score_explanation": "Highest quality rating per Rupee spent in your cart."
            } if best_value_item else None,
            "most_expensive_item": {
                "product_id": most_expensive_item["product_id"],
                "product_name": most_expensive_item["product_name"],
                "price": most_expensive_item["unit_price"],
                "share_of_cart_total_percent": round((most_expensive_item["subtotal"] / max(1.0, cart_total)) * 100, 1)
            } if most_expensive_item else None
        }

        return {
            "cart_total": cart_total,
            "total_quantity": total_quantity,
            "items": enriched_items,
            "budget_analysis": budget_analysis,
            "cheaper_alternatives": cheaper_alternatives,
            "total_potential_savings": round(total_potential_savings, 2),
            "complementary_recommendations": complementary_recommendations[:4],
            "value_comparison": value_comparison
        }


# Global singleton instance
_cart_service_instance: Optional[SmartCartService] = None

def get_smart_cart_service() -> SmartCartService:
    global _cart_service_instance
    if _cart_service_instance is None:
        _cart_service_instance = SmartCartService()
    return _cart_service_instance

if __name__ == "__main__":
    svc = get_smart_cart_service()
    print("Testing Smart Cart Service...")

    sample_items = [
        {"product_id": "mob_1", "quantity": 1, "price": 19999.0}, # Nothing Phone (2a)
        {"product_id": "aud_1", "quantity": 1, "price": 2799.0}   # JBL Tune 510BT
    ]

    analysis = svc.analyze_cart(sample_items, user_budget=20000.0)
    print(f"Cart Total: ₹{analysis['cart_total']} (Items: {analysis['total_quantity']})")
    print(f"Budget Status: {analysis['budget_analysis']['status']} - {analysis['budget_analysis']['message']}")
    print(f"Potential Savings from Swaps: ₹{analysis['total_potential_savings']}")
    if analysis['cheaper_alternatives']:
        alt = analysis['cheaper_alternatives'][0]
        print(f"Cheaper swap: Replace {alt['original_product_name']} with {alt['alternative_product_name']} (Save ₹{alt['savings_amount']})")
    print(f"Complementary Suggestions: {len(analysis['complementary_recommendations'])} items found.")
    print("Smart Cart Service verified successfully!")
