package com.lyferise.cube.internet;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static java.net.InetAddress.getByName;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class IpAddressTest {

    @Test
    @SneakyThrows
    public void shouldFormatIPv4Address() {
        final var text = "63.56.142.72";
        final var address = new IpAddress(getByName(text));
        assertThat(address.toString(), is(equalTo(text)));
    }

    @Test
    @SneakyThrows
    public void shouldFormatIPv6Address() {
        final var text = "2001:db8:85a3:0:0:8a2e:370:7334";
        final var address = new IpAddress(getByName(text));
        assertThat(address.toString(), is(equalTo(text)));
    }
}
