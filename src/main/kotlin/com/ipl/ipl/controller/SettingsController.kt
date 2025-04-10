package com.ipl.ipl.controller

import com.ipl.ipl.Service.AuctionService
import com.ipl.ipl.model.Settings
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
@RequestMapping("/api/settings")
class SettingsController (
    private val auctionService: AuctionService
) {
    @PostMapping("/update")
    fun updateSettings(@RequestBody setting: Settings): ResponseEntity<Settings> {
        return ResponseEntity.ok(auctionService.updateSettings(setting))
    }

    @GetMapping("/get")
    fun getSettings(): ResponseEntity<Settings> {
        return ResponseEntity.ok(auctionService.getSettings())
    }
}