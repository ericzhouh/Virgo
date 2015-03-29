package com.winterfarmer.virgo.database.helper.column.binary;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by yangtianhang on 15-3-17.
 */
public class ExtInfoColumn extends AbstractBinaryColumn {
    public static final String DATA_TYPE_NAME = "VARBINARY";
    public static final String EXT_INFO_COLUMN_NAME = "ext_info";
    public static final int EXT_INFO_MAX_SIZE = 32768;

    public ExtInfoColumn() {
        super(EXT_INFO_COLUMN_NAME, EXT_INFO_MAX_SIZE);
        this.setDefaultValue("NULL").setComment("扩展信息").setAllowNull(true);
    }

    @Override
    public String getDataTypeName() {
        return DATA_TYPE_NAME;
    }

    @Override
    public Map<String, Object> getValue(ResultSet rs) throws SQLException {
        byte[] bytes = rs.getBytes(EXT_INFO_COLUMN_NAME);
        if (bytes != null) {
            return JSON.parseObject(bytes, Map.class);
        } else {
            return Maps.newHashMap();
        }
    }

    public static byte[] toBytes(Map<String, Object> properties) {
        if (MapUtils.isEmpty(properties)) {
            return null;
        } else {
            return JSON.toJSONString(properties).getBytes(Charsets.UTF_8);
        }
    }
}
