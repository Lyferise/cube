package com.lyferise.cube.lang;

import com.lyferise.cube.lang.elements.Element;
import com.lyferise.cube.lang.elements.UnsupportedElementException;

import static com.lyferise.cube.lang.elements.SymbolType.*;

public class CubeLanguage implements LanguageDefinition {
    private static final CubeLanguage CUBE_LANGUAGE = new CubeLanguage();

    public static CubeLanguage cube() {
        return CUBE_LANGUAGE;
    }

    @Override
    public int bindingPower(final Element element) {
        if (element.is(CLOSE_BRACKET)) return 0;
        if (element.is(PLUS)) return 1;
        if (element.is(ASTERISK)) return 2;
        throw new UnsupportedElementException(this, element);
    }
}