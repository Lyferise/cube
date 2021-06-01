package com.lyferise.cube.lang.elements;

import com.lyferise.cube.lang.formatter.ElementFormatter;

import java.util.ArrayList;
import java.util.List;

import static com.lyferise.cube.lang.elements.ElementType.MULTIPART_IDENTIFIER;

public class MultipartIdentifier extends Element {
    private final List<Identifier> identifiers = new ArrayList<>();

    public MultipartIdentifier() {
        super(MULTIPART_IDENTIFIER);
    }

    public MultipartIdentifier(final String... parts) {
        super(MULTIPART_IDENTIFIER);
        for (final var part : parts) {
            add(new Identifier(part));
        }
    }

    public void add(final Identifier identifier) {
        identifiers.add(identifier);
    }

    @Override
    public void format(final ElementFormatter formatter) {
        final var size = identifiers.size();
        for (var i = 0; i < size; i++) {
            if (i > 0) formatter.write('.');
            identifiers.get(i).format(formatter);
        }
    }
}