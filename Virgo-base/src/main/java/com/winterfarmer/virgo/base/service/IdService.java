package com.winterfarmer.virgo.base.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.winterfarmer.virgo.base.service.coniferouse.gen.Coniferous;
import com.winterfarmer.virgo.log.VirgoLogger;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

/**
 * Created by yangtianhang on 15-3-24.
 */
public class IdService {
    private String token;
    private final List<TTransport> servers = Lists.newArrayList();

    private final int DEFAULT_PORT = 9100;
    private final int TIMEOUT_MS = 3000;
    private TTransport transport = null;
    private Coniferous.Client client = null;

    private static final int MAX_CONNECTION_ATTEMPTS = 5;
    private static final int MAX_GET_ID_COUNT = 255;

    public IdService() {
    }

    public IdService(String token, String addressStr) {
        setToken(token);
        setAddress(addressStr);
    }

    public void setToken(String token) {
        this.token = token == null ? "" : token;
    }

    public void setAddress(String addressStr) {
        String[] addresses = addressStr.split(",");
        for (String addr : addresses) {
            try {
                String[] split = addr.split(":", 2);
                String host = split[0];
                final int port;
                if (split.length == 2) {
                    port = Integer.parseInt(split[1]);
                } else {
                    port = DEFAULT_PORT;
                }

                InetSocketAddress address = new InetSocketAddress(host, port);
                if (address.isUnresolved()) {
                    VirgoLogger.error("Invalid address: {}", addr);
                } else {
                    servers.add(new TFramedTransport(new TSocket(host, port, TIMEOUT_MS)));
                }
            } catch (Exception e) {
                VirgoLogger.error(e, "pass id service failed: {}", addr);
            }
        }
    }


    private void connect() throws TTransportException {
        if (transport != null && transport.isOpen()) {
            return;
        }

        final int size = servers.size();
        int tries = Math.min(MAX_CONNECTION_ATTEMPTS, size);
        int n = (int) (Math.random() * size);

        while (true) {
            try {
                TTransport tTransport = servers.get(n % size);
                if (!tTransport.isOpen()) {
                    tTransport.open();
                }

                this.transport = tTransport;
                TProtocol protocol = new TBinaryProtocol(transport);
                this.client = new Coniferous.Client(protocol);
                break;
            } catch (TTransportException e) {
                disconnect();
                tries--;
                if (tries < 1) {
                    throw e;
                }
                ++n;
            }
        }
    }

    private void disconnect() {
        if (transport != null) {
            transport.close();
            transport = null;
        }
    }

    public void close() {
        disconnect();
    }

    public synchronized ImmutableList<Long> getIds(int num) {
        if (num < 1) {
            num = 1;
        } else if (num > MAX_GET_ID_COUNT) {
            throw new IllegalArgumentException("requested more than " + MAX_GET_ID_COUNT + " ids");
        }
        try {
            connect();
            return ImmutableList.copyOf(client.get_ids(this.token, (short) num));
        } catch (TException e) {
            VirgoLogger.error(e, "getIds failed: {}", e.getMessage());
            disconnect();
            return null;
        }
    }

    public long getId() {
        return getIds(1).get(0);
    }

    public static void main(String[] args) {
        IdService id = new IdService("a", "127.0.0.1:9100");
        while (true) {
            List<Long> d = id.getIds(10);
            for (long kd : d) {
                System.out.println(kd);
            }
        }
    }
}
