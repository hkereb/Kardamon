package com.github.hkereb.kardamon.parsers;

import org.json.JSONArray;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONObject;
import java.util.*;

public class MicrodataParser {
    public MicrodataParser() {}

    public boolean hasMicrodata(Document doc) {
        return !doc.select("[itemprop]").isEmpty();
    }
    public String extractSingleValue(Document doc, String itemprop) {
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
    public List<String> extractList(Document doc, String itemprop) {
        List<String> results = new ArrayList<>();
        Elements elements = doc.select("[itemprop='" + itemprop + "']");
        for (Element element : elements) {
            results.add(element.text());
        }
        return results;
    }
}
