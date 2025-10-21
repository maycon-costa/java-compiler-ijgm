package ijgm_project.visitor;

// Importa todas as classes da AST, incluindo a LiteralExpression
import ijgm_project.parser.ast.*;

/**
 * Interface do Padrão Visitor (Refatorada com Genéricos).
 * (Refatorada para usar LiteralExpression).
 */
public interface Visitor<R> {
    // Métodos para comandos (Statements)
    R visit(AssignStatement statement);
    R visit(PrintStatement statement);
    R visit(WhileStatement statement);
    R visit(IfStatement statement);
    R visit(DeclarationStatement statement);

    // Métodos para expressões (Expressions)
    R visit(BinaryExpression expression);
    R visit(VariableExpression expression);

    // --- MUDANÇA (4 métodos removidos, 1 adicionado) ---
    // Substitui visit(NumberExpression), visit(FloatExpression),
    // visit(StringExpression) e visit(BooleanExpression)
    R visit(LiteralExpression expression);
}