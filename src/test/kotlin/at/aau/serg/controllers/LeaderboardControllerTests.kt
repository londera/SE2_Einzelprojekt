package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as whenever // when is a reserved keyword in Kotlin
import org.junit.jupiter.api.assertThrows
import org.springframework.web.server.ResponseStatusException

class LeaderboardControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: LeaderboardController

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = LeaderboardController(mockedService)
    }

    @Test
    fun test_getLeaderboard_correctScoreSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 15, 10.0)
        val third = GameResult(3, "third", 10, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard(null)

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    @Test
    fun test_getLeaderboard_sameScore_CorrectTimeInSecondsSorting() {
        // timeInSeconds hinzugefügt
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 20, 10.0)
        val third = GameResult(3, "third", 20, 15.0)

        // unsortierte Liste
        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard(null)

        // Überprüfen, ob der Service aufgerufen wurde
        verify(mockedService).getGameResults()
        assertEquals(3, res.size)

        // second ist am schnellsten (10.0), dann third (15.0), dann first (20.0)
        assertEquals(second, res[0])
        assertEquals(third, res[1])
        assertEquals(first, res[2])
    }

    @Test
    fun test_rank_valid() {
        // Erstelle 10 Dummy-Spieler
        val results = (1..10).map { GameResult(it.toLong(), "Player$it", 100 - it, 10.0) }
        whenever(mockedService.getGameResults()).thenReturn(results)

        // Frage nach Platz 5.
        val res = controller.getLeaderboard(5)

        // Erwartet: Platz 2 bis 8 (7 Spieler)
        assertEquals(7, res.size)
        assertEquals("Player2", res[0].playerName)
        assertEquals("Player8", res[6].playerName)
    }

    @Test
    fun test_rank_too_high_400() {
        val result = GameResult(1, "Player", 100, 10.0)
        whenever(mockedService.getGameResults()).thenReturn(listOf(result))

        // Nur 1 Spieler, also ist Platz 5 ungültig -> Error 400
        assertThrows<ResponseStatusException> {
            controller.getLeaderboard(5)
        }
    }

    @Test
    fun test_rank_negative_400() {
        whenever(mockedService.getGameResults()).thenReturn(emptyList())

        // Platz 0 und negative Zahlen ungültig --> Error 400
        assertThrows<ResponseStatusException> {
            controller.getLeaderboard(-1)
        }
    }

}