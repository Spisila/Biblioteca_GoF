# Biblioteca GoF

Sistema de empréstimo de livros desenvolvido em Java 17 para demonstrar a
aplicação integrada dos dez padrões de projeto GoF exigidos pelo trabalho:
State, Strategy, Adapter, Facade, Observer, Command, Template Method,
Abstract Factory, Decorator e Composite.

## Visão geral

O sistema representa o ciclo de vida de livros, reservas, empréstimos,
renovações, devoluções e multas. As operações de circulação são centralizadas
pela fachada e acessadas pela interface de linha de comando por meio dos
tratadores.

O projeto possui:

- Estados de livro com transições protegidas por regras de domínio.
- Políticas configuráveis para cálculo de multas.
- Adaptadores para APIs incompatíveis de cobrança bancária.
- Persistência em memória e persistência relacional via JDBC/MySQL.
- Unidade de trabalho transacional com `commit` e `rollback`.
- Consultas extensíveis por cache e medição.
- Comandos idempotentes para solicitações de circulação.
- CLI interativa com fluxo demonstrativo completo.

## Requisitos técnicos

- Java Development Kit (JDK) 17.
- Gradle Wrapper incluído no repositório.
- MariaDB para o perfil de produção, usando o driver JDBC oficial.
- O perfil de teste utiliza persistência em memória e não exige um servidor
  MySQL.

## Como compilar

No Windows:

```powershell
.\gradlew.bat build
```

Em ambientes Unix-like:

```bash
./gradlew build
```

Esse comando compila o projeto e executa os testes automatizados.

## Como executar a aplicação/CLI

Por padrão, a aplicação inicia usando o perfil `TESTE`, com persistência em
memória:

```powershell
.\gradlew.bat run
```

O perfil de produção, com MariaDB, é o padrão:

```bash
./gradlew run
```

No Windows:

```powershell
.\gradlew.bat run
```

O menu permite cadastrar e consultar pessoas, estudantes, atendentes e livros,
solicitar reservas, efetivar empréstimos, renovar empréstimos, registrar
devoluções e emitir multas. A opção `D` executa um fluxo demonstrativo
completo, incluindo renovação idempotente e devolução atrasada.

Para usar persistência em memória:

```powershell
.\gradlew.bat run --args="--teste"
```

Configure a conexão JDBC em `config/biblioteca.properties`, copiando
`config/biblioteca.properties.example`. Esse arquivo local é ignorado pelo
Git e não deve ser versionado:

```properties
biblioteca.db.url=jdbc:mariadb://localhost:3306/biblioteca
biblioteca.db.usuario=root
biblioteca.db.senha=SUA_SENHA
```

Também é possível configurar por propriedades de sistema ou variáveis de ambiente:

| Propriedade | Variável de ambiente | Padrão |
|---|---|---|
| `biblioteca.db.url` | `BIBLIOTECA_DB_URL` | `jdbc:mariadb://localhost:3306/biblioteca` |
| `biblioteca.db.usuario` | `BIBLIOTECA_DB_USUARIO` | `root` |
| `biblioteca.db.senha` | `BIBLIOTECA_DB_SENHA` | vazio |

O esquema básico está disponível em [sql/schema.sql](sql/schema.sql).

## Como rodar os testes

Execute toda a suíte com:

```bash
./gradlew test
```

No Windows:

```powershell
.\gradlew.bat test
```

Os testes cobrem os requisitos R01, R02, R03, R04, R05, R06, R07, R08, R09 e
R10, além do teste de fumaça do projeto. O relatório de rastreabilidade está
em [docs/rastreabilidade.md](docs/rastreabilidade.md).

## Ferramentas de LLM usadas

Foi utilizado o GitHub Copilot, por meio do Copilot SDK no VS Code, como
ferramenta de apoio à geração, revisão e validação de código.

As sugestões foram analisadas e ajustadas conforme os requisitos do enunciado,
as instruções do repositório e os testes automatizados. O histórico de pedidos,
decisões e correções está registrado em
[REGISTRO_LLM.md](REGISTRO_LLM.md).

## Estrutura do projeto e padrões GoF

```text
src/main/java/br/pucpr/biblioteca/
├── app/                    # composição da aplicação e seleção do perfil
├── ui/                     # CLI interativa
├── tratadores/             # tradução das entradas da UI
├── biblioteca/
│   ├── livro/              # contexto e estados do livro
│   ├── movimentacao/       # empréstimos e transações
│   ├── relacionamento/     # reservas
│   └── acervo/
│       ├── consulta/       # serviço de consulta e Decorator
│       └── composite/      # organização hierárquica do acervo
├── circulacao/
│   ├── facade/             # operações integradas de circulação
│   └── comando/            # solicitações e executor idempotente
├── devolucao/              # eventos e ouvintes de devolução
├── financeiro/
│   ├── multa/              # políticas de multa
│   └── cobranca/           # contrato e adaptadores bancários
├── cadastro/               # Template Method de cadastro
├── persistencia/           # Abstract Factory e famílias de persistência
├── comum/                  # dinheiro, relógio e exceções
├── academico/              # estrutura de estudantes
└── rh/                     # estrutura de pessoas e atendentes
```

### Mapeamento dos padrões

| Requisito | Padrão | Aplicação principal |
|---|---|---|
| R01 | State | `Livro`, `EstadoLivro` e estados concretos do ciclo de vida |
| R02 | Strategy | `EmissorMulta`, `PoliticaMulta` e políticas de cálculo |
| R03 | Adapter | `Cobranca`, adaptadores e bancos simulados |
| R04 | Facade | `FachadaCirculacao` e os repositórios coordenados |
| R05 | Observer | `PublicadorDevolucao`, eventos e ouvintes |
| R06 | Command | `Comando`, comandos concretos e `ExecutorComando` |
| R07 | Template Method | `TemplateCadastro` e cadastros especializados |
| R08 | Abstract Factory | `FabricaPersistencia` e famílias memória/relacional |
| R09 | Decorator | `ServicoConsulta`, `DecoratorCache` e `DecoratorMedicao` |
| R10 | Composite | `ElementoAcervo`, `GrupoAcervo`, seções e itens |

