package com.ipl.ipl.model

data class Match(
    val id: String,
    val team1: String,
    val team2: String,
    val date: String,
    val venue: String
)

data class MatchList (
    val type: String? = "recent"
)