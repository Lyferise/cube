package com.lyferise.cube.lang.elements;

import com.lyferise.cube.lang.elements.constants.IntConstant;
import org.junit.jupiter.api.Test;

import static com.lyferise.cube.lang.CubeLanguage.cube;
import static com.lyferise.cube.lang.Operator.*;
import static com.lyferise.cube.lang.formatter.ElementFormatter.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class UnaryExpressionTest {

    @Test
    public void shouldFormatPositiveExpression() {
        final var expression = new UnaryExpression(POSITIVE, new Identifier("x"));
        assertThat(format(cube(), expression), is(equalTo("+x")));
    }

    @Test
    public void shouldFormatNegativeExpression() {
        final var expression = new UnaryExpression(NEGATIVE, new IntConstant(4));
        assertThat(format(cube(), expression), is(equalTo("-4")));
    }

    @Test
    public void shouldFormatNotExpression() {
        final var expression = new UnaryExpression(NOT, new Identifier("x"));
        assertThat(format(cube(), expression), is(equalTo("not x")));
    }
}