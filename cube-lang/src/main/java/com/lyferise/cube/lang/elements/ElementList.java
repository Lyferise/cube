package com.lyferise.cube.lang.elements;

import com.lyferise.cube.lang.formatter.ElementFormatter;

import java.util.ArrayList;
import java.util.List;

import static com.lyferise.cube.lang.elements.ElementType.ELEMENT_LIST;

public class ElementList extends Element {
    private final List<Element> elements = new ArrayList<>();

    public ElementList() {
        super(ELEMENT_LIST);
    }

    public void add(final Element element) {
        elements.add(element);
    }

    @Override
    public void format(final ElementFormatter formatter) {
        final var size = elements.size();
        for (var i = 0; i < size; i++) {
            if (i > 0) formatter.write(", ");
            elements.get(i).format(formatter);
        }
    }

    public static ElementList singleElementList(final Element element) {
        final var list = new ElementList();
        list.add(element);
        return list;
    }
}