package ijgm_project.visitor;

import ijgm_project.parser.ast.*;

/**
 * Implementa a operação de impressão (visualização) da AST.
 * (Refatorado para implementar Visitor<Void>).
 * (Refatorado para usar LiteralExpression).
 */
public class PrintVisitor implements Visitor<Void> {

    private int indent = 0;

    private void print(String message) {
        for (int i = 0; i < indent; i++) {
            System.out.print("  ");
        }
        System.out.println(message);
    }

    // ... (visit(AssignStatement), visit(PrintStatement), etc. NÃO MUDAM) ...
    @Override
    public Void visit(AssignStatement statement) {
        print("AssignStatement: " + statement.getVariableName());
        indent++;
        statement.getExpression().accept(this);
        indent--;
        return null;
    }
    @Override
    public Void visit(PrintStatement statement) {
        print("PrintStatement");
        indent++;
        statement.getExpression().accept(this);
        indent--;
        return null;
    }
    @Override
    public Void visit(WhileStatement statement) {
        print("WhileStatement");
        indent++;
        print("Condition:");
        indent++;
        statement.getCondition().accept(this);
        indent--;
        print("Body:");
        indent++;
        for (Statement stmt : statement.getBody()) {
            stmt.accept(this);
        }
        indent--;
        return null;
    }
    @Override
    public Void visit(IfStatement statement) {
        print("IfStatement");
        indent++;
        print("Condition:");
        indent++;
        statement.getCondition().accept(this);
        indent--;
        print("Then Body:");
        indent++;
        for (Statement stmt : statement.getThenBody()) {
            stmt.accept(this);
        }
        indent--;
        if (statement.getElseBody() != null) {
            print("Else Body:");
            indent++;
            for (Statement stmt : statement.getElseBody()) {
                stmt.accept(this);
            }
            indent--;
        }
        return null;
    }
    @Override
    public Void visit(DeclarationStatement statement) {
        print("DeclarationStatement: " + statement.getVariableName() + " (" + statement.getType() + ")");
        return null;
    }
    @Override
    public Void visit(BinaryExpression expression) {
        print("BinaryExpression: " + expression.getOperator());
        indent++;
        expression.getLeft().accept(this);
        expression.getRight().accept(this);
        indent--;
        return null;
    }
    @Override
    public Void visit(VariableExpression expression) {
        print("VariableExpression: " + expression.getName());
        return null;
    }

    // --- MUDANÇA (4 métodos removidos, 1 adicionado) ---
    // (visit(Number...), visit(Float...), visit(String...), visit(Boolean...) REMOVIDOS)

    /**
     * Visita um nó LiteralExpression (Number, Float, String, Boolean).
     * Usa 'instanceof' para determinar o tipo exato para impressão.
     */
    @Override
    public Void visit(LiteralExpression expression) {
        if (expression instanceof StringExpression) {
            print("StringExpression: \"" + expression.getValue() + "\"");
        } else if (expression instanceof NumberExpression) {
            print("NumberExpression: " + expression.getValue());
        } else if (expression instanceof FloatExpression) {
            print("FloatExpression: " + expression.getValue());
        } else if (expression instanceof BooleanExpression) {
            print("BooleanExpression: " + expression.getValue());
        } else {
            // Fallback (não deve acontecer)
            print("LiteralExpression: " + expression.getValue());
        }
        return null;
    }
}