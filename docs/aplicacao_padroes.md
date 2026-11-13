# Aplicação dos padrões GoF

Este documento descreve onde e como os dez padrões GoF são utilizados no
projeto `biblioteca-gof`. A tabela completa de requisitos, papéis e testes
está em [rastreabilidade.md](rastreabilidade.md).

## Visão arquitetural

O fluxo externo passa pela CLI em `ui`, que chama os tratadores. Os tratadores
traduzem entradas e delegam as operações para a fachada, templates, serviços
de consulta ou emissor de multas. A fachada coordena o domínio de circulação
e as unidades de trabalho da persistência.

As regras de negócio permanecem nos objetos de domínio, especialmente em
`Livro`, `Emprestimo` e suas estratégias/estados. A seleção da persistência é
feita na composição da aplicação por `Configuracao`.

## R01 — State

**Localização:** `biblioteca/livro` e `biblioteca/livro/estado`.

**Implementação:**

- `Livro` é o contexto e mantém uma referência ao `EstadoLivro` atual.
- `EstadoLivro` define os eventos possíveis do ciclo de vida.
- `EstadoLivroBase` fornece o comportamento padrão de recusa.
- Os estados concretos são `EstadoLivre`, `EstadoReservado`,
  `EstadoEmprestado`, `EstadoRenovado`, `EstadoDevolvido`,
  `EstadoReparando` e `EstadoIncinerado`.
- `EstadosLivro` reconstrói o estado a partir de `SituacaoLivro` persistido.

Cada evento é delegado ao estado atual. Assim, a validação de reserva,
empréstimo, renovação, devolução, reparo e incineração fica distribuída pelos
estados, sem condicionais de situação no contexto.

## R02 — Strategy

**Localização:** `financeiro/multa`.

**Implementação:**

- `PoliticaMulta` é o contrato da estratégia.
- `PoliticaOriginal`, `PoliticaComTeto` e `PoliticaComTolerancia` são
  estratégias substituíveis.
- `EmissorMulta` é o contexto e permite configurar a política vigente.
- `Multa` registra o valor e a política usada no momento da emissão.

A troca da política não altera multas já emitidas, pois cada registro conserva
a estratégia aplicada ao cálculo.

## R03 — Adapter

**Localização:** `financeiro/cobranca`.

**Implementação:**

- `Cobranca` é o contrato institucional usado pelo sistema.
- `BancoASimulado` trabalha com centavos inteiros e identificadores próprios.
- `BancoBSimulado` trabalha com valores em reais e outro formato de referência.
- `AdaptadorBancoA` e `AdaptadorBancoB` convertem valores, identificadores e
  notificações para o contrato institucional.
- `ServicoCobranca` depende somente de `Cobranca`, sem conhecer tipos de banco.

As notificações possuem controle de duplicidade para impedir que uma mesma
quitação gere baixa repetida.

## R04 — Facade

**Localização:** `circulacao/facade/FachadaCirculacao`.

**Implementação:**

`FachadaCirculacao` é a entrada única para operações completas de circulação.
Ela coordena:

- `RepositorioLivro`;
- `RepositorioMovimentacao`;
- `RepositorioRelacionamento`;
- `UnidadeDeTrabalho`;
- eventos de devolução, quando configurados.

O empréstimo valida a reserva, altera o estado do livro, remove a reserva,
salva a movimentação e confirma a unidade de trabalho. Renovação e devolução
seguem o mesmo limite transacional. Falhas provocam rollback.

## R05 — Observer

**Localização:** `devolucao/evento`, `devolucao/publicador` e
`devolucao/ouvintes`.

**Implementação:**

- `PublicadorDevolucao` é o sujeito.
- `OuvinteDevolucao` é o contrato de observação.
- `EventoDevolucao` transporta livro, estudante, vencimento, data real e atraso.
- `OuvinteInspecaoExemplar` registra exemplares pendentes.
- `OuvinteMulta` emite multas para devoluções atrasadas.
- `OuvinteIndicadores` atualiza estatísticas.
- `OuvinteEmail` encaminha notificações.

A publicação ocorre depois da confirmação da devolução. Uma falha em um
ouvinte é registrada sem impedir os demais.

## R06 — Command

**Localização:** `circulacao/comando`.

**Implementação:**

- `Comando<T>` define identificador, autor, instante de criação e execução.
- `ComandoReserva` encapsula uma solicitação de reserva.
- `ComandoRenovacao` encapsula uma solicitação de renovação.
- `ExecutorComando` é o invocador.

O executor associa cada identificador a uma tarefa concorrente. O uso de
`ConcurrentHashMap.putIfAbsent` e `FutureTask` garante que o mesmo comando seja
executado uma única vez, mesmo quando submetido simultaneamente.

## R07 — Template Method

**Localização:** `cadastro`.

**Implementação:**

- `TemplateCadastro.incluir` é o método-modelo final.
- `CadastroLivro`, `CadastroPessoa`, `CadastroEstudante` e
  `CadastroAtendente` implementam as etapas variáveis.
- `RepositorioCadastro` e `RegistroCadastravel` dão suporte aos cadastros
  genéricos ainda não representados por entidades específicas.

A ordem fixa é buscar existente, validar, verificar pré-requisitos, criar,
armazenar e apresentar. Duplicatas retornam o registro já existente e
pré-requisitos são verificados antes da gravação.

## R08 — Abstract Factory

**Localização:** `persistencia/fabrica`, `persistencia/memoria`,
`persistencia/relacional` e `app/Configuracao`.

**Implementação:**

- `FabricaPersistencia` define a família abstrata.
- `FabricaPersistenciaMemoria` cria unidades e repositórios em memória.
- `FabricaPersistenciaRelacional` cria unidades e repositórios JDBC.
- `UnidadeDeTrabalho`, `RepositorioLivro`, `RepositorioMovimentacao` e
  `RepositorioRelacionamento` são produtos abstratos.
- As implementações `*Memoria` e `*Relacional` são produtos concretos.

`Configuracao.fabricaPara` é o ponto único de escolha entre os perfis
`TESTE` e `PRODUCAO`. Os produtos de uma família não podem ser misturados.

## R09 — Decorator

**Localização:** `biblioteca/acervo/consulta`.

**Implementação:**

- `ServicoConsulta` é o componente.
- `ServicoConsultaOriginal` consulta o repositório sem extensões.
- `DecoratorConsulta` é o decorator abstrato.
- `DecoratorCache` adiciona cache de `MetadadosLivro`.
- `DecoratorMedicao` adiciona contagem e medição de duração das consultas.

Os decorators podem ser combinados sem alterar o serviço original. O cache
guarda somente metadados imutáveis; operações críticas continuam consultando
o `Livro` real por meio da fachada e das regras de domínio.

## R10 — Composite

**Localização:** `biblioteca/acervo/composite`.

**Implementação:**

- `ElementoAcervo` é o componente comum.
- `ItemAcervo` é a folha e referencia um livro por identificador.
- `GrupoAcervo` é o composite reutilizável.
- `SecaoAcervo` representa seções e subseções.
- `UnidadeAcervo` representa a raiz da organização.

Grupos e itens respondem uniformemente a contagem e listagem. A estrutura
mantém referências aos grupos-pai para rejeitar ciclos e impedir que um livro
apareça em mais de um grupo. Mover um item transfere a mesma referência,
portanto não altera o `Livro` nem seu estado de empréstimo.

## Integração com a aplicação

`app.Main` monta as dependências e seleciona o perfil de execução. A
`ConsoleBiblioteca` acessa os recursos somente pelos tratadores:

1. os tratadores recebem entradas da CLI;
2. a fachada coordena operações de circulação;
3. comandos encapsulam solicitações que precisam de idempotência;
4. eventos de devolução notificam observadores;
5. a fábrica fornece a família de persistência selecionada.

Esse fluxo mantém a interface desacoplada das entidades, repositórios e regras
internas do domínio.

