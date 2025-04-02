package com.github.hkereb.kardamon.parsers;

import org.json.JSONArray;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.json.JSONObject;
import java.util.*;

// todo split instructions in microdata
// todo gather patterns for ingredients (to later determine which pattern is present and parse ingredients)
// todo find and add/delete numbers in instructions ("1.", "3")
// todo unstructured data extraction (polish and english)
public class RecipeParser {
    private final Document doc;
    private final JsonLdParser jsonLdParser;
    private final MicrodataParser microdataParser;

    public RecipeParser(Document doc) {
        this.doc = doc;
        this.jsonLdParser = new JsonLdParser();
        this.microdataParser = new MicrodataParser();
    }
    public JSONObject parseRecipe() {
        JSONArray jsonLdObjects = jsonLdParser.getJsonLdObjects(doc);
        if (!jsonLdObjects.isEmpty()) {
            for (int i = 0; i < jsonLdObjects.length(); i++) {
                JSONObject jsonLdObject = jsonLdObjects.getJSONObject(i);
                JSONObject parsed = parseFromJsonLd(jsonLdObject);

                if (!parsed.getJSONArray("ingredients").isEmpty()) {
                    return parsed;
                }
            }
        }

        if (microdataParser.hasMicrodata(doc)) {
            return parseFromMicrodata();
        }

        // todo return parseFallback(); - (plan B aka unstructured data)
        return new JSONObject();
    }

    ///// JSON-LD ///////////////////////////////////////////////////////////////////////////////////////////////
    private JSONObject parseFromJsonLd(JSONObject jsonLDObject) {
        JSONObject recipeJson = new JSONObject();

        recipeJson.put("title", getTitle());
        recipeJson.put("description", getDescription());
        recipeJson.put("servings", jsonLdParser.extractSingleValue(jsonLDObject, "recipeYield"));
        recipeJson.put("ingredients", jsonLdParser.extractArray(jsonLDObject, "recipeIngredient"));
        recipeJson.put("instructions", extractInstructions(jsonLDObject));

        return recipeJson;
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
    private JSONObject parseFromMicrodata() {
        JSONObject recipeJson = new JSONObject();

        recipeJson.put("title", getTitle());
        recipeJson.put("description", getDescription());
        recipeJson.put("servings", microdataParser.extractSingleValue(doc, "recipeYield"));
        recipeJson.put("ingredients", microdataParser.extractList(doc, "recipeIngredient"));
        recipeJson.put("instructions", microdataParser.extractList(doc, "recipeInstructions"));

        return recipeJson;
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

    ///// EXTRA ////////////////////////////////////////////////////////////////////////////////////////////
    // todo find and add/delete numbers in instructions ("1.", "3")
    /*
    if there is an instance of the pattern (pattern not there yet) in the first element of the array then loop through all elements
    and delete the part that fits the pattern to normalize instructions. if not, skip the rest of the elements without modifications
     */
}
