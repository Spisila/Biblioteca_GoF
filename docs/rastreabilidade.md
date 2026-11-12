# Rastreabilidade

| Requisito | Padrão | Classes por papel | Teste |
|---|---|---|---|
| R01 | State | `Livro` (Contexto); `EstadoLivro`, `EstadoLivroBase` (Estado abstrato); `EstadoLivre`, `EstadoReservado`, `EstadoEmprestado`, `EstadoRenovado`, `EstadoDevolvido`, `EstadoReparando`, `EstadoIncinerado` (Estados concretos); `EstadosLivro` (fábrica de reconstituição) | `R01_CicloDeVidaLivroTest`, `EmprestimoTest` |
| R08 | Abstract Factory | `FabricaPersistencia` (Fábrica abstrata); `FabricaPersistenciaMemoria`, `FabricaPersistenciaRelacional` (Fábricas concretas); `UnidadeDeTrabalho`, `RepositorioLivro`, `RepositorioMovimentacao`, `RepositorioRelacionamento` (Produtos abstratos); produtos `*Memoria` e `*Relacional` (Produtos concretos) | `R08_FabricaPersistenciaTest` |

