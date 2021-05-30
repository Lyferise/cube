package com.lyferise.cube.lang.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class CubeLexerTest {

    @Test
    public void shouldTokenizeNullString() {
        final CubeLexer lexer = new CubeLexer();
        lexer.tokenize(null);
        assertThat(lexer.next(), is(equalTo(null)));
    }

    @Test
    public void shouldTokenizeEmptyString() {
        final CubeLexer lexer = new CubeLexer();
        lexer.tokenize("");
        assertThat(lexer.next(), is(equalTo(null)));
    }
}