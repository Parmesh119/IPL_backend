package com.ipl.ipl.controller

import com.ipl.ipl.Service.MatchService
import com.ipl.ipl.model.Match
import com.ipl.ipl.model.MatchList
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
@RequestMapping("/api/ipl")
class MatchController (
    private val matchService: MatchService
) {
    @GetMapping("/matches/list")
    fun getMatches(@RequestBody request: MatchList): ResponseEntity<List<Match>> {
        return ResponseEntity.ok(matchService.getMatches(request.type!!))
    }
}