package com.ipl.ipl.model

data class Match(
    val id: String,
    val team1: String,
    val team2: String,
    val date: String,
    val day: String,
    val time: String,
    val venue: String
)

data class MatchList (
    val search: String? = null,
    val page: Int = 1,
    val size: Int = 10,
    val type: String? = "All",
    val ipl_team: String? = null,
)

data class FantasyPointsRequest(
    val points: Map<String, Int>,
    val match_id: String? = null,
    val iplTeam1: String,
    val iplTeam2: String
)

data class FantasyPointsResponse(
    val success: Boolean,
    val message: String? = null
)

data class PlayerFantasyPoints(
    val playerId: Long? = null,
    val playerName: String,
    val points: Int,
    val matchId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class FantasyPointsEntry(
    val id: Long? = null,
    val matchId: String? = null,
    val submissionData: String, // JSON string of the full points map
    val timestamp: Long = System.currentTimeMillis()
)