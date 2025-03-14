package com.github.hkereb.kardamon.parser;

import com.github.hkereb.kardamon.model.JsonObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

@Component
public class RecipeParser {
    private static final Set<String> INSTRUCTION_KEYWORDS = Set.of("sprinkle", "mix", "heat", "stir", "add", "cook", "boil", "preheat");
    //private static final Set<String> INGREDIENT_UNITS

    public String getTitle(Document doc) {
        Element ogTitle = doc.selectFirst("meta[property=og:title]");
        if (ogTitle != null) {
            return ogTitle.attr("content");
        }
        return doc.title();
    }


}
