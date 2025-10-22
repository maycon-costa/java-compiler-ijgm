# ☕ Compilador **ijgm-project**

Este projeto implementa um **compilador/intérprete** para uma linguagem **imperativa simplificada**, desenvolvido em **Java**.
O foco está na aplicação correta dos princípios de **Programação Orientada a Objetos (POO)** e dos **Padrões de Projeto**: **Composite**, **Visitor** e **Iterator**.

---

## 🌟 Funcionalidades e Gramática

| Tipo de Funcionalidade   | Exemplos de Sintaxe                             | Observações                                                                           |
| :----------------------- | :---------------------------------------------- | :------------------------------------------------------------------------------------ |
| **Tipagem Estática**     | `int x;`, `string nome;`, `bool ativo;`         | Validação de tipos na declaração e atribuição.                                        |
| **Comandos de Controle** | `if (x > 10) { ... } else { ... }`              | Estruturas `if/else` e `while` totalmente funcionais.                                 |
| **Expressões Complexas** | `a = (b * 2) + 5;`                              | Respeita a hierarquia de precedência dos operadores.                                  |
| **Operadores**           | `==`, `!=`, `>`, `<=`, `&&`, `+`, `-`, `*`, `/` | Suporte completo para operadores aritméticos, lógicos e de comparação.                |
| **Literais**             | `"texto"`, `10`, `3.14`, `true`                 | Reconhecimento de todos os tipos de literais da linguagem.                            |
| **Coerção de Tipo**      | `float f; int i; f = i;`                        | Suporta coerção automática de `int` para `float` em atribuições e operações binárias. |
| **Concatenação**         | `print "Valor: " + 10;`                         | O operador `+` é sobrecarregado para concatenar strings com qualquer outro tipo.      |

---

## 🧱 Arquitetura e Padrões de Projeto

O projeto é dividido em pacotes que representam as **fases clássicas da compilação**, aplicando rigorosamente os padrões de design.

### Aplicação dos Padrões

| Padrão        | Local de Uso         | Teoria Aplicada                                                                                                                                                |
| :------------ | :------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **COMPOSITE** | `parser/ast`         | Permite que comandos e expressões — sejam complexos (`WhileStatement`, `BinaryExpression`) ou simples (`NumberExpression`) — sejam tratados de forma uniforme. |
| **VISITOR**   | `visitor/`           | Separa as operações de **execução e travessia** (`InterpreterVisitor`, `PrintVisitor`) da **estrutura da AST**, tornando o código extensível e modular.        |
| **ITERATOR**  | `parser/Parser.java` | O `Parser` consome tokens de forma sequencial e desacoplada, utilizando o padrão Iterator (`Iterator<Token>`) sobre a lista de tokens.                         |

---

### Estrutura de Pastas (`ijgm-project/`)

```bash
ijgm-project/
├── lexer/                # FASE LÉXICA
│   └── Lexer.java        # Implementa AFD manual e detecção de erros léxicos.
│
├── parser/               # FASE SINTÁTICA
│   ├── ast/              # AST (Padrão COMPOSITE)
│   └── Parser.java       # Parser recursivo-descendente com recuperação de erros.
│
├── symbol_table/         # FASE SEMÂNTICA (Contexto)
│   └── ExecutionContext.java # Gerencia tipos e valores de variáveis.
│
└── visitor/              # EXECUÇÃO (Padrão VISITOR)
    ├── InterpreterVisitor.java
    ├── PrintVisitor.java
    └── Visitor.java
```

---

## 🛠️ Detalhes da Implementação

### 1. Analisador Léxico (`Lexer.java`)

* **Técnica:** Implementa um **Autômato Finito Determinístico (AFD)** manual.
* **Tratamento de Erros:** Reporta **erros léxicos** com precisão de linha e coluna, usando um método `lexicalError` centralizado.
* **Lookahead:** Utiliza `peekNext()` e `match()` para lidar com operadores de um ou dois caracteres (ex: `=`, `==`, `!`, `!=`).

### 2. Análise Sintática (`Parser.java`)

* **Técnica:** **Parser Recursivo-Descendente**, em que cada método (`parseAddition`, `parseStatement`) corresponde a uma regra gramatical e mantém a precedência correta dos operadores.
* **Tratamento de Erros:** Utiliza **Recuperação por Pânico (Panic Mode)**. Ao encontrar um erro, o método `synchronize()` avança até um ponto seguro (como `;` ou início de um novo comando) para continuar a análise e reportar múltiplos erros.

### 3. Análise Semântica e Execução (`ExecutionContext.java` & `InterpreterVisitor.java`)

* **Validação:** O `ExecutionContext` implementa **tipagem estática**, garantindo que variáveis sejam **declaradas antes do uso** e armazenando seus tipos.
* **Execução:** O `InterpreterVisitor` percorre a AST e **interpreta** o código, realizando checagem de tipos em tempo de execução (por exemplo, em `visit(AssignStatement)`).

### 4. Refatoração do Padrão Visitor (`LiteralExpression`)

Uma das principais refatorações do projeto foi a introdução da interface `LiteralExpression` para simplificar o padrão Visitor.

* **Antes:** A interface `Visitor` possuía métodos específicos para cada tipo literal (`visit(NumberExpression)`, `visit(FloatExpression)`, etc.).
* **Agora:** A interface foi simplificada para um método genérico: `visit(LiteralExpression expression)`.
* **Implementação:** Classes como `NumberExpression`, `FloatExpression`, `BooleanExpression` e `StringExpression` implementam `LiteralExpression`.
* **Vantagem:** Os Visitors (`InterpreterVisitor`, `PrintVisitor`) lidam com nós literais de forma genérica, facilitando a adição de novos tipos sem alterar toda a hierarquia.

---

## ▶️ Como Executar e Testar

### 🔧 Pré-requisitos

* **Java Development Kit (JDK)** — compatível com **JDK 8+**.

### 🚀 Passos para execução

1. **Verifique o caminho do arquivo de entrada** no `Main.java`:

   ```java
   String filePath = "input/exemplo.txt";
   ```

2. **Compile e execute** o projeto:

   ```bash
   # Crie o diretório 'bin' se não existir
   mkdir -p bin
   javac -d bin $(find . -name "*.java")
   java -cp bin ijgm_project.Main
   ```

   Ou execute diretamente pela IDE (Eclipse, IntelliJ, VS Code).

### 💡 Saída Esperada

O programa apresenta **3 fases** no console:

1. **Fase 1 – Tokens:** lista sequencial dos tokens reconhecidos pelo `Lexer`.
2. **Fase 2 – AST:** impressão hierárquica da estrutura do código (via `PrintVisitor`).
3. **Fase 3 – Execução:** resultado final das instruções `print` (via `InterpreterVisitor`).

---

## 📝 Exemplo de Código (Funcional)

**Arquivo de entrada (`input/exemplo.txt`):**

```c
/*
 * Teste de Validação Final (Pós-Refatoração Completa)
 */

int a;
float b;
bool c;
string s;
int i;
int x;

a = 10;
b = 1.5;
c = true;
s = "Hello";

print s + " World!"; // Esperado: Hello World!

b = a;
print "Teste de Coercao (Atribuicao):";
print b; // Esperado: 10.0

b = b + 0.5;
print "Teste de Coercao (Aritmetica):";
print b; // Esperado: 10.5

i = 0;
while (i < 3) {
    if (c == true && i < 2) {
        print "Loop (i < 2):";
        print i;
    } else {
        print "Loop (else):";
        print i;
    }
    i = i + 1;
}

print "Teste Final (String + Int):";
print s + i; // Esperado: Hello3
```

---

## 📚 Conceitos Reforçados

* **POO aplicada à construção de compiladores**
* **Design Patterns (Composite, Visitor, Iterator)**
* **Análise Léxica, Sintática e Semântica**
* **Interpretação de código via AST**

---

Desenvolvido como parte de um estudo acadêmico sobre **implementação de compiladores orientados a objetos**, com ênfase em **compiladores** e **arquitetura de linguagens**.
