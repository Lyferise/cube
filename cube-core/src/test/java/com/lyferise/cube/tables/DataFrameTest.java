package com.lyferise.cube.tables;

import org.junit.jupiter.api.Test;

import static java.lang.String.join;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class DataFrameTest {

    @Test
    public void shouldFormatEmptyDataFrame() {
        final var df = new DataFrame("email", "name");
        final var expected = new String[]{
                "+-------+------+",
                "| email | name |",
                "+-------+------+"
        };

        assertThat(df.toString(), is(equalTo(join("\n", expected))));
    }
}