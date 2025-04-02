package com.github.hkereb.kardamon;

import com.github.hkereb.kardamon.model.RequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class KardamonController {
    private static final Logger logger = LoggerFactory.getLogger(KardamonController.class);
    private final KardamonService kardamonService;

    public KardamonController(KardamonService kardamonService) {
        this.kardamonService = kardamonService;
    }

    @PostMapping("/scrapeRecipe")
    public ResponseEntity<String> scrapeRecipe(@RequestBody RequestDto inputUrl, HttpServletRequest request) {
        String url = inputUrl.url();

        logger.info("handled request at {}", request.getRequestURL().toString());
        logger.debug("request body at scrapeRecipe: {}", url);

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();
            return ResponseEntity.ok(kardamonService.extractRecipe(doc).put("url", url).toString());
        } catch (IOException e) {
            logger.error("cant fetch page: {}: {}", url, e.getMessage());
            return ResponseEntity.badRequest().body("cant fetch the page");
        }
    }
}
