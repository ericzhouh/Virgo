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
        }

        return count;
    }

    @Override
    public boolean setCount(long id, int type, int count) {
        if (mysqlDao.setCount(id, type, count)) {
            return redisDao.setCount(id, type, count);
        } else {
            return false;
        }
    }
}
