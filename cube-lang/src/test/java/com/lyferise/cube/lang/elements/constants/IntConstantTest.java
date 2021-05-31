package com.lyferise.cube.lang.elements.constants;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class IntConstantTest {

    @Test
    public void shouldFormatIntConstant() {
        final var constant = new IntConstant(5);
        assertThat(constant.toString(), is(equalTo("5")));
    }
}