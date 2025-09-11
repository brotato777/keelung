package com.example.keelungapi.controller;

import com.example.keelungapi.models.Sight;
import com.example.keelungapi.service.SightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/SightAPI")
public class SightController {

    private final SightService sightService;
    private static final Logger logger = LoggerFactory.getLogger(SightController.class);

    public SightController(SightService sightService) {
        this.sightService = sightService;
    }

    @PostConstruct
    public void initializeDatabase() {
        logger.info("Initializing KeelungSights Crawler");
        sightService.crawlAllSights();
    }

    @GetMapping
    public List<Sight> getSightsByWeb(@RequestParam String zone) {
        logger.info("從網路爬取 {} 區的景點", zone);
        return sightService.getSightsByWeb(zone);
    }

    @GetMapping("/db")
    public List<Sight> getSightsFromDB(@RequestParam String zone) {
        logger.info("從資料庫查詢 {} 區的景點", zone);
        return sightService.getSightsByZone(zone);
    }

    @PostMapping("/crawl-all")
    public ResponseEntity<String> crawlAllSights() {
        logger.info("Crawling all Sights");
        sightService.crawlAllSights();
        return ResponseEntity.ok("Crawling all Sights completed");
    }
}
