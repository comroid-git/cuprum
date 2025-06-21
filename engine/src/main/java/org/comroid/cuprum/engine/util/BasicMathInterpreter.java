package org.comroid.cuprum.engine.util;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.UnbufferedCharStream;
import org.comroid.cuprum.grammar.BasicMathBaseVisitor;
import org.comroid.cuprum.grammar.BasicMathLexer;
import org.comroid.cuprum.grammar.BasicMathParser;
import org.jetbrains.annotations.NotNull;

import java.io.StringReader;
import java.util.Arrays;
import java.util.function.DoubleBinaryOperator;

public final class BasicMathInterpreter extends BasicMathBaseVisitor<@NotNull Double> {
    public static double eval(String text) {
        try (var sr = new StringReader(text.replace(',', '.'))) {
            var ucs  = new UnbufferedCharStream(sr);
            var bml  = new BasicMathLexer(ucs);
            var cts  = new CommonTokenStream(bml);
            var bmp  = new BasicMathParser(cts);
            var expr = bmp.expr();

            return new BasicMathInterpreter().visit(expr);
        }
    }

    private BasicMathInterpreter() {
    }

    @Override
    public @NotNull Double visitExprOperator(BasicMathParser.ExprOperatorContext ctx) {
        var    ident = ctx.operator().getText().charAt(0);
        double left  = visit(ctx.left), right = visit(ctx.right);
        return Arrays.stream(Operator.values())
                .filter(op -> op.c == ident)
                .findAny()
                .orElseThrow()
                .applyAsDouble(left, right);
    }

    @Override
    public @NotNull Double visitExprNumber(BasicMathParser.ExprNumberContext ctx) {
        return Double.parseDouble(ctx.getText());
    }

    @RequiredArgsConstructor
    @FieldDefaults(makeFinal = true)
    enum Operator implements DoubleBinaryOperator {
        PLUS('+') {
            @Override
            public double applyAsDouble(double left, double right) {
                return left + right;
            }
        }, MINUS('-') {
            @Override
            public double applyAsDouble(double left, double right) {
                return left - right;
            }
        }, MULTIPLY('*') {
            @Override
            public double applyAsDouble(double left, double right) {
                return left * right;
            }
        }, DIVIDE('/') {
            @Override
            public double applyAsDouble(double left, double right) {
                return left / right;
            }
        }, MODULUS('%') {
            @Override
            public double applyAsDouble(double left, double right) {
                return left % right;
            }
        }, ROOF('^') {
            @Override
            public double applyAsDouble(double left, double right) {
                return Math.pow(left, right);
            }
        };

        char c;
    }
}
