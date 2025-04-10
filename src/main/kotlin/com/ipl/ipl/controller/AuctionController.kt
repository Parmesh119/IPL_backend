package com.ipl.ipl.controller

import com.ipl.ipl.Service.AuctionService
import com.ipl.ipl.model.Auction
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

class TeamBudgetExceededException(message: String) : RuntimeException(message)
class PlayerNotFoundException(message: String) : RuntimeException(message)
class MaximumMinimumPlayersReachedException(message: String) : RuntimeException(message)

@RestController
@CrossOrigin
@RequestMapping("/api/auction")
class AuctionController(
    private val auctionService: AuctionService
) {

    @PostMapping("/get/players")
    fun getPlayers(@RequestHeader authorization: String): ResponseEntity<Any> {
        return try {
            val player = auctionService.getPlayers(authorization)
            ResponseEntity.ok(player)
        } catch (e: PlayerNotFoundException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(mapOf("error" to "Something went wrong"))
        }
    }


    @PostMapping("/mark/sold")
    fun markPlayerSold(@RequestBody auction: Auction): ResponseEntity<Any> {
        return try {
            auctionService.markPlayerSold(auction)
            ResponseEntity.ok("Player marked as sold.")
        } catch (e: TeamBudgetExceededException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        } catch (e: MaximumMinimumPlayersReachedException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error marking player as sold: ${e.message}")
        }
    }

    @PostMapping("/mark/unsold")
    fun markPlayerAsUnsold(@RequestBody auction: Auction): ResponseEntity<String> {
        // Implement logic to mark a player as unsold
        return ResponseEntity.ok(auctionService.markPlayerUnSold(auction))
    }

    @PostMapping("/update/status")
    fun updateStatus(@RequestBody auction: Auction) {
        auctionService.updateStatus(auction.playerId)
    }
}