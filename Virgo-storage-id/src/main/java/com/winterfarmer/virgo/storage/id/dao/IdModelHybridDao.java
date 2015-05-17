package com.winterfarmer.virgo.storage.id.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.winterfarmer.virgo.storage.id.model.BaseIdModel;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yangtianhang on 15/5/13.
 */
public class IdModelHybridDao<T extends BaseIdModel> implements IdModelDao<T> {
    private final IdModelRedisDao<T> redisDao;
    private final IdModelMysqlDao<T> mysqlDao;

    public IdModelHybridDao(IdModelRedisDao<T> redisDao, IdModelMysqlDao<T> mysqlDao) {
        this.redisDao = redisDao;
        this.mysqlDao = mysqlDao;
    }

    @Override
    public T get(long id) {
        T object = redisDao.get(id);

        if (object == null) {
            object = mysqlDao.get(id);
            if (object != null) {
                redisDao.insert(object);
            }
        }

        return object;
    }

    @Override
    public List<T> listByIds(long... ids) {
        List<T> objects = redisDao.listByIds(ids);

        List<Integer> notCachePositions = Lists.newArrayList();
        Map<Integer, Long> notCachePosIdMaps = Maps.newHashMap();
        Set<Long> notCacheIdSet = Sets.newHashSet();
        int pos = 0;
        for (T object : objects) {
            if (object == null) {
                notCachePositions.add(pos);
                notCachePosIdMaps.put(pos, ids[pos]);
                notCacheIdSet.add(ids[pos]);
            }
            pos++;
        }

        if (notCacheIdSet.isEmpty()) {
            return objects;
        }

        long[] notCacheIdArray = new long[notCacheIdSet.size()];
        int i = 0;
        for (Long id : notCacheIdSet) {
            notCacheIdArray[i++] = id;
        }
        List<T> notCacheObjects = mysqlDao.listByIds(notCacheIdArray);

        Map<Long, T> notCacheObjectMap = Maps.newHashMap();
        for (T notCacheObject : notCacheObjects) {
            notCacheObjectMap.put(notCacheObject.getId(), notCacheObject);
        }

        for (int position : notCachePositions) {
            long id = notCachePosIdMaps.get(position);
            T object = notCacheObjectMap.get(id);
            if (object != null) {
                redisDao.insert(object);
                objects.set(position, object);
            }
        }

        return objects;
    }

    @Override
    // 不支持事务
    public T insert(T object) {
        if (object == null) {
            return null;
        }

        object = mysqlDao.insert(object);
        if (object != null) {
            return redisDao.insert(object);
        } else {
            return null;
        }
    }

    @Override
    public T update(T object) {
        if (object == null) {
            return null;
        }

        object = mysqlDao.update(object);
        if (object != null) {
            return redisDao.update(object);
        } else {
            return null;
        }
    }
}
