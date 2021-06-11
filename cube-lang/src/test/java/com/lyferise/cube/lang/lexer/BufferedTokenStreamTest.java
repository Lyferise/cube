package com.lyferise.cube.lang.lexer;

import com.lyferise.cube.lang.elements.constants.IntConstant;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class BufferedTokenStreamTest {

    @Test
    public void shouldReadBufferedTokenStream() {

        // read
        final var tokenStream = new BufferedTokenStream(new LexerTokenStream(("1 2 3 4 5")));
        for (var i = 0; i < 5; i++) {
            final var token = new IntConstant(i + 1);
            assertThat(tokenStream.peek(), is(equalTo(token)));
            assertThat(tokenStream.next(), is(equalTo(token)));
        }

        // end of stream
        assertThat(tokenStream.peek(), is(equalTo(null)));
        assertThat(tokenStream.next(), is(equalTo(null)));
    }
}