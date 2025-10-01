COMPILADOR SIMPLIFICADO EM JAVA (ijgm-project):

Este projeto implementa um compilador básico para uma linguagem imperativa simplificada, desenvolvido em Java. O foco do projeto foi a aplicação correta de conceitos de Programação Orientada a Objetos (POO) e a utilização de Padrões de Projeto na construção das fases de compilação.

-------------------------------------------------------------------------------------------------------------------------------------

FUNCIONALIDADES PRINCIPAIS:

O compilador processa um código-fonte e executa as três fases principais: Análise Léxica, Análise Sintática e Análise Semântica (Interpretação).

-------------------------------------------------------------------------------------------------------------------------------------

CARACTERÍSTICAS DA LINGUAGEM IMPLEMENTADA:

Tipos Estáticos: Suporte para declaração e checagem de tipos (int, float, bool, string).

Comandos de Controle:

Condicionais: if e else.

Repetição: while.

Expressões Robustas: Suporte para precedência de operadores.

Aritméticos: +, -, *, /.

Comparação: ==, !=, >, <, >=, <=.

Lógicos: && (AND) e || (OR).

Literiais: Reconhecimento de números inteiros, números de ponto flutuante, strings e booleanos (true/false).

-------------------------------------------------------------------------------------------------------------------------------------

ARQUITETURA E ESTRUTURA DO PROJETO:

O projeto é organizado por pacotes que refletem as fases de compilação, garantindo a modularidade e a manutenibilidade.

ijgm-project/
├── lexer/             # Analisador Léxico
│   ├── Token.java         # Representa o lexema e o tipo (com linha/coluna)
│   └── Lexer.java         # Realiza a tokenização e detecção de erros léxicos
├── parser/            # Analisador Sintático
│   ├── ast/               # Árvore de Sintaxe Abstrata (Padrão Composite)
│   └── Parser.java        # Constrói a AST a partir dos tokens
├── visitor/           # Interpretação e Visualização (Padrão Visitor)
│   ├── InterpreterVisitor.java # Executa a lógica e a análise semântica
│   └── PrintVisitor.java       # Imprime a estrutura da AST
├── symbol_table/      # Análise Semântica
│   └── SymbolTable.java   # Armazena variáveis, tipos e valores
└── Main.java          # Ponto de entrada do compilador

-------------------------------------------------------------------------------------------------------------------------------------

APLICAÇÃO DOS PADRÕES DE PROJETO:

O projeto utiliza três padrões de projeto essenciais para compiladores:

COMPOSITE:	Pacote parser/ast	Cria a hierarquia de nós da AST (Statement, Expression, WhileStatement, BinaryExpression, etc.), permitindo que o cliente (Visitor) trate comandos complexos e simples de maneira uniforme.

VISITOR:	Pacote visitor	Separa a lógica de travessia/operação (Interpretar, Imprimir) da estrutura (AST). Temos InterpreterVisitor (execução) e PrintVisitor (visualização).

ITERATOR:	Classe Parser.java	O Parser consome a lista de tokens do Lexer através de um Iterator (de forma implícita, usando List.iterator()), garantindo o consumo sequencial e controlado dos tokens.

-------------------------------------------------------------------------------------------------------------------------------------

DETALHES DA IMPLEMENTAÇÃO:

1. Analisador Léxico (Lexer.java):

- Técnica: Autômato manual (AFD).

- Tratamento de Erros: Detecção de erros léxicos (caractere inválido) e relatório com indicação precisa de linha e coluna.

- Funcionalidades: Suporte a números inteiros, ponto flutuante, strings (com aspas), operadores de um e dois caracteres, e comentários (// e /* */).


2. Análise Sintática (Parser.java):

- Técnica: Parser Recursivo-Descendente.

- Precedência: Implementa a hierarquia completa de precedência de operadores (lógicos > comparação > adição > multiplicação).

- Recuperação de Erros: Utiliza a estratégia de Recuperação por Pânico (Panic-Mode). Em caso de erro sintático, o compilador reporta o erro e tenta se sincronizar, pulando tokens até encontrar um ponto seguro (como ; ou o início de um novo comando), permitindo que ele encontre múltiplos erros em uma única execução.

3. Análise Semântica / Interpretação:

- Checagem de Tipos: A classe SymbolTable armazena o tipo e o valor de cada variável. O sistema verifica a compatibilidade de tipos durante a declaração e a atribuição, garantindo que operações sejam realizadas apenas em tipos válidos (ex: não permite somar int e bool).

-------------------------------------------------------------------------------------------------------------------------------------

COMO EXECUTAR E TESTAR:

Pré-requisitos
Java Development Kit (JDK) 8 ou superior.

Estrutura Necessária
Certifique-se de que o seu arquivo de teste está na localização correta:

compiler-CN/
└── src/
    └── input/
        └── teste_completo.txt  <-- Arquivo de código-fonte


PASSO A PASSO:
Crie o Arquivo de Teste: O arquivo teste_completo.txt deve seguir a gramática da linguagem.

COMPILE E EXECUTE:
Execute a classe Main.java a partir do diretório raiz do projeto (compiler-CN).

ANÁLISE DE SAÍDA:
- O console mostrará a lista de Tokens gerados pelo Lexer.

- Em seguida, será impressa a Representação da Árvore de Sintaxe Abstrata (AST), mostrando a estrutura hierárquica do programa (graças ao PrintVisitor).

- Finalmente, a Fase de Execução (InterpreterVisitor) rodará o código e exibirá o Output:.
-------------------------------------------------------------------------------------------------------------------------------------

EXEMPLO DE CÓDIGO (FUNCIONAL)
// src/input/teste_completo.txt

// DECLARAÇÕES
int a;
int b;
string nome;
bool ativo;

// COMANDOS
a = 10;
b = (a * 2) - 5; 
nome = "Compilador OK";
ativo = true;

if (a > 5 && ativo == true) {
    print nome + " rodando!";
} else {
    print "Falha na condicao.";
}

while (b > 10) {
    b = b - 1;
    print b; 
}