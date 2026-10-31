package com.simats.smartcart.data

data class Product(
    val id: String,
    val name: String,
    val brand: String,
    val category: String, // "Mobiles", "Laptops", "Audio", "Fashion", "Home Appliances"
    val subcategory: String,
    val price: String,
    val numericPrice: Int,
    val rating: String = "4.2",
    val reviewCount: Int = 150,
    val stockQuantity: Int = 10,
    val description: String = "",
    val specs: String = "",
    val battery: String = "N/A",
    val callMic: String = "N/A",
    val weight: String = "N/A",
    val multipoint: String = "N/A",
    val activeNC: String = "N/A",
    val appEQ: String = "N/A",
    val warranty: String = "1 year",
    val valueScore: Double = 8.5,
    val matchScore: String = "90% match",
    val matchPercentage: Int = 90,
    val bestFor: String = "General use",
    val recommendationSummary: String = "Great value and features for your budget.",
    val needs: List<String> = emptyList(),
    val imageUrl: String = ""
) {
    val isAvailable: Boolean
        get() = stockQuantity > 0

    val stockStatus: String
        get() = if (isAvailable) "In Stock" else "Unavailable"
}

object ProductCatalog {

    val products = listOf(
        // ==================== MOBILES ====================
        Product(
            id = "mob_s23",
            name = "Samsung Galaxy S23",
            brand = "Samsung",
            category = "Mobiles",
            subcategory = "Smartphones",
            price = "₹49,999",
            numericPrice = 49999,
            rating = "4.6",
            reviewCount = 520,
            stockQuantity = 12,
            description = "Compact Android flagship with Snapdragon 8 Gen 2 processor, Dynamic AMOLED 2X 120Hz display, and 50MP triple rear camera system.",
            specs = "8GB RAM · 256GB Storage · 5G",
            matchScore = "95% match",
            matchPercentage = 95,
            valueScore = 9.2,
            bestFor = "Compact flagship performance & cameras",
            recommendationSummary = "Top Android pick under ₹50k with premium build, dynamic 120Hz display, and pro cameras.",
            needs = listOf("Mobiles", "Camera", "Flagship")
        ),
        Product(
            id = "mob_ip15",
            name = "Apple iPhone 15",
            brand = "Apple",
            category = "Mobiles",
            subcategory = "Smartphones",
            price = "₹71,900",
            numericPrice = 71900,
            rating = "4.7",
            reviewCount = 890,
            stockQuantity = 8,
            description = "Featuring Dynamic Island, 48MP Main camera with 2x Telephoto, USB-C connectivity, and durable color-infused glass design.",
            specs = "A16 Bionic · 128GB Storage · 5G",
            matchScore = "94% match",
            matchPercentage = 94,
            valueScore = 9.0,
            bestFor = "iOS ecosystem & video recording",
            recommendationSummary = "Premier smartphone for video creators, seamless iOS performance, and all-day battery.",
            needs = listOf("Mobiles", "Camera", "iOS")
        ),
        Product(
            id = "mob_op12r",
            name = "OnePlus 12R",
            brand = "OnePlus",
            category = "Mobiles",
            subcategory = "Smartphones",
            price = "₹39,999",
            numericPrice = 39999,
            rating = "4.5",
            reviewCount = 340,
            stockQuantity = 15,
            description = "Powered by Snapdragon 8 Gen 2, 5500mAh massive battery, 100W SUPERVOOC charging, and 120Hz ProXDR display.",
            specs = "16GB RAM · 256GB Storage · 100W Fast Charge",
            matchScore = "91% match",
            matchPercentage = 91,
            valueScore = 9.1,
            bestFor = "100W Fast charging & gaming speed",
            recommendationSummary = "Ideal for power users who want rapid charging and high FPS gaming at mid-tier pricing.",
            needs = listOf("Mobiles", "Fast charging", "Gaming")
        ),
        Product(
            id = "mob_redmi13p",
            name = "Redmi Note 13 Pro",
            brand = "Xiaomi",
            category = "Mobiles",
            subcategory = "Smartphones",
            price = "₹21,999",
            numericPrice = 21999,
            rating = "4.3",
            reviewCount = 610,
            stockQuantity = 20,
            description = "200MP OIS camera, 1.5K 120Hz AMOLED display, Snapdragon 7s Gen 2 chip, and 67W Turbo Charge.",
            specs = "8GB RAM · 128GB Storage · 200MP Camera",
            matchScore = "88% match",
            matchPercentage = 88,
            valueScore = 8.7,
            bestFor = "Budget high resolution camera",
            recommendationSummary = "Best value under ₹25k for detailed photography and sharp 1.5K display.",
            needs = listOf("Mobiles", "Under ₹25,000", "Camera")
        ),
        Product(
            id = "mob_realme12p",
            name = "Realme 12 Pro 5G",
            brand = "Realme",
            category = "Mobiles",
            subcategory = "Smartphones",
            price = "₹23,999",
            numericPrice = 23999,
            rating = "4.2",
            reviewCount = 280,
            stockQuantity = 0, // OUT OF STOCK TEST CASE
            description = "Submariner-inspired luxury design with Sony IMX882 OIS camera and 67W charging.",
            specs = "8GB RAM · 256GB Storage · Curved AMOLED",
            matchScore = "86% match",
            matchPercentage = 86,
            valueScore = 8.4,
            bestFor = "Curved display & premium back finish",
            recommendationSummary = "Stylish design with telephoto portrait lens.",
            needs = listOf("Mobiles", "Under ₹25,000")
        ),

        // ==================== LAPTOPS ====================
        Product(
            id = "lap_macm2",
            name = "Apple MacBook Air M2",
            brand = "Apple",
            category = "Laptops",
            subcategory = "Thin-and-light laptops",
            price = "₹89,900",
            numericPrice = 89900,
            rating = "4.8",
            reviewCount = 740,
            stockQuantity = 10,
            description = "Incredibly thin fanless laptop with Apple M2 chip, 13.6-inch Liquid Retina display, and up to 18 hours of battery life.",
            specs = "M2 Chip · 8GB RAM · 256GB SSD · 18h Battery",
            matchScore = "96% match",
            matchPercentage = 96,
            valueScore = 9.4,
            bestFor = "All-day productivity & fanless silent laptop",
            recommendationSummary = "Top recommendation for college and work due to silent, super-fast M2 chip and 18h battery.",
            needs = listOf("Laptops", "Long battery", "Study", "Work")
        ),
        Product(
            id = "lap_dellxps",
            name = "Dell XPS 13",
            brand = "Dell",
            category = "Laptops",
            subcategory = "Business laptops",
            price = "₹94,990",
            numericPrice = 94990,
            rating = "4.6",
            reviewCount = 210,
            stockQuantity = 5,
            description = "Precision-crafted machined aluminum chassis, 13.4-inch InfinityEdge display, 13th Gen Intel Core i7 processor.",
            specs = "Core i7 · 16GB RAM · 512GB SSD · Windows 11",
            matchScore = "91% match",
            matchPercentage = 91,
            valueScore = 8.9,
            bestFor = "Windows premium ultra-portable",
            recommendationSummary = "Ultralight business laptop with edge-to-edge screen and premium typing comfort.",
            needs = listOf("Laptops", "Business", "Work")
        ),
        Product(
            id = "lap_asusrog",
            name = "Asus ROG Strix G16",
            brand = "ASUS",
            category = "Laptops",
            subcategory = "Gaming laptops",
            price = "₹1,12,990",
            numericPrice = 112990,
            rating = "4.7",
            reviewCount = 310,
            stockQuantity = 7,
            description = "Intel Core i7-13650HX, Nvidia GeForce RTX 4060 8GB VRAM, ROG Intelligent Cooling, 165Hz ROG Nebula display.",
            specs = "Core i7 · RTX 4060 · 16GB RAM · 1TB SSD",
            matchScore = "93% match",
            matchPercentage = 93,
            valueScore = 9.0,
            bestFor = "High FPS Gaming & 3D rendering",
            recommendationSummary = "Powerhouse gaming laptop capable of ultra graphics gaming, video editing, and 3D work.",
            needs = listOf("Laptops", "Gaming")
        ),
        Product(
            id = "lap_hppavilion",
            name = "HP Pavilion 14",
            brand = "HP",
            category = "Laptops",
            subcategory = "Student laptops",
            price = "₹52,990",
            numericPrice = 52990,
            rating = "4.3",
            reviewCount = 450,
            stockQuantity = 14,
            description = "13th Gen Intel Core i5 processor, 14-inch Full HD IPS Anti-glare display, audio by B&O, aluminum cover.",
            specs = "Core i5 · 16GB RAM · 512GB SSD · Backlit KB",
            matchScore = "87% match",
            matchPercentage = 87,
            valueScore = 8.6,
            bestFor = "Student study & home office under ₹55k",
            recommendationSummary = "Balanced student laptop with 16GB RAM for multitasking and fast NVMe storage.",
            needs = listOf("Laptops", "Student", "Study")
        ),
        Product(
            id = "lap_lenovoidea",
            name = "Lenovo IdeaPad Slim 3",
            brand = "Lenovo",
            category = "Laptops",
            subcategory = "Student laptops",
            price = "₹38,990",
            numericPrice = 38990,
            rating = "4.2",
            reviewCount = 180,
            stockQuantity = 0, // OUT OF STOCK TEST CASE
            description = "12th Gen Intel Core i3, 8GB RAM, 512GB SSD, 15.6-inch FHD screen.",
            specs = "Core i3 · 8GB RAM · 512GB SSD",
            matchScore = "82% match",
            matchPercentage = 82,
            valueScore = 8.2,
            bestFor = "Entry budget laptop",
            recommendationSummary = "Affordable budget laptop for online classes and document work.",
            needs = listOf("Laptops", "Student")
        ),

        // ==================== AUDIO ====================
        Product(
            id = "jbl_510bt",
            name = "JBL Tune 510BT",
            brand = "JBL",
            category = "Audio",
            subcategory = "Headsets",
            price = "₹2,799",
            numericPrice = 2799,
            rating = "4.3",
            reviewCount = 980,
            stockQuantity = 25,
            description = "Wireless on-ear headphones with JBL Pure Bass sound, 40 hours battery life, Bluetooth 5.0, hands-free call controls.",
            specs = "40 hours battery · On-Ear · Hands-free mic",
            battery = "40 hours",
            callMic = "Clear indoors",
            weight = "160 g",
            multipoint = "Yes",
            activeNC = "No",
            appEQ = "No",
            warranty = "1 year",
            valueScore = 8.6,
            matchScore = "92% match",
            matchPercentage = 92,
            bestFor = "Clear calls + lighter fit",
            recommendationSummary = "Lighter weight and clearer indoor calls make it a top pick for classes.",
            needs = listOf("Clear calls", "Under ₹3,000", "Work-from-home")
        ),
        Product(
            id = "soundcore_h30i",
            name = "Soundcore H30i",
            brand = "Soundcore",
            category = "Audio",
            subcategory = "Headsets",
            price = "₹2,499",
            numericPrice = 2499,
            rating = "4.2",
            reviewCount = 430,
            stockQuantity = 18,
            description = "Foldable wireless headphones with 70 hours playtime, 40mm drivers, customized EQ via Soundcore app.",
            specs = "70 hours battery · App EQ · Foldable",
            battery = "70 hours",
            callMic = "Mixed in noise",
            weight = "183 g",
            multipoint = "Yes",
            activeNC = "No",
            appEQ = "Yes",
            warranty = "1 year",
            valueScore = 8.2,
            matchScore = "88% match",
            matchPercentage = 88,
            bestFor = "Battery + app EQ",
            recommendationSummary = "Extra long 70-hour battery life and custom app EQ at a lower budget.",
            needs = listOf("Audio", "Headsets", "Long battery", "Under ₹3,000", "Work-from-home")
        ),
        Product(
            id = "boat_450",
            name = "boAt Rockerz 450",
            brand = "boAt",
            category = "Audio",
            subcategory = "Headsets",
            price = "₹1,499",
            numericPrice = 1499,
            rating = "4.1",
            reviewCount = 1250,
            stockQuantity = 30,
            description = "40mm dynamic drivers, padded ear cushions, integrated controls, up to 15 hours playback.",
            specs = "15 hours battery · On-Ear · HD Sound",
            battery = "15 hours",
            callMic = "Basic mic",
            weight = "168 g",
            multipoint = "No",
            activeNC = "No",
            appEQ = "No",
            warranty = "1 year",
            valueScore = 8.0,
            matchScore = "84% match",
            matchPercentage = 84,
            bestFor = "Lower spend & budget value",
            recommendationSummary = "Unbeatable entry-level price for daily casual listening.",
            needs = listOf("Audio", "Headsets", "Under ₹3,000", "Travel audio")
        ),
        Product(
            id = "boat_stone_350",
            name = "boAt Stone 350 Speaker",
            brand = "boAt",
            category = "Audio",
            subcategory = "Speakers",
            price = "₹1,299",
            numericPrice = 1299,
            rating = "4.2",
            reviewCount = 670,
            stockQuantity = 15,
            description = "10W stereo sound, IPX7 splash resistance, TWS feature to pair 2 speakers together.",
            specs = "10W RMS · IPX7 Water Resistant · 12h Battery",
            battery = "12 hours",
            callMic = "Inbuilt mic",
            weight = "210 g",
            valueScore = 8.1,
            matchScore = "85% match",
            matchPercentage = 85,
            bestFor = "Portable room speaker",
            recommendationSummary = "Loud, punchy bass for small rooms and outdoor gatherings.",
            needs = listOf("Audio", "Speakers", "Under ₹3,000", "Travel audio")
        ),
        Product(
            id = "jbl_flip_6",
            name = "JBL Flip 6 Bluetooth Speaker",
            brand = "JBL",
            category = "Audio",
            subcategory = "Speakers",
            price = "₹9,999",
            numericPrice = 9999,
            rating = "4.6",
            reviewCount = 540,
            stockQuantity = 10,
            description = "2-way speaker system engineered for loud, crystal clear, powerful sound. IP67 waterproof and dustproof with 12 hours playtime.",
            specs = "20W RMS · IP67 Waterproof · 12h Playtime",
            battery = "12 hours",
            weight = "550 g",
            valueScore = 9.0,
            matchScore = "92% match",
            matchPercentage = 92,
            bestFor = "Outdoor parties & waterproof portability",
            recommendationSummary = "High power punchy bass and rugged waterproof build for outdoor adventures.",
            needs = listOf("Audio", "Speakers", "Bluetooth")
        ),
        Product(
            id = "sony_ch520",
            name = "Sony WH-CH520",
            brand = "Sony",
            category = "Audio",
            subcategory = "Headsets",
            price = "₹4,490",
            numericPrice = 4490,
            rating = "4.4",
            reviewCount = 820,
            stockQuantity = 12,
            description = "DSEE sound enhancement, 50 hours battery life, multipoint connection, hands-free calling with beamforming mic.",
            specs = "50 hours battery · DSEE · App Connect",
            battery = "50 hours",
            callMic = "HD voice mic",
            weight = "147 g",
            multipoint = "Yes",
            activeNC = "No",
            appEQ = "Yes",
            warranty = "1 year",
            valueScore = 8.8,
            matchScore = "90% match",
            matchPercentage = 90,
            bestFor = "Premium comfort & Sony sound",
            recommendationSummary = "Ultra-lightweight design with Sony Headphones Connect app customization.",
            needs = listOf("Audio", "Headsets", "Clear calls", "Long battery", "Work-from-home")
        ),
        Product(
            id = "realme_buds3",
            name = "Realme Buds Wireless 3",
            brand = "Realme",
            category = "Audio",
            subcategory = "Headsets",
            price = "₹1,699",
            numericPrice = 1699,
            rating = "4.0",
            reviewCount = 310,
            stockQuantity = 22,
            description = "30dB Active Noise Cancellation, 13.6mm Bass Boost Driver, 360-degree spatial audio, 40 hours total battery.",
            specs = "30dB ANC · 40 hours battery · 13.6mm driver",
            battery = "40 hours",
            callMic = "ENC mic",
            weight = "30 g",
            multipoint = "Yes",
            activeNC = "30dB ANC",
            appEQ = "Yes",
            warranty = "1 year",
            valueScore = 8.4,
            matchScore = "82% match",
            matchPercentage = 82,
            bestFor = "Active Noise Cancellation",
            recommendationSummary = "Incredible active noise cancellation for commuters at budget prices.",
            needs = listOf("Audio", "Headsets", "Travel audio", "Under ₹3,000")
        ),

        // ==================== FASHION ====================
        // ----- MEN: DRESSES -----
        Product(
            id = "fas_men_shirt",
            name = "Allen Solly Men's Cotton Formal Shirt",
            brand = "Allen Solly",
            category = "Fashion",
            subcategory = "Men",
            price = "₹1,899",
            numericPrice = 1899,
            rating = "4.5",
            reviewCount = 420,
            stockQuantity = 18,
            description = "Premium 100% breathable cotton formal shirt with a cutaway collar and slim tailored fit for office and smart occasions.",
            specs = "100% Cotton · Slim Fit · Formal Shirt",
            matchScore = "94% match",
            matchPercentage = 94,
            valueScore = 9.1,
            bestFor = "Office wear & formal occasions for Men",
            recommendationSummary = "Crisp breathable cotton fabric with tailored structure for all-day comfort.",
            needs = listOf("Fashion", "Men", "Dress", "Formal")
        ),
        Product(
            id = "fas_men_jeans",
            name = "Levi's Men's 511 Slim Fit Stretch Jeans",
            brand = "Levi's",
            category = "Fashion",
            subcategory = "Men",
            price = "₹2,899",
            numericPrice = 2899,
            rating = "4.6",
            reviewCount = 760,
            stockQuantity = 15,
            description = "Modern slim-fitting jeans with room to move. Crafted from premium stretch denim with signature arcuate back pocket stitching.",
            specs = "99% Cotton, 1% Elastane · Slim Fit · 5-Pocket",
            matchScore = "93% match",
            matchPercentage = 93,
            valueScore = 9.0,
            bestFor = "Everyday casual style & durability for Men",
            recommendationSummary = "Iconic stretch denim that holds shape while providing flexible movement.",
            needs = listOf("Fashion", "Men", "Dress", "Casual")
        ),
        Product(
            id = "fas_men_kurta",
            name = "Manyavar Men's Silk Blend Kurta Set",
            brand = "Manyavar",
            category = "Fashion",
            subcategory = "Men",
            price = "₹3,999",
            numericPrice = 3999,
            rating = "4.7",
            reviewCount = 310,
            stockQuantity = 10,
            description = "Exquisite jacquard woven silk blend ethnic kurta paired with a matching churidar, featuring a mandarin collar and refined buttons.",
            specs = "Jacquard Silk Blend · Regular Fit · Includes Churidar",
            matchScore = "91% match",
            matchPercentage = 91,
            valueScore = 8.9,
            bestFor = "Festive celebrations & weddings for Men",
            recommendationSummary = "Royal ethnic styling with rich sheen and breathable inner lining.",
            needs = listOf("Fashion", "Men", "Dress", "Ethnic")
        ),

        // ----- MEN: WATCHES & ACCESSORIES -----
        Product(
            id = "fas_fossil_watch",
            name = "Fossil Grant Chronograph Leather Watch",
            brand = "Fossil",
            category = "Fashion",
            subcategory = "Men",
            price = "₹8,495",
            numericPrice = 8495,
            rating = "4.6",
            reviewCount = 530,
            stockQuantity = 6,
            description = "Genuine brown leather strap, stainless steel case, Roman numeral markers, stopwatch chronograph, 50m water resistance.",
            specs = "Chronograph Quartz · 44mm Case · Leather Strap",
            matchScore = "95% match",
            matchPercentage = 95,
            valueScore = 9.2,
            bestFor = "Classic leather analog wristwear for Men",
            recommendationSummary = "Timeless chronograph aesthetic with durable genuine leather band.",
            needs = listOf("Fashion", "Men", "Watches & Accessories", "Watches")
        ),
        Product(
            id = "fas_titan_men_watch",
            name = "Titan Neo Men's Analog Quartz Watch",
            brand = "Titan",
            category = "Fashion",
            subcategory = "Men",
            price = "₹3,595",
            numericPrice = 3595,
            rating = "4.5",
            reviewCount = 610,
            stockQuantity = 14,
            description = "Midnight blue dial with stainless steel mesh strap, scratch-resistant mineral glass, and reliable quartz movement.",
            specs = "Stainless Steel Mesh · 50m Water Resistant · Quartz",
            matchScore = "92% match",
            matchPercentage = 92,
            valueScore = 9.0,
            bestFor = "Sleek modern business wristwear for Men",
            recommendationSummary = "Elegant minimalist blue dial suited for office and dinner wear.",
            needs = listOf("Fashion", "Men", "Watches & Accessories", "Watches")
        ),
        Product(
            id = "fas_men_belt_wallet",
            name = "Tommy Hilfiger Reversible Leather Belt & Wallet Set",
            brand = "Tommy Hilfiger",
            category = "Fashion",
            subcategory = "Men",
            price = "₹2,499",
            numericPrice = 2499,
            rating = "4.4",
            reviewCount = 380,
            stockQuantity = 12,
            description = "100% genuine top-grain leather reversible belt (Black/Brown) accompanied by an RFID-protected bifold leather wallet in a gift box.",
            specs = "100% Genuine Leather · Reversible Buckle · RFID Wallet",
            matchScore = "90% match",
            matchPercentage = 90,
            valueScore = 8.8,
            bestFor = "Essential daily leather accessories for Men",
            recommendationSummary = "2-in-1 reversible belt with matching RFID security wallet.",
            needs = listOf("Fashion", "Men", "Watches & Accessories", "Accessories")
        ),

        // ----- WOMEN: DRESSES -----
        Product(
            id = "fas_zara_dress",
            name = "Zara Women's Floral Summer Midi Dress",
            brand = "Zara",
            category = "Fashion",
            subcategory = "Women",
            price = "₹2,290",
            numericPrice = 2290,
            rating = "4.5",
            reviewCount = 410,
            stockQuantity = 16,
            description = "Flowy midi dress with vibrant botanical floral print, V-neckline, and comfortable elasticated waist fit.",
            specs = "Lightweight Viscose · Breathable · Women Dress",
            matchScore = "94% match",
            matchPercentage = 94,
            valueScore = 9.0,
            bestFor = "Breathable summer elegance for Women",
            recommendationSummary = "Flowy, elegant floral print dress ideal for sunny outings and events.",
            needs = listOf("Fashion", "Women", "Dress", "Casual")
        ),
        Product(
            id = "fas_biba_anarkali",
            name = "BIBA Women's Embroidered Anarkali Kurta Set",
            brand = "BIBA",
            category = "Fashion",
            subcategory = "Women",
            price = "₹3,299",
            numericPrice = 3299,
            rating = "4.7",
            reviewCount = 520,
            stockQuantity = 12,
            description = "Pure cotton floral embroidered Anarkali silhouette with matching churidar and crushed printed dupatta with golden gota work.",
            specs = "100% Cotton · Anarkali Fit · Dupatta Included",
            matchScore = "95% match",
            matchPercentage = 95,
            valueScore = 9.2,
            bestFor = "Festive events & wedding guest attire for Women",
            recommendationSummary = "Intricate floral embroidery with lightweight breathable pure cotton fabric.",
            needs = listOf("Fashion", "Women", "Dress", "Ethnic")
        ),
        Product(
            id = "fas_women_jeans",
            name = "Levi's Women's 721 High Rise Skinny Jeans",
            brand = "Levi's",
            category = "Fashion",
            subcategory = "Women",
            price = "₹2,699",
            numericPrice = 2699,
            rating = "4.4",
            reviewCount = 480,
            stockQuantity = 14,
            description = "Flattering high-rise waist with Levi's Sculpt Hyperstretch fabric that hugs your curves without losing shape.",
            specs = "High Rise · Hyperstretch Fabric · Ankle Length",
            matchScore = "91% match",
            matchPercentage = 91,
            valueScore = 8.8,
            bestFor = "Figure-flattering denim wear for Women",
            recommendationSummary = "Sculpting denim with maximum stretch recovery and comfortable waistband.",
            needs = listOf("Fashion", "Women", "Dress", "Casual")
        ),

        // ----- WOMEN: WATCHES & ACCESSORIES -----
        Product(
            id = "fas_titan_raga",
            name = "Titan Raga Viva Rose Gold Analog Watch",
            brand = "Titan",
            category = "Fashion",
            subcategory = "Women",
            price = "₹4,995",
            numericPrice = 4995,
            rating = "4.7",
            reviewCount = 680,
            stockQuantity = 9,
            description = "Mother of Pearl dial adorned with genuine Swarovski crystals, paired with a jewelry clasp rose gold plated metal strap.",
            specs = "Mother of Pearl Dial · Rose Gold · Swarovski Crystals",
            matchScore = "96% match",
            matchPercentage = 96,
            valueScore = 9.3,
            bestFor = "Jewelry inspired luxury watch for Women",
            recommendationSummary = "Dazzling crystal embellishments on pearl dial for formal & party looks.",
            needs = listOf("Fashion", "Women", "Watches & Accessories", "Watches")
        ),
        Product(
            id = "fas_mk_watch",
            name = "Michael Kors Parker Glitz Chronograph Watch",
            brand = "Michael Kors",
            category = "Fashion",
            subcategory = "Women",
            price = "₹12,995",
            numericPrice = 12995,
            rating = "4.8",
            reviewCount = 310,
            stockQuantity = 5,
            description = "Two-tone stainless steel with pavé crystal bezel, three sub-dials, date window, and water resistance up to 100 meters.",
            specs = "39mm Case · Pave Crystal Bezel · 100m Water Resistant",
            matchScore = "93% match",
            matchPercentage = 93,
            valueScore = 8.9,
            bestFor = "Designer luxury statement watch for Women",
            recommendationSummary = "Iconic Michael Kors glitz styling in polished two-tone links.",
            needs = listOf("Fashion", "Women", "Watches & Accessories", "Watches")
        ),
        Product(
            id = "fas_lavie_tote",
            name = "Lavie Women's Structured Tote Bag & Sunglasses Set",
            brand = "Lavie",
            category = "Fashion",
            subcategory = "Women",
            price = "₹2,199",
            numericPrice = 2199,
            rating = "4.3",
            reviewCount = 290,
            stockQuantity = 11,
            description = "Textured faux-leather tote handbag with multiple zip pockets, bundled with UV400 oversized gradient polarized sunglasses.",
            specs = "Dual Handle Tote · UV400 Protection · Water Resistant",
            matchScore = "89% match",
            matchPercentage = 89,
            valueScore = 8.7,
            bestFor = "Spacious daily work bag & eye protection for Women",
            recommendationSummary = "Spacious laptop-friendly tote with matching stylish UV sunglasses.",
            needs = listOf("Fashion", "Women", "Watches & Accessories", "Accessories")
        ),

        // ----- BOYS: DRESSES -----
        Product(
            id = "fas_boys_polo",
            name = "U.S. Polo Assn. Boys Polo T-Shirt & Shorts Set",
            brand = "U.S. Polo Assn.",
            category = "Fashion",
            subcategory = "Boys",
            price = "₹1,499",
            numericPrice = 1499,
            rating = "4.5",
            reviewCount = 260,
            stockQuantity = 20,
            description = "Pure breathable pique cotton polo shirt featuring iconic logo embroidery, coupled with elasticated drawstring chino shorts.",
            specs = "100% Pique Cotton · Elastic Drawstring · Ages 4-12",
            matchScore = "93% match",
            matchPercentage = 93,
            valueScore = 9.1,
            bestFor = "Comfortable playtime & outings for Boys",
            recommendationSummary = "Soft bio-washed cotton that withstands active outdoor play and frequent washing.",
            needs = listOf("Fashion", "Boys", "Dress", "Casual")
        ),
        Product(
            id = "fas_hm_hoodie",
            name = "H&M Boys Printed Fleece Pullover Hoodie",
            brand = "H&M",
            category = "Fashion",
            subcategory = "Boys",
            price = "₹1,199",
            numericPrice = 1199,
            rating = "4.3",
            reviewCount = 180,
            stockQuantity = 20,
            description = "Soft fleece sweatshirt hoodie with graphic chest print, kangaroo front pocket, and ribbed hem.",
            specs = "Soft Brushed Fleece · Boys 6-14 Yrs",
            matchScore = "88% match",
            matchPercentage = 88,
            valueScore = 8.5,
            bestFor = "Cozy daily warmth for Boys",
            recommendationSummary = "Ribbed cuffs and soft fleece lining keep active boys comfortable all day.",
            needs = listOf("Fashion", "Boys", "Dress", "Winter")
        ),
        Product(
            id = "fas_boys_kurta",
            name = "Hopscotch Boys Festive Cotton Kurta Pajama Set",
            brand = "Hopscotch",
            category = "Fashion",
            subcategory = "Boys",
            price = "₹1,299",
            numericPrice = 1299,
            rating = "4.4",
            reviewCount = 190,
            stockQuantity = 15,
            description = "Traditional printed cotton ethnic kurta with contrast placket and soft elasticized cotton pajama pants.",
            specs = "100% Soft Cotton · Mandarin Collar · Ages 3-10",
            matchScore = "90% match",
            matchPercentage = 90,
            valueScore = 8.8,
            bestFor = "Festivals, Diwali & family celebrations for Boys",
            recommendationSummary = "Skin-friendly pure cotton tailored for kids' delicate skin.",
            needs = listOf("Fashion", "Boys", "Dress", "Ethnic")
        ),

        // ----- BOYS: WATCHES & ACCESSORIES -----
        Product(
            id = "fas_fastrack_boys",
            name = "Fastrack Kids Digital Sports Watch for Boys",
            brand = "Fastrack",
            category = "Fashion",
            subcategory = "Boys",
            price = "₹995",
            numericPrice = 995,
            rating = "4.4",
            reviewCount = 340,
            stockQuantity = 18,
            description = "Rugged digital sports watch with stopwatch, daily alarm, vibrant EL backlight, and 30m water-resistant silicone casing.",
            specs = "Digital Display · 30m Water Resistant · Alarm & Stopwatch",
            matchScore = "92% match",
            matchPercentage = 92,
            valueScore = 9.1,
            bestFor = "School sports & everyday timekeeping for Boys",
            recommendationSummary = "Shock-resistant body with night backlight for active boys.",
            needs = listOf("Fashion", "Boys", "Watches & Accessories", "Watches")
        ),
        Product(
            id = "fas_wildcraft_boys",
            name = "Wildcraft Kids Adventure School Backpack & Cap",
            brand = "Wildcraft",
            category = "Fashion",
            subcategory = "Boys",
            price = "₹1,299",
            numericPrice = 1299,
            rating = "4.5",
            reviewCount = 410,
            stockQuantity = 16,
            description = "Ergonomic 3-compartment lightweight backpack with padded shoulder straps, reflective safety strips, and matching baseball cap.",
            specs = "28L Capacity · Rain Protected · Padded Straps",
            matchScore = "91% match",
            matchPercentage = 91,
            valueScore = 8.9,
            bestFor = "School, tuition & weekend trips for Boys",
            recommendationSummary = "Back-support padding and heavy-duty water-repellent fabric.",
            needs = listOf("Fashion", "Boys", "Watches & Accessories", "Accessories")
        ),

        // ----- GIRLS: DRESSES -----
        Product(
            id = "fas_max_frock",
            name = "Max Girls Satin Party Flare Frock",
            brand = "Max",
            category = "Fashion",
            subcategory = "Girls",
            price = "₹999",
            numericPrice = 999,
            rating = "4.3",
            reviewCount = 150,
            stockQuantity = 14,
            description = "A-line party frock with glossy satin finish, ribbon belt tie, and skin-friendly cotton lining.",
            specs = "Satin Finish · Girls 4-12 Yrs · Concealed Zipper",
            matchScore = "92% match",
            matchPercentage = 92,
            valueScore = 8.9,
            bestFor = "Birthday parties & festive celebrations for Girls",
            recommendationSummary = "Soft interior lining and vibrant satin flare for birthdays and family functions.",
            needs = listOf("Fashion", "Girls", "Dress", "Party")
        ),
        Product(
            id = "fas_girls_floral_dress",
            name = "Mothercare Girls Floral Cotton Tiered Sundress",
            brand = "Mothercare",
            category = "Fashion",
            subcategory = "Girls",
            price = "₹899",
            numericPrice = 899,
            rating = "4.5",
            reviewCount = 220,
            stockQuantity = 18,
            description = "Charming ditsy floral printed tiered sundress made from ultra-soft certified organic cotton with flutter cap sleeves.",
            specs = "100% Organic Cotton · Flutter Sleeves · Ages 2-8",
            matchScore = "94% match",
            matchPercentage = 94,
            valueScore = 9.2,
            bestFor = "Daily casual comfort & summer outings for Girls",
            recommendationSummary = "Lightweight airy cotton that prevents irritation in warm weather.",
            needs = listOf("Fashion", "Girls", "Dress", "Casual")
        ),
        Product(
            id = "fas_girls_lehenga",
            name = "Biba Girls Festive Embroidered Lehenga Choli Set",
            brand = "BIBA",
            category = "Fashion",
            subcategory = "Girls",
            price = "₹2,499",
            numericPrice = 2499,
            rating = "4.6",
            reviewCount = 175,
            stockQuantity = 8,
            description = "Bright festive flared lehenga skirt with gold sequin embroidery, coordinated choli top, and sheer net dupatta with border trim.",
            specs = "Embroidered Net Flare · Soft Cotton Lining · Ages 5-12",
            matchScore = "93% match",
            matchPercentage = 93,
            valueScore = 8.8,
            bestFor = "Weddings, festivals & family gatherings for Girls",
            recommendationSummary = "Dazzling sequin embroidery with comfortable soft cotton base.",
            needs = listOf("Fashion", "Girls", "Dress", "Ethnic")
        ),

        // ----- GIRLS: WATCHES & ACCESSORIES -----
        Product(
            id = "fas_zoop_girls",
            name = "Zoop by Titan Pink Digital Watch for Girls",
            brand = "Titan",
            category = "Fashion",
            subcategory = "Girls",
            price = "₹895",
            numericPrice = 895,
            rating = "4.6",
            reviewCount = 390,
            stockQuantity = 17,
            description = "Cute butterfly-themed digital watch featuring a shock-resistant casing, multi-color backlight, and soft skin-safe silicone strap.",
            specs = "Digital Display · Shock Resistant · Water Resistant 30m",
            matchScore = "94% match",
            matchPercentage = 94,
            valueScore = 9.3,
            bestFor = "First watch & time learning for Girls",
            recommendationSummary = "Playful butterfly accents with durable scratch-resistant glass.",
            needs = listOf("Fashion", "Girls", "Watches & Accessories", "Watches")
        ),
        Product(
            id = "fas_disney_girls",
            name = "Disney Princess Glitter Sling Bag & Hair Accessories Set",
            brand = "Disney",
            category = "Fashion",
            subcategory = "Girls",
            price = "₹799",
            numericPrice = 799,
            rating = "4.4",
            reviewCount = 280,
            stockQuantity = 15,
            description = "Sparkling holographic crossbody sling bag featuring Disney Princess graphics, bundled with matching satin hair bows and hair clips.",
            specs = "Glitter Crossbody Bag + 6-Piece Hair Bow & Clip Set",
            matchScore = "90% match",
            matchPercentage = 90,
            valueScore = 8.7,
            bestFor = "Cute styling & party dress-up for Girls",
            recommendationSummary = "Delightful sparkle sling bag with matching hair accessories.",
            needs = listOf("Fashion", "Girls", "Watches & Accessories", "Accessories")
        ),

        // ==================== HOME APPLIANCES ====================
        Product(
            id = "app_dyson_purifier",
            name = "Dyson Air Purifier Cool",
            brand = "Dyson",
            category = "Home Appliances",
            subcategory = "Air Purifiers",
            price = "₹32,900",
            numericPrice = 32900,
            rating = "4.7",
            reviewCount = 380,
            stockQuantity = 8,
            description = "Fully sealed HEPA H13 filtration system captures 99.95% of pollutants as small as 0.1 microns.",
            specs = "HEPA H13 Filter · Air Multiplier · Oscillates 350°",
            matchScore = "95% match",
            matchPercentage = 95,
            valueScore = 9.1,
            bestFor = "Allergen removal & cooling airflow",
            recommendationSummary = "Senses and captures 99.95% of ultrafine particles and allergens automatically.",
            needs = listOf("Home Appliances", "Air Purifier")
        ),
        Product(
            id = "app_lg_washing",
            name = "LG 8kg Front Load Washing Machine",
            brand = "LG",
            category = "Home Appliances",
            subcategory = "Washing Machines",
            price = "₹34,990",
            numericPrice = 34990,
            rating = "4.6",
            reviewCount = 710,
            stockQuantity = 10,
            description = "AI Direct Drive detects fabric weight and softness, Steam wash removes 99.9% virus and allergens.",
            specs = "AI Direct Drive · Steam Wash · 5 Star Inverter",
            matchScore = "93% match",
            matchPercentage = 93,
            valueScore = 9.0,
            bestFor = "Fabric protection & 5-star energy saving",
            recommendationSummary = "AI Direct Drive detects weight and fabric softness to optimize wash patterns.",
            needs = listOf("Home Appliances", "Washing Machine")
        ),
        Product(
            id = "app_philips_airfryer",
            name = "Philips Digital Air Fryer",
            brand = "Philips",
            category = "Home Appliances",
            subcategory = "Kitchen Appliances",
            price = "₹8,999",
            numericPrice = 8999,
            rating = "4.5",
            reviewCount = 1100,
            stockQuantity = 22,
            description = "Rapid Air Technology cooks food with 90% less oil, 4.1L capacity, touch screen with 7 presets.",
            specs = "Rapid Air Tech · 4.1L Capacity · 1400W",
            matchScore = "91% match",
            matchPercentage = 91,
            valueScore = 8.8,
            bestFor = "Oil-free healthy cooking",
            recommendationSummary = "Fries with up to 90% less fat while maintaining crispy outer texture.",
            needs = listOf("Home Appliances", "Kitchen")
        ),
        Product(
            id = "app_samsung_fridge",
            name = "Samsung 253L Double Door Refrigerator",
            brand = "Samsung",
            category = "Home Appliances",
            subcategory = "Refrigerators",
            price = "₹24,990",
            numericPrice = 24990,
            rating = "4.4",
            reviewCount = 890,
            stockQuantity = 15,
            description = "Digital Inverter compressor automatically adjusts speed in response to cooling demand, Frost Free.",
            specs = "3 Star Inverter · 253L Capacity · Frost Free",
            matchScore = "89% match",
            matchPercentage = 89,
            valueScore = 8.6,
            bestFor = "Long lasting food freshness & silent cooling",
            recommendationSummary = "Digital Inverter compressor uses less energy and automatically adjusts speed.",
            needs = listOf("Home Appliances", "Refrigerator")
        ),
        Product(
            id = "app_mi_tv",
            name = "Xiaomi Smart TV 43 Inch 4K",
            brand = "Xiaomi",
            category = "Home Appliances",
            subcategory = "Televisions",
            price = "₹22,999",
            numericPrice = 22999,
            rating = "4.3",
            reviewCount = 950,
            stockQuantity = 0, // OUT OF STOCK TEST CASE
            description = "4K Ultra HD display, Dolby Vision, 30W speaker sound with Dolby Audio, Google TV interface.",
            specs = "4K UHD · Dolby Vision · Google TV · 30W Audio",
            matchScore = "87% match",
            matchPercentage = 87,
            valueScore = 8.5,
            bestFor = "Budget 4K Smart TV",
            recommendationSummary = "Crisp 4K resolution with Google TV built-in.",
            needs = listOf("Home Appliances", "Televisions")
        )
    )

    fun getProductByName(name: String): Product {
        return products.find { it.name.equals(name, ignoreCase = true) }
            ?: products.first()
    }

    fun getProductsByCategory(category: String, subcategory: String? = null): List<Product> {
        val catFiltered = products.filter { it.category.equals(category, ignoreCase = true) }
        return if (subcategory != null && subcategory != "All") {
            catFiltered.filter { it.subcategory.equals(subcategory, ignoreCase = true) }
        } else {
            catFiltered
        }
    }

    fun searchProducts(
        query: String,
        categoryFilter: String? = null,
        maxPriceFilter: Int? = null,
        sortBy: String = "Popularity"
    ): List<Product> {
        val q = query.trim().lowercase()

        val filtered = products.filter { product ->
            val matchesCategory = if (categoryFilter.isNullOrBlank() || categoryFilter == "All") {
                true
            } else {
                product.category.equals(categoryFilter, ignoreCase = true) ||
                        product.subcategory.equals(categoryFilter, ignoreCase = true) ||
                        product.needs.any { it.equals(categoryFilter, ignoreCase = true) }
            }

            val matchesPrice = if (maxPriceFilter != null) {
                product.numericPrice <= maxPriceFilter
            } else {
                true
            }

            val matchesQuery = if (q.isEmpty()) {
                true
            } else {
                product.name.lowercase().contains(q) ||
                        product.brand.lowercase().contains(q) ||
                        product.category.lowercase().contains(q) ||
                        product.subcategory.lowercase().contains(q) ||
                        product.specs.lowercase().contains(q) ||
                        product.needs.any { it.lowercase().contains(q) }
            }

            matchesCategory && matchesPrice && matchesQuery
        }

        return when (sortBy) {
            "Price: Low to High" -> filtered.sortedBy { it.numericPrice }
            "Price: High to Low" -> filtered.sortedByDescending { it.numericPrice }
            "Rating" -> filtered.sortedByDescending { it.rating.toFloatOrNull() ?: 0f }
            else -> filtered.sortedByDescending { it.matchPercentage }
        }
    }

    fun getSimilarProducts(product: Product): List<Product> {
        return products.filter {
            it.category.equals(product.category, ignoreCase = true) && it.id != product.id
        }
    }

    fun getFashionProducts(group: String = "All", type: String = "All"): List<Product> {
        val fashionList = products.filter { it.category.equals("Fashion", ignoreCase = true) }
        val groupFiltered = if (group == "All") {
            fashionList
        } else {
            fashionList.filter { it.subcategory.equals(group, ignoreCase = true) }
        }
        return if (type == "All") {
            groupFiltered
        } else if (type == "Dress") {
            groupFiltered.filter { it.needs.contains("Dress") }
        } else if (type.contains("Watch") || type.contains("Accessory")) {
            groupFiltered.filter {
                it.needs.contains("Watches & Accessories") ||
                        it.needs.contains("Watches") ||
                        it.needs.contains("Accessories")
            }
        } else {
            groupFiltered
        }
    }
}
