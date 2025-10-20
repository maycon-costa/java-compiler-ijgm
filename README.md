
# ☕ Compilador Simplificado em Java (Projeto **ijgm-project**)

Este projeto implementa um **compilador/intérprete** para uma linguagem **imperativa simplificada**, desenvolvido em **Java**.
O foco está na aplicação correta dos princípios de **Programação Orientada a Objetos (POO)** e dos **Padrões de Projeto**: **Composite**, **Visitor** e **Iterator**.

---

## 🌟 Funcionalidades e Gramática

| Tipo de Funcionalidade   | Exemplos de Sintaxe                     | Observações                                                |                       |                                                                        |
| :----------------------- | :-------------------------------------- | :--------------------------------------------------------- | --------------------- | ---------------------------------------------------------------------- |
| **Tipagem Estática**     | `int x;`, `string nome;`, `bool ativo;` | Validação de tipos na declaração e atribuição.             |                       |                                                                        |
| **Comandos de Controle** | `if (x > 10) { ... } else { ... }`      | Estruturas `if/else` e `while` totalmente funcionais.      |                       |                                                                        |
| **Expressões Complexas** | `a = (b * 2) + 5;`                      | Respeita a hierarquia de precedência dos operadores.       |                       |                                                                        |
| **Operadores**           | `==`, `!=`, `>`, `<=`, `&&`, `          |                                                            | `, `+`, `-`, `*`, `/` | Suporte completo para operadores aritméticos, lógicos e de comparação. |
| **Literais**             | `"texto"`, `10`, `3.14`, `true`         | Reconhecimento de todos os tipos de literais da linguagem. |                       |                                                                        |

---

## 🧱 Arquitetura e Padrões de Projeto

O projeto é dividido em pacotes que representam as **fases clássicas da compilação**, aplicando rigorosamente os padrões de design.

### Aplicação dos Padrões

| Padrão        | Local de Uso         | Teoria Aplicada                                                                                                                                                |
| :------------ | :------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **COMPOSITE** | `parser/ast`         | Permite que comandos e expressões — sejam complexos (`WhileStatement`, `BinaryExpression`) ou simples (`NumberExpression`) — sejam tratados de forma uniforme. |
| **VISITOR**   | `visitor/`           | Separa as operações de **execução e travessia** (`InterpreterVisitor`, `PrintVisitor`) da **estrutura da AST**, tornando o código extensível e modular.        |
| **ITERATOR**  | `parser/Parser.java` | O `Parser` consome tokens de forma sequencial e desacoplada, utilizando o padrão Iterator sobre a lista de tokens (`List<Token>`).                             |

---

### Estrutura de Pastas (`ijgm-project/`)

```bash
ijgm-project/
├── lexer/               # FASE LÉXICA
│   └── Lexer.java       # Implementa AFD manual e detecção de erros léxicos.
│
├── parser/              # FASE SINTÁTICA
│   ├── ast/             # AST (Padrão COMPOSITE)
│   └── Parser.java      # Parser recursivo-descendente com recuperação de erros.
│
├── symbol_table/        # FASE SEMÂNTICA (Contexto)
│   └── SymbolTable.java # Gerencia tipos e valores de variáveis.
│
└── visitor/             # EXECUÇÃO (Padrão VISITOR)
    ├── InterpreterVisitor.java
    ├── PrintVisitor.java
    └── Visitor.java
```

---

## 🛠️ Detalhes da Implementação

### 1. Analisador Léxico (`Lexer.java`)

* **Técnica:** Implementa um **Autômato Finito Determinístico (AFD)** manual.
* **Tratamento de Erros:** Reporta **erros léxicos** com precisão de **linha e coluna**.

### 2. Análise Sintática (`Parser.java`)

* **Técnica:** **Parser Recursivo-Descendente**, em que cada método (`parseExpression`, `parseStatement`) corresponde a uma regra gramatical.
* **Tratamento de Erros:** Utiliza **Recuperação por Pânico (Panic Mode)**, permitindo identificar múltiplos erros antes de abortar a análise.

### 3. Análise Semântica e Execução (`SymbolTable.java` & `InterpreterVisitor.java`)

* **Validação:** A `SymbolTable` implementa **tipagem estática**, garantindo que variáveis sejam **declaradas antes do uso** e que **atribuições** respeitem os tipos (`int`, `float`, `string`, `bool`).
* **Execução:** O `InterpreterVisitor` percorre a AST e **interpreta** o código, realizando operações, condições e laços de repetição.

---

## ▶️ Como Executar e Testar

### 🔧 Pré-requisitos

* **Java Development Kit (JDK) 8** ou superior instalado.

### 🚀 Passos para execução

1. **Verifique o caminho do arquivo de entrada**:
   No arquivo `Main.java`, confirme se o caminho aponta para o teste desejado (ex:

   ````java
   String filePath = "input/exemplo.txt";
   ```)

   ````
2. **Compile e execute** o projeto:

   * Via terminal:

     ```bash
     javac -d bin $(find src -name "*.java")
     java -cp bin ijgm_project.Main
     ```
   * Ou diretamente pela IDE (Eclipse, IntelliJ, VS Code).

### 💡 Saída Esperada

O programa apresenta **3 fases** no console:

1. **Fase 1 – Tokens:** lista sequencial dos tokens reconhecidos.
2. **Fase 2 – AST:** impressão hierárquica e indentada da estrutura do código (via `PrintVisitor`).
3. **Fase 3 – Execução:** resultado final das instruções `print` (via `InterpreterVisitor`).

---

## 📝 Exemplo de Código (Funcional)

Arquivo de entrada (`src/input/teste_completo.txt`):

```c
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
```

---

## 📚 Conceitos Reforçados

* **POO aplicada à construção de compiladores**
* **Design Patterns (Composite, Visitor, Iterator)**
* **Análise Léxica, Sintática e Semântica**
* **Interpretação de código via AST**

---

Desenvolvido como parte de um estudo acadêmico sobre **implementação de compiladores orientados a objetos**, com ênfase em **compiladores** e **arquitetura de linguagens**.

