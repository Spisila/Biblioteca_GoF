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

