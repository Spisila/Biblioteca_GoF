# Registro de uso de LLM

Histórico dos pedidos realizados ao modelo durante a construção do projeto.
As sugestões foram revisadas e validadas conforme o enunciado, as instruções
do repositório e os testes automatizados.

| Data e hora (UTC−03:00) | Pedido ao modelo / progresso | Situação |
|---|---|---|
| 2026-11-10 15:20 | Criar o esqueleto de um projeto Java 17 com Gradle, estrutura de pacotes, testes iniciais, documentação, wrapper e configuração do sistema de biblioteca GoF. | Implementado; `build` validado. |
| 2026-11-10 16:34 | Criar os arquivos `.puml` dos casos de uso solicitados em `docs/plantuml`, preservando diagramas existentes. | Implementado; 16 arquivos criados. |
| 2026-11-10 18:28 | Implementar somente o pacote `comum`, incluindo `Dinheiro`, relógios, exceções de domínio e testes. | Implementado; testes passaram. |
| 2026-11-10 18:48 | Registrar no arquivo o resumo dos pedidos e do progresso do projeto. | Implementado. |
| 2026-11-11 | Implementar o R01 State para o ciclo de vida do livro, empréstimos, estados concretos, testes, PlantUML e rastreabilidade. | Implementado; testes passaram. |
| 2026-11-11 | Ajustar R01 e implementar R08 Abstract Factory com família em memória, esqueleto relacional, reservas, unidades de trabalho, fábricas e testes. | Implementado; testes passaram. |
| 2026-11-11 | Implementar R02 Strategy para políticas de multa original, com teto e com tolerância, emissão e testes. | Implementado; testes passaram. |
| 2026-11-11 | Implementar R07 Template Method para cadastros de livro, pessoa, estudante e atendente, incluindo duplicidade, pré-requisitos e testes. | Implementado; testes passaram. |
| 2026-11-11 | Implementar R03 Adapter para cobrança bancária, com contrato institucional, bancos simulados, conversão monetária, notificações idempotentes e testes. | Implementado; testes passaram. |
| 2026-11-11 | Implementar R05 Observer para devoluções, com evento, publicador, ouvintes de inspeção, multa, indicadores e e-mail. | Implementado; testes passaram. |
| 2026-11-12 | Implementar R04 Facade para operações transacionais de circulação, empréstimo, renovação, devolução, rollback, testes e rastreabilidade. | Implementado; testes passaram. |
| 2026-11-12 | Implementar R06 Command para reservas e renovações como comandos idempotentes, com executor concorrente e teste de renovação duplicada. | Implementado; testes passaram. |
| 2026-11-12 | Implementar R09 Decorator para consultas do acervo, com serviço original, cache de metadados, medição e teste de estado real. | Implementado; testes passaram. |
| 2026-11-12 | Implementar R10 Composite para unidades, seções, subseções e itens do acervo, com contagem recursiva, prevenção de ciclos, duplicidades e movimentação. | Implementado; testes passaram. |
| 2026-11-12 | Implementar os tratadores de acervo, atendente, estudante, pessoa, empréstimo, devolução e multa com injeção de dependências. | Implementado; suíte passou. |
| 2026-11-12 | Implementar a família de persistência relacional completa com JDBC/MySQL, transações, repositórios relacionais e `sql/schema.sql`. | Implementado; suíte passou; integração com servidor MySQL não executada no ambiente. |
| 2026-11-12 | Implementar a CLI interativa, atualizar `Main`, integrar tratadores, comandos idempotentes e ouvintes de devolução. | Implementado; CLI inicializada e suíte passou. |
| 2026-11-12 | Reescrever o `README.md` com visão geral, requisitos, comandos, CLI, LLM, estrutura e mapeamento dos padrões. | Implementado. |
| 2026-11-12 18:52 | Atualizar este registro com todos os pedidos realizados e criar documentação detalhada da aplicação dos padrões GoF. | Implementado. |

