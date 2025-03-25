package com.ipl.ipl.Service

import com.ipl.ipl.Repository.MatchRepository
import com.ipl.ipl.model.Match
import com.ipl.ipl.model.MatchList
import org.springframework.stereotype.Service

@Service
class MatchService (
    private val matchRepository: MatchRepository
) {
    fun getMatches(type: String): List<Match> = matchRepository.getMatches(type)
}