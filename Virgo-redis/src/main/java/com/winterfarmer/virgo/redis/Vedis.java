package com.winterfarmer.virgo.redis;

import com.winterfarmer.virgo.redis.command.JedisReadCommands;
import com.winterfarmer.virgo.redis.command.JedisWriteCommands;
import com.winterfarmer.virgo.redis.command.MultiKeyReadCommands;
import com.winterfarmer.virgo.redis.command.MultiKeyWriteCommands;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.MultiKeyCommands;

/**
 * Created by yangtianhang on 15-4-7.
 */
public interface Vedis extends
        JedisWriteCommands, JedisReadCommands, JedisCommands,
        MultiKeyWriteCommands, MultiKeyReadCommands, MultiKeyCommands {
}

