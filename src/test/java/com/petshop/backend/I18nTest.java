package com.petshop.backend;

import com.petshop.backend.common.Language;
import com.petshop.backend.common.PetType;
import com.petshop.backend.dto.common.ProductIngredients;
import com.petshop.backend.exception.BadRequestException;
import com.petshop.backend.i18n.Localized;
import com.petshop.backend.i18n.MessageTranslator;
import com.petshop.backend.i18n.TranslatableFields;
import com.petshop.backend.i18n.Translations;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class I18nTest {

    private static Map<String, Map<String, Object>> request(String lang, Map<String, Object> fields) {
        Map<String, Map<String, Object>> raw = new LinkedHashMap<>();
        raw.put(lang, new LinkedHashMap<>(fields));
        return raw;
    }

    @Test
    void languageComesFromQueryThenHeader() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        assertEquals(Language.AZ, Language.of(req));
        req.addHeader("Accept-Language", "ru-RU,ru;q=0.9,en;q=0.8");
        assertEquals(Language.RU, Language.of(req));
        req.setParameter("lang", "en");
        assertEquals(Language.EN, Language.of(req));
        req.setParameter("lang", "de");
        assertEquals(Language.RU, Language.of(req));
    }

    @Test
    void requestKeepsOnlyKnownLanguagesAndFields() {
        Map<String, Map<String, Object>> raw = request("en", Map.of("title", "  Dog food ", "hacked", "x", "description", " "));
        raw.put("az", new LinkedHashMap<>(Map.of("title", "ignored")));
        raw.put("de", new LinkedHashMap<>(Map.of("title", "Hundefutter")));
        Translations t = Translations.fromRequest(raw, TranslatableFields.PRODUCT);
        assertEquals(Map.of("en", Map.of("title", "Dog food")), t.asMap());
    }

    @Test
    void mergeReplacesOnlyTheSentLanguageAndFields() {
        Translations stored = Translations.of(Map.of(
                "en", new LinkedHashMap<>(Map.of("tagline", "Best", "badgeText", "Partner")),
                "ru", new LinkedHashMap<>(Map.of("tagline", "Лучший"))));

        Translations merged = stored.mergedWith(request("en", Map.of("tagline", "Better")), TranslatableFields.STORE);
        assertEquals("Better", merged.get(Language.EN, "tagline"));
        assertEquals("Partner", merged.get(Language.EN, "badgeText"));
        assertEquals("Лучший", merged.get(Language.RU, "tagline"));

        assertSame(stored, stored.mergedWith(null, TranslatableFields.STORE));
        Translations cleared = stored.mergedWith(request("ru", Map.of()), TranslatableFields.STORE);
        assertNull(cleared.get(Language.RU, "tagline"));
    }

    @Test
    void tooLongTranslationIsRejected() {
        String huge = "x".repeat(Translations.MAX_TEXT_LENGTH + 1);
        assertThrows(BadRequestException.class,
                () -> Translations.fromRequest(request("en", Map.of("title", huge)), TranslatableFields.PRODUCT));
    }

    @Test
    void jsonRoundTripAndFallback() {
        Translations t = Translations.fromRequest(request("ru", Map.of(
                "title", "Корм", "keyBenefits", List.of("Польза"),
                "ingredients", Map.of("activeIngredients", "Курица"))), TranslatableFields.PRODUCT);
        Translations loaded = Translations.fromJson(t.toJson());
        assertEquals(t, loaded);

        Localized ru = Localized.of(loaded, Language.RU);
        assertEquals("Корм", ru.text("title", "Yem"));
        assertEquals("Təsvir", ru.text("description", "Təsvir"));
        assertEquals(List.of("Польза"), ru.list("keyBenefits", List.of("Fayda")));
        assertEquals("Курица", ru.object("ingredients", null, ProductIngredients.class).getActiveIngredients());

        ProductIngredients azIngredients = ProductIngredients.builder()
                .freeFrom(List.of("Taxıl")).activeIngredients("Toyuq").build();
        ProductIngredients merged = ru.object("ingredients", azIngredients, ProductIngredients.class);
        assertEquals("Курица", merged.getActiveIngredients());
        assertEquals(List.of("Taxıl"), merged.getFreeFrom());

        assertEquals("Yem", Localized.of(loaded, Language.EN).text("title", "Yem"));
        assertEquals("Yem", Localized.of(loaded, Language.AZ).text("title", "Yem"));
        assertNull(Translations.fromJson(null).toJson());
    }

    @Test
    void editableViewHasAllThreeLanguages() {
        Translations t = Translations.of(Map.of("en", Map.of("title", "Dog food")));
        Map<String, Map<String, Object>> view = Localized.editable(t, "title", "İt yemi", "description", null);
        assertEquals(Map.of("title", "İt yemi"), view.get("az"));
        assertEquals(Map.of("title", "Dog food"), view.get("en"));
        assertEquals(Map.of(), view.get("ru"));
    }

    @Test
    void errorMessagesAreTranslated() {
        MessageTranslator tr = new MessageTranslator();
        assertEquals("Səbətiniz boşdur", tr.translate("Your cart is empty", Language.AZ));
        assertEquals("Ваша корзина пуста", tr.translate("Your cart is empty", Language.RU));
        assertEquals("Your cart is empty", tr.translate("Your cart is empty", Language.EN));
        assertEquals("Məhsul tapılmadı (id = '5')", tr.translate("Product not found: id = '5'", Language.AZ));
        assertEquals("'Kibble' məhsulundan yalnız 2 ədəd qalıb", tr.translate("Only 2 of 'Kibble' left", Language.AZ));
        assertEquals("Нельзя изменить статус заказа с «в ожидании» на «доставлен»",
                tr.translate("Cannot change order status from pending to delivered", Language.RU));
        assertEquals("Товара «A» нет в наличии; Товара «B» нет в наличии",
                tr.translate("'A' is out of stock; 'B' is out of stock", Language.RU));
        assertEquals("uzunluq 0 ilə 200 arasında olmalıdır", tr.translate("size must be between 0 and 200", Language.AZ));
        assertEquals("Some unknown message", tr.translate("Some unknown message", Language.AZ));
    }

    @Test
    void enumLabelsCoverAllLanguages() {
        assertEquals("İtlər", PetType.DOGS.label(Language.AZ));
        assertEquals("Dogs", PetType.DOGS.label(Language.EN));
        assertEquals("Собаки", PetType.DOGS.label(Language.RU));
    }
}
