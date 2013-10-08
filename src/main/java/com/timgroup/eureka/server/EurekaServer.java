package com.timgroup.eureka.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

public class EurekaServer {

    private final Container container = new EurekaContainer();
    private final Connection connection;
    private final InetSocketAddress address;
    
    public EurekaServer(int port) throws IOException {
        final Server server = new ContainerServer(container);
        connection = new SocketConnection(server);
        address = new InetSocketAddress(port);
    }

    public void start() throws IOException {
        final SocketAddress socketAddress = connection.connect(address);
        
        if (socketAddress instanceof InetSocketAddress) {
            System.out.format("Starting CI-Eye server on http://localhost:%d", ((InetSocketAddress)socketAddress).getPort());
            return;
        }
        
        System.out.format("Starting CI-Eye server on: %s", socketAddress.toString());
    }
}
