package com.example.data

data class Monument(
  val id: String,
  val name: String,
  val hindiName: String,
  val state: String,
  val era: String,
  val architecturalStyle: String,
  val unescoYear: Int?,
  val description: String,
  val highlights: List<String>,
  val imageUrl: String
)

data class IndianState(
  val name: String,
  val capital: String,
  val region: String, // North, South, East, West, Central, North-East
  val primaryLanguage: String,
  val traditionalArt: String,
  val famousFestivals: List<String>,
  val traditionalDances: List<String>,
  val famousCuisine: List<String>,
  val iconicLandmark: String
)

data class SupportedLanguage(
  val code: String,
  val name: String,
  val nativeName: String,
  val greeting: String,
  val script: String
)

data class QuizQuestion(
  val id: Int,
  val category: String,
  val question: String,
  val options: List<String>,
  val correctOptionIndex: Int,
  val explanation: String
)

data class BharatGuideTopic(
  val id: String,
  val title: String,
  val category: String,
  val answer: String,
  val tags: List<String>
)

object HeritageRepository {

  val languages = listOf(
    SupportedLanguage("en", "English", "English", "Welcome to Bharat Heritage", "Latin"),
    SupportedLanguage("hi", "Hindi", "हिन्दी", "भारत हेरिटेज में आपका स्वागत है", "Devanagari"),
    SupportedLanguage("sa", "Sanskrit", "संस्कृतम्", "भारतस्य सांस्कृतिकपरम्परायां भवतां स्वागतम्", "Devanagari"),
    SupportedLanguage("ta", "Tamil", "தமிழ்", "பாரத பாரம்பரியத்திற்கு உங்களை வரவேற்கிறோம்", "Tamil"),
    SupportedLanguage("te", "Telugu", "తెలుగు", "భారత వారసత్వానికి స్వాగతం", "Telugu"),
    SupportedLanguage("bn", "Bengali", "বাংলা", "ভারত ঐতিহ্যে আপনাকে স্বাগতম", "Bengali"),
    SupportedLanguage("mr", "Marathi", "मराठी", "भारत वारसा मध्ये आपले स्वागत आहे", "Devanagari"),
    SupportedLanguage("gu", "Gujarati", "ગુજરાતી", "ભારત વારસામાં આપનું સ્વાગત છે", "Gujarati"),
    SupportedLanguage("kn", "Kannada", "ಕನ್ನಡ", "ಭಾರತ ಪರಂಪರೆಗೆ ಸ್ವಾಗತ", "Kannada"),
    SupportedLanguage("ml", "Malayalam", "മലയാളം", "ഭാരത പൈതൃകത്തിലേക്ക് സ്വാഗതം", "Malayalam"),
    SupportedLanguage("pa", "Punjabi", "ਪੰਜਾਬੀ", "ਭਾਰਤ ਵਿਰਾਸਤ ਵਿੱਚ ਜੀ ਆਇਆਂ ਨੂੰ", "Gurmukhi"),
    SupportedLanguage("or", "Odia", "ଓଡ଼ିଆ", "ଭାରତ ଐତିହ୍ୟକୁ ସ୍ୱାଗତ", "Odia")
  )

  val monuments = listOf(
    Monument(
      id = "taj-mahal",
      name = "Taj Mahal",
      hindiName = "ताज महल",
      state = "Uttar Pradesh (Agra)",
      era = "Mughal Empire (1631–1653 CE)",
      architecturalStyle = "Indo-Islamic White Marble",
      unescoYear = 1983,
      description = "An ivory-white marble mausoleum on the south bank of the Yamuna river, celebrated globally as the jewel of Muslim art in India and a universally admired masterpiece of world heritage.",
      highlights = listOf("Pure Makrana marble", "Pietra dura floral inlays", "Perfect four-fold symmetrical Charbagh garden", "Central marble dome 73m high"),
      imageUrl = "https://images.unsplash.com/photo-1564507592333-c60657eea523?w=800&q=80"
    ),
    Monument(
      id = "hampi",
      name = "Hampi Monuments",
      hindiName = "हम्पी स्मारक",
      state = "Karnataka (Vijayanagara)",
      era = "Vijayanagara Empire (14th–16th Century)",
      architecturalStyle = "Dravidian Vijayanagara Architecture",
      unescoYear = 1986,
      description = "The majestic capital of the Vijayanagara Empire situated along the Tungabhadra River, dotted with colossal monoliths, musical stone pillars, and stone chariot shrines.",
      highlights = listOf("Vittala Temple Stone Chariot", "Musical pillars producing 7 notes (Sapthaswaras)", "Virupaksha Temple tower", "Lotus Mahal & Elephant Stables"),
      imageUrl = "https://images.unsplash.com/photo-1600100397608-f010f443b747?w=800&q=80"
    ),
    Monument(
      id = "konark",
      name = "Konark Sun Temple",
      hindiName = "कोणार्क सूर्य मंदिर",
      state = "Odisha (Puri)",
      era = "Eastern Ganga Dynasty (c. 1250 CE)",
      architecturalStyle = "Kalinga Architecture",
      unescoYear = 1984,
      description = "Conceived as a mammoth chariot for Surya the Sun God, with 24 carved stone wheels pulled by 7 galloping horses, aligned precisely with the solar calendar.",
      highlights = listOf("24 stone wheels acting as sundials", "Intricate erotic and courtly sculptures", "Chlorite stone deity carvings", "Calculates time to exact minutes"),
      imageUrl = "https://images.unsplash.com/photo-1599818816949-53e34b415a77?w=800&q=80"
    ),
    Monument(
      id = "ajanta-ellora",
      name = "Ajanta & Ellora Caves",
      hindiName = "अजंता एवं एलोरा गुफाएँ",
      state = "Maharashtra (Chhatrapati Sambhajinagar)",
      era = "2nd Century BCE – 10th Century CE",
      architecturalStyle = "Rock-Cut Basalt Cave Architecture",
      unescoYear = 1983,
      description = "Masterpieces of ancient rock-cut art. Ajanta features exquisite Buddhist fresco paintings (Jataka tales), while Ellora houses the world's largest monolithic excavation: the Kailash Temple.",
      highlights = listOf("Kailash Temple carved top-to-bottom from single rock", "Padmapani Bodhisattva frescoes", "Harmonious union of Buddhist, Hindu, and Jain shrines"),
      imageUrl = "https://images.unsplash.com/photo-1598890777032-bde835ba27c2?w=800&q=80"
    ),
    Monument(
      id = "khajuraho",
      name = "Khajuraho Group of Monuments",
      hindiName = "खजुराहो स्मारक समूह",
      state = "Madhya Pradesh (Chhatarpur)",
      era = "Chandela Dynasty (950–1050 CE)",
      architecturalStyle = "Nagara Architectural Style",
      unescoYear = 1986,
      description = "Celebrated for Nagara-style architectural symbolism and graceful sandstone sculptures depicting everyday medieval life, spirituality, celestial beings, and dharma.",
      highlights = listOf("Kandariya Mahadeva Temple", "Intricate friezes of music, dance & daily life", "Sandstone interlocking without mortar"),
      imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=800&q=80"
    ),
    Monument(
      id = "meenakshi",
      name = "Meenakshi Amman Temple",
      hindiName = "मीनाक्षी अम्मन मंदिर",
      state = "Tamil Nadu (Madurai)",
      era = "Pandyan / Nayaka Dynasty (14th–17th Century)",
      architecturalStyle = "Classical Dravidian Architecture",
      unescoYear = null,
      description = "Historic Hindu temple on the southern bank of the Vaigai River with 14 magnificent colorful gopurams (gateway towers), holding over 33,000 sculpted figures.",
      highlights = listOf("14 towering polychrome Gopurams", "Hall of Thousand Pillars (Ayiram Kaal Mandapam)", "Golden Lotus Pond (Porthamarai Kulam)"),
      imageUrl = "https://images.unsplash.com/photo-1609766857041-ed402ea8069a?w=800&q=80"
    ),
    Monument(
      id = "nalanda",
      name = "Nalanda Mahavihara",
      hindiName = "नालंदा महाविहार",
      state = "Bihar (Nalanda)",
      era = "Gupta Empire (5th Century CE)",
      architecturalStyle = "Ancient Monastic Stupa Architecture",
      unescoYear = 2016,
      description = "The ancient world's foremost residential university, where scholars like Aryabhata, Xuanzang, and Nagarjuna taught medicine, astronomy, mathematics, logic, and philosophy.",
      highlights = listOf("Library holding 9 million manuscripts (Dharmaganja)", "Sariputta Stupa with votive towers", "Accommodated 10,000 students from Asia"),
      imageUrl = "https://images.unsplash.com/photo-1627894483216-2138af692e32?w=800&q=80"
    ),
    Monument(
      id = "brihadeeswarar",
      name = "Brihadeeswarar Temple (Big Temple)",
      hindiName = "बृहदीश्वर मंदिर",
      state = "Tamil Nadu (Thanjavur)",
      era = "Chola Dynasty - Rajaraja I (1010 CE)",
      architecturalStyle = "Pure Dravidian Granite Architecture",
      unescoYear = 1987,
      description = "Built entirely of granite, this architectural wonder has a 66m high Vimana tower topped by an 80-tonne single granite block Kumbam, casting no shadow at noon on equinoxes.",
      highlights = listOf("80-tonne single granite capstone", "Massive monolithic Nandi bull carved from single stone", "Chola fresco paintings and bronze inscriptions"),
      imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=800&q=80"
    )
  )

  val states = listOf(
    IndianState(
      name = "Andhra Pradesh",
      capital = "Amaravati",
      region = "South",
      primaryLanguage = "Telugu",
      traditionalArt = "Kalamkari Textile Painting & Kondapalli Toys",
      famousFestivals = listOf("Sankranti", "Ugadi", "Tirupati Brahmotsavam"),
      traditionalDances = listOf("Kuchipudi (Classical)", "Vilasini Natyam"),
      famousCuisine = listOf("Gongura Pachadi", "Pootharekulu", "Pesarattu"),
      iconicLandmark = "Tirumala Venkateswara Temple & Lepakshi Nandi"
    ),
    IndianState(
      name = "Tamil Nadu",
      capital = "Chennai",
      region = "South",
      primaryLanguage = "Tamil",
      traditionalArt = "Tanjore Gold Foil Painting & Kanchipuram Silk",
      famousFestivals = listOf("Pongal", "Chithirai", "Karthigai Deepam"),
      traditionalDances = listOf("Bharatanatyam (Classical)", "Karakattam", "Mayil Attam"),
      famousCuisine = listOf("Chettinad Curry", "Idli Sambhar", "Filter Coffee"),
      iconicLandmark = "Brihadeeswarar Temple & Shore Temple Mamallapuram"
    ),
    IndianState(
      name = "Karnataka",
      capital = "Bengaluru",
      region = "South",
      primaryLanguage = "Kannada",
      traditionalArt = "Mysore Rosewood Inlay & Bidriware Craft",
      famousFestivals = listOf("Mysuru Dasara", "Karaga", "Ugadi"),
      traditionalDances = listOf("Yakshagana (Dance-Drama)", "Dollu Kunitha"),
      famousCuisine = listOf("Bisi Bele Bath", "Mysore Pak", "Ragi Mudde"),
      iconicLandmark = "Hampi Vijayanagara & Mysore Palace"
    ),
    IndianState(
      name = "Kerala",
      capital = "Thiruvananthapuram",
      region = "South",
      primaryLanguage = "Malayalam",
      traditionalArt = "Kathakali Facial Painting & Mural Paintings",
      famousFestivals = listOf("Onam", "Vishu", "Thrissur Pooram"),
      traditionalDances = listOf("Kathakali", "Mohiniyattam", "Theyyam"),
      famousCuisine = listOf("Sadya on Banana Leaf", "Appam with Stew", "Puttu"),
      iconicLandmark = "Padmanabhaswamy Temple & Kerala Backwaters"
    ),
    IndianState(
      name = "Maharashtra",
      capital = "Mumbai",
      region = "West",
      primaryLanguage = "Marathi",
      traditionalArt = "Warli Tribal Folk Painting & Paithani Sarees",
      famousFestivals = listOf("Ganesh Chaturthi", "Gudi Padwa", "Shivaji Jayanti"),
      traditionalDances = listOf("Lavani", "Koli Dance", "Dhangari Gaja"),
      famousCuisine = listOf("Puran Poli", "Misal Pav", "Vada Pav", "Pithla Bhakri"),
      iconicLandmark = "Ajanta & Ellora Caves & Gateway of India"
    ),
    IndianState(
      name = "Rajasthan",
      capital = "Jaipur",
      region = "North",
      primaryLanguage = "Hindi / Rajasthani",
      traditionalArt = "Phad Painting, Blue Pottery & Bandhani",
      famousFestivals = listOf("Pushkar Camel Fair", "Teej", "Gangaur"),
      traditionalDances = listOf("Ghoomar", "Kalbelia (Snake Dance)", "Chari"),
      famousCuisine = listOf("Dal Baati Churma", "Gatte ki Sabzi", "Ker Sangri"),
      iconicLandmark = "Hawa Mahal, Amber Fort & Mehrangarh Jodhpur"
    ),
    IndianState(
      name = "Gujarat",
      capital = "Gandhinagar",
      region = "West",
      primaryLanguage = "Gujarati",
      traditionalArt = "Rogan Art of Kutch & Patola Weaving",
      famousFestivals = listOf("Navratri Garba", "Uttarayan Kite Festival"),
      traditionalDances = listOf("Garba (UNESCO Intangible)", "Dandiya Raas"),
      famousCuisine = listOf("Dhokla", "Undhiyu", "Thepla", "Fafda Jalebi"),
      iconicLandmark = "Rani ki Vav & Somnath Temple"
    ),
    IndianState(
      name = "Uttar Pradesh",
      capital = "Lucknow",
      region = "North",
      primaryLanguage = "Hindi / Urdu",
      traditionalArt = "Chikankari Embroidery & Zardozi",
      famousFestivals = listOf("Dev Deepawali Varanasi", "Kumbh Mela Prayagraj"),
      traditionalDances = listOf("Kathak (Classical)", "Raslila", "Charkula"),
      famousCuisine = listOf("Awadhi Biryani", "Galouti Kebab", "Peda of Mathura"),
      iconicLandmark = "Kashi Vishwanath Varanasi & Taj Mahal Agra"
    ),
    IndianState(
      name = "West Bengal",
      capital = "Kolkata",
      region = "East",
      primaryLanguage = "Bengali",
      traditionalArt = "Kalighat Painting & Terracotta of Bishnupur",
      famousFestivals = listOf("Durga Puja (UNESCO Heritage)", "Poila Baisakh"),
      traditionalDances = listOf("Gaudiya Nritya", "Chhau Dance", "Baul Music"),
      famousCuisine = listOf("Shorshe Ilish", "Rosogolla", "Mishti Doi"),
      iconicLandmark = "Victoria Memorial, Dakshineswar & Sundarbans"
    ),
    IndianState(
      name = "Punjab",
      capital = "Chandigarh",
      region = "North",
      primaryLanguage = "Punjabi",
      traditionalArt = "Phulkari Floral Embroidery",
      famousFestivals = listOf("Baisakhi", "Lohri", "Gurpurab"),
      traditionalDances = listOf("Bhangra", "Giddha", "Jhumar"),
      famousCuisine = listOf("Makki di Roti & Sarson da Saag", "Dal Makhani", "Amritsari Kulcha"),
      iconicLandmark = "Golden Temple (Sri Harmandir Sahib) Amritsar"
    ),
    IndianState(
      name = "Odisha",
      capital = "Bhubaneswar",
      region = "East",
      primaryLanguage = "Odia",
      traditionalArt = "Pattachitra Scroll Painting & Silver Filigree (Tarakasi)",
      famousFestivals = listOf("Puri Rath Yatra", "Raja Parba", "Konark Dance Festival"),
      traditionalDances = listOf("Odissi (Classical)", "Chhau", "Gotipua"),
      famousCuisine = listOf("Chhena Poda", "Dalma", "Pakhala Bhata"),
      iconicLandmark = "Jagannath Temple Puri & Konark Sun Temple"
    ),
    IndianState(
      name = "Assam",
      capital = "Dispur",
      region = "North-East",
      primaryLanguage = "Assamese",
      traditionalArt = "Muga Golden Silk & Majuli Mask Making",
      famousFestivals = listOf("Rongali Bihu", "Kati Bihu", "Ambubachi Mela"),
      traditionalDances = listOf("Sattriya (Classical)", "Bihu Dance"),
      famousCuisine = listOf("Khaar", "Masor Tenga", "Pitha"),
      iconicLandmark = "Kamakhya Temple & Kaziranga National Park"
    ),
    IndianState(
      name = "Bihar",
      capital = "Patna",
      region = "East",
      primaryLanguage = "Hindi / Maithili",
      traditionalArt = "Madhubani / Mithila Painting",
      famousFestivals = listOf("Chhath Puja", "Sama Chakeva"),
      traditionalDances = listOf("Bidesia", "Jat-Jatin"),
      famousCuisine = listOf("Litti Chokha", "Thekua", "Khaja"),
      iconicLandmark = "Mahabodhi Temple Bodh Gaya & Nalanda University Ruins"
    ),
    IndianState(
      name = "Madhya Pradesh",
      capital = "Bhopal",
      region = "Central",
      primaryLanguage = "Hindi",
      traditionalArt = "Gond Tribal Art & Chanderi Silk",
      famousFestivals = listOf("Khajuraho Dance Festival", "Tansen Music Samaroh"),
      traditionalDances = listOf("Matki Dance", "Karma", "Grida"),
      famousCuisine = listOf("Poha Jalebi", "Bhutte Ka Kees", "Dal Bafla"),
      iconicLandmark = "Khajuraho Temples & Sanchi Stupa"
    )
  )

  val quizQuestions = listOf(
    QuizQuestion(
      id = 1,
      category = "Ancient Architecture",
      question = "Which ancient monument in Karnataka features 56 musical pillars that emit musical notes when gently tapped?",
      options = listOf("Chennakeshava Temple", "Vittala Temple at Hampi", "Halebidu Hoysaleswara", "Badami Cave Temples"),
      correctOptionIndex = 1,
      explanation = "The Vittala Temple at Hampi, built during the Vijayanagara Empire, contains 56 musical pillars (SaReGaMa pillars) engineered from resonant granite."
    ),
    QuizQuestion(
      id = 2,
      category = "World Heritage",
      question = "How many stone-carved wheels does the Konark Sun Temple in Odisha have, and what was their primary function?",
      options = listOf("12 wheels representing zodiacs", "24 wheels functioning as sundials", "8 wheels representing eightfold path", "16 wheels representing seasons"),
      correctOptionIndex = 1,
      explanation = "Konark Sun Temple has 24 intricately carved wheels representing 24 fortnights of the year, precisely engineered to tell exact solar time."
    ),
    QuizQuestion(
      id = 3,
      category = "Classical Arts",
      question = "Which Indian classical dance form originated in the temples of Tamil Nadu and was historically practiced by Devadasis?",
      options = listOf("Kathakali", "Kuchipudi", "Bharatanatyam", "Odissi"),
      correctOptionIndex = 2,
      explanation = "Bharatanatyam is one of the oldest classical dance traditions of India, codified in Bharata Muni's Natya Shastra and nurtured in the temples of Tamil Nadu."
    ),
    QuizQuestion(
      id = 4,
      category = "Indian Traditions",
      question = "Which ancient university in Bihar accommodated over 10,000 Buddhist and Hindu scholars from across Asia during the 5th to 12th century?",
      options = listOf("Takshashila", "Nalanda Mahavihara", "Vikramashila", "Vallabhi"),
      correctOptionIndex = 1,
      explanation = "Nalanda Mahavihara was a legendary center of higher learning patronized by the Guptas and Harsha, with an encyclopedic library called Dharmaganja."
    ),
    QuizQuestion(
      id = 5,
      category = "Folk Art & Textiles",
      question = "Madhubani painting, recognized for its two-dimensional geometrical patterns drawn with natural dyes, originated in which region?",
      options = listOf("Mithila region of Bihar", "Kutch in Gujarat", "Shekhawati in Rajasthan", "Warli in Maharashtra"),
      correctOptionIndex = 0,
      explanation = "Madhubani art (Mithila painting) is a traditional folk art practiced by women of the Mithila region in Bihar and Nepal, illustrating nature, deities, and wedding ceremonies."
    )
  )

  val guideTopics = listOf(
    BharatGuideTopic(
      id = "g-1",
      title = "Why was Hampi so prosperous in medieval times?",
      category = "History & Trade",
      answer = "Hampi was the jewel-capital of the Vijayanagara Empire (1336–1565 CE). Strategically nestled along the Tungabhadra River among boulder hills, it controlled major trade routes in South India. Portuguese, Persian, and Italian travelers recorded markets overflowing with diamonds, pearls, silks, and Arabian horses.",
      tags = listOf("Hampi", "Vijayanagara", "Medieval India", "Architecture")
    ),
    BharatGuideTopic(
      id = "g-2",
      title = "What is the science behind Konark Sun Temple's wheels?",
      category = "Ancient Science",
      answer = "Each of Konark's 24 wheels has 8 major spokes and 8 minor spokes. The spokes cast shadows that divide day into 8 Praharas (3 hours each). The beads along the wheel circumference allow reading time accurate to roughly 3 minutes!",
      tags = listOf("Konark", "Sun Temple", "Sundial", "Astronomy")
    ),
    BharatGuideTopic(
      id = "g-3",
      title = "What are the 8 Classical Dance forms recognized in India?",
      category = "Performing Arts",
      answer = "Sangeet Natak Akademi recognizes 8 classical dances: Bharatanatyam (Tamil Nadu), Kathak (North India), Kathakali (Kerala), Kuchipudi (Andhra Pradesh), Odissi (Odisha), Manipuri (Manipur), Mohiniyattam (Kerala), and Sattriya (Assam).",
      tags = listOf("Classical Dance", "Natya Shastra", "Culture")
    ),
    BharatGuideTopic(
      id = "g-4",
      title = "What is the cultural significance of the Ashoka Chakra?",
      category = "National Heritage",
      answer = "The 24-spoke Ashoka Chakra on India's flag was adopted from the Lion Capital of Ashoka at Sarnath (c. 250 BCE). It signifies the Dharmachakra — the eternal wheel of law, righteousness, and continuous dynamic progress for Bharat.",
      tags = listOf("Ashoka Chakra", "Sarnath", "National Symbol")
    )
  )
}
