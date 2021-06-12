package com.lyferise.cube.lang.parser;

import com.lyferise.cube.lang.elements.BinaryExpression;
import com.lyferise.cube.lang.elements.Identifier;
import com.lyferise.cube.lang.elements.constants.IntConstant;
import org.junit.jupiter.api.Test;

import static com.lyferise.cube.lang.CubeLanguage.cube;
import static com.lyferise.cube.lang.Operator.ADD;
import static com.lyferise.cube.lang.Operator.MULTIPLY;
import static com.lyferise.cube.lang.parser.CubeParser.parse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class CubeParserTest {

    @Test
    public void shouldParseIntConstant() {
        assertThat(parse(cube(), "1"), is(equalTo(new IntConstant(1))));
    }

    @Test
    public void shouldParseBinaryExpression1() {
        assertThat(
                parse(cube(), "1 + 2"),
                is(equalTo(
                        new BinaryExpression(ADD,
                                new IntConstant(1),
                                new IntConstant(2)))));
    }

    @Test
    public void shouldParseBinaryExpression2() {
        assertThat(
                parse(cube(), "1 + 2 + 3"),
                is(equalTo(
                        new BinaryExpression(ADD,
                                new BinaryExpression(ADD,
                                        new IntConstant(1),
                                        new IntConstant(2)),
                                new IntConstant(3)))));
    }

    @Test
    public void shouldParseBinaryExpression3() {
        assertThat(
                parse(cube(), "a + b * c"),
                is(equalTo(
                        new BinaryExpression(ADD,
                                new Identifier("a"),
                                new BinaryExpression(MULTIPLY,
                                        new Identifier("b"),
                                        new Identifier("c"))))));
    }

    @Test
    public void shouldParseBinaryExpression4() {
        assertThat(
                parse(cube(), "a * b + c"),
                is(equalTo(
                        new BinaryExpression(ADD,
                                new BinaryExpression(MULTIPLY,
                                        new Identifier("a"),
                                        new Identifier("b")),
                                new Identifier("c")))));
    }

    @Test
    public void shouldParseBinaryExpression5() {
        assertThat(
                parse(cube(), "(a + b) * c"),
                is(equalTo(
                        new BinaryExpression(MULTIPLY,
                                new BinaryExpression(ADD,
                                        new Identifier("a"),
                                        new Identifier("b")),
                                new Identifier("c")))));
    }

    @Test
    public void shouldParseBinaryExpression6() {
        assertThat(
                parse(cube(), "(a + b) * (c * d)"),
                is(equalTo(
                        new BinaryExpression(MULTIPLY,
                                new BinaryExpression(ADD,
                                        new Identifier("a"),
                                        new Identifier("b")),
                                new BinaryExpression(ADD,
                                        new Identifier("c"),
                                        new Identifier("d"))))));
    }

    @Test
    public void shouldParseBracketedIntConstant1() {
        assertThat(parse(cube(), "(1)"), is(equalTo(new IntConstant(1))));
    }

    @Test
    public void shouldParseBracketedIntConstant2() {
        assertThat(parse(cube(), "((2))"), is(equalTo(new IntConstant(2))));
    }

    @Test
    public void shouldParseBracketedIntConstant3() {
        assertThat(parse(cube(), "(((3)))"), is(equalTo(new IntConstant(3))));
    }
}