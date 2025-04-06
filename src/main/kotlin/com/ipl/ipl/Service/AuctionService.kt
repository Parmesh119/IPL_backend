package com.ipl.ipl.Service

import com.ipl.ipl.Repository.AuctionRepository
import com.ipl.ipl.Repository.PlayerRepository
import com.ipl.ipl.model.Auction
import org.springframework.stereotype.Service
class TeamBudgetExceededException(message: String) : RuntimeException(message)
@Service
class AuctionService (
    private val auctionRepository: AuctionRepository
) {
    fun getPlayers(): List<Auction> {
        var player = auctionRepository.getPlayerByRandom()
//        auctionRepository.updateStatusToCurrentBid(player.first().playerId)
//        player = auctionRepository.getPlayerByCurrent_Bid()
        return player
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
}