package com.github.hkereb.kardamon.parser;

import org.json.JSONArray;
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
        JSONObject jsonLdObject = getJsonLDObject();

        if (jsonLdObject != null) {
            JSONObject parsed = parseJsonLd(jsonLdObject);
            if (!parsed.getJSONArray("ingredients").isEmpty()) {
                return parsed;
            }
        }

        if (hasMicrodata()) {
            return parseMicrodata();
        }

        // Jeśli nic nie znaleźliśmy, stosujemy awaryjne parsowanie na podstawie heurystyk
        //return parseFallback();
        return new JSONObject();
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
    private JSONObject getJsonLDObject() {
        Element jsonLdScript = doc.selectFirst("script[type='application/ld+json']");
        if (jsonLdScript != null) {
            try {
                return new JSONObject(jsonLdScript.html());
            } catch (Exception e) {
                System.err.println("Cannot parse JsonLD" + e.getMessage());
            }
        }
        return null;
    }
    private JSONObject parseJsonLd(JSONObject jsonObject) {
        JSONObject recipeJson = new JSONObject();

        recipeJson.put("title", getTitle());
        recipeJson.put("description", getDescription());
        recipeJson.put("servings", jsonObject.optString("recipeYield", ""));
        recipeJson.put("ingredients", extractJsonArray(jsonObject, "recipeIngredient"));
        recipeJson.put("instructions", extractInstructions(jsonObject));

        return recipeJson;
    }
    private List<String> extractJsonArray(JSONObject jsonObject, String key) {
        List<String> result = new ArrayList<>();
        if (jsonObject.has(key)) {
            JSONArray array = jsonObject.getJSONArray(key);
            for (Object item : array) {
                result.add(item.toString());
            }
        }
        return result;
    }

    private List<String> extractInstructions(JSONObject jsonObject) {
        List<String> instructions = new ArrayList<>();
        if (jsonObject.has("recipeInstructions")) {
            JSONArray steps = jsonObject.getJSONArray("recipeInstructions");
            for (Object stepObj : steps) {
                if (stepObj instanceof JSONObject) {
                    JSONObject step = (JSONObject) stepObj;
                    instructions.add(step.optString("text", ""));
                } else if (stepObj instanceof String) {
                    instructions.add((String) stepObj);
                }
            }
        }
        return instructions;
    }

    private boolean hasMicrodata() {
        return !doc.select("[itemprop]").isEmpty();
    }

    private JSONObject parseMicrodata() {
        JSONObject recipeJson = new JSONObject();

        recipeJson.put("title", getTitle());
        recipeJson.put("description", getDescription());
        recipeJson.put("servings", extractMicrodataString("recipeYield"));
        recipeJson.put("ingredients", extractMicrodataList("recipeIngredient"));
        recipeJson.put("instructions", extractMicrodataList("recipeInstructions"));

        return recipeJson;
    }

    private String extractServings() {
        for (Element element : doc.getAllElements()) {
            String text = element.text().trim();
            var matcher = SERVINGS_PATTERN.matcher(text);
            if (matcher.find()) {
                return matcher.group(2);
            }
        }
        return "";
    }

    private List<String> extractMicrodataList(String itemprop) {
        List<String> results = new ArrayList<>();
        Elements elements = doc.select("[itemprop='" + itemprop + "']");
        for (Element element : elements) {
            results.add(element.text());
        }
        return results;
    }
    private String extractMicrodataString(String itemprop) {
        Element element = doc.selectFirst("[itemprop='" + itemprop + "']");
        if (element != null) {
            String content = element.attr("content");
            if (!content.isEmpty()) {
                return content;
            }
            return element.text();
        }
        return "";
    }
    private JSONObject parseFallback() {
        JSONObject recipeJson = new JSONObject();
        recipeJson.put("title", getTitle());
        recipeJson.put("description", getDescription());
        recipeJson.put("servings", extractServings());
        recipeJson.put("ingredients", Collections.emptyList());
        recipeJson.put("instructions", Collections.emptyList());
        return recipeJson;
    }
    private String getTitle() {
        Element ogTitle = doc.selectFirst("meta[property=og:title]");
        return (ogTitle != null) ? ogTitle.attr("content") : doc.title();
    }

    private String getDescription() {
        Element ogDescription = doc.selectFirst("meta[property=og:description]");
        if (ogDescription != null) return ogDescription.attr("content");

        Element description = doc.selectFirst("meta[name=description]");
        return (description != null) ? description.attr("content") : "";
    }
}
