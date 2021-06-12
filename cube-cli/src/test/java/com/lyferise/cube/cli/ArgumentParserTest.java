package com.lyferise.cube.cli;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ArgumentParserTest {
    ArgumentParser parser = new ArgumentParser();

    @Test
    public void checkHelpCommand() {
        final String[] args = {"--help"};
        assertThat(parser.parse(args), is(false));
    }

    @Test
    public void checkIncompleteCmd1() {
        final String[] args = {"--host", "127.0.0.1"};
        assertThat(parser.parse(args), is(false));
    }

    @Test
    public void checkIncompleteCmd2() {
        final String[] args = {"--u", "test"};
        assertThat(parser.parse(args), is(false));
    }

    @Test
    public void checkIncompleteCmd3() {
        final String[] args = {"-p", "password"};
        assertThat(parser.parse(args), is(false));
    }

    @Test
    public void checkCompleteCmd() {
        final String[] args = {"--host", "127.0.0.1", "-u", "test", "-p", "password"};
        assertThat(parser.parse(args), is(true));
        assertThat(parser.getHost(), is("127.0.0.1"));
        assertThat(parser.getUser(), is("test"));
        assertThat(parser.getPassword(), is("password"));
    }
}
