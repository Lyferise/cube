package com.lyferise.cube.lang.elements.constants;

import com.lyferise.cube.lang.elements.Element;
import com.lyferise.cube.lang.formatter.ElementFormatter;

import static com.lyferise.cube.lang.elements.ElementType.STRING_CONSTANT;

public class StringConstant extends Element {
    private final String text;

    public StringConstant(final String text) {
        super(STRING_CONSTANT);
        this.text = text;
    }

    @Override
    public void format(final ElementFormatter formatter) {
        formatter.write('\'');
        formatter.write(text);
        formatter.write('\'');
    }
}