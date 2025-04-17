package com.ipl.ipl.Repository

import com.ipl.ipl.model.Match
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.io.IOException

@Repository
class MatchRepository (
    private val jdbcTemplate: JdbcTemplate
) {

    private val excelFilePath = "excel/ipl_schedule.xlsx"

    fun readAllMatchesFromExcel(): List<Match> {
        val matches = mutableListOf<Match>()
        val resource = ClassPathResource(excelFilePath)

        if (!resource.exists()) {
            System.err.println("Error: Excel file not found at classpath:$excelFilePath")
            return emptyList()
        }

        try {
            resource.inputStream.use { fis ->
                val workbook = XSSFWorkbook(fis)
                val sheet = workbook.getSheetAt(0)
                val dataFormatter = DataFormatter()

                for (rowIndex in 1..sheet.lastRowNum) {
                    val row = sheet.getRow(rowIndex)
                    if (row != null && row.lastCellNum >= 7) {
                        val id = dataFormatter.formatCellValue(row.getCell(0))
                        val date = dataFormatter.formatCellValue(row.getCell(1))
                        val day = dataFormatter.formatCellValue(row.getCell(2))
                        val time = dataFormatter.formatCellValue(row.getCell(3))
                        val team1 = dataFormatter.formatCellValue(row.getCell(4))
                        val team2 = dataFormatter.formatCellValue(row.getCell(5))
                        val venue = dataFormatter.formatCellValue(row.getCell(6))

                        if (id.isNotBlank() && team1.isNotBlank() && team2.isNotBlank()) {
                            matches.add(
                                Match(
                                    id = id,
                                    team1 = team1,
                                    team2 = team2,
                                    date = date,
                                    day = day,
                                    time = time,
                                    venue = venue
                                )
                            )
                        }
                    }
                }
                workbook.close()
            }
        } catch (e: IOException) {
            System.err.println("Error reading Excel file '$excelFilePath': ${e.message}")
            e.printStackTrace()
            return emptyList()
        } catch (e: Exception) {
            System.err.println("An unexpected error occurred while processing Excel file '$excelFilePath': ${e.message}")
            e.printStackTrace()
            return emptyList()
        }
        return matches
    }

    fun getTeamByPlayerName(name: String, iplTeam: String): String? {
        try {
            val sql = "SELECT team_id FROM players WHERE name = ? AND ipl_team = ?"
            return jdbcTemplate.queryForObject(
                sql,
                arrayOf(name, iplTeam),
                String::class.java
            )
        } catch (e: Exception) {
            return e.message
        }
    }

    fun getPoints(teamId: String?): Int {
        return try {
            val sql = "SELECT SUM(points) FROM team WHERE id = ?"
            jdbcTemplate.queryForObject(sql, arrayOf(teamId), Int::class.java) ?: 0
        } catch (e: Exception) {
            return 0
        }
    }

    fun insertPoints(teamId: String?, point: Int) {
        try {
            val updateSql = "UPDATE team SET points = ? WHERE id = ?"
            jdbcTemplate.update(updateSql, point, teamId)
        } catch (e: Exception) {
            throw Exception("Error while storing points")
        }
    }

}