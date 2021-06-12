package com.lyferise.cube.lang.elements.constants;

import org.junit.jupiter.api.Test;

import static com.lyferise.cube.lang.CubeLanguage.cube;
import static com.lyferise.cube.lang.formatter.ElementFormatter.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class IntConstantTest {

    @Test
    public void shouldFormatIntConstant() {
        final var constant = new IntConstant(5);
        assertThat(format(cube(), constant), is(equalTo("5")));
    }
}