package com.lyferise.cube.lang.elements;

import com.lyferise.cube.lang.LanguageDefinition;

import static com.lyferise.cube.lang.formatter.ElementFormatter.format;

public class UnsupportedElementException extends RuntimeException {

    public UnsupportedElementException(final LanguageDefinition languageDefinition, Element element) {
        super("Unsupported element " + element.getClass() + " " + format(languageDefinition, element));
    }
}