package com.winterfarmer.virgo.storage.counter.dao;

/**
 * Created by yangtianhang on 15/6/4.
 */
public class CounterHybridDaoImpl implements CounterDao {
    private final CounterMysqlDaoImpl mysqlDao;
    private final CounterRedisDaoImpl redisDao;

    public CounterHybridDaoImpl(CounterMysqlDaoImpl mysqlDao, CounterRedisDaoImpl redisDao) {
        this.mysqlDao = mysqlDao;
        this.redisDao = redisDao;
    }

    @Override
    public Integer getCount(long id, int type) {
        Integer count = redisDao.getCount(id, type);
        if (count != null) {
            return count;
        }
        count = mysqlDao.getCount(id, type);
        if (count != null) {
            redisDao.setCount(id, type, count);
        } else {
            redisDao.setCount(id, type, 0);
        }

        return count;
    }

    @Override
    public boolean setCount(long id, int type, Integer count) {
        if (count == null) {
            return false;
        }

        if (mysqlDao.setCount(id, type, count)) {
            return redisDao.setCount(id, type, count);
        } else {
            return false;
        }
    }
}
