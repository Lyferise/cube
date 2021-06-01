package com.lyferise.cube.lang.elements;

import org.junit.jupiter.api.Test;

import static com.lyferise.cube.lang.Operator.ADD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class BinaryExpressionTest {

    @Test
    public void shouldFormatBinaryExpression() {
        final var expression = new BinaryExpression(ADD,
                new Identifier("a"),
                new Identifier("b"));

        assertThat(expression.toString(), is(equalTo("a + b")));
    }
}