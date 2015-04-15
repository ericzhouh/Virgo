package com.winterfarmer.virgo.base.model;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Created by yangtianhang on 15-2-14.
 */
public class BaseModel implements Serializable {
    private static final long serialVersionUID = -2411840775944740500L;

    private final Map<String, Object> properties = Maps.newHashMap();

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public Object setProperty(String key, Object value) {
        if (value != null) {
            properties.put(key, value);
        }

        return value;
    }

    public void setProperties(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            setProperty(entry.getKey(), entry.getValue());
        }
    }

    public void setProperties(Pair<String, Object>... keyValues) {
        if (ArrayUtils.isNotEmpty(keyValues)) {
            for (Pair<String, Object> kv : keyValues) {
                setProperty(kv.getKey(), kv.getValue());
            }
        }
    }

    public void setProperties(Collection<Pair<String, Object>> keyValues) {
        if (CollectionUtils.isNotEmpty(keyValues)) {
            for (Pair<String, Object> kv : keyValues) {
                setProperty(kv.getKey(), kv.getValue());
            }
        }
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(byte[] bytes) {
        if (ArrayUtils.isNotEmpty(bytes)) {
            Map<String, Object> map = JSON.parseObject(bytes, Map.class);
            setProperties(map);
        }
    }

    public String toJSONString() {
        return JSON.toJSONString(properties);
    }

    public byte[] toByts() {
        return toJSONString().getBytes(Charsets.UTF_8);
    }

    @Override
    public String toString() {
        return toJSONString();
    }
}
