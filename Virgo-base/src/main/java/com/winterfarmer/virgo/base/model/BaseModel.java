package com.winterfarmer.virgo.base.model;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import org.apache.http.annotation.NotThreadSafe;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by yangtianhang on 15-2-14.
 */
@NotThreadSafe
abstract public class BaseModel implements Serializable {
    private static final long serialVersionUID = -2411840775944740500L;

    private Map<String, Object> properties;

    public Object getProperty(String key) {
        if (properties == null) {
            return null;
        }

        return properties.get(key);
    }

    public Object setProperty(String key, Object value) {
        if (value != null) {
            getNotNullProperties().put(key, value);
        }

        return value;
    }

    public void setProperties(Map<String, Object> map) {
        this.properties = map;
    }

//    public void setProperties(Pair<String, Object>... keyValues) {
//        if (ArrayUtils.isNotEmpty(keyValues)) {
//            for (Pair<String, Object> kv : keyValues) {
//                setProperty(kv.getKey(), kv.getValue());
//            }
//        }
//    }
//
//    public void setProperties(Collection<Pair<String, Object>> keyValues) {
//        if (CollectionUtils.isNotEmpty(keyValues)) {
//            for (Pair<String, Object> kv : keyValues) {
//                setProperty(kv.getKey(), kv.getValue());
//            }
//        }
//    }

    public Map<String, Object> getProperties() {
        return properties;
    }

//    public void setProperties(byte[] bytes) {
//        if (ArrayUtils.isNotEmpty(bytes)) {
//            Map<String, Object> map = JSON.parseObject(bytes, Map.class);
//            setProperties(map);
//        }
//    }

    public String toJSONString() {
        return JSON.toJSONString(properties);
    }

    public byte[] toByts() {
        return toJSONString().getBytes(Charsets.UTF_8);
    }

    private Map<String, Object> getNotNullProperties() {
        if (properties == null) {
            properties = Maps.newHashMap();
        }

        return properties;
    }
}
