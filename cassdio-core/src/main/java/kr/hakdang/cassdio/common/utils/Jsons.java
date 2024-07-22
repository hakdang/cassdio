package kr.hakdang.cassdio.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Jsons
 *
 * @author seungh0
 * @since 2024-07-01
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Jsons {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .registerModules(new ParameterNamesModule(), new Jdk8Module(), new JavaTimeModule());

    public static <T> T toObject(String input, Class<T> toClass) {
        try {
            return OBJECT_MAPPER.readValue(input, toClass);
        } catch (Exception exception) {
            throw new IllegalArgumentException(String.format("failed to deserialize [input: (%s) toClass: (%s)]", input, toClass.getSimpleName()), exception);
        }
    }

    public static <T> T toObject(String input, TypeReference<T> toClass) {
        try {
            return OBJECT_MAPPER.readValue(input, toClass);
        } catch (Exception exception) {
            throw new IllegalArgumentException(String.format("failed to deserialize [input: (%s) toClass: (%s)]", input, toClass), exception);
        }
    }

    public static <T> String toJson(T input) {
        try {
            return OBJECT_MAPPER.writeValueAsString(input);
        } catch (Exception exception) {
            throw new IllegalArgumentException(String.format("failed to serialize [input: (%s)]", input), exception);
        }
    }

    public static <T> String toPrettyJson(T input) {
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(input);
        } catch (Exception exception) {
            throw new IllegalArgumentException(String.format("failed to serialize [input: (%s)]", input), exception);
        }
    }

}
