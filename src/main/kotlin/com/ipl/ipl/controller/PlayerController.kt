package com.ipl.ipl.controller

import com.ipl.ipl.Service.PlayerService
import com.ipl.ipl.model.Player
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = ["http://localhost:3001"])
@RequestMapping("/api/players")
class PlayerController (
    private val service: PlayerService
) {

    @PostMapping("/create")
    fun createPlayer(@RequestBody player: Player): ResponseEntity<Player> = ResponseEntity.ok(service.createPlayer(player))

    @GetMapping("/get/{id}")
    fun getPlayerById(@PathVariable id: String): Player? = service.getPlayerById(id)

    @GetMapping("/list")
    fun listPlayers(): ResponseEntity<List<Player>> {
        return ResponseEntity.ok(service.listPlayers())
    }

    @PostMapping("/update")
    fun updatePlayer(@RequestBody player: Player): ResponseEntity<Player> = ResponseEntity.ok(service.updatePlayer(player.id!!, player))

    @DeleteMapping("/delete/{id}")
    fun deletePlayer(@PathVariable id: String): ResponseEntity<String> = ResponseEntity.ok(service.deletePlayer(id))
}