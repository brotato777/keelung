package com.example.keelungapi.crawler;

import com.example.keelungapi.models.Sight;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class KeelungSightsCrawler {
    private static final String URL = "https://www.travelking.com.tw";
    private static final String MainPageURL = "https://www.travelking.com.tw/tourguide/taiwan/keelungcity/";
    private static final Logger logger = LoggerFactory.getLogger(KeelungSightsCrawler.class);

    private Sight geSight(String url, String zone) {
        Sight sight = new Sight();
        try {
            Document doc = Jsoup.connect(url).get();
            // System.out.println(doc.html());

            Element sightNameElement = doc.selectFirst("h1.h1");
            if (sightNameElement != null) {
                sight.setSightName(sightNameElement.text());
                // System.out.println("景點名稱: " + sight.getSightName());
            }

            sight.setZone(zone);
            // System.out.println("區域: " + sight.getZone());

            Element categoryElement = doc.selectFirst("span[property=rdfs:label] strong");
            if (categoryElement != null) {
                sight.setCategory(categoryElement.text());
                // System.out.println("類別: " + sight.getCategory());
            }

            Element pMainElement = doc.selectFirst("div.swiper-wrapper");
            boolean hasPhoto = false;
            if (pMainElement != null) {
                Element gpicElement = pMainElement.selectFirst("div.gpic");
                if (gpicElement != null) {
                    Element imgElement = gpicElement.selectFirst("img");
                    if (imgElement != null) {
                        String photoUrl = imgElement.attr("data-src");
                        if (!photoUrl.isEmpty()) {
                            sight.setPhotoURL(photoUrl);
                            hasPhoto = true;
                        }
                    }
                }
            }
            if (!hasPhoto) {
                sight.setPhotoURL("https://thumb.ac-illust.com/0c/0c3c64e631df8b99256cf6de8fa8f12f_t.jpeg");
            }

            Element descElement = doc.selectFirst("div.text[property=dc:description]");
            if (descElement != null) {
                descElement.select(".author").remove();
                sight.setDescription(descElement.text());
                // System.out.println("Description: " + sight.getDescription());
            }

            Element addressElement = doc.selectFirst("span[property=vcard:street-address]");
            if (addressElement != null) {
                sight.setAddress(addressElement.text());
                // System.out.println("地址: " + sight.getAddress());
            }

        } catch (IOException e) {
            logger.error("連線失敗: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("解析景點頁面時發生未知錯誤: {}", e.getMessage(), e);
        }
        return sight;
    }

    public Sight[] getItems(String zone) {
        if (zone == null || zone.trim().isEmpty()) {
            logger.warn("zone 參數為空或 null");
            return new Sight[0];
        }
        zone += "區";
        List<Sight> sightList = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(MainPageURL).get();
            // System.out.println(doc.html());

            Element guideSection = doc.selectFirst("div#guide-point");
            if (guideSection == null) {
                logger.error("找不到 guide-point 區塊！");
                return new Sight[0];
            }

            // System.out.println("Fetching sights for zone: " + zone);

            Elements regionHeaders = guideSection.select("h4");

            for (Element h4 : regionHeaders) {
                String regionName = h4.text();
                if (!regionName.contains(zone)) {
                    continue;
                }
                // System.out.println("區域: " + regionName);

                Element ul = h4.nextElementSibling();
                if (ul != null && ul.tagName().equals("ul")) {
                    Elements lis = ul.select("li");
                    for (Element li : lis) {
                        Element a = li.selectFirst("a");
                        if (a != null) {
                            String link = URL + a.attr("href");
                            Sight sight = geSight(link, zone);
                            sightList.add(sight);
                        }
                    }
                }
            }
            return sightList.toArray(new Sight[0]);

        } catch (IOException e) {
            logger.error("主頁連線失敗: {}", e.getMessage(), e);
            return new Sight[0];
        } catch (Exception e) {
            logger.error("解析主頁時發生未知錯誤: {}", e.getMessage(), e);
            return new Sight[0];
        }
    }
}
