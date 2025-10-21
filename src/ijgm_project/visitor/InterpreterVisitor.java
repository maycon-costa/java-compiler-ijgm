package ijgm_project.visitor;

import ijgm_project.parser.ast.*;
import ijgm_project.symbol_table.ExecutionContext;

/**
 * Implementa a operação de execução na AST.
 * (Refatorado para implementar Visitor<Object> e retornar valores,
 * eliminando o campo 'result').
 */
public class InterpreterVisitor implements Visitor<Object> {
    private final ExecutionContext context = new ExecutionContext();
    // private Object result = null; // <-- REMOVIDO! (Crítica do colega)

    /**
     * Avalia uma expressão e retorna seu valor.
     * (Função helper para clareza).
     */
    private Object evaluate(Expression expression) {
        return expression.accept(this);
    }

    /**
     * Executa um comando.
     * (Função helper para clareza).
     */
    private void execute(Statement statement) {
        statement.accept(this);
    }

    @Override
    public Object visit(AssignStatement statement) {
        // 1. Avalia a expressão do lado direito
        Object value = evaluate(statement.getExpression());
        // 2. Lógica Semântica: Armazena o valor.
        context.put(statement.getVariableName(), value);
        return null; // Comandos não retornam valor
    }

    @Override
    public Object visit(PrintStatement statement) {
        // Avalia a expressão e imprime o resultado.
        Object value = evaluate(statement.getExpression());
        System.out.println("Output: " + value);
        return null; // Comandos não retornam valor
    }

    @Override
    public Object visit(WhileStatement statement) {
        // Avalia a condição antes de cada iteração.
        Object condition = evaluate(statement.getCondition());
        if (!(condition instanceof Boolean)) {
            throw new RuntimeException("Condição do 'while' deve ser um booleano.");
        }
        while ((Boolean) condition) {
            // Executa o corpo do loop
            for (Statement stmt : statement.getBody()) {
                execute(stmt);
            }
            // Reavalia a condição
            condition = evaluate(statement.getCondition());
        }
        return null; // Comandos não retornam valor
    }

    @Override
    public Object visit(IfStatement statement) {
        // Avalia a condição
        Object condition = evaluate(statement.getCondition());
        if (!(condition instanceof Boolean)) {
            throw new RuntimeException("Condição do 'if' deve ser um booleano.");
        }
        // Executa o bloco 'then' ou 'else'
        if ((Boolean) condition) {
            for (Statement stmt : statement.getThenBody()) {
                execute(stmt);
            }
        } else {
            if (statement.getElseBody() != null) {
                for (Statement stmt : statement.getElseBody()) {
                    execute(stmt);
                }
            }
        }
        return null; // Comandos não retornam valor
    }

    // ------------------------------------------------------------------------
    // Métodos visit para expressões (Avaliação de valores)
    // ------------------------------------------------------------------------

    @Override
    public Object visit(BinaryExpression expression) {
        // Avaliação recursiva dos lados esquerdo e direito.
        Object leftVal = evaluate(expression.getLeft());
        Object rightVal = evaluate(expression.getRight());

        // Lógica de Coerção (Promoção de Tipo)
        boolean isNumericOperation = switch (expression.getOperator()) {
            case GREATER_THAN, LESS_THAN, GREATER_EQUAL, LESS_EQUAL,
                    MINUS, MULTIPLY, DIVIDE ->
                true;
            case PLUS ->
                !(leftVal instanceof String || rightVal instanceof String);
            default ->
                false;
        };

        if (isNumericOperation) {
            if (leftVal instanceof Float && rightVal instanceof Integer) {
                rightVal = ((Integer) rightVal).floatValue();
            } else if (leftVal instanceof Integer && rightVal instanceof Float) {
                leftVal = ((Integer) leftVal).floatValue();
            }
        }

        switch (expression.getOperator()) {
            // Comparações
            case GREATER_THAN -> {
                if (leftVal instanceof Integer && rightVal instanceof Integer) {
                    return (Integer) leftVal > (Integer) rightVal;
                } else if (leftVal instanceof Float && rightVal instanceof Float) {
                    return (Float) leftVal > (Float) rightVal;
                }
                throw new RuntimeException("Operadores de comparação suportam apenas números");
            }
            case LESS_THAN -> {
                if (leftVal instanceof Integer && rightVal instanceof Integer) {
                    return (Integer) leftVal < (Integer) rightVal;
                } else if (leftVal instanceof Float && rightVal instanceof Float) {
                    return (Float) leftVal < (Float) rightVal;
                }
                throw new RuntimeException("Operadores de comparação suportam apenas números");
            }
            case GREATER_EQUAL -> {
                if (leftVal instanceof Integer && rightVal instanceof Integer) {
                    return (Integer) leftVal >= (Integer) rightVal;
                } else if (leftVal instanceof Float && rightVal instanceof Float) {
                    return (Float) leftVal >= (Float) rightVal;
                }
                throw new RuntimeException("Operadores de comparação suportam apenas números");
            }
            case LESS_EQUAL -> {
                if (leftVal instanceof Integer && rightVal instanceof Integer) {
                    return (Integer) leftVal <= (Integer) rightVal;
                } else if (leftVal instanceof Float && rightVal instanceof Float) {
                    return (Float) leftVal <= (Float) rightVal;
                }
                throw new RuntimeException("Operadores de comparação suportam apenas números");
            }

            // Igualdade
            case EQUAL_EQUAL -> {
                return leftVal.equals(rightVal);
            }
            case NOT_EQUAL -> {
                return !leftVal.equals(rightVal);
            }

            // Lógicos
            case AND -> {
                if (leftVal instanceof Boolean && rightVal instanceof Boolean) {
                    return (Boolean) leftVal && (Boolean) rightVal;
                }
                throw new RuntimeException("Operadores lógicos suportam apenas booleanos.");
            }
            case OR -> {
                if (leftVal instanceof Boolean && rightVal instanceof Boolean) {
                    return (Boolean) leftVal || (Boolean) rightVal;
                }
                throw new RuntimeException("Operadores lógicos suportam apenas booleanos.");
            }

            // Aritméticos
            case MINUS -> {
                if (leftVal instanceof Integer && rightVal instanceof Integer) {
                    return (Integer) leftVal - (Integer) rightVal;
                } else if (leftVal instanceof Float && rightVal instanceof Float) {
                    return (Float) leftVal - (Float) rightVal;
                }
                throw new RuntimeException("Operadores aritméticos (MINUS) suportam apenas números");
            }
            case MULTIPLY -> {
                if (leftVal instanceof Integer && rightVal instanceof Integer) {
                    return (Integer) leftVal * (Integer) rightVal;
                } else if (leftVal instanceof Float && rightVal instanceof Float) {
                    return (Float) leftVal * (Float) rightVal;
                }
                throw new RuntimeException("Operadores aritméticos (MULTIPLY) suportam apenas números");
            }
            case DIVIDE -> {
                if (leftVal instanceof Integer && rightVal instanceof Integer) {
                    if ((Integer) rightVal == 0) {
                        throw new RuntimeException("Divisão por zero");
                    }
                    return (Integer) leftVal / (Integer) rightVal;
                } else if (leftVal instanceof Float && rightVal instanceof Float) {
                    if ((Float) rightVal == 0.0f) {
                        throw new RuntimeException("Divisão por zero");
                    }
                    return (Float) leftVal / (Float) rightVal;
                }
                throw new RuntimeException("Operadores aritméticos (DIVIDE) suportam apenas números");
            }
            case PLUS -> {
                if (leftVal instanceof String || rightVal instanceof String) {
                    return String.valueOf(leftVal) + String.valueOf(rightVal);
                } else if (leftVal instanceof Integer && rightVal instanceof Integer) {
                    return (Integer) leftVal + (Integer) rightVal;
                } else if (leftVal instanceof Float && rightVal instanceof Float) {
                    return (Float) leftVal + (Float) rightVal;
                }
                throw new RuntimeException("Tipos incompatíveis para o operador PLUS");
            }

            default -> throw new RuntimeException(
                    "Operador binário não suportado ou token inesperado: " + expression.getOperator());
        }
    }

    // TODO: Considerar uma classe abstrata LiteralExpression para evitar repetição em FloatExpression, NumberExpression, StringExpression e BooleanExpression
    @Override
    public Object visit(NumberExpression expression) {
        return expression.getValue(); // Retorna o valor
    }

    @Override
    public Object visit(FloatExpression expression) {
        return expression.getValue(); // Retorna o valor
    }

    @Override
    public Object visit(VariableExpression expression) {
        return context.get(expression.getName()); // Retorna o valor
    }

    @Override
    public Object visit(StringExpression expression) {
        return expression.getValue(); // Retorna o valor
    }

    @Override
    public Object visit(DeclarationStatement statement) {
        context.declare(statement.getVariableName(), statement.getType());
        // A palavra 'Read' foi REMOVIDA daqui.
        return null; // Comandos não retornam valor
    }

    @Override
    public Object visit(BooleanExpression expression) {
        return expression.getValue(); // Retorna o valor
    }
}