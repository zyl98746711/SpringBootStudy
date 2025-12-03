package com.yan.util;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import lombok.experimental.UtilityClass;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.PropertyNamingStrategy;
import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

@UtilityClass
public class JsonUtil {

    private static final JsonMapper SNAKE_OBJECT_MAPPER;

    private static final TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<>() {
    };

    static {
        // 设置 Jackson
        SimpleModule module = new SimpleModule();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        module.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));
        PropertyNamingStrategy snakeCase = PropertyNamingStrategies.SNAKE_CASE;
        SNAKE_OBJECT_MAPPER = JsonMapper.builder().propertyNamingStrategy(snakeCase)
                .addModule(module)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();

    }

    /**
     * 序列化为JSON字符串
     *
     * @param object 对象
     * @return JSON字符串
     */
    public static String toJsonString(Object object) {
        try {
            return SNAKE_OBJECT_MAPPER.writeValueAsString(object);
        } catch (JacksonException ex) {
            throw new RuntimeException("Json序列化失败：" + ex.getMessage());
        }
    }

    /**
     * 序列化为Java对象，支持带泛型的参数
     *
     * @param value         JSON字符串
     * @param typeReference java类型引用
     * @param <T>           java类型
     * @return 实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T toJavaObject(String value, TypeReference<?> typeReference) {
        try {
            return (T) SNAKE_OBJECT_MAPPER.readValue(value, typeReference);
        } catch (JacksonException ex) {
            throw new RuntimeException("Json反序列化失败：" + ex.getMessage());
        }
    }

    /**
     * 序列化为Java对象
     *
     * @param value JSON字符串
     * @param type  java类型
     * @param <T>   java类型
     * @return 实例
     */
    public static <T> T toJavaObject(String value, Class<T> type) {
        try {
            return SNAKE_OBJECT_MAPPER.readValue(value, type);
        } catch (JacksonException e) {
            throw new RuntimeException("Json反序列化失败：" + e.getMessage());
        }
    }

    /**
     * 对象转Map
     *
     * @param t   对象
     * @param <T> 对象类型
     * @return Map
     */
    public static <T> Map<String, Object> objectToMap(T t) {
        return toJavaObject(toJsonString(t), MAP_TYPE_REFERENCE);
    }

    /**
     * map转对象
     *
     * @param variables 参数
     * @param clazz     类
     * @param <T>       类型
     * @return 对象
     */
    public static <T> T mapToObject(Map<String, Object> variables, Class<T> clazz) {
        return toJavaObject(toJsonString(variables), clazz);
    }
}
