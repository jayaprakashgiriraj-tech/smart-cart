# Smart Cart AI - Custom Machine Learning System

A complete, 100% local, CPU-optimized Machine Learning system built specifically for the **Smart Cart AI** platform without any reliance on third-party AI APIs (OpenAI, Gemini, ChatGPT, Claude, etc.).

---

## 1. System Overview & Hardware Constraints

- **Execution Environment:** 100% Local on Desktop PC (CPU-based, zero GPU required).
- **RAM Footprint:** Strictly optimized for **4 GB RAM** hardware. Active service footprint is **<80 MB RAM** with all three models loaded simultaneously in memory.
- **Technology Stack:**
  - Python 3.11.9 (Standalone Workspace Runtime)
  - Scikit-learn 1.9.1
  - Pandas 2.2.2
  - NumPy 2.4.6
  - Joblib 1.6.0
  - FastAPI 0.163.2 + Uvicorn
  - Firebase Admin SDK 7.8.0

---

## 2. The Three Machine Learning Models

### Model 1: Product Recommendation Engine
- **Algorithm:** K-Nearest Neighbors (`NearestNeighbors(metric='cosine', algorithm='brute')`) + Collaborative Interaction Hybrid Layer.
- **Features Used:**
  - One-hot encoded product category and subcategory (60% weight).
  - MinMax scaled logarithmic price brackets (20% weight).
  - MinMax scaled user star ratings & logarithmic review counts (20% weight).
- **Hybrid Interaction Layer:**
  - Ingests user behavior events: `view (1.0)`, `compare (2.5)`, `wishlist (4.0)`, `buy_click (5.0)`.
  - Recency weighting with continuous exponential time-decay ($e^{-0.03 \times \text{days}}$).
  - Blending formula: $\text{Final Score} = \alpha \times \text{Cosine Similarity} + (1 - \alpha) \times \text{User Affinity}$.
  - Graceful cold-start fallback to high-rated category items.
- **Evaluation Metrics:**
  - **Top-5 Category Cohesion:** 100.00%
  - **Average Cosine Similarity:** 0.8527
  - **Catalog Coverage:** 100.0% (146 products indexable)
- **Model Artifact:** `ml_service/models/recommender_model.joblib` (4.4 kB) & `recommender_metadata.joblib` (20.7 kB).

---

### Model 2: Price Prediction Engine
- **Algorithm:** Random Forest Regressor (`n_estimators=60, max_depth=10, min_samples_split=4`).
- **Features Used:**
  - `lag_price_1d`, `lag_price_7d`
  - `rolling_mean_7d`, `rolling_mean_30d`
  - `lowest_price_30d`, `highest_price_30d`
  - `day_of_week`, `day_of_month`, `month`, `is_weekend`, `discount_rate`
- **Chronological Training & Validation:**
  - Strict out-of-time chronological split (earlier 80% train, latest 20% validation). Zero lookahead bias or future data leakage.
- **Forecasting Capabilities:**
  - Autoregressive recursive multi-day price forecasting (1 to 30 days ahead).
  - Predicts price trends (`dropping`, `rising`, `stable`) and pinpoints the optimal predicted purchase day.
- **Evaluation Metrics:**
  - **Mean Absolute Error (MAE):** ₹194.12
  - **Root Mean Squared Error (RMSE):** ₹433.94
  - **Mean Absolute Percentage Error (MAPE):** 0.70%
  - **Out-of-Time Validation $R^2$ Score:** 0.9997
- **Mandatory Statistical Disclaimer:**
  - *"DISCLAIMER: Predicted prices are algorithmic statistical estimates derived from historical pricing trends, moving averages, and seasonal patterns. Actual marketplace prices on Amazon and Flipkart fluctuate continuously based on merchant inventory, real-time demand, and flash sales. These predictions are provided for analytical comparison only and do not represent a guaranteed or committed purchase price."*
- **Model Artifact:** `ml_service/models/price_predictor_model.joblib` (1.58 MB).

---

### Model 3: Product Classification Engine
- **Algorithm:** TF-IDF Feature Extraction + Multinomial Logistic Regression (`solver='lbfgs', max_iter=1000`).
- **Features Used:** Sublinear TF-IDF n-grams (unigrams & bigrams, `max_features=500`, English stop words removed).
- **Target Classes:** `Mobiles`, `Laptops`, `Audio`, `Fashion`, `Home Appliances`.
- **Evaluation Metrics:**
  - **Held-out Test Accuracy:** 91.67%
  - **5-Fold Cross-Validation Accuracy:** 95.71% (+/- 5.71%)
  - **Weighted F1-Score:** 0.9197
  - **Weighted Precision:** 0.9394
  - **Weighted Recall:** 0.9167
- **Model Artifact:** `ml_service/models/classifier_model.joblib` (21.6 kB).

---

## 3. Data Integrity: Real Production vs Sample Benchmark Data

All datasets explicitly tag every record to prevent mixing synthetic data with production data:
- `is_production_catalog = 1`: Exact product specifications, IDs, and prices matching `ProductCatalog.kt` and MySQL `smart_cart.products`.
- `is_production_catalog = 0`: Supplementary benchmark records used exclusively for density smoothing and cross-validation benchmarking.

Generated Datasets located in `ml_service/datasets/`:
1. `products_dataset.csv` (146 products: 27 production + 119 augmented)
2. `price_history_dataset.csv` (5,187 chronological daily price records)
3. `product_classification_dataset.csv` (176 title & description training samples)
4. `user_interactions_dataset.csv` (206 weighted user action records)

---

## 4. FastAPI Endpoints & API Reference

Start the server using `start_ml_service.bat` or:
```bash
c:\Users\admin\AndroidStudioProjects\SMARTCART\python311\python.exe -m uvicorn main:app --app-dir ml_service --host 0.0.0.0 --port 8000
```

### Endpoints:
1. `GET /health`
   - Returns service status, loaded model indicators, active memory usage, and Firestore connection state.
2. `POST /recommend`
   - Input: `{"product_id": "mob_1", "user_id": 10, "top_k": 5, "alpha": 0.65}`
   - Output: Content & hybrid ranked recommendations with transparent match scores and explanations.
3. `POST /predict-price`
   - Input: `{"product_id": "mob_1", "days_ahead": 7}`
   - Output: Daily forecast array, price trend, best day to buy, and non-guaranteed statistical disclaimer.
4. `POST /classify`
   - Input: `{"title": "Sony WH-1000XM4 Noise Cancelling", "description": "30-hour battery life LDAC audio"}`
   - Output: Predicted category, confidence score, and probability distribution across all categories.
5. `POST /interactions/record`
   - Input: `{"user_id": 10, "product_id": "mob_1", "product_name": "Nothing Phone (2a) 5G", "category": "Mobiles", "interaction_type": "wishlist"}`
   - Output: Status of local CSV append and Firestore document creation.
6. `GET /model-metrics`
   - Returns validation metrics ($R^2$, MAE, Accuracy, F1, Cosine Similarity) for all models.

---

## 5. Firebase Firestore Integration & Fallback Mode

The service provides dual-mode persistence (`firestore_client.py`):
1. **Firestore Mode:** Place your Firebase service account private key at `ml_service/credentials/firebase_credentials.json` (or set `FIREBASE_CREDENTIALS` env variable). The service will automatically initialize the Google Cloud Firestore client and sync interaction events.
2. **Local Fallback Mode:** If no Firebase credential file is present, the service automatically and silently falls back to local dataset storage and MySQL data with zero downtime or errors.

---

## 6. Android Frontend Integration

The Android app communicates with the ML engine via `MlNetworkClient.kt` (`com.simats.smartcart.network`):
- Connects automatically across `http://10.0.2.2:8000` (Android Studio Emulator), `http://127.0.0.1:8000` (ADB reverse), or LAN IP.
- Features resilient timeout and offline fallback: if the ML server is temporarily inactive, the client seamlessly provides catalog-backed recommendations so UI screens never crash or freeze.
