package com.winterfarmer.virgo.base.dao;

import com.winterfarmer.virgo.base.model.BaseIdModel;

import java.util.List;

/**
 * Created by yangtianhang on 15/5/14.
 * <p/>
 * 在运行阶段, java会擦除泛型的类型, 所以要在spring config里显式指定类型,
 * 在使用的时候要注意泛型类型与spring config里的类型一致!!!
 */
public interface IdModelDao<T extends BaseIdModel> {
    T get(long id);

    List<T> list(long... ids);

    boolean set(long id, T object);

    boolean update(long id, T object);
}
