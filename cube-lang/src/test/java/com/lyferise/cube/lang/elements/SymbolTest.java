package com.lyferise.cube.lang.elements;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class SymbolTest {

    @Test
    public void shouldFormatSymbol() {
        final var symbol = new Symbol("*");
        assertThat(symbol.toString(), is(equalTo("*")));
    }
}