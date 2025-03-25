package com.ipl.ipl.Service

import com.ipl.ipl.Repository.TeamRepository
import com.ipl.ipl.model.Team
import org.springframework.stereotype.Service

@Service
class TeamService (
    private val teamRepository: TeamRepository
) {

    fun createTeam(team: Team): Team = teamRepository.createTeam(team)

    fun getTeamById(id: String): Team? = teamRepository.getTeamById(id)

    fun listTeams(): List<Team> = teamRepository.listTeams()

    fun updateTeam(id: String, team: Team): Team? = teamRepository.updateTeam(id, team)

    fun deleteTeam(id: String): String {
        val team = teamRepository.deleteTeam(id)
        return if (team > 0) "Team deleted successfully" else "Team not found"
    }
}