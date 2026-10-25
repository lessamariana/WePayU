# WePayU

Sistema de folha de pagamento desenvolvido para a disciplina de Programação 2 do curso de Ciência da Computação da Universidade Federal de Alagoas (UFAL).

## Informações acadêmicas

**Mariana Lessa - mlcs@ic.ufal.br**

**Curso de Ciência da Computação**

**Instituto de Computação — UFAL**

**Universidade Federal de Alagoas — UFAL**

**Programação 2 (COMP372) com o Professor Mario Hozano**

A disciplina de Programação 2 integra a matriz curricular do curso de Ciência da Computação da UFAL.

---

## Sobre o projeto

O **WePayU** é um sistema de folha de pagamento desenvolvido em Java com foco na aplicação de conceitos de **Programação Orientada a Objetos**.

O sistema permite cadastrar e administrar empregados, registrar informações relacionadas ao trabalho e calcular pagamentos de acordo com diferentes tipos de empregados.

O projeto também implementa persistência dos dados, permitindo que as informações do sistema sejam armazenadas e recuperadas após o encerramento da aplicação.

---

## Objetivos

O projeto tem como principais objetivos:

* Aplicar conceitos de Programação Orientada a Objetos
* Trabalhar com classes, objetos, encapsulamento, herança e polimorfismo
* Utilizar tratamento de exceções específicas
* Desenvolver uma arquitetura organizada em diferentes responsabilidades
* Implementar regras de cálculo de folha de pagamento
* Trabalhar com testes de aceitação
* Implementar persistência de dados utilizando XML
* Utilizar `XMLEncoder` e `XMLDecoder` da biblioteca padrão do Java

---

## Tecnologias utilizadas

* **Java**
* **IntelliJ IDEA**
* **Git**
* **GitHub**
* **EasyAccept**
* **XMLEncoder**
* **XMLDecoder**

A persistência utiliza as classes:

```java
java.beans.XMLEncoder
java.beans.XMLDecoder
```

---

# Modelo de empregados

O sistema utiliza uma classe base:

```text
Empregado
    │
    ├── EmpregadoHorista
    ├── EmpregadoAssalariado
    └── EmpregadoComissionado
```

Essa estrutura permite utilizar **herança e polimorfismo** para representar os diferentes tipos de empregados.

### Empregado Horista

O empregado horista recebe de acordo com as horas trabalhadas.

O sistema registra os cartões de ponto e diferencia:

* horas normais
* horas extras

As horas extras possuem uma regra de cálculo específica.

### Empregado Assalariado

O empregado assalariado recebe um salário fixo de acordo com as regras definidas para a folha de pagamento.

### Empregado Comissionado

O empregado comissionado possui:

* salário
* percentual de comissão
* vendas realizadas

O valor das vendas é utilizado no cálculo da comissão.

---

# Métodos de pagamento

O sistema possui diferentes formas de pagamento:

### Em mãos

O pagamento é realizado diretamente ao empregado.

### Correios

O pagamento é realizado por meio de envio para o endereço cadastrado.

### Banco

O pagamento é realizado por depósito bancário.

Nesse caso são armazenadas informações como:

* banco
* agência
* conta corrente

---

# Funcionalidades

Entre as funcionalidades implementadas estão:

* Criação de empregados
* Remoção de empregados
* Alteração de dados
* Consulta de empregados
* Alteração de salário
* Registro de cartões de ponto
* Registro de vendas
* Registro de sindicato
* Registro de taxas de serviço
* Alteração do método de pagamento
* Cálculo da folha de pagamento
* Execução da folha
* Desfazer e refazer comandos (undo/redo)
* Agendas de pagamento
* Criação de agendas de pagamento customizadas
* Persistência dos dados

---

# Histórias de usuário implementadas

Todas as histórias de usuário, da US1 até a US10_1, estão implementadas e passando nos testes de aceitação.

Nesta última etapa foram implementadas:

* **US9 – Agendas de pagamento:** cada empregado passou a ter o atributo `agendaPagamento`, que pode ser consultado com `getAtributoEmpregado` e alterado com `alteraEmpregado`. As agendas padrão são `semanal 5` para horistas, `mensal $` para assalariados e `semanal 2 5` para comissionados.
* **US9_1 – Persistência da US9:** a agenda de cada empregado é salva no XML e recuperada quando o sistema é iniciado novamente.
* **US10 – Agendas de pagamento customizadas:** o comando `criarAgendaDePagamentos` permite cadastrar novas agendas, como `semanal 1`, `mensal 15` ou `semanal 4 3`. A descrição é validada: `semanal` aceita de 1 a 7 (dia da semana) ou, com dois parâmetros, de 1 a 52 semanas e de 1 a 7; `mensal` aceita de 1 a 28 ou `$` (último dia útil do mês).
* **US10_1 – Persistência da US10:** as agendas criadas também são salvas no XML e continuam disponíveis depois que o sistema é reiniciado.

---

# Persistência

A persistência foi implementada utilizando as classes da API padrão do Java:

```java
XMLEncoder
XMLDecoder
```

O estado do sistema é armazenado em um arquivo XML.

O processo de gravação ocorre quando o sistema é encerrado:

```text
Facade
   │
   ▼
encerrarSistema()
   │
   ▼
Persistencia.salvar()
   │
   ▼
XMLEncoder
   │
   ▼
wepayu.xml
```

Na inicialização do sistema, os dados são recuperados:

```text
Facade
   │
   ▼
carregarPersistencia()
   │
   ▼
Persistencia.carregar()
   │
   ▼
XMLDecoder
   │
   ▼
reconstrução dos objetos
```

A persistência mantém informações importantes dos empregados, incluindo dados cadastrais, pagamento, sindicato e registros necessários para o cálculo da folha.

---

# Testes

O projeto utiliza **EasyAccept** para execução dos testes de aceitação.

Os testes estão organizados em arquivos:

```text
tests/
├── us1.txt
├── us1_1.txt
├── us2.txt
├── us2_1.txt
├── us3.txt
├── us3_1.txt
├── us4.txt
├── us4_1.txt
├── us5.txt
├── us5_1.txt
├── us6.txt
├── us6_1.txt
├── us7.txt
└── us8.txt
```

Os arquivos com sufixo `_1` estão relacionados aos testes de persistência das respectivas funcionalidades.

---

# Execução dos testes

Com o projeto configurado no IntelliJ IDEA, é possível executar os testes através da classe `Main`.

Para os testes de persistência, é importante respeitar a ordem de execução.

```text
US1 → US1_1
US2 → US2_1
US3 → US3_1
US4 → US4_1
US5 → US5_1
US6 → US6_1
```

Isso ocorre porque os testes de persistência verificam se o estado criado anteriormente continua disponível depois de uma nova execução do sistema.

---

# Refatoração

Depois de concluir as histórias de usuário, o projeto foi refatorado com base no que foi estudado sobre refatoração, padrões de projeto e princípios SOLID, então deixo aqui alguns materiais que foram sugestões do monitor:

* [Refactoring Guru – O que é refatoração](https://refactoring.guru/refactoring/what-is-refactoring)
* [Alura – SOLID](https://www.alura.com.br/artigos/solid)

A refatoração também levou em consideração os comentários da última correção do projeto. O comportamento do sistema foi mantido: todos os testes de aceitação continuam passando e as folhas de pagamento geradas são as mesmas de antes da refatoração. Os comentários feitos durante o desenvolvimento foram mantidos e acompanharam o código para as novas classes.

---

## Padrões de projeto utilizados

* **Facade:** a classe `Facade` é o ponto único de entrada usado pelos testes e apenas delega para as demais classes.
* **Factory:** `EmpregadoFactory` cria os tipos de empregado (inclusive na mudança de tipo) e `MetodoPagamentoFactory` cria os métodos de pagamento.
* **Command:** cada operação que pode ser desfeita é representada por um `Controle` (`ControleEmpregado` ou `ControleGlobal`), empilhado no `HistoricoComandos`.
* **Memento:** o estado dos empregados é guardado antes e depois de cada operação (`EstadoRepositorio`), permitindo o undo/redo.
* **Repository:** `RepositorioEmpregados` e `RepositorioAgendas` concentram o armazenamento e a busca dos objetos.

---

# Conceitos de Programação Orientada a Objetos utilizados

O projeto busca aplicar conceitos fundamentais de POO.

## Encapsulamento

Os dados dos empregados são mantidos dentro das respectivas classes, sendo acessados através de métodos.

## Herança

As diferentes categorias de empregados herdam características da classe:

## Polimorfismo

O comportamento específico de cada tipo de empregado é definido nas subclasses.

Por exemplo, o cálculo do pagamento é diferente para cada categoria:

Isso permite que a folha de pagamento trabalhe com `Empregado` sem precisar conhecer todos os detalhes de cada subclasse.

## Factory

A criação dos empregados é centralizada. Isso evita espalhar a lógica de criação dos diferentes tipos pelo sistema.

## Exceções específicas

O projeto possui exceções específicas para diferentes situações inválidas, evitando concentrar todos os erros em uma única exceção genérica.

---

# Persistência e polimorfismo

A camada de persistência também utiliza o polimorfismo presente no domínio, já que cada classe fornece os dados específicos necessários através de métodos polimórficos.

Isso mantém a responsabilidade distribuída entre as classes e evita verificações explícitas do tipo do objeto.

---

# Estrutura do projeto

```text
src/br/ufal/ic/p2/wepayu
├── Facade.java            → ponto de entrada dos testes, apenas delega
├── Exception/             → exceções específicas do sistema
├── models/
│   ├── empregado/         → Empregado e subclasses, EmpregadoFactory, CartaoPonto, Venda, TaxaServico
│   ├── pagamento/         → MetodoPagamento e subclasses, MetodoPagamentoFactory, AgendaPagamento
│   └── folha/             → FolhaPagamento, RelatorioFolha, ResultadoPagamento, RegistroPagamento e subclasses
├── repositorio/           → RepositorioEmpregados, GeradorId, EstadoRepositorio, MembrosSindicato, RepositorioAgendas
├── servicos/              → AlteradorEmpregado
├── controle/              → undo/redo (Controle, ControleEmpregado, ControleGlobal, HistoricoComandos) e EstadoSistema
├── persistencia/          → Persistencia (XMLEncoder/XMLDecoder)
└── utilitarios/           → DataTratamento, Formatador, ConversorValores, Periodo
```

---

# Desenvolvimento

O projeto foi desenvolvido de forma incremental, implementando as funcionalidades de acordo com as histórias de usuário e seus respectivos testes de aceitação.

A implementação foi organizada para preservar as funcionalidades anteriormente desenvolvidas enquanto novas funcionalidades eram adicionadas.

---
