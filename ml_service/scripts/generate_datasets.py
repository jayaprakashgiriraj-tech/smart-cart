"""
Smart Cart AI - Dataset Generation & Preprocessing Pipeline
Generates clean CSV datasets for:
1. Product Recommendation (KNN Content-Based + Hybrid)
2. Price Prediction (Random Forest Regressor Time-Series)
3. Product Classification (TF-IDF + Logistic Regression)
4. User Interactions (Collaborative / Hybrid Filtering)

Clearly tags 'is_production_catalog' and 'data_source' to distinguish
real application catalog items from augmented benchmark samples.
"""

import os
import csv
import json
import random
from datetime import datetime, timedelta

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATASETS_DIR = os.path.join(BASE_DIR, "datasets")
os.makedirs(DATASETS_DIR, exist_ok=True)

# -----------------------------------------------------------------------------
# 1. CORE PRODUCTION CATALOG (Matching ProductCatalog.kt & MySQL Database)
# -----------------------------------------------------------------------------
PRODUCTION_PRODUCTS = [
    # Mobiles
    {"id": "mob_1", "name": "Nothing Phone (2a) 5G", "brand": "Nothing", "category": "Mobiles", "subcategory": "Mid-Range", "price": "₹23,999", "numeric_price": 23999, "rating": 4.4, "review_count": 2840, "specs": "8GB RAM · 128GB Storage · Dimensity 7200 Pro · 50MP OIS Camera", "description": "Iconic transparent design with Glyph Interface, vivid 120Hz AMOLED display and powerful dual 50MP cameras.", "best_for": "Design lovers and clean software enthusiasts"},
    {"id": "mob_2", "name": "Samsung Galaxy S23 5G", "brand": "Samsung", "category": "Mobiles", "subcategory": "Flagship", "price": "₹64,999", "numeric_price": 64999, "rating": 4.6, "review_count": 8920, "specs": "8GB RAM · 256GB Storage · Snapdragon 8 Gen 2 · Dynamic AMOLED 2X", "description": "Compact flagship powerhouse with Nightography cameras, Snapdragon 8 Gen 2 for Galaxy and durable Armor Aluminum frame.", "best_for": "Compact flagship performance & photography"},
    {"id": "mob_3", "name": "Apple iPhone 15", "brand": "Apple", "category": "Mobiles", "subcategory": "Flagship", "price": "₹71,999", "numeric_price": 71999, "rating": 4.7, "review_count": 14200, "specs": "128GB Storage · A16 Bionic · Dynamic Island · 48MP Main Camera", "description": "Dynamic Island, 48MP camera with 2x Telephoto, color-infused glass and USB-C connectivity.", "best_for": "iOS ecosystem and cinematic video recording"},
    {"id": "mob_4", "name": "OnePlus 12R 5G", "brand": "OnePlus", "category": "Mobiles", "subcategory": "Performance", "price": "₹39,999", "numeric_price": 39999, "rating": 4.5, "review_count": 5120, "specs": "16GB RAM · 256GB Storage · Snapdragon 8 Gen 2 · 100W SUPERVOOC", "description": "High-refresh 4th Gen LTPO AMOLED display, massive 5500mAh battery and blazing 100W charging.", "best_for": "Intensive mobile gaming and battery life"},
    {"id": "mob_5", "name": "Redmi Note 13 Pro+ 5G", "brand": "Xiaomi", "category": "Mobiles", "subcategory": "Mid-Range", "price": "₹29,999", "numeric_price": 29999, "rating": 4.3, "review_count": 4180, "specs": "12GB RAM · 256GB Storage · 200MP OIS Camera · 120W HyperCharge", "description": "Curved AMOLED display, IP68 water resistance and flagship 200MP camera sensor.", "best_for": "High megapixel photography and ultra fast charging"},
    {"id": "mob_6", "name": "Google Pixel 8a", "brand": "Google", "category": "Mobiles", "subcategory": "Camera", "price": "₹49,999", "numeric_price": 49999, "rating": 4.5, "review_count": 3200, "specs": "8GB RAM · 128GB Storage · Google Tensor G3 · Best Take Camera", "description": "Google AI built-in, class-leading computational photography and 7 years of OS updates.", "best_for": "AI photo editing and clean stock Android"},

    # Laptops
    {"id": "lap_1", "name": "Apple MacBook Air M2", "brand": "Apple", "category": "Laptops", "subcategory": "Thin & Light", "price": "₹94,900", "numeric_price": 94900, "rating": 4.8, "review_count": 6430, "specs": "8GB Unified Memory · 256GB SSD · M2 8-core CPU · 13.6-inch Liquid Retina", "description": "Incredibly thin and silent fanless design with 18-hour battery life and stunning Liquid Retina display.", "best_for": "Students, coding on the go and battery life"},
    {"id": "lap_2", "name": "ASUS ROG Zephyrus G14", "brand": "ASUS", "category": "Laptops", "subcategory": "Gaming", "price": "₹1,44,990", "numeric_price": 144990, "rating": 4.7, "review_count": 1820, "specs": "16GB DDR5 · 1TB NVMe SSD · Ryzen 9 8945HS · RTX 4060 8GB Graphics", "description": "Ultraportable 14-inch OLED gaming laptop with aluminum unibody chassis and vapor chamber cooling.", "best_for": "High-end AAA gaming and creative rendering"},
    {"id": "lap_3", "name": "Lenovo IdeaPad Slim 3", "brand": "Lenovo", "category": "Laptops", "subcategory": "Student", "price": "₹42,990", "numeric_price": 42990, "rating": 4.3, "review_count": 5210, "specs": "16GB RAM · 512GB SSD · Intel Core i5 12th Gen · 15.6-inch FHD Anti-Glare", "description": "Reliable daily computing laptop with military-grade durability, rapid charge and physical webcam privacy shutter.", "best_for": "College assignments and office productivity"},
    {"id": "lap_4", "name": "Dell Inspiron 15 3520", "brand": "Dell", "category": "Laptops", "subcategory": "Office", "price": "₹48,990", "numeric_price": 48990, "rating": 4.2, "review_count": 3950, "specs": "16GB RAM · 512GB SSD · Intel Core i5 1235U · 120Hz 15.6-inch FHD", "description": "Smooth 120Hz display, ergonomic lift hinge keyboard and spacious keycaps for long typing sessions.", "best_for": "Work-from-home spreadsheets and multitasking"},
    {"id": "lap_5", "name": "HP Pavilion Plus 14", "brand": "HP", "category": "Laptops", "subcategory": "Creator", "price": "₹78,990", "numeric_price": 78990, "rating": 4.5, "review_count": 2100, "specs": "16GB LPDDR5x · 1TB SSD · Intel Core Ultra 5 · 2.8K 120Hz OLED", "description": "Intel AI Boost NPU, IMAX Enhanced 2.8K OLED screen and 5MP IR webcam with temporal noise reduction.", "best_for": "Photo/video editing and AI acceleration"},

    # Audio
    {"id": "aud_1", "name": "JBL Tune 510BT", "brand": "JBL", "category": "Audio", "subcategory": "Headphones", "price": "₹2,372", "numeric_price": 2372, "rating": 4.1, "review_count": 30450, "specs": "40H Playtime · Pure Bass Sound · 160g Lightweight · Multi-point Connection", "description": "Renowned JBL Pure Bass sound, dual-device Bluetooth switching and quick 5-min charge for 2 hours playback.", "best_for": "Online classes, lectures and clear calls"},
    {"id": "aud_2", "name": "Soundcore by Anker H30i", "brand": "Soundcore", "category": "Audio", "subcategory": "Headphones", "price": "₹2,499", "numeric_price": 2499, "rating": 4.2, "review_count": 4210, "specs": "70H Battery Life · 40mm Dynamic Drivers · Foldable · Bluetooth 5.3", "description": "Ultra-long 70-hour battery life with BassUp technology and customizable sound profile via Soundcore app.", "best_for": "Long study marathons without charging"},
    {"id": "aud_3", "name": "Sony WH-1000XM4", "brand": "Sony", "category": "Audio", "subcategory": "Headphones", "price": "₹22,990", "numeric_price": 22990, "rating": 4.7, "review_count": 21800, "specs": "Active Noise Cancelling · 30H Battery · LDAC Hi-Res · Speak-to-Chat", "description": "Industry leading ANC with QN1 processor, multipoint pairing and atmospheric pressure sensor for flights.", "best_for": "Travel, deep work and audiophile listening"},
    {"id": "aud_4", "name": "Sony WH-CH520", "brand": "Sony", "category": "Audio", "subcategory": "Headphones", "price": "₹4,490", "numeric_price": 4490, "rating": 4.4, "review_count": 9120, "specs": "50H Battery · DSEE Sound Restoration · 147g Featherweight · Fast Pair", "description": "DSEE engine restores high-frequency details lost in compression; lightweight on-ear design for all-day comfort.", "best_for": "Everyday commuting and clear voice pickup"},
    {"id": "aud_5", "name": "boAt Rockerz 450", "brand": "boAt", "category": "Audio", "subcategory": "Headphones", "price": "₹1,499", "numeric_price": 1499, "rating": 4.1, "review_count": 45300, "specs": "15H Playtime · 40mm Drivers · Padded Ear Cushions · AUX Compatibility", "description": "Punchy bass delivery, matte finish exterior and budget-friendly wireless audio experience.", "best_for": "Budget-conscious casual music listening"},
    {"id": "aud_6", "name": "JBL Flip 6", "brand": "JBL", "category": "Audio", "subcategory": "Speakers", "price": "₹9,999", "numeric_price": 9999, "rating": 4.6, "review_count": 18200, "specs": "IP67 Waterproof & Dustproof · 12H Playtime · 2-way Speaker System · PartyBoost", "description": "Engineered for powerful outdoor sound with racetrack woofer, separate tweeter and dual passive radiators.", "best_for": "Outdoor gatherings, pool parties and rugged travel"},
    {"id": "aud_7", "name": "boAt Stone 650", "brand": "boAt", "category": "Audio", "subcategory": "Speakers", "price": "₹1,999", "numeric_price": 1999, "rating": 4.2, "review_count": 12800, "specs": "10W Stereo Sound · IPX5 Splashproof · 7H Battery · Diamond Mesh Grill", "description": "Rugged build with rhythmic deep bass response, dual dynamic drivers and multiple connectivity modes.", "best_for": "Indoor room listening and desktop speaker setup"},

    # Fashion & Accessories
    {"id": "fsh_1", "name": "Titan Neo Chronograph Watch", "brand": "Titan", "category": "Fashion", "subcategory": "Watches", "price": "₹5,495", "numeric_price": 5495, "rating": 4.4, "review_count": 3120, "specs": "Stainless Steel Case · Quartz Movement · 50M Water Resistance · Leather Strap", "description": "Classic contemporary chronograph watch featuring mineral glass dial and genuine brown leather band.", "best_for": "Business casual meetings and formal events"},
    {"id": "fsh_2", "name": "Fossil Gen 6 Smartwatch", "brand": "Fossil", "category": "Fashion", "subcategory": "Watches", "price": "₹14,995", "numeric_price": 14995, "rating": 4.1, "review_count": 1940, "specs": "Wear OS · Snapdragon Wear 4100+ · SpO2 & Heart Rate · Fast Charging", "description": "Combines classic timepiece aesthetic with smart notifications, continuous heart-rate tracking and Google Assistant.", "best_for": "Tech-savvy professionals who love analog watch styling"},
    {"id": "fsh_3", "name": "Allen Solly Slim Fit Formal Shirt", "brand": "Allen Solly", "category": "Fashion", "subcategory": "Apparel", "price": "₹1,699", "numeric_price": 1699, "rating": 4.3, "review_count": 6800, "specs": "100% Breathable Cotton · Regular Collar · Machine Washable · Wrinkle Resistant", "description": "Crisp solid cotton weave engineered for full workday comfort and neat collar structure.", "best_for": "Interviews, office presentations and formal wear"},
    {"id": "fsh_4", "name": "Levi's Trucker Denim Jacket", "brand": "Levi's", "category": "Fashion", "subcategory": "Apparel", "price": "₹3,999", "numeric_price": 3999, "rating": 4.6, "review_count": 4210, "specs": "Non-Stretch Denim · Point Collar · Button Flap Pockets · Regular Fit", "description": "Timeless iconic design since 1967, crafted from robust cotton denim with adjustable side hem tabs.", "best_for": "Casual layering and street style"},

    # Home Appliances
    {"id": "app_1", "name": "LG 7 Kg 5 Star Front Load Washer", "brand": "LG", "category": "Home Appliances", "subcategory": "Washing Machines", "price": "₹28,990", "numeric_price": 28990, "rating": 4.5, "review_count": 7800, "specs": "AI Direct Drive · Steam Wash · 1200 RPM · 6 Motion DD Technology", "description": "Inverter Direct Drive motor calculates optimal wash motions based on fabric weight and softness.", "best_for": "Energy-saving hygienic family laundry"},
    {"id": "app_2", "name": "Samsung 253L 3 Star Inverter Refrigerator", "brand": "Samsung", "category": "Home Appliances", "subcategory": "Refrigerators", "price": "₹24,490", "numeric_price": 24490, "rating": 4.4, "review_count": 9210, "specs": "Digital Inverter · Frost Free · All-Round Cooling · Toughened Glass Shelves", "description": "Smart digital inverter compressor automatically regulates speed across 7 cooling demand levels.", "best_for": "Medium households wanting consistent food freshness"},
    {"id": "app_3", "name": "Philips Digital Air Fryer HD9200", "brand": "Philips", "category": "Home Appliances", "subcategory": "Kitchen Appliances", "price": "₹6,499", "numeric_price": 6499, "rating": 4.6, "review_count": 15400, "specs": "Rapid Air Technology · 4.1L Capacity · 1400W Power · Dishwasher Safe Basket", "description": "Cooks crispy fried favorites with up to 90% less oil using starfish-design bottom airflow.", "best_for": "Healthy oil-free cooking and quick snacks"},
    {"id": "app_4", "name": "Dyson V8 Cordless Vacuum Cleaner", "brand": "Dyson", "category": "Home Appliances", "subcategory": "Cleaning", "price": "₹29,900", "numeric_price": 29900, "rating": 4.5, "review_count": 3400, "specs": "V8 Digital Motor · 40 Min Run Time · De-tangling Motorbar · Whole-machine Filtration", "description": "Powerful fade-free cordless suction that converts instantly to handheld for car and sofa cleaning.", "best_for": "Deep home dust cleaning and pet hair removal"},
    {"id": "app_5", "name": "Sony Bravia 55-inch 4K Google TV", "brand": "Sony", "category": "Home Appliances", "subcategory": "Televisions", "price": "₹57,990", "numeric_price": 57990, "rating": 4.7, "review_count": 6800, "specs": "4K Ultra HD · X1 4K Processor · Dolby Audio · Google TV Interface", "description": "Motionflow XR technology keeps fast-paced sports sharp, with Triluminos Pro lifelike color accuracy.", "best_for": "Living room cinema experience and sports"}
]

# -----------------------------------------------------------------------------
# 2. GENERATE PRODUCTS DATASET (Content Recommender & Baseline Catalog)
# -----------------------------------------------------------------------------
def generate_products_dataset():
    filepath = os.path.join(DATASETS_DIR, "products_dataset.csv")
    products = []

    # 1. Add all real production products
    for p in PRODUCTION_PRODUCTS:
        row = dict(p)
        row["is_production_catalog"] = 1
        products.append(row)

    # 2. Add realistic augmented catalog samples to create continuous feature density
    augmented_brands = {
        "Mobiles": ["Realme", "Motorola", "iQOO", "Vivo", "Poco", "Infinix"],
        "Laptops": ["Acer", "MSI", "Microsoft Surface", "Honor", "Xiaomi"],
        "Audio": ["Sennheiser", "Audio-Technica", "Noise", "Fire-Boltt", "Skullcandy", "Crossbeats"],
        "Fashion": ["Fastrack", "Casio", "Raymond", "Peter England", "Puma", "Nike"],
        "Home Appliances": ["Whirlpool", "Haier", "Bosch", "Panasonic", "Voltas", "Bajaj"]
    }

    subcat_map = {
        "Mobiles": ["Budget 5G", "Gaming", "Camera", "Flagship Killer", "Foldable"],
        "Laptops": ["Ultrabook", "Gaming", "Business", "2-in-1 Touch", "Budget Student"],
        "Audio": ["TWS Earbuds", "Neckband", "Over-Ear ANC", "Party Speaker", "Soundbar"],
        "Fashion": ["Analog Watch", "Digital Sports Watch", "Chronograph", "Formal Wear", "Casual Wear"],
        "Home Appliances": ["Microwave Oven", "Water Purifier", "Air Conditioner", "Mixer Grinder", "Dishwasher"]
    }

    random.seed(42) # Reproducible benchmark dataset
    for i in range(1, 120):
        cat = random.choice(list(augmented_brands.keys()))
        brand = random.choice(augmented_brands[cat])
        subcat = random.choice(subcat_map[cat])

        if cat == "Mobiles":
            num_price = random.randint(9999, 89999)
            specs = f"{random.choice([6, 8, 12, 16])}GB RAM · {random.choice([128, 256, 512])}GB Storage · 5G Dual SIM · 5000mAh Battery"
            best_for = f"Everyday {subcat.lower()} performance"
        elif cat == "Laptops":
            num_price = random.randint(29990, 169990)
            specs = f"{random.choice([8, 16, 32])}GB RAM · {random.choice([512, 1024])}GB SSD · {random.choice(['Intel Core i5', 'Intel Core i7', 'AMD Ryzen 7', 'AMD Ryzen 5'])}"
            best_for = f"Demanding {subcat.lower()} workflows"
        elif cat == "Audio":
            num_price = random.randint(999, 29990)
            specs = f"{random.choice([20, 35, 50, 70])}H Playtime · {random.choice(['Bluetooth 5.3', 'Active Noise Cancelling', 'Bass Boost Drivers'])}"
            best_for = f"Great sound for {subcat.lower()}"
        elif cat == "Fashion":
            num_price = random.randint(799, 18999)
            specs = f"Premium {random.choice(['Quartz', 'Automatic', 'Cotton Fabric', 'Leather'])} · Durable Build"
            best_for = f"Stylish {subcat.lower()} choice"
        else:
            num_price = random.randint(2499, 64990)
            specs = f"{random.choice(['5 Star Rated', 'Inverter Motor', 'Smart App Control'])} · 2 Year Warranty"
            best_for = f"Modern household {subcat.lower()}"

        rating = round(random.uniform(3.8, 4.9), 1)
        review_count = random.randint(150, 15000)
        formatted_price = f"₹{num_price:,}"
        name = f"{brand} {subcat} {random.choice(['Pro', 'Max', 'Plus', 'Classic', 'Lite', 'Neo', 'Series X', 'Elite'])} {random.randint(10, 99)}"

        products.append({
            "id": f"aug_{cat[:3].lower()}_{i}",
            "name": name,
            "brand": brand,
            "category": cat,
            "subcategory": subcat,
            "price": formatted_price,
            "numeric_price": num_price,
            "rating": rating,
            "review_count": review_count,
            "specs": specs,
            "description": f"Engineered by {brand} featuring modern {specs.lower()} with optimized durability and customer satisfaction.",
            "best_for": best_for,
            "is_production_catalog": 0
        })

    fieldnames = ["product_id", "name", "brand", "category", "subcategory", "price", "numeric_price", "rating", "review_count", "specs", "description", "best_for", "is_production_catalog"]
    with open(filepath, mode="w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow(fieldnames)
        for p in products:
            writer.writerow([
                p["id"], p["name"], p["brand"], p["category"], p["subcategory"],
                p["price"], p["numeric_price"], p["rating"], p["review_count"],
                p["specs"], p["description"], p["best_for"], p["is_production_catalog"]
            ])

    print(f"[Dataset Generator] Created {len(products)} products in: {filepath} ({len(PRODUCTION_PRODUCTS)} production + {len(products)-len(PRODUCTION_PRODUCTS)} augmented)")
    return products

# -----------------------------------------------------------------------------
# 3. GENERATE TIME-SERIES PRICE HISTORY DATASET (Price Predictor)
# -----------------------------------------------------------------------------
def generate_price_history_dataset(products):
    filepath = os.path.join(DATASETS_DIR, "price_history_dataset.csv")
    history_rows = []

    # Target key tracked products
    tracked_products = [p for p in products if p.get("is_production_catalog", 0) == 1]
    # Sample additional products for cross-category price forecasting
    tracked_products.extend([p for p in products if p.get("is_production_catalog", 0) == 0][:30])

    random.seed(101)
    end_date = datetime(2026, 11, 18) # Current timeline
    days_back = 90

    for prod in tracked_products:
        base_price = prod["numeric_price"]
        prod_id = prod.get("id") or prod.get("product_id")
        prod_name = prod["name"]
        cat = prod["category"]
        is_prod = prod.get("is_production_catalog", 0)

        # Generate realistic temporal price walk
        current_price = float(base_price)
        price_series = []

        for day_offset in range(days_back, -1, -1):
            dt = end_date - timedelta(days=day_offset)
            day_of_week = dt.weekday() # 0 = Monday, 6 = Sunday
            is_weekend = 1 if day_of_week in [5, 6] else 0

            # Seasonal/promotional weekend dips and supply fluctuations
            fluctuation = random.uniform(-0.02, 0.02)
            if is_weekend:
                fluctuation -= random.uniform(0.005, 0.025) # Weekend promotional deals

            current_price = round(current_price * (1.0 + fluctuation), 2)
            # Bound within sensible bracket (+/- 15% of MSRP)
            min_bound = base_price * 0.82
            max_bound = base_price * 1.15
            current_price = max(min_bound, min(max_bound, current_price))

            price_series.append((dt, round(current_price, 2), day_of_week, dt.day, dt.month, dt.year, is_weekend))

        # Calculate lag features and rolling statistics chronologically
        for i, (dt, price, dow, dom, month, year, is_wknd) in enumerate(price_series):
            lag_1d = price_series[i - 1][1] if i >= 1 else price
            lag_7d = price_series[i - 7][1] if i >= 7 else lag_1d

            window_7 = [p[1] for p in price_series[max(0, i - 6): i + 1]]
            rolling_mean_7 = round(sum(window_7) / len(window_7), 2)

            window_30 = [p[1] for p in price_series[max(0, i - 29): i + 1]]
            rolling_mean_30 = round(sum(window_30) / len(window_30), 2)
            lowest_30d = round(min(window_30), 2)
            highest_30d = round(max(window_30), 2)

            # Store platform variations
            amazon_price = round(price * random.uniform(0.98, 1.01), 2)
            flipkart_price = round(price * random.uniform(0.98, 1.01), 2)
            discount_rate = round(max(0.0, (base_price - price) / base_price * 100), 2)

            data_source = "production_tracking" if is_prod == 1 else "historical_sample"

            history_rows.append({
                "product_id": prod_id,
                "product_name": prod_name,
                "category": cat,
                "date": dt.strftime("%Y-%m-%d"),
                "day_of_week": dow,
                "day_of_month": dom,
                "month": month,
                "year": year,
                "is_weekend": is_wknd,
                "price": price,
                "lag_price_1d": lag_1d,
                "lag_price_7d": lag_7d,
                "rolling_mean_7d": rolling_mean_7,
                "rolling_mean_30d": rolling_mean_30,
                "lowest_price_30d": lowest_30d,
                "highest_price_30d": highest_30d,
                "amazon_price": amazon_price,
                "flipkart_price": flipkart_price,
                "discount_rate": discount_rate,
                "data_source": data_source
            })

    # Chronologically sort rows
    history_rows.sort(key=lambda x: (x["date"], x["product_id"]))

    fieldnames = [
        "product_id", "product_name", "category", "date", "day_of_week", "day_of_month",
        "month", "year", "is_weekend", "price", "lag_price_1d", "lag_price_7d",
        "rolling_mean_7d", "rolling_mean_30d", "lowest_price_30d", "highest_price_30d",
        "amazon_price", "flipkart_price", "discount_rate", "data_source"
    ]

    with open(filepath, mode="w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(history_rows)

    print(f"[Dataset Generator] Created {len(history_rows)} time-series price records in: {filepath}")
    return history_rows

# -----------------------------------------------------------------------------
# 4. GENERATE PRODUCT CLASSIFICATION DATASET (TF-IDF + Logistic Regression)
# -----------------------------------------------------------------------------
def generate_classification_dataset(products):
    filepath = os.path.join(DATASETS_DIR, "product_classification_dataset.csv")
    rows = []

    # Map existing products
    for p in products:
        title = p["name"]
        desc = f"{p['brand']} {p['specs']} {p['description']}"
        cat = p["category"]
        rows.append({
            "product_id": p.get("id") or p.get("product_id"),
            "title": title,
            "description": desc,
            "combined_text": f"{title} {desc}".strip(),
            "category": cat,
            "is_production_catalog": p.get("is_production_catalog", 0)
        })

    # Supplementary realistic marketplace e-commerce listing texts
    supplementary_texts = [
        ("Apple iPhone 14 Pro Max 256GB Space Black", "A16 Bionic Super Retina XDR Always-On display 48MP Pro camera system iOS smartphone", "Mobiles"),
        ("Samsung Galaxy Z Flip5 Mint 512GB", "Foldable smartphone Snapdragon 8 Gen 2 Flex Window water resistant dual camera 5G", "Mobiles"),
        ("Google Pixel 7 Pro Hazel 128GB", "Google Tensor G2 Telephoto macro focus Android smartphone 120Hz OLED smooth display", "Mobiles"),
        ("OnePlus Nord CE 4 Lite 5G 8GB 128GB", "OxygenOS 5500mAh battery 80W charging dual stereo speakers Sony LYT-600 mobile phone", "Mobiles"),
        ("Redmi 13C 5G Starlight Green", "Dimensity 6100+ 50MP AI camera 90Hz display 5000mAh battery smartphone", "Mobiles"),
        ("Motorola Edge 50 Fusion 5G Marshmallow Blue", "Snapdragon 7s Gen 2 curved pOLED display 68W TurboPower water resistant IP68 phone", "Mobiles"),

        ("Apple MacBook Pro 14 M3 Pro Chip", "18GB Unified Memory 512GB SSD Liquid Retina XDR screen 14-inch laptop macOS Sonoma", "Laptops"),
        ("Lenovo ThinkPad X1 Carbon Gen 11", "Intel Core i7 13th Gen 16GB RAM 1TB SSD ultralight business laptop carbon fiber", "Laptops"),
        ("ASUS TUF Gaming A15 Mecha Gray", "AMD Ryzen 7 7735HS RTX 4050 6GB 144Hz FHD gaming laptop RGB backlit keyboard", "Laptops"),
        ("Acer Swift Go 14 OLED EVO Certified", "Intel Core Ultra 7 155H 16GB LPDDR5X 1TB SSD 2.8K 90Hz OLED thin lightweight notebook", "Laptops"),
        ("HP Envy x360 2-in-1 Touchscreen Laptop", "AMD Ryzen 5 7530U 16GB RAM 512GB SSD convertible tablet laptop with stylus pen", "Laptops"),
        ("Dell XPS 13 9315 CNC Machined Aluminum", "Intel Core i5 12th Gen 8GB RAM 512GB PCIe SSD FHD+ InfinityEdge portable laptop", "Laptops"),

        ("Sony WF-1000XM5 True Wireless Earbuds", "Noise Cancelling bluetooth earbuds LDAC dual processor Hi-Res audio wireless charging case", "Audio"),
        ("Bose QuietComfort 45 Over-Ear Headphones", "Acoustic noise cancelling TriPort acoustic architecture comfortable plush ear cushions", "Audio"),
        ("JBL Cinema SB271 220W Dolby Soundbar", "Wireless subwoofer HDMI ARC optical bluetooth connection deep bass home theater sound", "Audio"),
        ("Sennheiser Momentum 4 Wireless Headphones", "60-hour battery life audiophile acoustic system active noise cancellation smart pause", "Audio"),
        ("boAt Airdopes 141 Bluetooth TWS Earbuds", "42H playtime ENx tech clear voice calls IPX4 water resistance low latency beast mode", "Audio"),
        ("Marshall Acton III Bluetooth Home Speaker", "Iconic vintage brass design room-filling stereo sound analog volume bass treble knobs", "Audio"),

        ("Casio Vintage Digital Gold Dial Watch", "Stainless steel mesh band daily alarm stopwatch water resistant unisex wrist watch", "Fashion"),
        ("Fossil Grant Chronograph Blue Leather Watch", "Roman numeral indices stopwatch subdials genuine navy blue leather band formal", "Fashion"),
        ("Levi's Men 511 Slim Fit Jeans", "Stretch denim cotton elastane zip fly 5-pocket styling durable casual trousers", "Fashion"),
        ("Peter England Men Classic Formal Trouser", "Poly viscose blend wrinkle free crease retention office formal pants", "Fashion"),
        ("Titan Raga Viva Rose Gold Analog Watch", "Mother of pearl dial metallic jewelry strap elegant women designer watch", "Fashion"),
        ("Zara Floral Print Poplin Midi Dress", "V-neck short puff sleeves flared hemline summer breathable casual party dress", "Fashion"),

        ("LG 8kg 5 Star Inverter Front Load Washing Machine", "TurboWash AI direct drive hygienic steam wash 1400 RPM smart diagnosis home washer", "Home Appliances"),
        ("Whirlpool 265L 3 Star Frost Free Double Door Refrigerator", "Intellisense inverter technology 6th sense deep freeze microblock vegetable crisper", "Home Appliances"),
        ("IFB 30L Convection Microwave Oven", "Rotisserie motorized grill auto cook menus defrost timer stainless steel cavity", "Home Appliances"),
        ("Eureka Forbes Aquaguard Aura Water Purifier", "Active copper zinc mineral guard UV e-boiling 8 stage water filtration technology", "Home Appliances"),
        ("Voltas 1.5 Ton 5 Star Split Inverter AC", "Adjustable cooling turbo mode copper condenser anti-microbial filter air conditioner", "Home Appliances"),
        ("Havells Maxx 750W Mixer Grinder 3 Jars", "Heavy duty copper motor stainless steel flow breaker jars safety overload protection", "Home Appliances")
    ]

    for i, (title, desc, cat) in enumerate(supplementary_texts):
        rows.append({
            "product_id": f"clf_supp_{i+1}",
            "title": title,
            "description": desc,
            "combined_text": f"{title} {desc}".strip(),
            "category": cat,
            "is_production_catalog": 0
        })

    fieldnames = ["product_id", "title", "description", "combined_text", "category", "is_production_catalog"]
    with open(filepath, mode="w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(rows)

    print(f"[Dataset Generator] Created {len(rows)} classification training records in: {filepath}")
    return rows

# -----------------------------------------------------------------------------
# 5. GENERATE USER INTERACTIONS DATASET (Hybrid Recommendation & Firestore Seed)
# -----------------------------------------------------------------------------
def generate_user_interactions_dataset(products):
    filepath = os.path.join(DATASETS_DIR, "user_interactions_dataset.csv")
    interactions = []

    # Users in production system (e.g. 10: Aarav Mehta / Jayaprakash G, 11: Demo User)
    users = [10, 11, 1, 4, 7, 9]

    # Interaction weights
    # view = 1.0, compare = 2.5, wishlist = 4.0, buy_click = 5.0
    action_weights = {
        "view": 1.0,
        "compare": 2.5,
        "wishlist": 4.0,
        "buy_click": 5.0
    }

    random.seed(999)
    end_date = datetime(2026, 11, 18)

    for uid in users:
        # Give each user strong preference clusters (e.g. user 10 loves Audio and Mobiles)
        preferred_cat = random.choice(["Audio", "Mobiles", "Laptops"])
        preferred_products = [p for p in products if p.get("category") == preferred_cat]
        other_products = [p for p in products if p.get("category") != preferred_cat]

        num_events = random.randint(25, 60)
        for _ in range(num_events):
            if random.random() < 0.70 and preferred_products:
                chosen_p = random.choice(preferred_products)
            else:
                chosen_p = random.choice(other_products)

            action = random.choices(
                ["view", "compare", "wishlist", "buy_click"],
                weights=[0.50, 0.25, 0.15, 0.10],
                k=1
            )[0]

            dt = end_date - timedelta(days=random.randint(0, 30), minutes=random.randint(5, 1400))
            p_id = chosen_p.get("id") or chosen_p.get("product_id")

            interactions.append({
                "user_id": uid,
                "product_id": p_id,
                "product_name": chosen_p["name"],
                "category": chosen_p["category"],
                "interaction_type": action,
                "weight": action_weights[action],
                "timestamp": dt.strftime("%Y-%m-%d %H:%M:%S")
            })

    interactions.sort(key=lambda x: x["timestamp"], reverse=True)

    fieldnames = ["user_id", "product_id", "product_name", "category", "interaction_type", "weight", "timestamp"]
    with open(filepath, mode="w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(interactions)

    print(f"[Dataset Generator] Created {len(interactions)} user interaction records in: {filepath}")
    return interactions

if __name__ == "__main__":
    prods = generate_products_dataset()
    generate_price_history_dataset(prods)
    generate_classification_dataset(prods)
    generate_user_interactions_dataset(prods)
    print("\n[SUCCESS] All datasets generated cleanly with unambiguous production vs sample flags.")
