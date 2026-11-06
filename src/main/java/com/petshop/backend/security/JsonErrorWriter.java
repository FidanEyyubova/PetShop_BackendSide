package com.petshop.backend.security;

import com.petshop.backend.i18n.MessageTranslator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.backend.dto.response.common.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class JsonErrorWriter {

    private final ObjectMapper objectMapper;
    private final MessageTranslator translator;

    public JsonErrorWriter(ObjectMapper objectMapper, MessageTranslator translator) {
        this.objectMapper = objectMapper;
        this.translator = translator;
    }

    public void write(HttpServletResponse response, int status, String code, String message) throws IOException {
        if (response.isCommitted()) return;
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), ApiResponse.error(code, translator.translate(message), null));
    }
}
