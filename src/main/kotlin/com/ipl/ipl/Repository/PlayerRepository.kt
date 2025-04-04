package com.ipl.ipl.Repository

import com.ipl.ipl.model.Player
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.Instant

@Repository
class PlayerRepository (
    private val jdbcTemplate: JdbcTemplate
) {
    private val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        Player(
            id = rs.getString("id"),
            name = rs.getString("name"),
            country = rs.getString("country"),
            age = rs.getInt("age"),
            role = rs.getString("role"),
            battingStyle = rs.getString("batting_style"),
            bowlingStyle = rs.getString("bowling_style"),
            teamId = rs.getString("team_id"),
            basePrice = rs.getString("baseprice"),
            sellPrice = rs.getString("sellprice"),
            status = rs.getString("status"),
            createdAt = rs.getLong("created_at"),
            updatedAt = rs.getLong("updated_at")
        )
    }

    fun createPlayer(player: Player, id: String): Player? {
        jdbcTemplate.update(
            "INSERT INTO players (id, name, country, age, role, batting_style, bowling_style, team_id, baseprice, sellprice, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            id, player.name, player.country, player.age, player.role, player.battingStyle, player.bowlingStyle, player.teamId, player.basePrice, player.sellPrice, player.status, player.createdAt, player.updatedAt

        )
        return getPlayerById(id)
    }

    fun getPlayerById(id: String): Player? = jdbcTemplate.queryForObject(
        "SELECT * FROM players WHERE id = ?", rowMapper, id
    )

    fun listPlayers(): List<Player> {
        val players = jdbcTemplate.query("SELECT * FROM players", rowMapper)
        return players
    }

    fun updatePlayer(id: String, player: Player): Player? {
        jdbcTemplate.update(
            "UPDATE players SET name = ?, country = ?, age = ?, role = ?, batting_style = ?, bowling_style = ?, team_id = ?, baseprice = ?, sellprice = ?, status = ?, updated_at = ? WHERE id = ?",
            player.name, player.country, player.age, player.role, player.battingStyle, player.bowlingStyle, player.teamId, player.basePrice, player.sellPrice, player.status, Instant.now().toEpochMilli(), id
        )

        return getPlayerById(id)
    }

    fun findTeamByPlayerId(playerId: String): Int {
        return jdbcTemplate.queryForObject(
            "SELECT COUNT(team_id) FROM players WHERE id = ?",
            Int::class.java, playerId
        ) ?: 0
    }

    fun deletePlayer(id: String): Int = jdbcTemplate.update("DELETE FROM players WHERE id = ?", id)
}