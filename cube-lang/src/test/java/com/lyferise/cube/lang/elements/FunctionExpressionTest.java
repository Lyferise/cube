package com.lyferise.cube.lang.elements;

import com.lyferise.cube.lang.elements.constants.LongConstant;
import com.lyferise.cube.lang.elements.constants.StringConstant;
import org.junit.jupiter.api.Test;

import static com.lyferise.cube.lang.CubeLanguage.cube;
import static com.lyferise.cube.lang.Operator.EQUAL;
import static com.lyferise.cube.lang.formatter.ElementFormatter.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class FunctionExpressionTest {

    @Test
    public void shouldFormatFunctionExpression() {
        final var expression = new FunctionExpression(
                new Identifier("send_chat_message"),
                new ElementList(
                        new BinaryExpression(EQUAL,
                                new Identifier("channel_id"),
                                new LongConstant(78910283918293L)),
                        new BinaryExpression(EQUAL,
                                new Identifier("text"),
                                new StringConstant("Hola!"))));

        assertThat(
                format(cube(), expression),
                is(equalTo("send_chat_message(channel_id = 78910283918293, text = 'Hola!')")));
    }
}