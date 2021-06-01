package com.lyferise.cube.lang.parser;

import com.lyferise.cube.lang.elements.Identifier;
import com.lyferise.cube.lang.elements.Symbol;
import com.lyferise.cube.lang.elements.constants.IntConstant;
import org.junit.jupiter.api.Disabled;
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
    public void shouldTokenizeWhitespaceString() {
        assertThat(tokenize("    "), is(empty()));
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

    @Test
    public void shouldTokenizeSingleSymbolWithWhitespace() {
        assertThat(
                tokenize("  *  "),
                contains(new Symbol("*")));
    }

    @Test
    public void shouldTokenizeIdentifiers() {
        assertThat(
                tokenize("aaa a.b xxx.yyy test_1 test1 _test1 __test2"),
                contains(
                        new Identifier("aaa"),
                        new Identifier("a"),
                        new Symbol("."),
                        new Identifier("b"),
                        new Identifier("xxx"),
                        new Symbol("."),
                        new Identifier("yyy"),
                        new Identifier("test_1"),
                        new Identifier("test1"),
                        new Identifier("_test1"),
                        new Identifier("__test2")));
    }

    @Test
    @Disabled
    public void shouldTokenizeIntConstants() {
        assertThat(
                tokenize("0 1 -1 +1 -2147483648 2147483647"),
                contains(
                        new IntConstant(0),
                        new IntConstant(1),
                        new IntConstant(-1),
                        new IntConstant(1),
                        new IntConstant(Integer.MIN_VALUE),
                        new IntConstant(Integer.MAX_VALUE)));
    }
}