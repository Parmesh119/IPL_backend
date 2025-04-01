package com.ipl.ipl.Repository

import com.ipl.ipl.model.Team
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class TeamRepository (
    private val jdbcTemplate: JdbcTemplate
) {

    private val rowMapper = { rs: java.sql.ResultSet, _: Int ->
        Team(
            id = rs.getString("id"),
            name = rs.getString("name"),
            owner = rs.getString("owner"),
            coach = rs.getString("coach"),
            captain = rs.getString("captain"),
            viceCaptain = rs.getString("vice_captain"),
            createdAt = rs.getLong("created_at"),
            updatedAt = rs.getLong("updated_at")
        )
    }

    fun createTeam(team: Team): Team {
        try {
            jdbcTemplate.update(
                "INSERT INTO team (id, name, owner, coach, captain, vice_captain, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                team.id, team.name, team.owner, team.coach, team.captain, team.viceCaptain, team.createdAt, team.updatedAt
            )
            return team
        } catch (e: Exception) {
            throw Exception("Failed to create team")
        }
    }

    fun getTeamById(id: String): Team? {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT * FROM team WHERE id = ?", rowMapper, id
            )
        } catch (e: Exception) {
            println(e.message)
            throw Exception("Failed to get team by id")
        }
    }

    fun listTeams(): List<Team> {
        try {
            return jdbcTemplate.query(
                """
            SELECT 
                t.id, t.name, t.owner, t.coach, t.captain, t.vice_captain, 
                COUNT(p.id) as players, 
                COALESCE(SUM(NULLIF(REGEXP_REPLACE(p.sellprice, '[^0-9.]', '', 'g'), '')::NUMERIC), 0) AS spent_money, 
                t.created_at, t.updated_at 
            FROM team t 
            LEFT JOIN players p ON t.id = p.team_id 
            GROUP BY t.id
            """,
                { rs, _ ->
                    Team(
                        id = rs.getString("id"),
                        name = rs.getString("name"),
                        owner = rs.getString("owner"),
                        coach = rs.getString("coach"),
                        captain = rs.getString("captain"),
                        viceCaptain = rs.getString("vice_captain"),
                        players = rs.getInt("players"),
                        spent = rs.getDouble("spent_money"),
                        createdAt = rs.getLong("created_at"),
                        updatedAt = rs.getLong("updated_at"),
                    )
                }
            )
        } catch (e: Exception) {
            println(e.message)
            e.printStackTrace()
            throw Exception("Failed to list teams")
        }
    }


    fun updateTeam(id: String, team: Team): Team? {
        try {
            jdbcTemplate.update(
                "UPDATE team SET name = ?, owner = ?, coach = ?, captain = ?, vice_captain = ?, updated_at = ? WHERE id = ?",
                team.name, team.owner, team.coach, team.captain, team.viceCaptain, team.updatedAt, id
            )
            return getTeamById(id)
        } catch (e: Exception) {
            throw Exception("Failed to update team")
        }
    }

    fun deleteTeam(id: String): Int {
        try {
            return jdbcTemplate.update("DELETE FROM team WHERE id = ?", id)
        } catch (e: Exception) {
            throw Exception("Failed to delete team")
        }
    }

    fun addPlayerToTeam(playerId: String, teamId: String): Team? {
        return try {
            jdbcTemplate.update(
                "UPDATE players SET team_id = ? WHERE id = ?",
                teamId, playerId
            )
            getTeamById(teamId)
        } catch (e: DataAccessException) {
            throw RuntimeException("Failed to add player to team")
        }
    }

    fun removePlayerFromTeam(playerId: String): Int {
        return try {
            jdbcTemplate.update(
                "UPDATE players SET team_id = NULL WHERE id = ?",
                playerId
            )
        } catch (e: DataAccessException) {
            throw RuntimeException("Failed to remove player from team")
        }
    }

}