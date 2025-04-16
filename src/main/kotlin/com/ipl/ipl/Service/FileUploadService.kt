package com.ipl.ipl.Service

import com.ipl.ipl.Repository.FileUploadRepository
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.math.floor // Import floor for better double to string conversion if needed

@Service
class FileUploadService (
    private val fileUploadRepository: FileUploadRepository
) {
    fun processPlayersFile(file: MultipartFile): String {
        val players = mutableListOf<Map<String, Any?>>()

        val filename = file.originalFilename ?: ""
        when {
            filename.endsWith(".csv") -> players.addAll(parseCSV(file))
            filename.endsWith(".xlsx") -> players.addAll(parseExcel(file))
            else -> throw IllegalArgumentException("Invalid file format. Only CSV and Excel files are supported.") // More specific exception
        }

        val enhancedPlayers = players.mapNotNull { player -> // Use mapNotNull to potentially filter out invalid rows later if needed
            try {
                enhancePlayerData(player)
            } catch (e: Exception) {
                println("Skipping player due to error during enhancement: ${e.message} - Data: $player")
                null // Skip this player if enhancement fails (e.g., validation error)
            }
        }

        if (enhancedPlayers.isEmpty() && players.isNotEmpty()) {
            return "File processed, but no valid player data found or all rows failed enhancement."
        }

        val savedCount = fileUploadRepository.saveAllPlayers(enhancedPlayers)
        val totalProcessed = players.size
        val failedCount = totalProcessed - savedCount
        return "Successfully uploaded $savedCount player records. Failed to save $failedCount records (check logs for details)."
    }

    private fun enhancePlayerData(player: Map<String, Any?>): Map<String, Any?> {
        val currentTime = System.currentTimeMillis()

        // Retrieve parsed values, respecting nulls from parsing
        val name = player["name"] as? String
        val country = player["country"] as? String
        val role = player["role"] as? String
        val baseprice = player["baseprice"] as? Double
        val iplTeam = player["ipl_team"] as? String // Retrieve ipl_team if it's in the file
        val image_url = player["image_url"] as? String

        // Apply database rules: NOT NULL constraints must be met by input or defaults
        // If a required field (NOT NULL, no default) is null here, the DB insert will fail, which is correct.

        return mapOf(
            "id" to UUID.randomUUID().toString(), // Default: Auto-generated
            "name" to name, // Required (NOT NULL) - null will cause DB error if input missing
            "country" to country, // Required (NOT NULL) - null will cause DB error if input missing
            "role" to role, // Required (NOT NULL) - null will cause DB error if input missing
            "created_at" to currentTime, // Nullable, but we always set it
            "updated_at" to currentTime, // Nullable, but we always set it
            "baseprice" to (baseprice ?: 0.0), // NOT NULL, Default: 0.0
            "status" to "Pending", // Nullable, but we set a default
            "ipl_team" to (iplTeam ?: ""), // NOT NULL, Default: ''
            "image_url" to (image_url ?: "https://static-00.iconduck.com/assets.00/profile-circle-icon-512x512-zxne30hp.png"), // NOT NULL, Default: ''
        )
    }

    private fun parseCSV(file: MultipartFile): List<Map<String, Any?>> {
        val players = mutableListOf<Map<String, Any?>>()
        BufferedReader(InputStreamReader(file.inputStream)).use { reader ->
            val header = reader.readLine()?.split(",")?.map { it.trim() }
            if (header == null) return emptyList() // Handle empty file

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line.isNullOrBlank()) continue // Skip empty lines
                val data = line!!.split(",").map { it.trim() } // Trim whitespace from each value

                // Basic check for expected number of columns based on header
                // This doesn't guarantee correctness but prevents IndexOutOfBounds
                if (data.size >= header.size) { // Or a fixed minimum number if header not used strictly
                    try {
                        val player = createPlayerFromCSVData(data, header)
                        players.add(player)
                    } catch (e: Exception) {
                        println("Skipping CSV row due to parsing error: ${e.message} - Row data: $data")
                    }
                } else {
                    println("Skipping CSV row due to insufficient columns. Expected ${header.size}, got ${data.size}. Row data: $data")
                }
            }
        }
        return players
    }

    // Helper function for CSV parsing based on header names
    private fun createPlayerFromCSVData(data: List<String>, header: List<String>): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        for ((index, headerName) in header.withIndex()) {
            val value = data.getOrNull(index)?.takeIf { it.isNotBlank() } // Get value, treat blank as null
            map[headerName] = when (headerName) {
                "age" -> value?.toIntOrNull()
                "baseprice" -> value?.toDoubleOrNull()
                // Add other type conversions if necessary
                else -> value // Keep as string or null
            }
        }
        // Explicitly map parsed values to expected keys, handling potential discrepancies
        // between CSV header and internal keys if necessary. This example assumes
        // CSV headers match the keys used in enhancePlayerData directly.
        // If they differ, add mapping logic here. E.g., if CSV has "Player Name", map it to "name".
        return mapOf(
            "name" to map["name"],
            "country" to map["country"],
            "role" to map["role"],
            "baseprice" to map["baseprice"],
            "team_id" to map["team_id"],
            "ipl_team" to map["ipl_team"],
            "image_url" to map["image_url"],
        )
    }


    private fun parseExcel(file: MultipartFile): List<Map<String, Any?>> {
        val players = mutableListOf<Map<String, Any?>>()
        val headers = mutableListOf<String>()

        XSSFWorkbook(file.inputStream).use { workbook ->
            val sheet = workbook.getSheetAt(0)
            val rowIterator = sheet.iterator()

            // Read header row
            if (rowIterator.hasNext()) {
                val headerRow = rowIterator.next()
                headerRow.forEach { cell ->
                    headers.add(getCellValueAsString(cell)?.lowercase(Locale.getDefault())?.replace(" ", "_") ?: "unknown_${cell.columnIndex}")
                }
            } else {
                return emptyList() // No data
            }


            while (rowIterator.hasNext()) {
                val currentRow = rowIterator.next()
                // Check if row is completely empty - adjust threshold as needed
                if (isRowEmpty(currentRow, headers.size)) {
                    continue
                }
                try {
                    val player = createPlayerFromExcelRow(currentRow, headers)
                    players.add(player)
                } catch (e: Exception) {
                    println("Skipping Excel row ${currentRow.rowNum + 1} due to parsing error: ${e.message}")
                }
            }
        }
        return players
    }

    // Helper to check if an Excel row is effectively empty
    private fun isRowEmpty(row: Row?, columnCount: Int): Boolean {
        if (row == null) {
            return true
        }
        for (cn in 0 until columnCount) {
            val cell = row.getCell(cn, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)
            if (cell != null && cell.cellType != CellType.BLANK) {
                if (cell.cellType == CellType.STRING && cell.stringCellValue?.isNotBlank() == true) return false
                if (cell.cellType != CellType.STRING) return false // Consider any non-blank, non-string cell as non-empty
            }
        }
        return true
    }


    private fun createPlayerFromExcelRow(row: Row, headers: List<String>): Map<String, Any?> {
        val playerMap = mutableMapOf<String, Any?>()
        headers.forEachIndexed { index, headerName ->
            // Use MissingCellPolicy.RETURN_BLANK_AS_NULL to differentiate empty and null cells if needed by POI version
            val cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)
            val rawValue = getCellValueAsString(cell) // Returns String?
            val value = rawValue?.takeIf { it.isNotBlank() } // Treat blank strings as null

            playerMap[headerName] = when (headerName) {
                "age" -> value?.toIntOrNull()
                "baseprice" -> value?.toDoubleOrNull()
                // Add specific conversions for other columns if needed (e.g., dates, specific numeric types)
                else -> value // Keep as String or null
            }
        }
        // Similar to CSV, map the parsed values based on headers to the expected keys
        return mapOf(
            "name" to playerMap["name"],
            "country" to playerMap["country"],
            "role" to playerMap["role"],
            "baseprice" to playerMap["baseprice"],
            "team_id" to playerMap["team_id"],
            "ipl_team" to playerMap["ipl_team"], // Include if ipl_team is in your Excel
            "image_url" to playerMap["image_url"], // Include if image_url is in your Excel
        )
    }

    // Updated to return String? (nullable String)
    private fun getCellValueAsString(cell: Cell?): String? {
        if (cell == null || cell.cellType == CellType.BLANK) {
            return null // Treat blank cells as null
        }

        return when (cell.cellType) {
            CellType.STRING -> cell.stringCellValue.trim().let { if (it.isEmpty()) null else it }
            CellType.NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Handle date formatting if necessary, otherwise return as string/timestamp
                    cell.dateCellValue?.toString() // Or format as needed
                } else {
                    // Handle numeric: Check if it's an integer
                    val numericValue = cell.numericCellValue
                    if (numericValue == floor(numericValue)) {
                        // It's an integer value
                        numericValue.toLong().toString()
                    } else {
                        // It's a double/decimal value
                        numericValue.toString()
                    }
                }
            }
            CellType.BOOLEAN -> cell.booleanCellValue.toString()
            CellType.FORMULA -> try { // Evaluate formula if possible
                cell.stringCellValue.trim().let { if (it.isEmpty()) null else it } // Get cached value
            } catch (e: Exception) {
                println("Could not evaluate formula in cell ${cell.address}: ${e.message}")
                null // Cannot evaluate
            }
            else -> null // Treat other types (ERROR, _NONE) as null
        }
    }
}