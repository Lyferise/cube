package com.lyferise.cube.lang.elements.constants;

import org.junit.jupiter.api.Test;

import static com.lyferise.cube.lang.CubeLanguage.cube;
import static com.lyferise.cube.lang.formatter.ElementFormatter.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class StringConstantTest {

    @Test
    public void shouldFormatStringConstant() {
        final var constant = new StringConstant("Hola!");
        assertThat(format(cube(), constant), is(equalTo("'Hola!'")));
    }
}