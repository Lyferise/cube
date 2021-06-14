package com.lyferise.cube.lang.elements;

import com.lyferise.cube.lang.Keyword;
import com.lyferise.cube.lang.formatter.ElementFormatter;

import static com.lyferise.cube.lang.elements.ElementType.KEYWORD;

public class KeywordToken extends Element {
    private final Keyword keyword;

    public KeywordToken(final Keyword keyword) {
        super(KEYWORD);
        this.keyword = keyword;
    }

    public Keyword getKeyword() {
        return keyword;
    }

    @Override
    public void format(final ElementFormatter formatter) {
        formatter.write(keyword.text());
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof KeywordToken)) return false;
        return keyword == ((KeywordToken) object).keyword;
    }
}