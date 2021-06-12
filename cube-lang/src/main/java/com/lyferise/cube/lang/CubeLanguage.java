package com.lyferise.cube.lang;

import com.lyferise.cube.lang.elements.Element;
import com.lyferise.cube.lang.elements.UnsupportedElementException;

import static com.lyferise.cube.lang.elements.SymbolType.*;

public class CubeLanguage implements LanguageDefinition {
    private static final CubeLanguage CUBE_LANGUAGE = new CubeLanguage();

    private static final int NO_POWER = 0;
    private static final int ADD_POWER = 1;
    private static final int MULTIPLY_POWER = 2;

    public static CubeLanguage cube() {
        return CUBE_LANGUAGE;
    }

    @Override
    public int bindingPower(final Operator operator) {
        return switch (operator) {
            case ADD -> ADD_POWER;
            case MULTIPLY -> MULTIPLY_POWER;
            default -> throw new UnsupportedOperatorException(operator);
        };
    }

    @Override
    public int bindingPower(final Element element) {
        if (element.is(CLOSE_BRACKET)) return NO_POWER;
        if (element.is(PLUS)) return ADD_POWER;
        if (element.is(ASTERISK)) return MULTIPLY_POWER;
        throw new UnsupportedElementException(this, element);
    }
}