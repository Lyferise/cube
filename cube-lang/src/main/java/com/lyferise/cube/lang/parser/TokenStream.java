package com.lyferise.cube.lang.parser;

import com.lyferise.cube.lang.elements.Element;

public interface TokenStream {

    Element next();
}