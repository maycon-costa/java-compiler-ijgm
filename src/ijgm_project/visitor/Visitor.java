package ijgm_project.visitor;

import ijgm_project.parser.ast.*;

/**
 * Interface do Padrão Visitor (Refatorada com Genéricos).
 * Define um método 'visit' para cada classe concreta de nó da AST.
 * Cada método agora retorna um valor de tipo genérico 'R'.
 */
public interface Visitor<R> {
    // Métodos para comandos (Statements)
    // TODO: Se comandos não retornam nada, considerar usar Void como tipo genérico, 
    // TODO: separar os métodos para visitar comandos em outra interface
    R visit(AssignStatement statement);

    R visit(PrintStatement statement);

    R visit(WhileStatement statement);

    R visit(IfStatement statement);

    R visit(DeclarationStatement statement);

    // Métodos para expressões (Expressions)
    R visit(BinaryExpression expression);

    R visit(NumberExpression expression);

    R visit(FloatExpression expression);

    R visit(VariableExpression expression);

    R visit(StringExpression expression);

    R visit(BooleanExpression expression);
}