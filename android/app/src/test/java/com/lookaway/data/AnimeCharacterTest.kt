package com.lookaway.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AnimeCharacterTest {

    @Test
    fun `presets contain famous anime characters`() {
        val presets = AnimeCharacter.presets
        assertTrue(presets.size >= 5)

        val characterNames = presets.map { it.name }
        assertTrue(characterNames.contains("Goku"))
        assertTrue(characterNames.contains("Naruto"))
        assertTrue(characterNames.contains("Luffy"))
        assertTrue(characterNames.contains("Anya Forger"))
        assertTrue(characterNames.contains("Pikachu"))
    }

    @Test
    fun `finds preset by id`() {
        val goku = AnimeCharacter.findById("goku")
        assertNotNull(goku)
        assertEquals("Goku", goku?.name)
        assertEquals("Dragon Ball Z", goku?.animeName)
    }

    @Test
    fun `fallback to default character when id is unknown`() {
        val character = AnimeCharacter.getOrDefault("unknown_id")
        assertEquals(AnimeCharacter.DEFAULT_CHARACTER_ID, character.id)
    }

    @Test
    fun `custom character creates valid model from url`() {
        val customUrl = "https://example.com/custom_anime.gif"
        val custom = AnimeCharacter.createCustom(customUrl)
        assertEquals(AnimeCharacter.CUSTOM_ID, custom.id)
        assertEquals(customUrl, custom.animationUrl)
    }
}
