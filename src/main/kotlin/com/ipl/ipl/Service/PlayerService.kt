package com.ipl.ipl.Service

import com.ipl.ipl.Repository.PlayerRepository
import com.ipl.ipl.model.Player
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PlayerService (
    private val repository: PlayerRepository
){
    fun createPlayer(player: Player): Player? = repository.createPlayer(player, UUID.randomUUID().toString())
    fun getPlayerById(id: String): Player? = repository.getPlayerById(id)
    fun listPlayers(): List<Player> {
        return repository.listPlayers()
    }
    fun updatePlayer(id: String, player: Player): Player? = repository.updatePlayer(id, player)
    fun deletePlayer(id: String): String {
        val player = repository.deletePlayer(id)
        return if (player > 0) "Player deleted successfully" else "Player not found"
    }
}