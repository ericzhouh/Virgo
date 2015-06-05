package com.winterfarmer.virgo.storage.counter.dao;

/**
 * Created by yangtianhang on 15/6/4.
 */
public interface CounterDao {
    Integer getCount(long id, int type);

    boolean setCount(long id, int type, Integer count);
}
