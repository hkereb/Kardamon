package com.github.hkereb.kardamon.parsers;

import org.json.JSONArray;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONObject;
import java.util.*;

public class JsonLdParser {
    public JsonLdParser() {}

    public JSONArray getJsonLdObjects(Document doc) {
        Elements jsonLdScripts = doc.select("script[type='application/ld+json']");
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

    public String extractSingleValue(JSONObject jsonLDObject, String key) {
        if (jsonLDObject.has(key)) {
            Object item = jsonLDObject.get(key);
            if (item instanceof JSONArray) {
                JSONArray array = jsonLDObject.getJSONArray(key);
                return array.getString(0);
            }
            return item.toString();
        }
        return "";
    }
    public List<String> extractArray(JSONObject jsonLDObject, String key) {
        List<String> result = new ArrayList<>();
        if (jsonLDObject.has(key)) {
            JSONArray array = jsonLDObject.getJSONArray(key);
            for (int i = 0; i < array.length(); i++) {
                result.add(array.getString(i));
            }
        }
        return result;
    }
}
