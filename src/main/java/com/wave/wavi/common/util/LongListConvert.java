package com.wave.wavi.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Converter
@RequiredArgsConstructor
public class LongListConvert implements AttributeConverter<List<Long>, String> {
    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(List<Long> longList) {
        if (longList == null || longList.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(longList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Long> convertToEntityAttribute(String data) {
        if (data == null || data.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(data, new TypeReference<List<Long>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
