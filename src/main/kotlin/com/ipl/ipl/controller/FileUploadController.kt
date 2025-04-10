package com.ipl.ipl.controller

import com.ipl.ipl.Service.FileUploadService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@CrossOrigin
@RequestMapping("/api/file")
class FileUploadController (
    private val fileUploadService: FileUploadService
) {
    @PostMapping("/upload")
    fun uploadFile(@RequestBody file: MultipartFile): ResponseEntity<Any> {
        return try {
            val fileType = file.originalFilename?.substringAfterLast(".", "")
            if (fileType != "csv" && fileType != "xlsx") {
                return ResponseEntity.badRequest().body("Please upload CSV or Excel file only")
            }

            val message = fileUploadService.processPlayersFile(file)
            ResponseEntity.ok().body(message)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to upload file: ${e.message}")
        }
    }
}