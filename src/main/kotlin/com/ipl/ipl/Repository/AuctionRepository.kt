package com.ipl.ipl.Repository

import com.ipl.ipl.controller.PlayerNotFoundException
import com.ipl.ipl.model.Auction
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class AuctionRepository (private val jdbcTemplate: JdbcTemplate) {
    private val rowMapper = { rs: java.sql.ResultSet, _: Int ->
        Auction(
            playerId = rs.getString("id"),
            name = rs.getString("name"),
            country = rs.getString("country"),
            basePrice = rs.getDouble("baseprice"),
            role = rs.getString("role"),
            iplTeam = rs.getString("ipl_team"),
            status = rs.getString("status")
        )
    }

    fun getPlayerByRandom(): Auction {
        val sql = """
        SELECT * FROM players 
        WHERE status = 'Pending' AND team_id IS NULL 
        ORDER BY RANDOM() 
        LIMIT 1
    """.trimIndent()

        val result = jdbcTemplate.query(sql, rowMapper)
        return result.firstOrNull() ?: throw PlayerNotFoundException("No player found with status 'Pending'")
    }


    fun updateStatusToCurrentBid(Id: String) {
        jdbcTemplate.update(
            "UPDATE players SET status = 'Current_Bid' WHERE id = ?",
            Id
        )
    }


    fun getPlayerByCurrent_Bid(): Auction {
        val result = jdbcTemplate.query(
            "SELECT * FROM players WHERE status = 'Current_Bid' ORDER BY RANDOM() LIMIT 1",
            rowMapper
        )
        return result.firstOrNull() ?: throw PlayerNotFoundException("No player found with status 'Current_Bid'")
    }


    fun markPlayerSold(auction: Auction): String {
        jdbcTemplate.update(
            "UPDATE players SET status = 'Sold', sellprice = ?, team_id = ? WHERE id = ?",
            auction.sellPrice,
            auction.teamId,
            auction.playerId
        )
        return "Player sold successfully"
    }

    fun markPlayerUnSold(auction: Auction): String {
        jdbcTemplate.update(
            "UPDATE players SET status = 'Unsold', sellprice = 0.0, team_id = null WHERE id = ?",
            auction.playerId
        )
        return "Player unSold successfully"
    }

    fun findSpentFromTeamId(teamId: String): Double {
        val spent = jdbcTemplate.queryForObject(
            """
        SELECT COALESCE(SUM(sellprice), 0)
        FROM players 
        WHERE team_id = ?
        """,
            Double::class.java,
            teamId
        ) ?: 0.0

        return spent
    }


    fun updateStatus(playerId: String) {
        jdbcTemplate.update(
            "UPDATE players SET status = 'Pending' WHERE id = ?",
            playerId
        )
    }
}