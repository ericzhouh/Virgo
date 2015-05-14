package com.winterfarmer.virgo.database.helper.column;

import com.winterfarmer.virgo.database.helper.column.date.DatetimeColumn;
import com.winterfarmer.virgo.database.helper.column.date.TimeStampColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.BigintColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.SmallIntColumn;
import com.winterfarmer.virgo.database.helper.column.numeric.TinyIntColumn;

/**
 * Created by yangtianhang on 15-3-8.
 */
public class Columns {
    public static final String CREATE_AT_COLUMN_NAME = "create_at";
    public static final String USER_ID_COLUMN_NAME = "user_id";
    public static final String EXPIRE_AT_COLUMN_NAME = "expire_at";
    public static final String APP_KEY_COLUMN_NAME = "app_key";
    public static final String STATE_COLUMN_NAME = "state";

    public static TimeStampColumn newCreateAtColumn() {
        return (TimeStampColumn) new TimeStampColumn(CREATE_AT_COLUMN_NAME).setDefaultValue("CURRENT_TIMESTAMP").setComment("创建时间").setAllowNull(false);
    }

    public static DatetimeColumn newExpireAtColumn(boolean allowNull) {
        return (DatetimeColumn) new DatetimeColumn(EXPIRE_AT_COLUMN_NAME).setComment("过期时间").setAllowNull(allowNull);
    }

    public static BigintColumn newUserIdColumn(boolean autoIncrease, boolean allowNull) {
        return (BigintColumn) new BigintColumn(USER_ID_COLUMN_NAME).setDisplaySize(20).setAutoIncrease(autoIncrease).setAllowNull(allowNull);
    }

    public static BigintColumn newIdColumn(String columnIdName, boolean autoIncrease, boolean allowNull) {
        return (BigintColumn) new BigintColumn(columnIdName).setDisplaySize(20).setAutoIncrease(autoIncrease).setAllowNull(allowNull);
    }

    public static SmallIntColumn newAppKeyColumn(boolean allowNull) {
        return (SmallIntColumn) new SmallIntColumn(APP_KEY_COLUMN_NAME).setDisplaySize(5).setAllowNull(allowNull);
    }

    public static TinyIntColumn newStateColumn(boolean allowNull) {
        return (TinyIntColumn) new TinyIntColumn(STATE_COLUMN_NAME).setAllowNull(allowNull);
    }

    public static TinyIntColumn newStateColumn(boolean allowNull, boolean defaultZero) {
        return (TinyIntColumn) new TinyIntColumn(STATE_COLUMN_NAME).setDefaultValue(defaultZero ? "0" : "1");
    }
}
