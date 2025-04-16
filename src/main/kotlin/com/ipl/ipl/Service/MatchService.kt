package com.ipl.ipl.Service
import com.ipl.ipl.model.Match
import com.ipl.ipl.model.MatchList
import com.ipl.ipl.Repository.MatchRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import kotlin.math.min

@Service
class MatchService(private val matchRepository: MatchRepository) {

    private val excelDateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yy", Locale.ENGLISH)

    fun getMatches(request: MatchList): List<Match> {
        val allMatches: List<Match> = matchRepository.readAllMatchesFromExcel()

        val filteredMatches = if (!request.search.isNullOrBlank()) {
            allMatches.filter { match ->
                match.team1.contains(request.search, ignoreCase = true) ||
                        match.team2.contains(request.search, ignoreCase = true) ||
                        match.venue.contains(request.search, ignoreCase = true) ||
                        match.day.contains(request.search, ignoreCase = true) ||
                        match.date.contains(request.search, ignoreCase = true) // Keep basic string search on date too
            }
        } else {
            allMatches
        }

        // Apply filtering based on 'type'
        val typedMatches = filterByType(filteredMatches, request.type ?: "All")

        // Apply Pagination
        val pageIndex = if (request.page >= 1) request.page - 1 else 0
        val startIndex = pageIndex * request.size

        if (startIndex >= typedMatches.size) {
            return emptyList()
        }

        val endIndex = min(startIndex + request.size, typedMatches.size)

        return typedMatches.subList(startIndex, endIndex)
    }

    private fun filterByType(matches: List<Match>, type: String): List<Match> {
        // Get current date details only if needed
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)
        val yesterday = today.minusDays(1)

        return when (type.lowercase()) { // Use lowercase for case-insensitive comparison
            "today" -> matches.filter { match ->
                parseAndCompareDate(match.date, today)
            }
            "tomorrow" -> matches.filter { match ->
                parseAndCompareDate(match.date, tomorrow)
            }
            "yesterday" -> matches.filter { match ->
                parseAndCompareDate(match.date, yesterday)
            }
            "all" -> matches // No type-specific filtering needed
            else -> {
                // If type is not a special date keyword or "all",
                // assume it's filtering by the 'Day' column (e.g., "Sunday")
                matches.filter { match ->
                    match.day.equals(type, ignoreCase = true)
                }
            }
        }
    }

    // Helper function to parse date string from Excel and compare with target date
    private fun parseAndCompareDate(dateString: String, targetDate: LocalDate): Boolean {
        return try {
            val matchDate = LocalDate.parse(dateString, excelDateFormatter)
            matchDate == targetDate
        } catch (e: DateTimeParseException) {
            // Log the error if needed, but treat unparseable dates as non-matching
            System.err.println("Could not parse date string: '$dateString'. Error: ${e.message}")
            false
        } catch (e: Exception) {
            // Catch any other unexpected errors during parsing/comparison
            System.err.println("Unexpected error parsing or comparing date '$dateString': ${e.message}")
            false
        }
    }
}