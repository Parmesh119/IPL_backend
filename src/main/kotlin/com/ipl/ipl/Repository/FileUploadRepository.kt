package com.ipl.ipl.Repository

import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class FileUploadRepository (
    private val jdbcTemplate: JdbcTemplate
) {
    fun saveAllPlayers(players: List<Map<String, Any?>>): Int {
        var savedCount = 0

        players.forEachIndexed { index, player ->
            try {
                val sql = """
                    INSERT INTO players (
                        id, name, country, role,
                        created_at, updated_at, baseprice, status, ipl_team, image_url
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent()

                jdbcTemplate.update(sql,
                    player["id"],           // text, not null (auto-generated)
                    player["name"],         // character varying(255), not null
                    player["country"],      // character varying(50), not null
                    player["role"],         // character varying(50), not null
                    player["created_at"],   // bigint, nullable (but we set it)
                    player["updated_at"],   // bigint, nullable (but we set it)
                    player["baseprice"],    // double precision, not null, default 0.0
                    player["status"],       // text, nullable (but we set it)
                    player["ipl_team"],      // text, not null, default ''
                    player["image_url"]     // text, not null, default ''
                )
                savedCount++
            } catch (e: DataAccessException) {
                // Log the specific error and the data causing it
                println("Error saving player at index $index: ${e.mostSpecificCause.message}") // More specific message
                println("Failing Player Data: $player")
                // Optionally, collect failing records/errors to return more details
            } catch (e: Exception) {
                // Catch unexpected errors during processing a player map
                println("Unexpected error processing player at index $index: ${e.message}")
                println("Problematic Player Data: $player")
            }
        }

        return savedCount
    }
}