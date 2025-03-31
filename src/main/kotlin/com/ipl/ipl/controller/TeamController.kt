package com.ipl.ipl.controller

import com.ipl.ipl.Service.TeamService
import com.ipl.ipl.model.Team
import com.ipl.ipl.model.player_team
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("/api/team")
class TeamController (
    private val teamService: TeamService
) {
    // Implement team-related endpoints here
    @PostMapping("/create")
    fun createTeam(@RequestBody team: Team): ResponseEntity<Team> = ResponseEntity.ok(teamService.createTeam(team))

    @GetMapping("/get/{id}")
    fun getTeamById(@PathVariable id: String): Team? = teamService.getTeamById(id)

    @GetMapping("/list")
    fun listTeams(): ResponseEntity<List<Team>> = ResponseEntity.ok(teamService.listTeams())

    @PostMapping("/update")
    fun updateTeam(@RequestBody team: Team): ResponseEntity<Team> = ResponseEntity.ok(teamService.updateTeam(team.id!!, team))

    @DeleteMapping("/delete/{id}")
    fun deleteTeam(@PathVariable id: String): ResponseEntity<String> = ResponseEntity.ok(teamService.deleteTeam(id))

    @PostMapping("/add/player")
    fun addPlayerToTeam(@RequestBody addPlayerRequest: player_team): ResponseEntity<Team> = ResponseEntity.ok(teamService.addPlayerToTeam(addPlayerRequest))

    @DeleteMapping("/remove/player/{playerId}")
    fun removePlayerFromTeam(@PathVariable playerId: String): ResponseEntity<String> {
        // Implement logic to remove a player from a team
        teamService.removePlayerFromTeam(playerId)
        return ResponseEntity.ok("Player removed from team")
    }
}