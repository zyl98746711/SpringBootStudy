package com.yan.tool;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.util.Map;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtil {

    private static final ObjectMapper SNAKE_OBJECT_MAPPER = new ObjectMapper();

    private static final TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<>() {
    };

    static {
        // 设置 Jackson
        SimpleModule module = new SimpleModule();
        SNAKE_OBJECT_MAPPER.registerModule(module);
        SNAKE_OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        SNAKE_OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SNAKE_OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS, false);

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
        } catch (JsonProcessingException ex) {
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
        } catch (IOException ex) {
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
        } catch (IOException e) {
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
