# Rastreabilidade

| Requisito | Padrão | Classes por papel | Teste |
|---|---|---|---|
| R01 | State | `Livro` (Contexto); `EstadoLivro`, `EstadoLivroBase` (Estado abstrato); `EstadoLivre`, `EstadoReservado`, `EstadoEmprestado`, `EstadoRenovado`, `EstadoDevolvido`, `EstadoReparando`, `EstadoIncinerado` (Estados concretos); `EstadosLivro` (fábrica de reconstituição) | `R01_CicloDeVidaLivroTest`, `EmprestimoTest` |
| R08 | Abstract Factory | `FabricaPersistencia` (Fábrica abstrata); `FabricaPersistenciaMemoria`, `FabricaPersistenciaRelacional` (Fábricas concretas); `UnidadeDeTrabalho`, `RepositorioLivro`, `RepositorioMovimentacao`, `RepositorioRelacionamento` (Produtos abstratos); produtos `*Memoria` e `*Relacional` (Produtos concretos) | `R08_FabricaPersistenciaTest` |
| R02 | Strategy | `EmissorMulta` (Contexto); `PoliticaMulta` (Estratégia abstrata); `PoliticaOriginal`, `PoliticaComTeto`, `PoliticaComTolerancia` (Estratégias concretas); `Multa` (registro do valor e da política aplicada) | `R02_PoliticasMultaTest` |
| R07 | Template Method | `TemplateCadastro` (Classe abstrata); `CadastroLivro`, `CadastroPessoa`, `CadastroEstudante`, `CadastroAtendente` (Subclasses concretas); `RepositorioCadastro` e `RegistroCadastravel` (apoio aos cadastros ainda não modelados) | `R07_TemplateCadastroTest` |
| R03 | Adapter | `Cobranca` (Alvo); `AdaptadorBancoA`, `AdaptadorBancoB` (Adaptadores concretos); `BancoASimulado`, `BancoBSimulado` (Adaptees); `ServicoCobranca` (cliente do contrato) | `R03_AdaptadorBancoTest` |
| R05 | Observer | `PublicadorDevolucao` (Sujeito concreto); `OuvinteDevolucao` (Observador); `OuvinteInspecaoExemplar`, `OuvinteMulta`, `OuvinteIndicadores`, `OuvinteEmail` (Observadores concretos); `EventoDevolucao` (evento) | `R05_ObserverDevolucaoTest` |
| R04 | Facade | `FachadaCirculacao` (Fachada); `RepositorioLivro`, `RepositorioMovimentacao`, `RepositorioRelacionamento`, `UnidadeDeTrabalho` (subsistemas coordenados) | `R04_FachadaCirculacaoTest` |
| R06 | Command | `Comando` (Comando); `ComandoReserva`, `ComandoRenovacao` (Comandos concretos); `ExecutorComando` (Invocador) | `R06_ComandoIdempotenteTest` |
| R09 | Decorator | `ServicoConsulta` (Componente); `ServicoConsultaOriginal` (Componente concreto); `DecoratorConsulta` (Decorator abstrato); `DecoratorCache`, `DecoratorMedicao` (Decorators concretos); `MetadadosLivro` (metadados) | `R09_DecoratorConsultaTest` |
| R10 | Composite | `ElementoAcervo` (Componente); `ItemAcervo` (Folha); `GrupoAcervo`, `SecaoAcervo`, `UnidadeAcervo` (Composites) | `R10_CompositeAcervoTest` |

## Cenários realmente verificados nesta revisão

- R01: estados concretos, transições, limite de duas renovações, devolução, inspeção e reconstituição em `R01_CicloDeVidaLivroTest` e `EmprestimoTest`.
- R02: valores original, teto e tolerância e preservação da política emitida em `R02_PoliticasMultaTest`.
- R03: conversões dos bancos A/B e idempotência em `R03_AdaptadorBancoTest`.
- R04: empréstimo completo e rollback em `R04_FachadaCirculacaoTest`.
- R05: publicação, ouvintes e falha isolada em `R05_ObserverDevolucaoTest`.
- R06: renovação e reserva concorrentes com identificadores idempotentes em `R06_ComandoIdempotenteTest`.
- R07: ordem do template, duplicidade, CPF inexistente e compartilhamento do ID da Pessoa em `R07_TemplateCadastroTest`.
- R08: famílias memória/relacional, mixagem recusada e transações em memória em `R08_FabricaPersistenciaTest`.
- R09: combinação cache/medição e consulta crítica do estado real em `R09_DecoratorConsultaTest`.
- R10: cinco itens em duas seções, subseções, ciclos, duplicidade e movimentação em `R10_CompositeAcervoTest`.

