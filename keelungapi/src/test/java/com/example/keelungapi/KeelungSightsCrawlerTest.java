package com.example.keelungapi;

import com.example.keelungapi.crawler.KeelungSightsCrawler;
import com.example.keelungapi.models.Sight;
import org.junit.jupiter.api.Test;

public class KeelungSightsCrawlerTest {
    @Test
    public void testGetItems() {
        KeelungSightsCrawler crawler = new KeelungSightsCrawler();
        Sight[] sights = crawler.getItems("七堵");
        for (Sight s : sights) {
            System.out.println(s);
        }
    }
}

