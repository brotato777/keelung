package com.example.keelungapi.service;

import com.example.keelungapi.models.Sight;
import com.example.keelungapi.repository.SightRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
public class SightService {
    private final SightRepository sightRepository;
    private static final Logger logger = LoggerFactory.getLogger(SightService.class);

    public SightService(SightRepository sightRepository) {
        this.sightRepository = sightRepository;
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
}
