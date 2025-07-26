package com.pulseras.api.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateString = p.getValueAsString();
        
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        
        try {
            // Try to parse as ISO format with timezone (e.g., 2025-07-26T08:21:12.548Z)
            if (dateString.endsWith("Z") || dateString.contains("+") || (dateString.contains("-") && dateString.length() > 19)) {
                return OffsetDateTime.parse(dateString).toLocalDateTime();
            }
            
            // Try to parse as ISO format with milliseconds but no timezone (e.g., 2025-07-26T08:21:12.548)
            if (dateString.contains(".")) {
                return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
            }
            
            // Try to parse as simple ISO format (e.g., 2025-07-26T08:21:12)
            return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            
        } catch (DateTimeParseException e) {
            throw new IOException("Unable to parse date: " + dateString, e);
        }
    }
}
