package com.example.keelungapi.service;

import com.example.keelungapi.crawler.KeelungSightsCrawler;
import com.example.keelungapi.models.Sight;
import com.example.keelungapi.repository.SightRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class SightService {
    private final SightRepository sightRepository;
    private final KeelungSightsCrawler crawler;
    private static final Logger logger = LoggerFactory.getLogger(SightService.class);
    private static final String[] ZONES = {"中正", "仁愛", "信義", "中山", "安樂", "暖暖", "七堵"};

    public SightService(SightRepository sightRepository, KeelungSightsCrawler crawler) {
        this.sightRepository = sightRepository;
        this.crawler = crawler;
    }

    public List<Sight> getSightsByZone(String zone) {
        if (zone == null || zone.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "區域名稱不能為空");
        }

        if (!zone.endsWith("區")) {
            zone += "區";
        }

        List<Sight> sights = sightRepository.findByZone(zone);
        if (sights.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "查無此區域資料");
        }

        logger.info("從資料庫查詢到 {} 個景點 ({})", sights.size(), zone);
        return sights;
    }

    public void crawlAllSights() {
        sightRepository.deleteAll();
        logger.info("已清空資料庫舊有景點資料");
        logger.info("開始爬取所有景點資料");

        for (String zone : ZONES) {
            try {
                Sight[] sights = crawler.getItems(zone);

                for (Sight sight : sights) {
                    sight.setId(null);
                    sightRepository.save(sight);
                    logger.info("已保存景點: {} ({})", sight.getSightName(), zone);
                }

                logger.info("完成 {} 區的爬取並保存，共 {} 個景點", zone, sights.length);
            } catch (Exception e) {
                logger.error("初始化爬取 {} 區景點時發生錯誤: {}", zone, e.getMessage());
            }
        }
    }

    public List<Sight> getSightsByWeb(@RequestParam String zone) {
        Sight[] sights = crawler.getItems(zone);
        logger.info("從網站爬取資料 zone={}, sights={}", zone, java.util.Arrays.toString(sights));
        if (sights.length == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "查無資料");
        }
        return List.of(sights);
    }
}
