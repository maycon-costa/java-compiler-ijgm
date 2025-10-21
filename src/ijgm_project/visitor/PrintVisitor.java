package ijgm_project.visitor;

import ijgm_project.parser.ast.*;

/**
 * Implementa a operação de impressão (visualização) da ÁSrvore de Sintaxe
 * Abstrata (AST).
 * (Refatorado para implementar Visitor<Void>).
 */
public class PrintVisitor implements Visitor<Void> {

    private int indent = 0;

    /**
     * Método auxiliar para imprimir mensagens com a indentação correta.
     * * @param message A mensagem a ser impressa (representando o nó da AST).
     */
    private void print(String message) {
        // Usa a contagem de 'indent' para adicionar espaços e mostrar a hierarquia.
        for (int i = 0; i < indent; i++) {
            System.out.print("  "); // Usa 2 espaços para cada nível de indentação
        }
        System.out.println(message);
    }

    @Override
    public Void visit(AssignStatement statement) {
        print("AssignStatement: " + statement.getVariableName());
        indent++;
        // Desce para o nó filho (a expressão do lado direito)
        statement.getExpression().accept(this); // A chamada accept agora é void (em espírito)
        indent--;
        return null; // Retorna Void
    }

    @Override
    public Void visit(PrintStatement statement) {
        print("PrintStatement");
        indent++;
        // Desce para o nó filho (a expressão a ser impressa)
        statement.getExpression().accept(this);
        indent--;
        return null; // Retorna Void
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
        // Itera sobre a lista de comandos do corpo (Padrão Composite)
        for (Statement stmt : statement.getBody()) {
            stmt.accept(this);
        }
        indent--;
        return null; // Retorna Void
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
        // Itera sobre a lista de comandos do bloco THEN
        for (Statement stmt : statement.getThenBody()) {
            stmt.accept(this);
        }
        indent--;

        // Verifica e imprime o bloco ELSE, se existir
        if (statement.getElseBody() != null) {
            print("Else Body:");
            indent++;
            for (Statement stmt : statement.getElseBody()) {
                stmt.accept(this);
            }
            indent--;
        }
        return null; // Retorna Void
    }

    @Override
    public Void visit(DeclarationStatement statement) {
        // Nó folha de comando que não tem filhos Expression/Statement
        print("DeclarationStatement: " + statement.getVariableName() + " (" + statement.getType() + ")");
        return null; // Retorna Void
    }

    @Override
    public Void visit(BinaryExpression expression) {
        print("BinaryExpression: " + expression.getOperator());
        indent++;
        // Desce recursivamente para os nós da esquerda e da direita
        expression.getLeft().accept(this);
        expression.getRight().accept(this);
        indent--;
        return null; // Retorna Void
    }

    @Override
    public Void visit(NumberExpression expression) {
        // Nó folha de expressão
        print("NumberExpression: " + expression.getValue());
        return null; // Retorna Void
    }

    @Override
    public Void visit(FloatExpression expression) {
        // Nó folha de expressão
        print("FloatExpression: " + expression.getValue());
        return null; // Retorna Void
    }

    @Override
    public Void visit(VariableExpression expression) {
        // Nó folha de expressão
        print("VariableExpression: " + expression.getName());
        return null; // Retorna Void
    }

    @Override
    public Void visit(StringExpression expression) {
        // Nó folha de expressão
        print("StringExpression: \"" + expression.getValue() + "\"");
        return null; // Retorna Void
    }

    @Override
    public Void visit(BooleanExpression expression) {
        // Nó folha de expressão
        print("BooleanExpression: " + expression.getValue());
        return null; // Retorna Void
    }
}