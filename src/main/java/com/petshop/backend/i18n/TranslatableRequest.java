package com.petshop.backend.i18n;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

public interface TranslatableRequest {

    String TRANSLATIONS_DOC = "EN / RU versions of the text fields, e.g. "
            + "{\"en\": {\"title\": \"Dog food\"}, \"ru\": {\"title\": \"Корм для собак\"}}. "
            + "The plain fields carry the Azerbaijani text.";

    @Schema(description = TRANSLATIONS_DOC)
    Map<String, Map<String, Object>> getTranslations();
}
