package ijgm_project.visitor;

import ijgm_project.parser.ast.*;
import ijgm_project.symbol_table.SymbolTable;
//import ijgm_project.lexer.TokenType; // Import adicionado para o TokenType, se necessário

/**
 * Implementa a operação de execução na AST.
 * Realiza a Análise Semântica (validação de tipos) e a Interpretação
 * (execução).
 */
public class InterpreterVisitor implements Visitor {
    private final SymbolTable symbolTable = new SymbolTable();
    private Object result = null; // Armazena o resultado da avaliação da última expressão

    @Override
    public void visit(AssignStatement statement) {
        // 1. Avalia a expressão do lado direito
        statement.getExpression().accept(this);
        // 2. Lógica Semântica: Checa a compatibilidade de tipos e armazena o valor.
        symbolTable.put(statement.getVariableName(), result);
    }

    @Override
    public void visit(PrintStatement statement) {
        // Avalia a expressão e imprime o resultado.
        statement.getExpression().accept(this);
        System.out.println("Output: " + result);
    }

    @Override
    public void visit(WhileStatement statement) {
        // Avalia a condição antes de cada iteração.
        statement.getCondition().accept(this);
        if (!(result instanceof Boolean)) {
            throw new RuntimeException("Condição do 'while' deve ser um booleano.");
        }
        while ((Boolean) result) {
            // Executa o corpo do loop
            for (Statement stmt : statement.getBody()) {
                stmt.accept(this);
            }
            // Reavalia a condição
            statement.getCondition().accept(this);
        }
    }

    @Override
    public void visit(IfStatement statement) {
        // Avalia a condição
        statement.getCondition().accept(this);
        if (!(result instanceof Boolean)) {
            throw new RuntimeException("Condição do 'if' deve ser um booleano.");
        }
        // Executa o bloco 'then' ou 'else'
        if ((Boolean) result) {
            for (Statement stmt : statement.getThenBody()) {
                stmt.accept(this);
            }
        } else {
            if (statement.getElseBody() != null) {
                for (Statement stmt : statement.getElseBody()) {
                    stmt.accept(this);
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // Métodos visit para expressões (Avaliação de valores)
    // ------------------------------------------------------------------------

    @Override
    public void visit(BinaryExpression expression) {
        // Avaliação recursiva dos lados esquerdo e direito.
        expression.getLeft().accept(this);
        Object leftVal = result;
        expression.getRight().accept(this);
        Object rightVal = result;

        switch (expression.getOperator()) {
            case GREATER_THAN -> {
                if (leftVal instanceof Integer && rightVal instanceof Integer) {
                    result = (Integer) leftVal > (Integer) rightVal;
                } else if (leftVal instanceof Float && rightVal instanceof Float) {
                    result = (Float) leftVal > (Float) rightVal;
                } else {
                    throw new RuntimeException("Operadores de comparação suportam apenas números");
                }
            }
            case LESS_THAN -> {
                if (leftVal instanceof Integer && rightVal instanceof Integer) {
                    result = (Integer) leftVal < (Integer) rightVal;
                } else if (leftVal instanceof Float && rightVal instanceof Float) {
                    result = (Float) leftVal < (Float) rightVal;
                } else {
                    throw new RuntimeException("Operadores de comparação suportam apenas números");
                }
            }
            case EQUAL_EQUAL -> result = leftVal.equals(rightVal);
            case NOT_EQUAL -> result = !leftVal.equals(rightVal);
            case GREATER_EQUAL -> {
                if (leftVal instanceof Integer && rightVal instanceof Integer) {
                    result = (Integer) leftVal >= (Integer) rightVal;
                } else if (leftVal instanceof Float && rightVal instanceof Float) {
                    result = (Float) leftVal >= (Float) rightVal;
                } else {
                    throw new RuntimeException("Operadores de comparação suportam apenas números");
                }
            }
            case LESS_EQUAL -> {
                if (leftVal instanceof Integer && rightVal instanceof Integer) {
                    result = (Integer) leftVal <= (Integer) rightVal;
                } else if (leftVal instanceof Float && rightVal instanceof Float) {
                    result = (Float) leftVal <= (Float) rightVal;
                } else {
                    throw new RuntimeException("Operadores de comparação suportam apenas números");
                }
            }
            case AND -> {
                if (leftVal instanceof Boolean && rightVal instanceof Boolean) {
                    result = (Boolean) leftVal && (Boolean) rightVal;
                } else {
                    throw new RuntimeException("Operadores lógicos suportam apenas booleanos.");
                }
            }
            case OR -> {
                if (leftVal instanceof Boolean && rightVal instanceof Boolean) {
                    result = (Boolean) leftVal || (Boolean) rightVal;
                } else {
                    throw new RuntimeException("Operadores lógicos suportam apenas booleanos.");
                }
            }
            case MINUS -> {
                if (leftVal instanceof Integer && rightVal instanceof Integer) {
                    result = (Integer) leftVal - (Integer) rightVal;
                } else if (leftVal instanceof Float && rightVal instanceof Float) {
                    result = (Float) leftVal - (Float) rightVal;
                } else {
                    throw new RuntimeException("Operadores aritméticos suportam apenas números");
                }
            }
            case PLUS -> {
                if (leftVal instanceof Integer && rightVal instanceof Integer) {
                    result = (Integer) leftVal + (Integer) rightVal;
                } else if (leftVal instanceof Float && rightVal instanceof Float) {
                    result = (Float) leftVal + (Float) rightVal;
                } else if (leftVal instanceof String || rightVal instanceof String) {
                    result = String.valueOf(leftVal) + String.valueOf(rightVal);
                } else {
                    throw new RuntimeException("Tipos incompatíveis para o operador PLUS");
                }
            }
            case MULTIPLY -> {
                if (leftVal instanceof Integer && rightVal instanceof Integer) {
                    result = (Integer) leftVal * (Integer) rightVal;
                } else if (leftVal instanceof Float && rightVal instanceof Float) {
                    result = (Float) leftVal * (Float) rightVal;
                } else {
                    throw new RuntimeException("Operadores aritméticos suportam apenas números");
                }
            }
            case DIVIDE -> {
                if (leftVal instanceof Integer && rightVal instanceof Integer) {
                    if ((Integer) rightVal == 0) {
                        throw new RuntimeException("Divisão por zero");
                    }
                    result = (Integer) leftVal / (Integer) rightVal;
                } else if (leftVal instanceof Float && rightVal instanceof Float) {
                    if ((Float) rightVal == 0.0f) {
                        throw new RuntimeException("Divisão por zero");
                    }
                    result = (Float) leftVal / (Float) rightVal;
                } else {
                    throw new RuntimeException("Operadores aritméticos suportam apenas números");
                }
            }
            // Bloco DEFAULT adicionado para cobrir todos os outros tokens do TokenType
            default -> throw new RuntimeException(
                    "Operador binário não suportado ou token inesperado: " + expression.getOperator());
        }
    }

    @Override
    public void visit(NumberExpression expression) {
        result = expression.getValue();
    }

    @Override
    public void visit(FloatExpression expression) {
        result = expression.getValue();
    }

    @Override
    public void visit(VariableExpression expression) {
        result = symbolTable.get(expression.getName());
    }

    @Override
    public void visit(StringExpression expression) {
        result = expression.getValue();
    }

    @Override
    public void visit(DeclarationStatement statement) {
        // Lógica Semântica: Registra o nome e o tipo da variável na Tabela de Símbolos.
        symbolTable.declare(statement.getVariableName(), statement.getType());
    }

    @Override
    public void visit(BooleanExpression expression) {
        result = expression.getValue();
    }
}