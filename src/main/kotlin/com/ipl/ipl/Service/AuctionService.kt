package com.ipl.ipl.Service

import com.ipl.ipl.Repository.AuctionRepository
import com.ipl.ipl.Repository.PlayerRepository
import com.ipl.ipl.config.JwtUtil
import com.ipl.ipl.controller.PlayerNotFoundException
import com.ipl.ipl.model.Auction
import com.ipl.ipl.model.Settings
import org.springframework.stereotype.Service

class TeamBudgetExceededException(message: String) : RuntimeException(message)
@Service
class AuctionService(
    private val auctionRepository: AuctionRepository,
    private val jwtUtil: JwtUtil,
    private val playerRepository: PlayerRepository
) {
    var maxPlayers: Int = 23
    var minPlayers: Int = 15
    var budgetLimit: Int = 100
    var maxTeam: Int = 1
    val settings = mutableMapOf<String, Any>(
        "maxPlayers" to maxPlayers,
        "minPlayers" to minPlayers,
        "budgetLimit" to budgetLimit,
        "maxTeam" to maxTeam
    )

    fun getPlayers(authorization: String): Auction {
        try {
            val currentBidPlayer = auctionRepository.getCountCurrentBid()
            if(currentBidPlayer > 0) {
                if(currentBidPlayer != 1) {
                    val onePlayer = auctionRepository.updatePlayerStatuses()
                    return onePlayer ?: throw PlayerNotFoundException("No player found with status 'Current_Bid'")
                }
            } else {
                val token = authorization.substring(7)
                val role = jwtUtil.extractRoles(token)
                val cleanedRole = role[0]

                if (cleanedRole.contains("ADMIN")) {
                    val player = auctionRepository.getPlayerByRandom()
                    auctionRepository.updateStatusToCurrentBid(player.playerId)
                }
            }

        } catch (e: PlayerNotFoundException) {
            throw PlayerNotFoundException("No more players left to auction.")
        }

        return try {
            auctionRepository.getPlayerByCurrent_Bid()
        } catch (e: PlayerNotFoundException) {
            throw PlayerNotFoundException("No player is currently in bidding.")
        }
    }


    fun markPlayerSold(auction: Auction): String {
        try {
            val spent = auctionRepository.findSpentFromTeamId(auction.teamId!!)
            val sanitizedSellPrice = sanitizeSellPrice(auction.sellPrice)

            if (spent + sanitizedSellPrice > settings["budgetLimit"] as Int) {
                throw TeamBudgetExceededException("Team budget exceeded!!")
            }

            val totalPlayer = playerRepository.countPlayerByTeamId(auction.teamId)
            if(totalPlayer >= settings["maxPlayers"] as Int) {
                throw TeamBudgetExceededException("Team has reached the maximum number of players.")
            }
            return auctionRepository.markPlayerSold(auction)
        } catch (e: TeamBudgetExceededException) {
            throw e
        }
    }

    fun sanitizeSellPrice(price: Double?): Double {
        return price ?: 0.0
    }


    fun markPlayerUnSold(auction: Auction): String {
        return auctionRepository.markPlayerUnSold(auction)
    }

    fun updateStatus(playerId: String) {
        return auctionRepository.updateStatus(playerId)
    }

    fun updateSettings(newSettings: Settings): Settings {
        settings.clear()
        settings["maxPlayers"] = newSettings.maxPlayers
        settings["minPlayers"] = newSettings.minPlayers
        settings["budgetLimit"] = newSettings.budgetLimit
        settings["maxTeam"] = newSettings.maxTeam
        return Settings(
            maxPlayers = settings["maxPlayers"] as Int,
            minPlayers = settings["minPlayers"] as Int,
            budgetLimit = settings["budgetLimit"] as Int,
            maxTeam = settings["maxTeam"] as Int
        )
    }

    fun getSettings(): Settings {
        return Settings(
            maxPlayers = settings["maxPlayers"] as Int,
            minPlayers = settings["minPlayers"] as Int,
            budgetLimit = settings["budgetLimit"] as Int,
            maxTeam = settings["maxTeam"] as Int
        )
    }
}