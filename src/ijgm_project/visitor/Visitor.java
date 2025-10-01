package ijgm_project.visitor;

import ijgm_project.parser.ast.*;

/**
 * Interface do Padrão Visitor.
 * Define um método 'visit' para cada classe concreta de nó da AST,
 * garantindo o princípio de que a lógica da operação é separada da estrutura.
 */
public interface Visitor {
    // Métodos para comandos (Statements)
    void visit(AssignStatement statement);

    void visit(PrintStatement statement);

    void visit(WhileStatement statement);

    void visit(IfStatement statement);

    void visit(DeclarationStatement statement);

    // Métodos para expressões (Expressions)
    void visit(BinaryExpression expression);

    void visit(NumberExpression expression);

    void visit(FloatExpression expression); // Para literais float

    void visit(VariableExpression expression);

    void visit(StringExpression expression);

    void visit(BooleanExpression expression);
}