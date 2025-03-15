package com.github.hkereb.kardamon;

import com.github.hkereb.kardamon.model.JsonObject;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.github.hkereb.kardamon.parser.RecipeParser;
import org.json.JSONObject;

import java.util.*;

@Service
public class KardamonService {
    private static final Logger logger = LoggerFactory.getLogger(KardamonService.class);

    public JSONObject extractRecipe(Document doc) {
        RecipeParser recipeParser = new RecipeParser(doc);
        return recipeParser.parseRecipe();
    }
}
