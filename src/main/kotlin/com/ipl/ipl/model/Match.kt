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