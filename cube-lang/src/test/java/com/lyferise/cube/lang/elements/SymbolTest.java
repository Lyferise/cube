package com.lyferise.cube.lang.elements;

import org.junit.jupiter.api.Test;

import static com.lyferise.cube.lang.CubeLanguage.cube;
import static com.lyferise.cube.lang.elements.SymbolType.ASTERISK;
import static com.lyferise.cube.lang.formatter.ElementFormatter.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class SymbolTest {

    @Test
    public void shouldFormatSymbol() {
        final var symbol = new Symbol(ASTERISK);
        assertThat(format(cube(), symbol), is(equalTo("*")));
    }
}