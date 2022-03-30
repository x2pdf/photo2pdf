package com.logan.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Logan Qin
 * @date 2021/12/22 11:27
 */
public class JSONUtils {
    static ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object o) {
        try {
//            objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
