package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GameResultControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: GameResultController

    @BeforeEach
    fun setup() {
        mockedService = mock(GameResultService::class.java)
        controller = GameResultController(mockedService)
    }

    @Test
    fun test_getAllGameResults() {
        val result = GameResult(1, "Player", 100, 10.0)
        `when`(mockedService.getGameResults()).thenReturn(listOf(result))

        val res = controller.getAllGameResults()

        verify(mockedService).getGameResults()
        assertEquals(1, res.size)
        assertEquals(result, res[0])
    }

    @Test
    fun test_getGameResult_existingId() {
        val result = GameResult(1, "Player", 100, 10.0)
        `when`(mockedService.getGameResult(1)).thenReturn(result)

        val res = controller.getGameResult(1)

        verify(mockedService).getGameResult(1)
        assertEquals(result, res)
    }

    @Test
    fun test_getGameResult_nonexistentId() {
        `when`(mockedService.getGameResult(99)).thenReturn(null)

        val res = controller.getGameResult(99)

        verify(mockedService).getGameResult(99)
        assertNull(res)
    }

    @Test
    fun test_addGameResult() {
        val result = GameResult(1, "Player", 100, 10.0)

        controller.addGameResult(result)

        verify(mockedService).addGameResult(result)
    }

    @Test
    fun test_deleteGameResult() {
        controller.deleteGameResult(1)

        verify(mockedService).deleteGameResult(1)
    }
}