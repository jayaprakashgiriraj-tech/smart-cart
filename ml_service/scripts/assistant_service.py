"""
Smart Cart AI - Module 4: Shopping Assistant Dialog Manager
A reliable, task-focused shopping assistant built without external AI APIs.
Features:
- Intent classification (search, compare, alternatives, explain specs, budget inquiry, greeting)
- Rule-based slot and entity extraction (category, budget, brand, specs, priorities)
- Session conversation state management (remembers discussed product and constraints)
- Clarification questions when constraints are ambiguous
- Plain-English specification explainer
- Seamless catalog search and comparison retrieval
- 100% Local Scikit-learn & Python, <20 MB RAM footprint
"""

import os
import re
import uuid
import logging
from typing import List, Dict, Any, Optional
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression

logger = logging.getLogger("smartcart_ml.assistant")

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SCRIPTS_DIR = os.path.join(BASE_DIR, "scripts")

import search_engine
import recommender_service

# Plain-English technical explanations dictionary
SPEC_KNOWLEDGE_BASE = {
    "amoled": (
        "AMOLED (Active-Matrix Organic Light-Emitting Diode) displays use individual self-lit pixels. "
        "Unlike LCD screens with a backlight, AMOLED pixels turn off completely for pure pitch blacks, "
        "richer contrast, and significant battery savings in dark mode."
    ),
    "oled": (
        "OLED screens offer individual pixel illumination, providing virtually infinite contrast ratio, "
        "accurate color reproduction, and instant pixel response times ideal for cinematic movies and gaming."
    ),
    "ois": (
        "OIS (Optical Image Stabilization) uses tiny gyroscopes to physically counter hand tremors in real time. "
        "This prevents blurry photos in low light and produces steady, cinematic video without jitter."
    ),
    "anc": (
        "ANC (Active Noise Cancellation) uses exterior microphones to analyze ambient noises (airplane rumble, office chatter) "
        "and generates an exact inverse sound wave to cancel them out before they reach your ears."
    ),
    "120hz": (
        "A 120Hz refresh rate refreshes the screen 120 times every second—double standard 60Hz displays. "
        "This makes scrolling through apps, reading text, and playing fast-action games noticeably smoother."
    ),
    "unified memory": (
        "Apple's Unified Memory Architecture allows the CPU, GPU, and Neural Engine to access the exact same pool "
        "of high-bandwidth memory instantly, eliminating slow data transfers between components."
    ),
    "direct drive": (
        "Direct Drive motors in washing machines connect the motor directly to the wash drum without belts or pulleys. "
        "This significantly reduces motor noise, vibration, and long-term mechanical wear while saving energy."
    ),
    "inverter": (
        "An Inverter compressor or motor continuously adjusts its cooling or washing speed rather than turning completely "
        "on and off. This keeps internal temperatures precise and cuts electrical power bills by up to 35%."
    ),
    "fast charge": (
        "Fast charging (e.g. 65W to 120W) safely delivers high-current power in short bursts, giving you "
        "a full day of phone battery in approximately 15 to 25 minutes."
    )
}

# Training samples for shopping intent classifier
INTENT_TRAINING_DATA = [
    # search_products
    ("find me a phone under 25000", "search_products"),
    ("show me laptops for coding", "search_products"),
    ("I need wireless headphones below 3000", "search_products"),
    ("looking for a 5 star refrigerator", "search_products"),
    ("search gaming phones with 16GB RAM", "search_products"),
    ("suggest formal shirts for men", "search_products"),
    ("can you show me budget earbuds", "search_products"),

    # compare_products
    ("compare Nothing Phone 2a with OnePlus 12R", "compare_products"),
    ("what is the difference between MacBook Air M2 and Asus Zephyrus", "compare_products"),
    ("which is better JBL Tune 510BT or boAt Rockerz 450", "compare_products"),
    ("compare these two phones", "compare_products"),
    ("compare Samsung S23 and iPhone 15", "compare_products"),

    # recommend_alternatives
    ("suggest something cheaper than MacBook Air", "recommend_alternatives"),
    ("are there alternatives to this phone", "recommend_alternatives"),
    ("give me better value alternatives", "recommend_alternatives"),
    ("something similar but under 20000", "recommend_alternatives"),

    # explain_specs
    ("what is AMOLED display", "explain_specs"),
    ("explain what OIS means", "explain_specs"),
    ("what does ANC do in headphones", "explain_specs"),
    ("why do I need 120Hz screen", "explain_specs"),
    ("what is direct drive in washing machine", "explain_specs"),
    ("tell me about unified memory", "explain_specs"),

    # budget_inquiry
    ("what can I buy for 50000", "budget_inquiry"),
    ("I have 15000 budget what is best", "budget_inquiry"),
    ("best options within 30k", "budget_inquiry"),

    # greeting_help
    ("hello", "greeting_help"),
    ("hi there", "greeting_help"),
    ("what can you do", "greeting_help"),
    ("how can you help me shop", "greeting_help")
]


class ConversationSession:
    def __init__(self, session_id: str):
        self.session_id = session_id
        self.current_product_id: Optional[str] = None
        self.current_product_name: Optional[str] = None
        self.current_category: Optional[str] = None
        self.current_budget: Optional[int] = None
        self.current_priority: Optional[str] = None
        self.awaiting_clarification: bool = False
        self.clarification_type: Optional[str] = None
        self.history: List[Dict[str, str]] = []

    def update_from_parsed(self, parsed: Dict[str, Any]):
        if parsed.get("category"):
            self.current_category = parsed["category"]
        if parsed.get("max_budget"):
            self.current_budget = parsed["max_budget"]
        if parsed.get("purpose"):
            self.current_priority = parsed["purpose"]


class ShoppingAssistant:
    def __init__(self):
        self.sessions: Dict[str, ConversationSession] = {}
        self.search_engine = search_engine.get_search_engine()
        self.recommender = recommender_service.get_recommender_service()
        self._init_intent_model()

    def _init_intent_model(self):
        """Fits lightweight TF-IDF + Logistic Regression intent classifier."""
        texts = [x[0] for x in INTENT_TRAINING_DATA]
        labels = [x[1] for x in INTENT_TRAINING_DATA]

        self.vectorizer = TfidfVectorizer(ngram_range=(1, 2), stop_words="english")
        X = self.vectorizer.fit_transform(texts)
        self.clf = LogisticRegression(C=1.0, max_iter=200, random_state=42)
        self.clf.fit(X, labels)
        logger.info("[Shopping Assistant] Intent classifier ready (6 intent classes).")

    def predict_intent(self, text: str) -> str:
        """Classifies intent with fallback to keyword heuristics."""
        t_lower = text.lower()
        if any(w in t_lower for w in ["compare", "difference between", "versus", " vs "]):
            return "compare_products"
        if any(w in t_lower for w in ["what is", "what does", "explain", "meaning of"]):
            return "explain_specs"
        if any(w in t_lower for w in ["cheaper than", "alternative", "alternatives to", "similar to"]):
            return "recommend_alternatives"

        vec = self.vectorizer.transform([text])
        return self.clf.predict(vec)[0]

    def get_or_create_session(self, session_id: Optional[str] = None) -> ConversationSession:
        if not session_id or session_id not in self.sessions:
            new_id = session_id or str(uuid.uuid4())
            self.sessions[new_id] = ConversationSession(new_id)
            return self.sessions[new_id]
        return self.sessions[session_id]

    def chat(self, user_message: str, session_id: Optional[str] = None, user_id: Optional[int] = None) -> Dict[str, Any]:
        """Processes user message through stateful dialog flow."""
        session = self.get_or_create_session(session_id)
        session.history.append({"role": "user", "text": user_message})

        clean_msg = user_message.strip()
        lower_msg = clean_msg.lower()

        # Handle pending clarification response
        if session.awaiting_clarification:
            session.awaiting_clarification = False
            priority_detected = None
            if "camera" in lower_msg or "photo" in lower_msg:
                priority_detected = "camera"
            elif "game" in lower_msg or "gaming" in lower_msg:
                priority_detected = "gaming"
            elif "battery" in lower_msg or "charging" in lower_msg:
                priority_detected = "battery"
            elif "overall" in lower_msg or "performance" in lower_msg:
                priority_detected = "performance"

            if priority_detected:
                session.current_priority = priority_detected

            # Execute search with accumulated session context
            search_query = f"{session.current_category or 'product'} {session.current_priority or ''}"
            search_res = self.search_engine.search(
                query=search_query,
                category=session.current_category,
                max_budget=session.current_budget,
                top_k=4
            )

            budget_str = f" under ₹{session.current_budget:,}" if session.current_budget else ""
            reply_text = (
                f"Got it! Prioritizing **{session.current_priority or 'overall value'}** for {session.current_category or 'products'}{budget_str}. "
                f"Here are the strongest options from our catalog that fit your criteria:"
            )

            response_data = {
                "session_id": session.session_id,
                "reply": reply_text,
                "intent": "clarification_resolved",
                "recommended_products": search_res["results"],
                "active_context": {
                    "category": session.current_category,
                    "budget": session.current_budget,
                    "priority": session.current_priority,
                    "current_product": session.current_product_name
                }
            }
            session.history.append({"role": "assistant", "text": reply_text})
            return response_data

        # Detect intent
        intent = self.predict_intent(clean_msg)
        parsed_slots = self.search_engine.parse_natural_language_query(clean_msg)
        session.update_from_parsed(parsed_slots)

        # -------------------------------------------------------------
        # Intent 1: Greeting & Help
        # -------------------------------------------------------------
        if intent == "greeting_help":
            reply_text = (
                "Hello! I am your **Smart Cart AI Shopping Assistant**.\n\n"
                "I can help you:\n"
                "• Find the best products for your budget (e.g. *'Best phone under 25000'*)\n"
                "• Compare devices side by side (e.g. *'Compare Nothing Phone 2a with OnePlus 12R'*)\n"
                "• Explain technical specifications in simple words (e.g. *'What is AMOLED?'*)\n"
                "• Suggest cheaper, high-value alternatives\n\n"
                "What product are you shopping for today?"
            )
            return self._format_response(session, reply_text, intent, [])

        # -------------------------------------------------------------
        # Intent 2: Explain Specifications
        # -------------------------------------------------------------
        if intent == "explain_specs":
            found_explanation = None
            for key, exp in SPEC_KNOWLEDGE_BASE.items():
                if key in lower_msg:
                    found_explanation = exp
                    break

            if found_explanation:
                reply_text = f"**Technical Specification Explained:**\n\n{found_explanation}"
            else:
                reply_text = (
                    "I can explain hardware specs like AMOLED, OIS, ANC, 120Hz, Fast Charging, and Inverter motors. "
                    "Which specification would you like me to clarify?"
                )
            return self._format_response(session, reply_text, intent, [])

        # -------------------------------------------------------------
        # Intent 3: Recommend Alternatives
        # -------------------------------------------------------------
        if intent == "recommend_alternatives":
            target_pid = session.current_product_id or "mob_1"
            alt_res = self.recommender.get_budget_alternatives(target_pid, top_k=3)
            if alt_res["recommendations"]:
                reply_text = (
                    f"Here are top-value alternatives to **{alt_res.get('base_product_name', 'your product')}** "
                    f"that deliver comparable performance while saving your budget:"
                )
                return self._format_response(session, reply_text, intent, alt_res["recommendations"])
            else:
                search_res = self.search_engine.search(query=clean_msg, top_k=3)
                reply_text = "Here are related alternative options with strong customer ratings:"
                return self._format_response(session, reply_text, intent, search_res["results"])

        # -------------------------------------------------------------
        # Intent 4: Compare Products
        # -------------------------------------------------------------
        if intent == "compare_products":
            # Extract names mentioned
            prods = self.search_engine.products_df
            found_names = []
            if prods is not None and not prods.empty:
                for name in prods["name"].tolist():
                    if name.lower() in lower_msg or any(w.lower() in lower_msg for w in name.split()[:2] if len(w) > 3):
                        found_names.append(name)

            found_names = list(dict.fromkeys(found_names))[:2]
            if len(found_names) >= 2:
                p1 = prods[prods["name"] == found_names[0]].iloc[0]
                p2 = prods[prods["name"] == found_names[1]].iloc[0]
                reply_text = (
                    f"**Comparison: {p1['name']} vs {p2['name']}**\n\n"
                    f"• **{p1['name']}** ({p1['price']}, {p1['rating']}★): {p1.get('specs', '')}. *Best for:* {p1.get('best_for', '')}\n\n"
                    f"• **{p2['name']}** ({p2['price']}, {p2['rating']}★): {p2.get('specs', '')}. *Best for:* {p2.get('best_for', '')}\n\n"
                    f"Would you like to see a deeper comparison or check price drop history for either?"
                )
                recs = [p1.to_dict(), p2.to_dict()]
                return self._format_response(session, reply_text, intent, recs)

        # -------------------------------------------------------------
        # Intent 5 & 6: Search Products & Budget Inquiry
        # -------------------------------------------------------------
        # Check if clarification is needed: category + budget provided, but no priority
        if parsed_slots["category"] and parsed_slots["max_budget"] and not parsed_slots["purpose"]:
            session.awaiting_clarification = True
            session.clarification_type = "priority_preference"
            reply_text = (
                f"I found matching **{parsed_slots['category']}** under **₹{parsed_slots['max_budget']:,}**!\n\n"
                f"To help me pick the single best choice for you, what matters most: "
                f"**camera photography**, **gaming speed**, **long battery life**, or **overall balanced performance**?"
            )
            return self._format_response(session, reply_text, "clarification_requested", [])

        # Standard AI Search Execution
        search_res = self.search_engine.search(
            query=clean_msg,
            category=parsed_slots["category"],
            max_budget=parsed_slots["max_budget"],
            brand=parsed_slots["brand"],
            top_k=4
        )

        if search_res["results"]:
            top_prod = search_res["results"][0]
            session.current_product_id = top_prod["id"]
            session.current_product_name = top_prod["name"]

            budget_text = f" within ₹{parsed_slots['max_budget']:,}" if parsed_slots["max_budget"] else ""
            reply_text = (
                f"I found {search_res['count']} great choices{budget_text}. "
                f"The top recommendation is the **{top_prod['name']}** ({top_prod['price']}, {top_prod['rating']}★). "
                f"Here are the details and alternatives:"
            )
            return self._format_response(session, reply_text, intent, search_res["results"])
        else:
            reply_text = (
                f"I couldn't find an exact match for '{clean_msg}'. "
                f"Try adjusting your budget or searching by category like Mobiles, Laptops, or Audio."
            )
            return self._format_response(session, reply_text, intent, [])

    def _format_response(self, session: ConversationSession, reply: str, intent: str, recs: List[Dict[str, Any]]) -> Dict[str, Any]:
        session.history.append({"role": "assistant", "text": reply})
        return {
            "session_id": session.session_id,
            "reply": reply,
            "intent": intent,
            "recommended_products": recs,
            "active_context": {
                "category": session.current_category,
                "budget": session.current_budget,
                "priority": session.current_priority,
                "current_product": session.current_product_name
            }
        }

    handle_turn = chat


# Global Singleton
_assistant_instance: Optional[ShoppingAssistant] = None


def get_shopping_assistant() -> ShoppingAssistant:
    global _assistant_instance
    if _assistant_instance is None:
        _assistant_instance = ShoppingAssistant()
    return _assistant_instance


if __name__ == "__main__":
    assistant = ShoppingAssistant()
    print("=" * 60)
    print("TESTING MODULE 4: SHOPPING ASSISTANT DIALOG MANAGER")
    print("=" * 60)

    session_id = "test_user_session_1"

    # Multi-turn conversation simulation
    # Turn 1: User specifies category and budget -> Assistant asks clarification
    print("\n[Turn 1] User: 'I need a phone under 25000'")
    r1 = assistant.chat("I need a phone under 25000", session_id=session_id)
    print("Assistant Reply:\n", r1["reply"])
    print("Context:", r1["active_context"])

    # Turn 2: User answers clarification -> Assistant provides focused recommendations
    print("\n[Turn 2] User: 'Camera and battery'")
    r2 = assistant.chat("Camera and battery", session_id=session_id)
    print("Assistant Reply:\n", r2["reply"])
    print("Recommended Products Count:", len(r2["recommended_products"]))
    for i, it in enumerate(r2["recommended_products"], 1):
        print(f"  {i}. {it['name']} ({it['price']}) - {it['match_reason']}")

    # Turn 3: User asks for spec explanation
    print("\n[Turn 3] User: 'What does OIS mean?'")
    r3 = assistant.chat("What does OIS mean?", session_id=session_id)
    print("Assistant Reply:\n", r3["reply"])

    # Turn 4: User asks for comparison
    print("\n[Turn 4] User: 'Compare Nothing Phone 2a with OnePlus 12R'")
    r4 = assistant.chat("Compare Nothing Phone 2a with OnePlus 12R", session_id=session_id)
    print("Assistant Reply:\n", r4["reply"])
