package com.ipl.ipl.Service

import com.ipl.ipl.Repository.PlayerRepository
import com.ipl.ipl.Repository.TeamRepository
import com.ipl.ipl.model.Team
import com.ipl.ipl.model.Team_DTO
import com.ipl.ipl.model.player_team
import org.springframework.stereotype.Service
import java.lang.IllegalStateException

@Service
class TeamService (
    private val teamRepository: TeamRepository,
    private val playerRepository: PlayerRepository
) {

    fun createTeam(team: Team): Team = teamRepository.createTeam(team)

    fun getTeamDetailsById(id: String): Team_DTO? = teamRepository.getTeamDetailById(id)

    fun listTeams(): List<Team> = teamRepository.listTeams()

    fun updateTeam(id: String, team: Team): Team? = teamRepository.updateTeam(id, team)

    fun deleteTeam(id: String): String {
        val count = playerRepository.countPlayerByTeamId(id)
        var updateStatus = 0
        var team = 0
        if(count > 0){
            updateStatus = playerRepository.updatePlayerSellPriceStatus("Pending", id)
            if(updateStatus > 0) {
                team = teamRepository.deleteTeam(id)
            }
        } else {
            team = teamRepository.deleteTeam(id)
        }
        return if (team > 0) "Team deleted successfully" else "Team not found"
    }

    fun addPlayerToTeam(addPlayerRequest: player_team): Team {
        val team = teamRepository.getTeamById(addPlayerRequest.teamId)
        if (team != null) {
            teamRepository.addPlayerToTeam(addPlayerRequest.playerId, addPlayerRequest.teamId)
            return team
        } else {
            throw Exception("Team not found")
        }
    }

    fun removePlayerFromTeam(playerId: String): String {
        val team = teamRepository.removePlayerFromTeam(playerId)
        return if (team > 0) "Player removed from team" else "Player not found in the team"
    }
}