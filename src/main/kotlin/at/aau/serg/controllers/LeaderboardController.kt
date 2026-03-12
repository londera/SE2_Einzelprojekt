package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val gameResultService: GameResultService
) {

    @GetMapping
    fun getLeaderboard(@RequestParam(required = false) rank: Int?): List<GameResult> {
        // Liste holen und sortieren
        val sorted = gameResultService.getGameResults()
            .sortedWith(compareByDescending<GameResult> { it.score }.thenBy { it.timeInSeconds })

        // Wenn kein Rank --> alles zurückgeben
        if (rank == null) return sorted

        // Listen-Index berechnen (Platz 1 ist Index 0)
        val index = rank - 1

        // Fehler, falls der Rank ungültig (zu groß oder negativ) ist
        if (index < 0 || index >= sorted.size) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Ungültiger Rang")
        }

        // Die 3 Plätze davor und danach berechnen
        // maxOf verhindert negative Indizes, minOf verhindert OutOfBounds am Listenende
        val start = maxOf(0, index - 3)
        val end = minOf(sorted.size, index + 4) // +4, weil das Ende bei subList exklusiv (NICHT inklusiv!) ist

        return sorted.subList(start, end)
    }
}