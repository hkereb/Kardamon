package com.github.hkereb.kardamon.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONObject;
import java.util.*;
import java.util.regex.Pattern;

public class RecipeParser {
    private final Document doc;

    public RecipeParser(Document doc) {
        this.doc = doc;
    }

    private static final Pattern SERVINGS_PATTERN = Pattern.compile("(?i)(makes|serves|servings|yield|liczba\\s+porcji|porcje|porcja|porcji|os\\.)\\s*:?\\\\s*(\\d+)\n");

    public JSONObject parseRecipe() {
        JSONObject recipeJson = new JSONObject();

        recipeJson.put("title", getTitle());
        recipeJson.put("description", getDescription());
        recipeJson.put("ingredients", getIngredients());

        return recipeJson;
    }

    private String getTitle() {
        Element ogTitle = doc.selectFirst("meta[property=og:title]");
        if (ogTitle != null) {
            return ogTitle.attr("content");
        }
        return doc.title();
    }

    private String getDescription() {
        Element ogDescription = doc.selectFirst("meta[property=og:description]");
        if (ogDescription != null) {
            return ogDescription.attr("content");
        }
        Element description = doc.selectFirst("meta[name=description]");
        if (description != null) {
            return description.attr("content");
        }
        return "";
    }

    private String getServings() {
        for (Element element : doc.getAllElements()) {
            String text = element.text().trim();
            var matcher = SERVINGS_PATTERN.matcher(text);
            if (matcher.find()) {
                return matcher.group(2);
            }
        }
        return "";
    }

    private List<String> getIngredients() {
        List<String> ingredients = new ArrayList<>();

        // JSON-LD
        Element jsonLdScript = doc.selectFirst("script[type='application/ld+json']");
        if (jsonLdScript != null) {
            String jsonContent = jsonLdScript.html();
            try {
                JSONObject jsonObject = new JSONObject(jsonContent);
                if (jsonObject.has("recipeIngredient")) {
                    for (Object ingredient : jsonObject.getJSONArray("recipeIngredient")) {
                        ingredients.add((String) ingredient);
                    }
                    return ingredients;
                }
            } catch (Exception e) {
                //
            }
        }

        // alternative - microdata
        Elements microdataIngredients = doc.select("[itemprop='recipeIngredient']");
        for (Element ingredient : microdataIngredients) {
            ingredients.add(ingredient.text().trim());
        }

        return ingredients;
    }
}
