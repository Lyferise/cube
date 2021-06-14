package com.lyferise.cube.lang.parser;

import com.lyferise.cube.lang.elements.Element;
import com.lyferise.cube.lang.elements.Identifier;
import com.lyferise.cube.lang.elements.UnaryExpression;
import com.lyferise.cube.lang.elements.constants.Constant;
import com.lyferise.cube.lang.elements.constants.IntConstant;

import java.util.Random;

import static com.lyferise.cube.lang.Operator.NEGATIVE;
import static com.lyferise.cube.lang.Operator.NOT;

public class RandomElementGenerator {
    private final Random random = new Random();

    public Element element() {
        return switch (random.nextInt(3)) {
            case 0 -> intConstant();
            case 1 -> identifier();
            case 2 -> unaryExpression();
            default -> throw new UnsupportedOperationException();
        };
    }

    public IntConstant intConstant() {
        return new IntConstant(random.nextInt());
    }

    public Identifier identifier() {
        return new Identifier(Character.toString('a' + random.nextInt(26)));
    }

    private Element unaryExpression() {
        final var operator = random.nextBoolean() ? NOT : NEGATIVE;
        final var operand = element();
        return operator == NEGATIVE && operand instanceof Constant
                ? operand
                : new UnaryExpression(operator, operand);
    }
}