package com.lyferise.cube.lang;

import com.lyferise.cube.lang.elements.Element;

public interface LanguageDefinition {

    int bindingPower(Operator operator);

    int bindingPower(Element element);
}