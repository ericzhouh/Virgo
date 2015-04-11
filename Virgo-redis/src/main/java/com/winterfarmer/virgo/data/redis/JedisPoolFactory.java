package com.winterfarmer.virgo.data.redis;

import com.google.common.collect.Lists;
import com.winterfarmer.virgo.log.VirgoLogger;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import redis.clients.jedis.JedisPoolConfig;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yangtianhang on 15-4-9.
 */
public class JedisPoolFactory {
    public static final char MS_SEPARATOR = '|';
    public static final char IP_PORT_SEPARATOR = ':';
    public static final char SLAVE_SEPARATOR = ';';

    private final JedisReadPoolStorage jedisReadPoolFactory;
    private final JedisWritePoolStorage jedisWritePoolFactory;

    // connection格式:
    // ip:port|ip:port;ip:port
    // master|slave0;slave1
    public JedisPoolFactory(JedisPoolConfig config, String connection, int timeout, String password) {
        List<Pair<String, Integer>> conns = checkAndConvertConnection(connection);
        List<Pair<String, Integer>> readPoolConns = conns.subList(1, conns.size());
        if (StringUtils.isBlank(password)) {
            password = null;
        }

        this.jedisReadPoolFactory = new JedisReadPoolStorage(config, readPoolConns, timeout, password);
        this.jedisWritePoolFactory = new JedisWritePoolStorage(config, conns.get(0), timeout, password);
    }

    public JedisReadPoolStorage getJedisReadPoolFactory() {
        return jedisReadPoolFactory;
    }

    public JedisWritePoolStorage getJedisWritePoolFactory() {
        return jedisWritePoolFactory;
    }

    private static List<Pair<String, Integer>> checkAndConvertConnection(String connection) {
        final String errorMsg = "invalid redis connection info: " + connection;
        if (StringUtils.isBlank(connection)) {
            VirgoLogger.fatal("redis connection info cannot is empty");
            throw new RuntimeException("redis connection info cannot be empty");
        }

        String[] connectionSplit = StringUtils.split(connection, MS_SEPARATOR);
        if (ArrayUtils.isEmpty(connectionSplit) || connectionSplit.length < 2) {
            VirgoLogger.fatal(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        Pair<String, Integer> masterIpPort = checkAndCreateMasterIpPort(connectionSplit[0], connection);
        LinkedList<Pair<String, Integer>> slaveIpPortList = checkAndCreateSlaveIpPortList(connectionSplit[1], connection);
        slaveIpPortList.addFirst(masterIpPort);
        return slaveIpPortList;
    }

    private static Pair<String, Integer> checkAndCreateMasterIpPort(String connection, String errorMsg) {
        return checkAndCreateIpPort(connection, errorMsg);
    }

    private static LinkedList<Pair<String, Integer>> checkAndCreateSlaveIpPortList(String connections, String errorMsg) {
        String[] slaveConnSplit = StringUtils.split(connections, SLAVE_SEPARATOR);
        if (ArrayUtils.isEmpty(slaveConnSplit)) {
            VirgoLogger.fatal(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        LinkedList<Pair<String, Integer>> list = Lists.newLinkedList();
        for (String slaveConn : slaveConnSplit) {
            list.add(checkAndCreateIpPort(slaveConn, errorMsg));
        }

        return list;
    }

    private static Pair<String, Integer> checkAndCreateIpPort(String connection, String errorMsg) {
        String[] ipPortSpit = StringUtils.split(connection, IP_PORT_SEPARATOR);
        if (ArrayUtils.isEmpty(ipPortSpit) || ipPortSpit.length < 2) {
            VirgoLogger.fatal(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        try {
            InetAddress.getByName(ipPortSpit[0]);
        } catch (UnknownHostException e) {
            VirgoLogger.fatal(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        Integer port;
        try {
            port = Integer.parseInt(ipPortSpit[1]);
        } catch (Exception e) {
            VirgoLogger.fatal(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        return Pair.of(ipPortSpit[0], port);
    }
}
