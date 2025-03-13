package com.github.hkereb.kardamon;

import com.github.hkereb.kardamon.model.UrlDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class KardamonController {
    private static final Logger logger = LoggerFactory.getLogger(KardamonController.class);

    public KardamonController(KardamonService kardamonService) {
    }

    @PostMapping("/fetchHTML")
    public ResponseEntity<String> fetchHTML (@RequestBody UrlDto inputUrl, HttpServletRequest request) {
        String url = inputUrl.url();

        logger.info("handled request at {}", request.getRequestURL().toString());
        logger.debug("request body at fetchHTML: {}", url);

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();
            return ResponseEntity.ok(doc.html());
        } catch (IOException e) {
            logger.error("cant fetch page: {}: {}", url, e.getMessage());
            return ResponseEntity.badRequest().body("error occurred when fetching the page");
        }
    }
}
