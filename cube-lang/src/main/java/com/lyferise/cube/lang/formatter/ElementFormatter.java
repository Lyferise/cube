package com.lyferise.cube.lang.formatter;

import com.lyferise.cube.lang.LanguageDefinition;
import com.lyferise.cube.lang.elements.Element;

public class ElementFormatter {
    private final LanguageDefinition languageDefinition;
    private final StringBuilder text = new StringBuilder();

    public ElementFormatter(final LanguageDefinition languageDefinition) {
        this.languageDefinition = languageDefinition;
    }

    public LanguageDefinition getLanguageDefinition() {
        return languageDefinition;
    }

    public void write(final String text) {
        this.text.append(text);
    }

    public void write(final char ch) {
        this.text.append(ch);
    }

    public void write(final int value) {
        this.text.append(value);
    }

    public void write(final long value) {
        this.text.append(value);
    }

    @Override
    public String toString() {
        return text.toString();
    }

    public static String format(final LanguageDefinition languageDefinition, final Element element) {
        final var formatter = new ElementFormatter(languageDefinition);
        element.format(formatter);
        return formatter.toString();
    }
}