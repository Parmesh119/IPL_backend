package com.ipl.ipl.controller

import com.ipl.ipl.model.Match
import com.ipl.ipl.model.MatchList
import com.ipl.ipl.Service.MatchService
import com.ipl.ipl.model.FantasyPointsRequest
import com.ipl.ipl.model.FantasyPointsResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/api/ipl")
class MatchController (
    private val matchService: MatchService
) {
    @PostMapping("/matches/list")
    fun getMatches(
        @RequestBody request: MatchList
    ): ResponseEntity<List<Match>> {
        val matches = matchService.getMatches(request)
        return ResponseEntity.ok(matches)
    }

    @PostMapping("/matches/fantacy-points")
    fun addFantacyPoints(@RequestBody request: FantasyPointsRequest): FantasyPointsResponse {
        return matchService.savePoints(request)

    }
}