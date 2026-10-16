package dev.franek.customitems.shared.placeholder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceholderSet {
    private final Map<String, String> placeholders = new HashMap<String, String>();

    public PlaceholderSet addPlaceholder(String key, String value) {
        this.placeholders.put(key, value);
        return this;
    }

    public PlaceholderSet addPlaceholder(String key, int value) {
        this.placeholders.put(key, Integer.toString(value));
        return this;
    }

    public PlaceholderSet addPlaceholder(String key, double value) {
        this.placeholders.put(key, Double.toString(value));
        return this;
    }

    public String prepareAndGetResult(String input) {
        String result = input;
        for (Map.Entry<String, String> entry : this.placeholders.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public List<String> prepareAndGetResult(List<String> input) {
        ArrayList<String> result = new ArrayList<String>();
        for (String line : input) {
            result.add(this.prepareAndGetResult(line));
        }
        return result;
    }
}

