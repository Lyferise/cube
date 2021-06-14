package com.lyferise.cube.lang.parser;

import org.junit.jupiter.api.Test;

import static com.lyferise.cube.lang.CubeLanguage.cube;
import static com.lyferise.cube.lang.formatter.ElementFormatter.format;
import static com.lyferise.cube.lang.parser.CubeParser.parse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;

public class CubeParserStressTest {

    @Test
    public void shouldParseRandomElements() {

        // Parse.
        final var generator = new RandomElementGenerator();
        String minText = null;
        Throwable minCause = null;
        final var elementCount = 10000;
        var errorCount = 0;
        for (var i = 0; i < elementCount; i++) {
            final var element = generator.element();
            final var text = format(cube(), element);
            try {
                assertThat(parse(cube(), text), is(equalTo(element)));
            } catch (final Throwable e) {
                errorCount++;
                if (minText == null || text.length() < minText.length()) {
                    minText = text;
                    minCause = e;
                }
            }
        }

        // Errors?
        if (errorCount > 0) {
            fail(
                    "Failed: " + errorCount + " / " + elementCount + ". Minimum example: " + minText,
                    minCause);
        }
    }
}