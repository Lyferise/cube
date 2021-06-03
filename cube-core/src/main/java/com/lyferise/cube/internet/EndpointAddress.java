package com.lyferise.cube.internet;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.net.InetSocketAddress;

@Value
@AllArgsConstructor
public class EndpointAddress {
    IpAddress ipAddress;
    int port;

    public EndpointAddress(final InetSocketAddress socketAddress) {
        this(new IpAddress(socketAddress.getAddress()), socketAddress.getPort());
    }
}