package com.lyferise.cube.lang.elements.constants;

import com.lyferise.cube.lang.elements.Element;
import com.lyferise.cube.lang.formatter.ElementFormatter;

import static com.lyferise.cube.lang.elements.ElementType.INT_CONSTANT;

public class IntConstant extends  Element {
    private final int value;

    public IntConstant(final int value) {
        super(INT_CONSTANT);
        this.value = value;
    }

    @Override
    public void format(final ElementFormatter formatter) {
        formatter.write(value);
    }
}