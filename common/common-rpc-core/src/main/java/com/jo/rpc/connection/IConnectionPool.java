package com.jo.rpc.connection;

import java.util.List;

/**
 * @author Jo
 * @date 2024/7/8
 */
public interface IConnectionPool {
    List<IConnection> getAllConnections();

    IConnection getConnection();

    void addConnection(IConnection connection);

    void releaseConnection(Long id);

    int currentSize();

    void close();
}
