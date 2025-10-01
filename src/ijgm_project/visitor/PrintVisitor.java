package ijgm_project.visitor;

import ijgm_project.parser.ast.*;

/**
 * Implementa a operação de impressão (visualização) da Árvore de Sintaxe
 * Abstrata (AST).
 * Este Visitor percorre a árvore e imprime a estrutura hierárquica usando
 * indentação.
 */
public class PrintVisitor implements Visitor {

    private int indent = 0;

    /**
     * Método auxiliar para imprimir mensagens com a indentação correta.
     * 
     * @param message A mensagem a ser impressa (representando o nó da AST).
     */
    private void print(String message) {
        // Usa a contagem de 'indent' para adicionar espaços e mostrar a hierarquia.
        for (int i = 0; i < indent; i++) {
            System.out.print("  "); // Usa 2 espaços para cada nível de indentação
        }
        System.out.println(message);
    }

    @Override
    public void visit(AssignStatement statement) {
        print("AssignStatement: " + statement.getVariableName());
        indent++;
        // Desce para o nó filho (a expressão do lado direito)
        statement.getExpression().accept(this);
        indent--;
    }

    @Override
    public void visit(PrintStatement statement) {
        print("PrintStatement");
        indent++;
        // Desce para o nó filho (a expressão a ser impressa)
        statement.getExpression().accept(this);
        indent--;
    }

    @Override
    public void visit(WhileStatement statement) {
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
    }

    @Override
    public void visit(IfStatement statement) {
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
    }

    @Override
    public void visit(DeclarationStatement statement) {
        // Nó folha de comando que não tem filhos Expression/Statement
        print("DeclarationStatement: " + statement.getVariableName() + " (" + statement.getType() + ")");
    }

    @Override
    public void visit(BinaryExpression expression) {
        print("BinaryExpression: " + expression.getOperator());
        indent++;
        // Desce recursivamente para os nós da esquerda e da direita
        expression.getLeft().accept(this);
        expression.getRight().accept(this);
        indent--;
    }

    @Override
    public void visit(NumberExpression expression) {
        // Nó folha de expressão
        print("NumberExpression: " + expression.getValue());
    }

    @Override
    public void visit(FloatExpression expression) {
        // Nó folha de expressão
        print("FloatExpression: " + expression.getValue());
    }

    @Override
    public void visit(VariableExpression expression) {
        // Nó folha de expressão
        print("VariableExpression: " + expression.getName());
    }

    @Override
    public void visit(StringExpression expression) {
        // Nó folha de expressão
        print("StringExpression: \"" + expression.getValue() + "\"");
    }

    @Override
    public void visit(BooleanExpression expression) {
        // Nó folha de expressão
        print("BooleanExpression: " + expression.getValue());
    }
}