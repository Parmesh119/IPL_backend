package com.ipl.ipl.Service

import com.ipl.ipl.Repository.AuctionRepository
import com.ipl.ipl.config.JwtUtil
import com.ipl.ipl.model.Auction
import org.springframework.stereotype.Service
class TeamBudgetExceededException(message: String) : RuntimeException(message)
@Service
class AuctionService(
    private val auctionRepository: AuctionRepository,
    private val jwtUtil: JwtUtil
) {
    fun getPlayers(authorization: String): Auction {
        val player = auctionRepository.getPlayerByRandom()
        val token = authorization.substring(7)
        val role = jwtUtil.extractRoles(token)
        val cleanedRole = role[0]
        if(cleanedRole.contains("ADMIN")) {
            if (player != null) {
                auctionRepository.updateStatusToCurrentBid(player.playerId)
            }
        }
        val return_player = auctionRepository.getPlayerByCurrent_Bid()
        return return_player
    }

    fun markPlayerSold(auction: Auction): String {
        try {
            val spent = auctionRepository.findSpentFromTeamId(auction.teamId!!)
            val sanitizedSellPrice = sanitizeSellPrice(auction.sellPrice)

            if (spent + sanitizedSellPrice > 100.0) {
                throw TeamBudgetExceededException("Team budget exceeded!!")
            }

            return auctionRepository.markPlayerSold(auction)
        } catch (e: TeamBudgetExceededException) {
            throw e
        }
    }

    fun sanitizeSellPrice(price: String?): Double {
        return price?.split(" ")?.get(0)?.toDoubleOrNull() ?: 0.0
    }


    fun markPlayerUnSold(auction: Auction): String {
        return auctionRepository.markPlayerUnSold(auction)
    }

    fun updateStatus(playerId: String) {
        return auctionRepository.updateStatus(playerId)
    }
}