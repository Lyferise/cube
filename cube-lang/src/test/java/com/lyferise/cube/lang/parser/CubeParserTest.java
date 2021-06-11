package com.lyferise.cube.lang.parser;

import com.lyferise.cube.lang.elements.BinaryExpression;
import com.lyferise.cube.lang.elements.constants.IntConstant;
import org.junit.jupiter.api.Test;

import static com.lyferise.cube.lang.Operator.ADD;
import static com.lyferise.cube.lang.parser.CubeParser.parse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class CubeParserTest {

    @Test
    public void shouldParse1() {
        assertThat(parse("1"), is(equalTo(new IntConstant(1))));
    }

    @Test
    public void shouldParse1Plus2() {
        assertThat(
                parse("1 + 2"),
                is(equalTo(
                        new BinaryExpression(ADD,
                                new IntConstant(1),
                                new IntConstant(2)))));
    }
}