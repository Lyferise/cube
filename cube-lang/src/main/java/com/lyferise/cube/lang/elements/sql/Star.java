package com.lyferise.cube.lang.elements.sql;

import com.lyferise.cube.lang.elements.Element;
import com.lyferise.cube.lang.formatter.ElementFormatter;

import static com.lyferise.cube.lang.elements.ElementType.STAR;

public class Star extends Element {

    public Star() {
        super(STAR);
    }

    @Override
    public void format(final ElementFormatter formatter) {
        formatter.write('*');
    }
}