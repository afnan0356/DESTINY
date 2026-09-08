package com.example.data

/**
 * CHARACTER DATA & PRESETS (Prompt 02 of 20: CHARACTER SYSTEM)
 *
 * International countries, dependent cities, appearance options,
 * color mappings, talents, and names.
 */
object CharacterData {

    val COUNTRIES_AND_CITIES: Map<String, List<String>> = mapOf(
        "United States" to listOf("New York", "Los Angeles", "Chicago", "Houston", "Miami", "San Francisco"),
        "United Kingdom" to listOf("London", "Manchester", "Birmingham", "Edinburgh", "Glasgow"),
        "Japan" to listOf("Tokyo", "Kyoto", "Osaka", "Yokohama", "Sapporo"),
        "Germany" to listOf("Berlin", "Munich", "Frankfurt", "Hamburg", "Cologne"),
        "Canada" to listOf("Toronto", "Vancouver", "Montreal", "Calgary", "Ottawa"),
        "Australia" to listOf("Sydney", "Melbourne", "Brisbane", "Perth", "Adelaide"),
        "France" to listOf("Paris", "Lyon", "Marseille", "Nice", "Bordeaux"),
        "Brazil" to listOf("São Paulo", "Rio de Janeiro", "Salvador", "Brasília"),
        "South Korea" to listOf("Seoul", "Busan", "Incheon", "Daegu"),
        "Italy" to listOf("Rome", "Milan", "Florence", "Naples", "Venice")
    )

    val COUNTRIES: List<String> = COUNTRIES_AND_CITIES.keys.toList()

    val GENDERS: List<String> = listOf("Male", "Female", "Non-Binary")

    val SEXUALITIES: List<String> = listOf("Heterosexual", "Homosexual", "Bisexual", "Asexual")

    data class TalentOption(val id: String, val label: String, val desc: String)

    val TALENTS: List<TalentOption> = listOf(
        TalentOption("None", "None", "Generalist — Balanced, no innate specialization"),
        TalentOption("Acting", "Acting", "Dramatic Arts — Charismatic presence and emotional projection"),
        TalentOption("Crime", "Crime", "Underworld Cunning — Stealth, threat detection, and street instinct"),
        TalentOption("Dealing", "Dealing", "Commerce — High-stakes negotiation and trade acumen"),
        TalentOption("Modeling", "Modeling", "High Aesthetics — Photogenic magnetism and poise"),
        TalentOption("Music", "Music", "Acoustic Genius — Rhythmic intuition and auditory composition"),
        TalentOption("Sports", "Sports", "Kinetic Athletics — Explosive physical coordination and stamina")
    )

    data class ColorOption(val name: String, val hex: Long)

    val SKIN_TONES: List<ColorOption> = listOf(
        ColorOption("Fair", 0xFFFDE2D2),
        ColorOption("Light", 0xFFF4CFB7),
        ColorOption("Medium", 0xFFE0AC84),
        ColorOption("Olive", 0xFFC69165),
        ColorOption("Tan", 0xFFA87046),
        ColorOption("Dark", 0xFF7D4E2D),
        ColorOption("Deep", 0xFF4A2C1A)
    )

    val EYE_COLORS: List<ColorOption> = listOf(
        ColorOption("Brown", 0xFF5A3825),
        ColorOption("Blue", 0xFF3A75C4),
        ColorOption("Green", 0xFF388E3C),
        ColorOption("Hazel", 0xFF8D6E63),
        ColorOption("Amber", 0xFFFFB300),
        ColorOption("Gray", 0xFF78909C)
    )

    val EYE_STYLES: List<String> = listOf("Almond", "Round", "Hooded", "Deep-Set", "Monolid")

    val BROW_STYLES: List<String> = listOf("Straight", "Arched", "Soft Arch", "Thick Bushy", "Thin Curved")

    val HAIR_COLORS: List<ColorOption> = listOf(
        ColorOption("Black", 0xFF1E1E1E),
        ColorOption("Dark Brown", 0xFF3E2723),
        ColorOption("Chestnut", 0xFF5D4037),
        ColorOption("Golden Blonde", 0xFFE0B050),
        ColorOption("Auburn Red", 0xFFB71C1C),
        ColorOption("Platinum", 0xFFE0E0E0),
        ColorOption("Silver Gray", 0xFF9E9E9E)
    )

    val HAIR_STYLES: List<String> = listOf(
        "Short Crop",
        "Buzz Cut",
        "Side Part",
        "Medium Waves",
        "Long Flow",
        "Afro",
        "Slicked Back",
        "Bald"
    )

    val FACIAL_HAIR_STYLES: List<String> = listOf(
        "Clean Shaven",
        "Light Stubble",
        "Full Beard",
        "Goatee",
        "Classic Mustache"
    )

    val FIRST_NAMES_MALE = listOf("Julian", "Marcus", "Adrian", "Leo", "Ethan", "Lucas", "Oliver", "Dante", "Kai", "Liam")
    val FIRST_NAMES_FEMALE = listOf("Elena", "Sophia", "Maya", "Clara", "Aria", "Chloe", "Isla", "Valerie", "Zoe", "Nora")
    val FIRST_NAMES_NB = listOf("Rowan", "Alex", "Jordan", "Morgan", "Taylor", "Sam", "Casey", "Avery", "Riley", "Cameron")

    val LAST_NAMES = listOf(
        "Vance", "Sterling", "Mercer", "Hawthorne", "Sinclair", "Cross",
        "Blackwood", "Valenti", "Kovacs", "Chen", "Saito", "Schmidt", "Moreau", "Silva"
    )

    fun getRandomName(gender: String): Pair<String, String> {
        val fList = when (gender) {
            "Female" -> FIRST_NAMES_FEMALE
            "Non-Binary" -> FIRST_NAMES_NB
            else -> FIRST_NAMES_MALE
        }
        val first = fList.random()
        val last = LAST_NAMES.random()
        return Pair(first, last)
    }

    fun getColorHex(colorName: String, list: List<ColorOption>, defaultHex: Long): Long {
        return list.firstOrNull { it.name.equals(colorName, ignoreCase = true) }?.hex ?: defaultHex
    }
}
