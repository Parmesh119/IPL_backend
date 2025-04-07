package com.ipl.ipl.Repository

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
            basePrice = rs.getString("baseprice"),
            role = rs.getString("role"),
            iplTeam = rs.getString("ipl_team"),
            status = rs.getString("status")
        )
    }

    fun getPlayerByRandom(): List<Auction> {
        return jdbcTemplate.query(
            "SELECT * FROM players WHERE status = 'Pending' ORDER BY RANDOM() LIMIT 1",
            rowMapper
        )
    }

    fun updateStatusToCurrentBid(Id: String) {
        jdbcTemplate.update(
            "UPDATE players SET status = 'Current_Bid' WHERE id = ?",
            Id
        )
    }

    fun getPlayerByCurrent_Bid(): List<Auction> {
        try {
            return jdbcTemplate.query(
                "SELECT * FROM players WHERE status = 'Current_Bid'",
                rowMapper
            )
        } catch (e: Exception) {
            return emptyList()
        }
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
            "UPDATE players SET status = 'Unsold', sellprice = null, team_id = null WHERE id = ?",
            auction.playerId
        )
        return "Player unSold successfully"
    }

    fun findSpentFromTeamId(teamId: String): Double {
        val spent = jdbcTemplate.queryForObject(
            """
        SELECT COALESCE(SUM(
            CASE 
                WHEN sellprice ~ '^[0-9]+(\.[0-9]+)? Cr$'  -- Matches valid numbers followed by ' Cr'
                THEN CAST(SPLIT_PART(sellprice, ' ', 1) AS DOUBLE PRECISION) 
                ELSE 0
            END
        ), 0)
        FROM players 
        WHERE team_id = ?
        """,
            Double::class.java,
            teamId
        ) ?: 0.0

        return spent
    }
}