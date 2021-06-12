package com.lyferise.cube.lang.elements;

import com.lyferise.cube.lang.elements.constants.IntConstant;
import org.junit.jupiter.api.Test;

import static com.lyferise.cube.lang.CubeLanguage.cube;
import static com.lyferise.cube.lang.Operator.*;
import static com.lyferise.cube.lang.formatter.ElementFormatter.format;
import static com.lyferise.cube.lang.parser.CubeParser.parse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class BinaryExpressionTest {

    @Test
    public void shouldFormatBinaryExpression1() {
        final var expression = new BinaryExpression(GREATER,
                new Identifier("a"),
                new Identifier("b"));

        assertThat(format(cube(), expression), is(equalTo("a > b")));
    }

    @Test
    public void shouldFormatBinaryExpression2() {
        final var expression = new BinaryExpression(ADD,
                new IntConstant(1),
                new BinaryExpression(ADD,
                        new IntConstant(2),
                        new IntConstant(3)));

        assertThat(format(cube(), expression), is(equalTo("1 + 2 + 3")));
    }

    @Test
    public void shouldFormatBinaryExpression3() {
        final var expression = new BinaryExpression(ADD,
                new Identifier("a"),
                new BinaryExpression(MULTIPLY,
                        new Identifier("b"),
                        new Identifier("c")));

        assertThat(format(cube(), expression), is(equalTo("a + b * c")));
    }

    @Test
    public void shouldFormatBinaryExpression4() {
        final var expression = new BinaryExpression(ADD,
                new BinaryExpression(MULTIPLY,
                        new Identifier("a"),
                        new Identifier("b")),
                new Identifier("c"));

        assertThat(format(cube(), expression), is(equalTo("a * b + c")));
    }

    @Test
    public void shouldFormatBinaryExpression5() {
        final var expression = new BinaryExpression(MULTIPLY,
                new BinaryExpression(ADD,
                        new Identifier("a"),
                        new Identifier("b")),
                new Identifier("c"));

        assertThat(format(cube(), expression), is(equalTo("(a + b) * c")));
    }

    @Test
    public void shouldFormatBinaryExpression6() {
        final var expression = new BinaryExpression(MULTIPLY,
                new BinaryExpression(ADD,
                        new Identifier("a"),
                        new Identifier("b")),
                new BinaryExpression(ADD,
                        new Identifier("c"),
                        new Identifier("d")));

        assertThat(format(cube(), expression), is(equalTo("(a + b) * (c + d)")));
    }
}