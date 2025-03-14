package com.github.hkereb.kardamon;

import com.github.hkereb.kardamon.model.JsonObject;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.github.hkereb.kardamon.parser.RecipeParser;

import java.util.*;

@Service
public class KardamonService {
    private static final Logger logger = LoggerFactory.getLogger(KardamonService.class);
    private final RecipeParser recipeParser;

    public KardamonService(RecipeParser recipeParser) {
        this.recipeParser = recipeParser;
    }

    public String extractRecipe(Document doc) {
        return recipeParser.getTitle(doc);
    }
}
