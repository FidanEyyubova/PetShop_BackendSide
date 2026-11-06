package com.petshop.backend.i18n;

import com.fasterxml.jackson.databind.JsonNode;
import com.petshop.backend.common.Language;
import com.petshop.backend.util.JsonUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MessageTranslator {

    private static final String CATALOG = "i18n/messages.json";
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{(\\d)}");

    private record Template(Pattern pattern, List<Integer> order, String az, String ru) {}

    private final Map<String, String[]> exact = new HashMap<>();
    private final List<Template> templates = new ArrayList<>();
    private final Map<String, String[]> terms = new HashMap<>();

    public MessageTranslator() {
        JsonNode root = load();
        for (JsonNode entry : root.path("messages")) {
            String en = entry.get(0).asText();
            String az = entry.get(1).asText();
            String ru = entry.get(2).asText();
            if (PLACEHOLDER.matcher(en).find()) templates.add(compile(en, az, ru));
            else exact.put(en, new String[]{az, ru});
        }
        root.path("terms").fields().forEachRemaining(e ->
                terms.put(e.getKey(), new String[]{e.getValue().get(0).asText(), e.getValue().get(1).asText()}));
    }

    public String translate(String message) {
        return translate(message, Language.current());
    }

    public String translate(String message, Language language) {
        if (message == null || language == Language.EN) return message;
        String[] known = exact.get(message);
        if (known != null) return known[language == Language.AZ ? 0 : 1];
        if (message.contains("; ")) {
            StringJoiner joined = new StringJoiner("; ");
            for (String part : message.split("; ")) {
                String translated = lookup(part, language);
                joined.add(translated != null ? translated : part);
            }
            return joined.toString();
        }
        String translated = lookup(message, language);
        return translated != null ? translated : message;
    }

    public Map<String, String> translateValues(Map<String, String> messages) {
        if (messages == null) return null;
        Language language = Language.current();
        Map<String, String> result = new LinkedHashMap<>();
        messages.forEach((k, v) -> result.put(k, translate(v, language)));
        return result;
    }

    private String lookup(String message, Language language) {
        int index = language == Language.AZ ? 0 : 1;
        String[] known = exact.get(message);
        if (known != null) return known[index];
        for (Template t : templates) {
            Matcher m = t.pattern().matcher(message);
            if (!m.matches()) continue;
            String target = language == Language.AZ ? t.az() : t.ru();
            for (int g = 0; g < t.order().size(); g++) {
                String value = m.group(g + 1);
                String[] term = terms.get(value);
                target = target.replace("{" + t.order().get(g) + "}", term != null ? term[index] : value);
            }
            return target;
        }
        return null;
    }

    private static Template compile(String en, String az, String ru) {
        StringBuilder regex = new StringBuilder("^");
        List<Integer> order = new ArrayList<>();
        Matcher m = PLACEHOLDER.matcher(en);
        int last = 0;
        while (m.find()) {
            regex.append(Pattern.quote(en.substring(last, m.start()))).append("(.+?)");
            order.add(Integer.parseInt(m.group(1)));
            last = m.end();
        }
        regex.append(Pattern.quote(en.substring(last))).append("$");
        return new Template(Pattern.compile(regex.toString(), Pattern.DOTALL), order, az, ru);
    }

    private static JsonNode load() {
        try (InputStream in = new ClassPathResource(CATALOG).getInputStream()) {
            return JsonUtils.MAPPER.readTree(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read " + CATALOG, e);
        }
    }
}
