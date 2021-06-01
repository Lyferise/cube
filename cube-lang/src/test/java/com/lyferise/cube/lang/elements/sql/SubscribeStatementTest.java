package com.lyferise.cube.lang.elements.sql;

import com.lyferise.cube.lang.elements.BinaryExpression;
import com.lyferise.cube.lang.elements.Identifier;
import org.junit.jupiter.api.Test;

import static com.lyferise.cube.lang.Operator.EQUAL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class SubscribeStatementTest {

    @Test
    public void shouldFormatSubscribeTo() {
        final var statement = new SubscribeStatement(new Identifier("x"));

        assertThat(
                statement.toString(),
                is(equalTo("subscribe to x")));
    }

    @Test
    public void shouldFormatSubscribeToWhere() {
        final var statement = new SubscribeStatement(new Identifier("x"),
                new WhereClause(
                        new BinaryExpression(EQUAL,
                                new Identifier("a"),
                                new Identifier("b"))));

        assertThat(
                statement.toString(),
                is(equalTo("subscribe to x where a = b")));
    }
}