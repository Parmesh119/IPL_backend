package com.ipl.ipl.Service

import com.ipl.ipl.Repository.PlayerRepository
import com.ipl.ipl.model.Player
import com.ipl.ipl.model.PlayerList
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PlayerService (
    private val repository: PlayerRepository
){
    fun createPlayer(player: Player): Player? {
        if (player.sellPrice != null && player.sellPrice >= player.basePrice) {
            player.status = "Sold"
        } else {
            player.status = "Pending"
        }
        return repository.createPlayer(player, UUID.randomUUID().toString())
    }
    fun getPlayerById(id: String): Player? = repository.getPlayerById(id)
    fun listPlayers(playerList: PlayerList): List<Player> {
        return repository.listPlayers(playerList)
    }
    fun updatePlayer(id: String, player: Player): Player? {
        if (player.sellPrice != null && player.sellPrice >= player.basePrice) {
            player.status = "Sold"
        } else {
            player.status = "Pending"
        }
        return repository.updatePlayer(id, player)
    }
    fun deletePlayer(id: String): String {
        val team = repository.findTeamByPlayerId(id)
        if(team == 0) {
            val player = repository.deletePlayer(id)
            return if (player > 0) "Player deleted successfully" else "Player not found"
        } else {
            throw Exception("Player is already a part of a team")
        }
    }
}