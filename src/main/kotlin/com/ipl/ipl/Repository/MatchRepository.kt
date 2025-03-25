package com.ipl.ipl.Repository

import com.fasterxml.jackson.databind.JsonNode
import com.ipl.ipl.model.Match
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.web.client.RestTemplate

@Repository
class MatchRepository (
    private val jdbcTemplate: JdbcTemplate
) {
    private val baseUrl = "https://cricbuzz-cricket.p.rapidapi.com/matches/v1"
    private val apiKey = "41f3001a6emsh0110854b5b87aaep1692c5jsn31b8a2ce925f"
    private val apiHost = "cricbuzz-cricket.p.rapidapi.com"
    private val restTemplate = RestTemplate()

    fun getMatches(type: String): List<Match> {
        val url = "$baseUrl/$type" // Append type (recent, upcoming, live) to API URL

        val headers = HttpHeaders().apply {
            set("X-Rapidapi-Key", "41f3001a6emsh0110854b5b87aaep1692c5jsn31b8a2ce925f")
            set("X-Rapidapi-Host", "cricbuzz-cricket.p.rapidapi.com")
        }

        val entity = HttpEntity<String>(headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode::class.java)

        // Extract only the League matches (IPL data)
        val typeMatchesNode = response.body?.get("typeMatches") ?: return emptyList()
        val leagueMatches = typeMatchesNode.find { it["matchType"].asText() == "League" }
            ?.get("seriesMatches") ?: return emptyList()

        return leagueMatches.flatMap { seriesMatch ->
            val seriesName = seriesMatch["seriesAdWrapper"]?.get("seriesName")?.asText() ?: ""

            // **Filter to include only IPL matches, excluding "Asian Legends League 2025"**
            if (!seriesName.contains("Indian Premier League", ignoreCase = true)) return@flatMap emptyList()

            seriesMatch["seriesAdWrapper"]?.get("matches")?.mapNotNull { matchNode ->
                matchNode["matchInfo"]?.let {
                    Match(
                        id = it["matchId"].asText(),
                        team1 = it["team1"]["teamName"].asText(),
                        team2 = it["team2"]["teamName"].asText(),
                        date = it["startDate"].asText(),
                        venue = it["venueInfo"]["ground"].asText()
                    )
                }
            } ?: emptyList()
        }
    }
}