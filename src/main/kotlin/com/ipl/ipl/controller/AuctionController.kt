package com.ipl.ipl.controller

import com.ipl.ipl.Service.AuctionService
import com.ipl.ipl.model.Auction
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
class TeamBudgetExceededException(message: String) : RuntimeException(message)

@RestController
@CrossOrigin
@RequestMapping("/api/auction")
class AuctionController (
    private val auctionService: AuctionService
) {

    @PostMapping("/get/players")
    fun getPlayers(): ResponseEntity<List<Auction>> {
        // Implement logic to fetch players from the auction
        return ResponseEntity.ok(auctionService.getPlayers())
    }

    @PostMapping("/mark/sold")
    fun markPlayerSold(@RequestBody auction: Auction): ResponseEntity<Any> {
        return try {
            auctionService.markPlayerSold(auction)
            ResponseEntity.ok("Player marked as sold.")
        } catch (e: TeamBudgetExceededException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message) // Return 400 for budget exceeded
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error marking player as sold: ${e.message}")
        }
    }

    @PostMapping("/mark/unsold")
    fun markPlayerAsUnsold(@RequestBody auction: Auction): ResponseEntity<String> {
        // Implement logic to mark a player as unsold
        return ResponseEntity.ok(auctionService.markPlayerUnSold(auction))
    }

}