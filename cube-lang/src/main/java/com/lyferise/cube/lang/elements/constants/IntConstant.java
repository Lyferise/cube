package com.lyferise.cube.lang.elements.constants;

import com.lyferise.cube.lang.formatter.ElementFormatter;

import static com.lyferise.cube.lang.elements.ElementType.INT_CONSTANT;

public class IntConstant extends Constant {
    private final int value;

    public IntConstant(final int value) {
        super(INT_CONSTANT);
        this.value = value;
    }

    @Override
    public void format(final ElementFormatter formatter) {
        formatter.write(value);
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof IntConstant)) return false;
        return value == ((IntConstant) object).value;
    }
}