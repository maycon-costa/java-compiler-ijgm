package ijgm_project.vm;

import ijgm_project.parser.ast.*;
import ijgm_project.visitor.Visitor;
import java.util.List;

/**
 * Traduz a AST para Bytecode.
 * (Corrigido para usar OpCode.getValue() - A Boa Prática)
 * (Corrigido o bug de "off-by-one" do emitLoopJump)
 */
public class CompilerVisitor implements Visitor<Void> {

    private final BytecodeChunk chunk;
    private final CompilerSymbolTable table;

    public CompilerVisitor() {
        this.chunk = new BytecodeChunk();
        this.table = new CompilerSymbolTable();
    }

    public BytecodeChunk compile(List<Statement> ast) {
        try {
            for (Statement stmt : ast) {
                stmt.accept(this);
            }
            chunk.writeByte(OpCode.OP_RETURN.getValue());
            return this.chunk;
        } catch (Exception e) {
            System.err.println("Erro de Compilação: " + e.getMessage());
            return null;
        }
    }


    @Override
    public Void visit(LiteralExpression expression) {
        int constantIndex = chunk.addConstant(expression.getValue());
        chunk.writeByte(OpCode.OP_PUSH_CONST.getValue());
        chunk.writeByte((byte) constantIndex); 
        return null;
    }

    @Override
    public Void visit(BinaryExpression expression) {
        expression.getLeft().accept(this);
        expression.getRight().accept(this);

        switch (expression.getOperator()) {
            case PLUS -> chunk.writeByte(OpCode.OP_ADD.getValue());
            case MINUS -> chunk.writeByte(OpCode.OP_SUBTRACT.getValue());
            case MULTIPLY -> chunk.writeByte(OpCode.OP_MULTIPLY.getValue());
            case DIVIDE -> chunk.writeByte(OpCode.OP_DIVIDE.getValue());
            case EQUAL_EQUAL -> chunk.writeByte(OpCode.OP_EQUAL.getValue());
            case NOT_EQUAL -> chunk.writeByte(OpCode.OP_NOT_EQUAL.getValue());
            case GREATER_THAN -> chunk.writeByte(OpCode.OP_GREATER.getValue());
            case GREATER_EQUAL -> chunk.writeByte(OpCode.OP_GREATER_EQUAL.getValue());
            case LESS_THAN -> chunk.writeByte(OpCode.OP_LESS.getValue());
            case LESS_EQUAL -> chunk.writeByte(OpCode.OP_LESS_EQUAL.getValue());
            case AND -> chunk.writeByte(OpCode.OP_AND.getValue());
            case OR -> chunk.writeByte(OpCode.OP_OR.getValue());
            default -> throw new RuntimeException("Operador binário desconhecido: " + expression.getOperator());
        }
        return null;
    }

    @Override
    public Void visit(VariableExpression expression) {
        CompilerSymbolTable.Symbol symbol = table.resolve(expression.getName());

        if (symbol.isLocal) {
            chunk.writeByte(OpCode.OP_LOAD_LOCAL.getValue());
            chunk.writeByte((byte) symbol.index);
        } else {
            chunk.writeByte(OpCode.OP_LOAD_GLOBAL.getValue());
            chunk.writeByte((byte) symbol.index);
        }
        return null;
    }

    // --- MÉTODOS VISIT PARA COMANDOS (Statements) ---

    @Override
    public Void visit(DeclarationStatement statement) {
        Object defaultValue = switch (statement.getType()) {
            case INT -> 0;
            case FLOAT -> 0.0f;
            case BOOL -> false;
            case STRING_TYPE -> "";
            default -> null;
        };
        
        int constIndex = chunk.addConstant(defaultValue);
        chunk.writeByte(OpCode.OP_PUSH_CONST.getValue());
        chunk.writeByte((byte) constIndex);

        CompilerSymbolTable.Symbol symbol = table.declare(statement.getVariableName(), chunk);

        if (!symbol.isLocal) {
            chunk.writeByte(OpCode.OP_DEFINE_GLOBAL.getValue());
            chunk.writeByte((byte) symbol.index);
        }
        return null;
    }

    @Override
    public Void visit(AssignStatement statement) {
        statement.getExpression().accept(this);
        CompilerSymbolTable.Symbol symbol = table.resolve(statement.getVariableName());

        if (symbol.isLocal) {
            chunk.writeByte(OpCode.OP_STORE_LOCAL.getValue());
            chunk.writeByte((byte) symbol.index);
        } else {
            chunk.writeByte(OpCode.OP_STORE_GLOBAL.getValue());
            chunk.writeByte((byte) symbol.index);
        }
        return null;
    }
    
    @Override
    public Void visit(IncrementStatement statement) {
        CompilerSymbolTable.Symbol symbol = table.resolve(statement.getVariableName());

        if (symbol.isLocal) {
            chunk.writeByte(OpCode.OP_INCREMENT_LOCAL.getValue());
            chunk.writeByte((byte) symbol.index);
               } else {
            chunk.writeByte(OpCode.OP_INCREMENT_GLOBAL.getValue());
           chunk.writeByte((byte) symbol.index);
        }
        return null;
    }

    @Override
    public Void visit(DecrementStatement statement) {
        CompilerSymbolTable.Symbol symbol = table.resolve(statement.getVariableName());

        if (symbol.isLocal) {
            chunk.writeByte(OpCode.OP_DECREMENT_LOCAL.getValue());
            chunk.writeByte((byte) symbol.index);
        } else {
            chunk.writeByte(OpCode.OP_DECREMENT_GLOBAL.getValue());
            chunk.writeByte((byte) symbol.index);
        }
        return null;
    }


    @Override
    public Void visit(PrintStatement statement) {
        statement.getExpression().accept(this);
        chunk.writeByte(OpCode.OP_PRINT.getValue());
        return null;
    }

    @Override
    public Void visit(ScopeStatement statement) {
        table.beginScope();

        for (Statement stmt : statement.getStatements()) {
            stmt.accept(this);
        }

        int numPopped = table.endScope();

        for (int i = 0; i < numPopped; i++) {
            chunk.writeByte(OpCode.OP_POP.getValue());
        }
        return null;
    }

    @Override
    public Void visit(IfStatement statement) {
        statement.getCondition().accept(this);

        int thenJump = emitJump(OpCode.OP_JUMP_IF_FALSE.getValue());

        for (Statement stmt : statement.getThenBody()) {
            stmt.accept(this);
        }

        int elseJump = emitJump(OpCode.OP_JUMP.getValue());

        patchJump(thenJump);

        if (statement.getElseBody() != null) {
            for (Statement stmt : statement.getElseBody()) {
                stmt.accept(this);
            }
        }

        patchJump(elseJump);

        return null;
    }

    @Override
    public Void visit(WhileStatement statement) {
        int loopStart = chunk.getCode().size();

        statement.getCondition().accept(this);

        int exitJump = emitJump(OpCode.OP_JUMP_IF_FALSE.getValue());

        for (Statement stmt : statement.getBody()) {
            stmt.accept(this);
        }

        emitLoopJump(loopStart); // <-- Chamada do método corrigido

        patchJump(exitJump);

        return null;
    }

    // --- MÉTODOS AUXILIARES (Helpers) ---

    // (Helper de 'if', não muda e está correto)
    private int emitJump(byte instruction) {
        chunk.writeByte(instruction);
        chunk.writeByte((byte) 0xFF); // Placeholder
        return chunk.getCode().size() - 1; // Endereço do placeholder
    }

    // (Helper de 'if', não muda e está correto)
    private void patchJump(int offsetAddress) {
        // O pulo é relativo ao *fim* da instrução de pulo (ip + 2)
        int jump = chunk.getCode().size() - offsetAddress - 1;

        if (jump > 255) {
            throw new RuntimeException("Erro de Compilação: Bloco de código muito grande para pular.");
        }

        chunk.getCode().set(offsetAddress, (byte) jump);
    }

    // --- CORREÇÃO (FURO DO JUMP) ---
    // A lógica de cálculo do offset para pulos para trás é diferente.
    private void emitLoopJump(int loopStart) {
        chunk.writeByte(OpCode.OP_JUMP.getValue());

        // O offset é (destino - (local_atual + 2))
        // +1 para o operando que estamos prestes a escrever
        int ip_after_jump = chunk.getCode().size() + 1; 
        int offset = loopStart - ip_after_jump;

        if (Math.abs(offset) > 255) {
            throw new RuntimeException("Erro de Compilação: Corpo de loop muito grande.");
        }

        chunk.writeByte((byte) offset);
    }
}