package cn.enumaelish.file.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;
import java.util.Map;

/**
 * @author: EnumaElish
 * @Date: 2019/12/13 10:27
 * @Description: json序列化，反序列化工具类
 */
public class JsonUtil {
    private static final SerializeConfig CONFIG;

    private JsonUtil() {
    }

    static {
        CONFIG = new SerializeConfig();
        // 使用和json-lib兼容的日期输出格式
//        CONFIG.put(java.util.Date.class, new JSONLibDataFormatSerializer());
//        // 使用和json-lib兼容的日期输出格式
//        CONFIG.put(java.sql.Date.class, new JSONLibDataFormatSerializer());
    }

    protected static final SerializerFeature[] FEATURES = {
            // 输出空置字段
            SerializerFeature.WriteMapNullValue,
            // list字段如果为null，输出为[]，而不是null
            SerializerFeature.WriteNullListAsEmpty,
            // 数值字段如果为null，输出为0，而不是null
//            SerializerFeature.WriteNullNumberAsZero,
            // Boolean字段如果为null，输出为false，而不是null
            SerializerFeature.WriteNullBooleanAsFalse,
            // 字符类型字段如果为null，输出为""，而不是null
//            SerializerFeature.WriteNullStringAsEmpty,
            // 常量转toString
            SerializerFeature.WriteEnumUsingToString,
            SerializerFeature.WriteDateUseDateFormat
    };

    public static SerializerFeature[] getFEATURES() {
        return FEATURES;
    }

    public static String toJSONString(Object object) {
        return JSON.toJSONString(object, CONFIG, FEATURES);
    }


    public static JSONObject toJSONObject(Object object){
        return JSONObject.parseObject(toJSONString(object));
    }

    public static String toJSONNoFeatures(Object object) {
        return JSON.toJSONString(object, CONFIG);
    }


    public static Object toBean(String text) {
        return JSON.parse(text);
    }

    public static <T> T toBean(String text, Class<T> clazz) {
        return JSON.parseObject(text, clazz);
    }

    /**
     * 转换为数组
     */
    public static Object[] toArray(String text) {
        return toArray(text, null);
    }

    /**
     *  转换为数组
     */
    public static <T> Object[] toArray(String text, Class<T> clazz) {
        return JSON.parseArray(text, clazz).toArray();
    }

    /**
     * 转换为List
      */
    public static <T> List<T> toList(String text, Class<T> clazz) {
        return JSON.parseArray(text, clazz);
    }


    /**
     * 将string转化为序列化的json字符串
     */
    public static Object textToJson(String text) {
        return JSON.parse(text);
    }

    /**
     * json字符串转化为map
     */
    public static Map stringToCollect(String s) {
        return JSONObject.parseObject(s);
    }

    /**
     * 将map转化为string
     */
    public static String mapToString(Map m) {
        return JSONObject.toJSONString(m);
    }

    /**
     * 将map转化为JSONObject
     */
    public static JSONObject mapToJson(Map m) {
        return JSONObject.parseObject(JSONObject.toJSONString(m));
    }
}
