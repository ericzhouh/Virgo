package com.winterfarmer.virgo.storage.id.dao;


import com.winterfarmer.virgo.storage.id.model.BaseIdModel;

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

    T insert(T object);

    T update(T object);
}
