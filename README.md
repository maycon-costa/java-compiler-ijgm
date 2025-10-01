# ☕ Compilador Simplificado em JAVA (Projeto **ijgm-project**)

Este projeto implementa um compilador/intérprete para uma linguagem imperativa simplificada, desenvolvido em Java. O foco é a correta aplicação de **Programação Orientada a Objetos (POO)** e **Padrões de Projeto** (Composite, Visitor e Iterator).

## 🌟 Funcionalidades e Gramática

| Tipo de Funcionalidade | Exemplos de Sintaxe | Observações |
| :--- | :--- | :--- |
| **Tipagem Estática** | `int x;`, `string nome;`, `bool ativo;` | Validação de tipos na declaração e atribuição. |
| **Comandos de Controle** | `if (x > 10) { ... } else { ... }` | Estruturas `if/else` e `while` totalmente funcionais. |
| **Expressões Complexas** | `a = (b * 2) + 5;` | Respeita a hierarquia de precedência de operadores. |
| **Operadores** | `==`, `!=`, `>`, `<=`, `&&`, `||`, `+`, `-`, `*`, `/` | Suporte completo para operadores de comparação e lógicos. |
| **Literiais** | `"texto"`, `10`, `3.14`, `true` | Reconhecimento de todos os tipos de literais da linguagem. |

---

## 🧱 Arquitetura e Padrões de Projeto

O projeto é estruturado em pacotes que refletem as fases de compilação, aplicando os padrões de design de forma estrita.

### Aplicação dos Padrões

| Padrão | Local de Uso | Teoria Aplicada |
| :--- | :--- | :--- |
| **COMPOSITE** | `parser/ast` | Permite que comandos e expressões complexas (`WhileStatement`, `BinaryExpression`) e simples (`NumberExpression`) sejam tratadas de forma uniforme. |
| **VISITOR** | `visitor/` | Separa as operações de **travessia/execução** (`InterpreterVisitor`, `PrintVisitor`) da **estrutura** (AST), tornando o código escalável. |
| **ITERATOR** | `parser/Parser.java` | O `Parser` consome tokens de forma sequencial e controlada, desacoplando-se da fonte de dados (`List<Token>`). |

### Estrutura de Pastas (`ijgm-project/`)

ijgm-project/
├── lexer/               # FASE LÉXICA

│   ├── Lexer.java       # Implementa AFD manual e detecção de erros léxicos.

├── parser/              # FASE SINTÁTICA

│   ├── ast/             # AST (Padrão COMPOSITE)

│   └── Parser.java      # Implementa Parser Recursivo-Descendente e Recuperação de Erros.

├── symbol_table/        # FASE SEMÂNTICA (Contexto)

│   └── SymbolTable.java # Gerencia Tipos e Valores de Variáveis.

└── visitor/             # EXECUÇÃO (Padrão VISITOR)



---

## 🛠️ Detalhes da Implementação

### 1. Analisador Léxico (`Lexer.java`)

* **Técnica:** **Autômato Finito Determinístico (AFD)** manual.
* **Tratamento de Erros:** Reporta **erros léxicos** com precisão de **linha e coluna**.

### 2. Análise Sintática (`Parser.java`)

* **Técnica:** **Parser Recursivo-Descendente**. Cada método (`parseExpression`, `parseStatement`) representa uma regra gramatical.
* **Tratamento de Erros:** Implementa **Recuperação por Pânico (Panic-Mode)**, permitindo que o compilador encontre múltiplos erros sintáticos antes de interromper a compilação.

### 3. Análise Semântica (`SymbolTable.java` & `InterpreterVisitor.java`)

* **Validação:** A `SymbolTable` impõe a **tipagem estática**, verificando se a variável foi declarada e se o valor atribuído é compatível com o tipo definido (`int`, `float`, `string`, etc.).

---
## ▶️ Como Executar e Testar

Pré-requisitos
Java Development Kit (JDK) 8 ou superior.

1.  **Caminho do Arquivo:** Verifique se o `Main.java` aponta para o arquivo de teste correto (ex: `src/input/teste_completo.txt`).
2.  **Execução:** Execute a classe `Main.java`.

### Saída Esperada no Console

O programa irá gerar uma saída de 3 fases:

1.  **Tokens:** Lista sequencial dos tokens reconhecidos.
2.  **AST:** Impressão hierárquica e indentada da estrutura do código (via `PrintVisitor`).
3.  **Execução:** Resultado final das instruções de `print` (via `InterpreterVisitor`).

---

## 📝 Exemplo de Código (Funcional)

📝 Exemplo de Código (Funcional)
Claro! Aqui está o código Markdown completo para o seu arquivo README.md, exatamente como formatado para o GitHub, incluindo todas as tabelas e blocos de código.

Você pode copiar e colar todo o conteúdo abaixo no seu arquivo README.md.

Markdown

# ☕ Compilador Simplificado em JAVA (Projeto **ijgm-project**)

Este projeto implementa um compilador/intérprete para uma linguagem imperativa simplificada, desenvolvido em Java. O foco é a correta aplicação de **Programação Orientada a Objetos (POO)** e **Padrões de Projeto** (Composite, Visitor e Iterator).

## 🌟 Funcionalidades e Gramática

| Tipo de Funcionalidade | Exemplos de Sintaxe | Observações |
| :--- | :--- | :--- |
| **Tipagem Estática** | `int x;`, `string nome;`, `bool ativo;` | Validação de tipos na declaração e atribuição. |
| **Comandos de Controle** | `if (x > 10) { ... } else { ... }` | Estruturas `if/else` e `while` totalmente funcionais. |
| **Expressões Complexas** | `a = (b * 2) + 5;` | Respeita a hierarquia de precedência de operadores. |
| **Operadores** | `==`, `!=`, `>`, `<=`, `&&`, `||`, `+`, `-`, `*`, `/` | Suporte completo para operadores de comparação e lógicos. |
| **Literiais** | `"texto"`, `10`, `3.14`, `true` | Reconhecimento de todos os tipos de literais da linguagem. |

---

## 🧱 Arquitetura e Padrões de Projeto

O projeto é estruturado em pacotes que refletem as fases de compilação, aplicando os padrões de design de forma estrita.

### Aplicação dos Padrões

| Padrão | Local de Uso | Teoria Aplicada |
| :--- | :--- | :--- |
| **COMPOSITE** | `parser/ast` | Permite que comandos e expressões complexas (`WhileStatement`, `BinaryExpression`) e simples (`NumberExpression`) sejam tratadas de forma uniforme. |
| **VISITOR** | `visitor/` | Separa as operações de **travessia/execução** (`InterpreterVisitor`, `PrintVisitor`) da **estrutura** (AST), tornando o código escalável. |
| **ITERATOR** | `parser/Parser.java` | O `Parser` consome tokens de forma sequencial e controlada, desacoplando-se da fonte de dados (`List<Token>`). |

### Estrutura de Pastas (`ijgm-project/`)

ijgm-project/
├── lexer/               # FASE LÉXICA
│   ├── Lexer.java       # Implementa AFD manual e detecção de erros léxicos.
├── parser/              # FASE SINTÁTICA
│   ├── ast/             # AST (Padrão COMPOSITE)
│   └── Parser.java      # Implementa Parser Recursivo-Descendente e Recuperação de Erros.
├── symbol_table/        # FASE SEMÂNTICA (Contexto)
│   └── SymbolTable.java # Gerencia Tipos e Valores de Variáveis.
└── visitor/             # EXECUÇÃO (Padrão VISITOR)


---

## 🛠️ Detalhes da Implementação

### 1. Analisador Léxico (`Lexer.java`)

* **Técnica:** **Autômato Finito Determinístico (AFD)** manual.
* **Tratamento de Erros:** Reporta **erros léxicos** com precisão de **linha e coluna**.

### 2. Análise Sintática (`Parser.java`)

* **Técnica:** **Parser Recursivo-Descendente**. Cada método (`parseExpression`, `parseStatement`) representa uma regra gramatical.
* **Tratamento de Erros:** Implementa **Recuperação por Pânico (Panic-Mode)**, permitindo que o compilador encontre múltiplos erros sintáticos antes de interromper a compilação.

### 3. Análise Semântica (`SymbolTable.java` & `InterpreterVisitor.java`)

* **Validação:** A `SymbolTable` impõe a **tipagem estática**, verificando se a variável foi declarada e se o valor atribuído é compatível com o tipo definido (`int`, `float`, `string`, etc.).

---

## ▶️ Como Executar e Testar

1.  **Caminho do Arquivo:** Verifique se o `Main.java` aponta para o arquivo de teste correto (ex: `src/input/teste_completo.txt`).
2.  **Execução:** Execute a classe `Main.java`.

### Saída Esperada no Console

O programa irá gerar uma saída de 3 fases:

1.  **Tokens:** Lista sequencial dos tokens reconhecidos.
2.  **AST:** Impressão hierárquica e indentada da estrutura do código (via `PrintVisitor`).
3.  **Execução:** Resultado final das instruções de `print` (via `InterpreterVisitor`).

---

## 📝 Exemplo de Código (Funcional)

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
