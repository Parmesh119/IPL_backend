package com.ipl.ipl.Repository

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ipl.ipl.model.Player
import com.ipl.ipl.model.Player_DTO
import com.ipl.ipl.model.Team
import com.ipl.ipl.model.Team_DTO
import org.json.JSONArray
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository


@Repository
class TeamRepository(
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
                team.id,
                team.name,
                team.owner,
                team.coach,
                team.captain,
                team.viceCaptain,
                team.createdAt,
                team.updatedAt
            )
            return team
        } catch (e: Exception) {
            throw Exception("Failed to create team")
        }
    }

    fun getTeamDetailById(id: String): Team_DTO? {
        try {
            return jdbcTemplate.queryForObject(
                """
            WITH PlayerData AS (
                SELECT 
                    p.id AS player_id,
                    p.name AS player_name,
                    p.role,
                    p.sellprice,
                    p.team_id,
                    p.ipl_team,
                    p.status,
                    ROW_NUMBER() OVER (PARTITION BY p.team_id ORDER BY p.created_at) AS sr_no
                FROM players p
                WHERE p.status = 'Sold'
            )
            SELECT 
                t.id AS id, 
                t.name AS name, 
                t.owner AS owner, 
                t.coach AS coach, 
                t.captain AS captain, 
                t.vice_captain AS vice_captain, 
                t.created_at AS created_at, 
                t.updated_at AS updated_at, 
                COUNT(p.player_id) AS players, 
                COALESCE(SUM(p.sellprice), 0) AS spent, 
                COUNT(CASE WHEN p.role = 'Batsman' THEN 1 END) AS batsmen_count, 
                COUNT(CASE WHEN p.role = 'Bowler' THEN 1 END) AS bowlers_count, 
                COUNT(CASE WHEN p.role = 'All-rounder' THEN 1 END) AS all_rounders_count, 
                JSON_AGG(
                    JSON_BUILD_OBJECT(
                        'id', p.player_id,
                        'srNo', p.sr_no,
                        'player', COALESCE(p.player_name, ''),
                        'iplTeam', p.ipl_team,
                        'role', p.role,
                        'price', p.sellprice
                    )
                ) FILTER (WHERE p.player_id IS NOT NULL) AS players_bought
            FROM team t
            LEFT JOIN PlayerData p ON t.id = p.team_id
            WHERE t.id = ?
            GROUP BY t.id;
            """,
                { rs, _ ->
                    Team_DTO(
                        id = rs.getString("id"),
                        name = rs.getString("name"),
                        owner = rs.getString("owner"),
                        coach = rs.getString("coach"),
                        captain = rs.getString("captain"),
                        viceCaptain = rs.getString("vice_captain"),
                        players = rs.getInt("players"),
                        spent = rs.getDouble("spent"),
                        batsmenCount = rs.getInt("batsmen_count"),
                        bowlersCount = rs.getInt("bowlers_count"),
                        allRoundersCount = rs.getInt("all_rounders_count"),
                        createdAt = rs.getLong("created_at"),
                        updatedAt = rs.getLong("updated_at"),
                        playersBought = parsePlayersBought(rs.getString("players_bought"))
                    )
                }, id
            )
        } catch (e: Exception) {
            println(e.message)
            e.printStackTrace()
            throw Exception("Failed to get team by id")
        }
    }


    fun parsePlayersBought(jsonArrayString: String?): List<Player_DTO> {
        if (jsonArrayString.isNullOrEmpty()) {
            return emptyList()
        }

        val objectMapper = jacksonObjectMapper()
        return try {
            val result: List<Player_DTO> = objectMapper.readValue(jsonArrayString)
            result
        } catch (e: Exception) {
            e.printStackTrace()

            try {
                val jsonArray = objectMapper.readTree(jsonArrayString) as ArrayNode
                val playersList = mutableListOf<Player_DTO>()

                for (node in jsonArray) {
                    val player = Player_DTO(
                        id = node.get("id")?.asText() ?: "",
                        srNo = node.get("srNo")?.asInt() ?: 0,
                        player = node.get("player")?.asText() ?: "",
                        iplTeam = node.get("iplTeam")?.asText() ?: "",
                        role = node.get("role")?.asText() ?: "",
                        price = node.get("price")?.let {
                            if (it.isTextual) it.asText().toDoubleOrNull() ?: 0.0
                            else it.asDouble(0.0)
                        } ?: 0.0
                    )
                    playersList.add(player)
                }
//                println("Manual parsing successful: ${playersList.size} players")
                playersList
            } catch (e2: Exception) {
//                println("Manual parsing also failed: ${e2.message}")
                emptyList()
            }
        }
    }


    fun getTeamById(id: String): Team? {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT * FROM team WHERE id = ?",
                rowMapper, id
            )
        } catch (e: Exception) {
            throw Exception("Failed to get team by id")
        }
    }


    fun listTeams(): List<Team> {
        try {
            return jdbcTemplate.query(
                """
            SELECT 
                t.id, 
                t.name, 
                t.owner, 
                t.coach, 
                t.captain, 
                t.vice_captain, 
                COUNT(CASE WHEN p.status = 'Sold' THEN p.id END) AS players, 
                COALESCE(SUM(CASE WHEN p.status = 'Sold' THEN p.sellprice END), 0) AS spent_money, 
                t.created_at, 
                t.updated_at 
            FROM team t 
            LEFT JOIN players p ON t.id = p.team_id 
            GROUP BY t.id;
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
            return team
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