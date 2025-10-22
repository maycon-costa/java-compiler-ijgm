package ijgm_project.visitor;

import ijgm_project.lexer.TokenType;
import ijgm_project.parser.ast.*;
import ijgm_project.symbol_table.ExecutionContext;

/**
 * Implementa a operação de execução na AST.
 * (Refatorado para implementar Visitor<Object> e retornar valores).
 * (Refatorado para assumir a lógica semântica de atribuição, resolvendo o SOLID).
 * (Ajustado para evitar NullPointerException em checagens de tipo).
 */
public class InterpreterVisitor implements Visitor<Object> {
    private final ExecutionContext context = new ExecutionContext();

    private Object evaluate(Expression expression) {
        return expression.accept(this);
    }

    private void execute(Statement statement) {
        statement.accept(this);
    }
    
    private String getTypeName(Object obj) {
        if (obj == null) {
            return "null";
        }
        return obj.getClass().getSimpleName();
    }

    // ... (visit(AssignStatement), visit(PrintStatement), visit(WhileStatement), visit(IfStatement) NÃO MUDAM) ...
    @Override
    public Object visit(AssignStatement statement) {
        Object value = evaluate(statement.getExpression());
        String varName = statement.getVariableName();
        TokenType expectedType = context.getDeclaredType(varName);

        if (expectedType == TokenType.FLOAT && value instanceof Integer) {
            value = ((Integer) value).floatValue();
        }

        if (expectedType == TokenType.INT && !(value instanceof Integer)) {
            throw new RuntimeException(
                    "Erro Semântico: Esperado INT para '" + varName + "', mas recebido " + getTypeName(value));
        }
        if (expectedType == TokenType.FLOAT && !(value instanceof Float)) {
            throw new RuntimeException(
                    "Erro Semântico: Esperado FLOAT para '" + varName + "', mas recebido " + getTypeName(value));
        }
        if (expectedType == TokenType.BOOL && !(value instanceof Boolean)) {
            throw new RuntimeException(
                    "Erro Semântico: Esperado BOOL para '" + varName + "', mas recebido " + getTypeName(value));
        }
        if (expectedType == TokenType.STRING_TYPE && !(value instanceof String)) {
            throw new RuntimeException(
                    "Erro Semântico: Esperado STRING para '" + varName + "', mas recebido " + getTypeName(value));
        }
        
        context.put(varName, value);
        return null;
    }
    @Override
    public Object visit(PrintStatement statement) {
        Object value = evaluate(statement.getExpression());
        System.out.println("Output: " + value);
        return null;
    }
    @Override
    public Object visit(WhileStatement statement) {
        Object condition = evaluate(statement.getCondition());
        if (!(condition instanceof Boolean)) {
            throw new RuntimeException("Condição do 'while' deve ser um booleano.");
        }
        while ((Boolean) condition) {
            for (Statement stmt : statement.getBody()) {
                execute(stmt);
            }
            condition = evaluate(statement.getCondition());
        }
        return null;
    }
    @Override
    public Object visit(IfStatement statement) {
        Object condition = evaluate(statement.getCondition());
        if (!(condition instanceof Boolean)) {
            throw new RuntimeException("Condição do 'if' deve ser um booleano.");
        }
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
        return null;
    }

    // ... (visit(BinaryExpression) NÃO MUDA) ...
    @Override
    public Object visit(BinaryExpression expression) {
        Object leftVal = evaluate(expression.getLeft());
        Object rightVal = evaluate(expression.getRight());

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
            // ... (Todos os cases de BinaryExpression não mudam) ...
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
            case EQUAL_EQUAL -> {
                if (leftVal == null) {
                    return rightVal == null;
                }
                return leftVal.equals(rightVal);
            }
            case NOT_EQUAL -> {
                if (leftVal == null) {
                    return rightVal != null;
                }
                return !leftVal.equals(rightVal);
            }
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

    // --- MUDANÇA  ---
    // (visit(Number...), visit(Float...), visit(String...), visit(Boolean...) REMOVIDOS)

    @Override
    public Object visit(VariableExpression expression) {
        return context.get(expression.getName()); // Retorna o valor
    }

    @Override
    public Object visit(DeclarationStatement statement) {
        context.declare(statement.getVariableName(), statement.getType());
        return null; // Comandos não retornam valor
    }

    /**
     * Visita qualquer nó literal (Number, Float, String, Boolean)
     * e retorna seu valor (autoboxed).
     */
    @Override
    public Object visit(LiteralExpression expression) {
        return expression.getValue();
    }

    @Override
    public Object visit(ScopeStatement statement) {
        for(Statement stmt : statement.getStatements()) {
            execute(stmt);
        }

        return null;
    }
}