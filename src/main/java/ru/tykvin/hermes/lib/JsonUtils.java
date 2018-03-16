package ru.tykvin.hermes.lib;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NonNull;

import java.io.IOException;

public class JsonUtils {


    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(MapperFeature.AUTO_DETECT_FIELDS, true)
                .configure(MapperFeature.AUTO_DETECT_SETTERS, true)
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    public static String pojoToString(@NonNull Object pojo) {
        return pojoToString(pojo, OBJECT_MAPPER);
    }

    public static <T> T stringToPojo(@NonNull String json,
                                     @NonNull Class<T> pojoClass) {
        return stringToPojo(json, pojoClass, OBJECT_MAPPER);
    }

    public static String pojoToString(@NonNull Object pojo,
                                      @NonNull ObjectMapper objectMapper) {

        try {
            return objectMapper.writeValueAsString(pojo);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static <T> T stringToPojo(@NonNull String json,
                                     @NonNull Class<T> pojoClass,
                                     @NonNull ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(json, pojoClass);
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }
}