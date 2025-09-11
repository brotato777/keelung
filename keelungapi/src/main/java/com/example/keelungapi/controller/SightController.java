package com.example.keelungapi.controller;

import com.example.keelungapi.models.Sight;
import com.example.keelungapi.repository.SightRepository;
import com.example.keelungapi.service.SightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.annotation.PostConstruct;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/SightAPI")
public class SightController {

    private final SightRepository sightRepository;
    private final SightService sightService;
    private static final Logger logger = LoggerFactory.getLogger(SightController.class);

    public SightController(SightRepository sightRepository, SightService sightService) {
        this.sightRepository = sightRepository;
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
        return sightService.getSightsByZone(zone);
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

    @PostMapping
    public ResponseEntity<Void> createSight(@RequestBody Sight sight) {
        sight.setId(null);
        sightRepository.insert(sight);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .build(Map.of("id", sight.getId()));

        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sight> getSight(@PathVariable String id) {
        Optional<Sight> sightOp = sightRepository.findById(id);
        return sightOp.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
