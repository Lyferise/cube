package com.lyferise.cube.lang.parser;

import com.lyferise.cube.lang.elements.Symbol;
import org.junit.jupiter.api.Test;

import static com.lyferise.cube.lang.parser.CubeLexer.tokenize;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CubeLexerTest {

    @Test
    public void shouldTokenizeNullString() {
        assertThat(tokenize(null), is(empty()));
    }

    @Test
    public void shouldTokenizeEmptyString() {
        assertThat(tokenize(""), is(empty()));
    }

    @Test
    public void shouldTokenizeSymbols() {
        assertThat(
                tokenize("*.='()"),
                contains(
                        new Symbol("*"),
                        new Symbol("."),
                        new Symbol("="),
                        new Symbol("'"),
                        new Symbol("("),
                        new Symbol(")")));
    }
}