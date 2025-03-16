package com.github.hkereb.kardamon.parser;

import org.json.JSONArray;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONObject;
import java.util.*;
import java.util.regex.Pattern;

// TODO split to smaller parsers
public class RecipeParser {
    private final Document doc;
    public RecipeParser(Document doc) {
        this.doc = doc;
    }
    public JSONObject parseRecipe() {
        ///// JSON-LD ///////////////////////////////////////////////////////////////////////////////////////////////
        JSONArray jsonLdObjects = getJsonLDObjects();
        if (!jsonLdObjects.isEmpty()) {
            for (int i = 0; i < jsonLdObjects.length(); i++) {
                JSONObject jsonLdObject = jsonLdObjects.getJSONObject(i);
                JSONObject parsed = parseJsonLd(jsonLdObject);

                if (!parsed.getJSONArray("ingredients").isEmpty()) {
                    return parsed;
                }
            }
        }
        ///// MICRODATA ///////////////////////////////////////////////////////////////////////////////////////////////
        if (hasMicrodata()) {
            return parseMicrodata();
        }
        ///// UNSTRUCTURED ////////////////////////////////////////////////////////////////////////////////////////////
        //return parseFallback();
        return new JSONObject();
    }



    ///// JSON-LD ///////////////////////////////////////////////////////////////////////////////////////////////
    private JSONArray getJsonLDObjects() {
        Elements jsonLdScripts = doc.select("script[type='application/ld+json']"); // Zmieniamy na select(), aby uzyskać wszystkie tagi
        JSONArray jsonObjects = new JSONArray();

        for (Element jsonLdScript : jsonLdScripts) {
            try {
                JSONObject jsonObject = new JSONObject(jsonLdScript.html());
                jsonObjects.put(jsonObject);
            } catch (Exception e) {
                System.err.println("Cannot parse JsonLD: " + e.getMessage());
            }
        }

        return jsonObjects;
    }
    private JSONObject parseJsonLd(JSONObject jsonLDObject) {
        JSONObject recipeJson = new JSONObject();

        recipeJson.put("title", getTitle());
        recipeJson.put("description", getDescription());
        recipeJson.put("servings", extractSingleValue(jsonLDObject, "recipeYield"));
        recipeJson.put("ingredients", extractJsonArray(jsonLDObject, "recipeIngredient"));
        recipeJson.put("instructions", extractInstructions(jsonLDObject));

        return recipeJson;
    }
    private String extractSingleValue(JSONObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            return jsonObject.getJSONArray(key).getString(0);
        }
        return "";
    }
    private List<String> extractJsonArray(JSONObject jsonLDObject, String key) {
        List<String> result = new ArrayList<>();
        if (jsonLDObject.has(key)) {
            JSONArray array = jsonLDObject.getJSONArray(key);
            for (int i = 0; i < array.length(); i++) {
                result.add(array.getString(i));
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

    ///// MICRODATA ///////////////////////////////////////////////////////////////////////////////////////////////
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

    ///// UNSTRUCTURED ////////////////////////////////////////////////////////////////////////////////////////////
    private JSONObject parseFallback() {
        JSONObject recipeJson = new JSONObject();
        recipeJson.put("title", getTitle());
        recipeJson.put("description", getDescription());
        //recipeJson.put("servings", extractServings());
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
