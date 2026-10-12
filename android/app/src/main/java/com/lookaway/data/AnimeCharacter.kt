package com.lookaway.data

data class AnimeCharacter(
    val id: String,
    val name: String,
    val animeName: String,
    val animationUrl: String,
    val tagLine: String
) {
    companion object {
        const val DEFAULT_CHARACTER_ID = "goku"
        const val CUSTOM_ID = "custom"

        val presets = listOf(
            AnimeCharacter(
                id = "goku",
                name = "Goku",
                animeName = "Dragon Ball Z",
                animationUrl = "https://media.giphy.com/media/ul1omlrGG6kpO/giphy.gif",
                tagLine = "Take a break and recharge your Ki!"
            ),
            AnimeCharacter(
                id = "naruto",
                name = "Naruto",
                animeName = "Naruto Shippuden",
                animationUrl = "https://media.giphy.com/media/2y98KScHKeaQM/giphy.gif",
                tagLine = "Believe it! Give those ninja eyes a rest."
            ),
            AnimeCharacter(
                id = "luffy",
                name = "Luffy",
                animeName = "One Piece",
                animationUrl = "https://media.giphy.com/media/C3brYLms1bhv2/giphy.gif",
                tagLine = "Future King of the Pirates says: look away!"
            ),
            AnimeCharacter(
                id = "anya",
                name = "Anya Forger",
                animeName = "Spy x Family",
                animationUrl = "https://media.giphy.com/media/FWAcpJsFT9mVRv0e7a/giphy.gif",
                tagLine = "Waku Waku! Rest your eyes for 20 seconds!"
            ),
            AnimeCharacter(
                id = "pikachu",
                name = "Pikachu",
                animeName = "Pokémon",
                animationUrl = "https://media.giphy.com/media/13G7hmmFr9yuxG/giphy.gif",
                tagLine = "Pika-Pika! Time to look 20 feet away!"
            ),
            AnimeCharacter(
                id = "totoro",
                name = "Totoro",
                animeName = "My Neighbor Totoro",
                animationUrl = "https://media.giphy.com/media/11Tz2uB7gJumNW/giphy.gif",
                tagLine = "Forest spirits remind you to breathe & rest."
            )
        )

        fun findById(id: String): AnimeCharacter? {
            return presets.find { it.id.equals(id, ignoreCase = true) }
        }

        fun getOrDefault(id: String, customUrl: String? = null): AnimeCharacter {
            if (id == CUSTOM_ID && !customUrl.isNullOrBlank()) {
                return createCustom(customUrl)
            }
            return findById(id) ?: presets.first()
        }

        fun createCustom(url: String): AnimeCharacter {
            return AnimeCharacter(
                id = CUSTOM_ID,
                name = "Custom Character",
                animeName = "Web Animation",
                animationUrl = url,
                tagLine = "Relax your eyes for 20 seconds!"
            )
        }
    }
}
