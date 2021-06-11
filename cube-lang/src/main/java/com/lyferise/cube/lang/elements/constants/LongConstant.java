package com.lyferise.cube.lang.elements.constants;

import com.lyferise.cube.lang.formatter.ElementFormatter;

import static com.lyferise.cube.lang.elements.ElementType.LONG_CONSTANT;

public class LongConstant extends Constant {
    private final long value;

    public LongConstant(final long value) {
        super(LONG_CONSTANT);
        this.value = value;
    }

    @Override
    public void format(final ElementFormatter formatter) {
        formatter.write(value);
    }
}