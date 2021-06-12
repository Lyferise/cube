package com.lyferise.cube.lang.elements.sql;

import com.lyferise.cube.lang.elements.BinaryExpression;
import com.lyferise.cube.lang.elements.ElementList;
import com.lyferise.cube.lang.elements.Identifier;
import com.lyferise.cube.lang.elements.MultipartIdentifier;
import com.lyferise.cube.lang.elements.constants.IntConstant;
import org.junit.jupiter.api.Test;

import static com.lyferise.cube.lang.CubeLanguage.cube;
import static com.lyferise.cube.lang.Operator.EQUAL;
import static com.lyferise.cube.lang.formatter.ElementFormatter.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class SelectStatementTest {

    @Test
    public void shouldFormatSelect1() {
        final var statement = new SelectStatement(
                new ElementList(new IntConstant(1)));

        assertThat(
                format(cube(), statement),
                is(equalTo("select 1")));
    }

    @Test
    public void shouldFormatSelectStarFrom() {
        final var statement = new SelectStatement(new ElementList(new Star()),
                new FromClause(new MultipartIdentifier("a", "b")));

        assertThat(
                format(cube(), statement),
                is(equalTo("select * from a.b")));
    }

    @Test
    public void shouldFormatSelectStarFromWhere() {
        final var statement = new SelectStatement(new ElementList(new Star()),
                new FromClause(new MultipartIdentifier("a", "b")),
                new WhereClause(
                        new BinaryExpression(EQUAL,
                                new Identifier("x"),
                                new Identifier("y"))));

        assertThat(
                format(cube(), statement),
                is(equalTo("select * from a.b where x = y")));
    }
}