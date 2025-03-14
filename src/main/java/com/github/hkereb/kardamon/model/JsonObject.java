package com.github.hkereb.kardamon.model;

import java.util.Objects;
import java.util.Map;

public class JsonObject {
    private Map<String, Objects> values;

    public JsonObject(Map<String, Objects> values) {
        this.values = values;
    }

    public Map<String, Objects> getValues() {
        return values;
    }
    public void setValues(Map<String, Objects> values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        JsonObject that = (JsonObject) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
