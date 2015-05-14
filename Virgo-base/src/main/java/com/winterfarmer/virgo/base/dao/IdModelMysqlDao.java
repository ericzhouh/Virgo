package com.winterfarmer.virgo.base.dao;

import com.winterfarmer.virgo.base.model.BaseIdModel;
import com.winterfarmer.virgo.database.BaseMysqlDao;

/**
 * Created by yangtianhang on 15/5/13.
 */
public abstract class IdModelMysqlDao<T extends BaseIdModel> extends BaseMysqlDao implements IdModelDao<T> {
}
