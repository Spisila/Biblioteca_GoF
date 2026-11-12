package br.pucpr.biblioteca.persistencia.fabrica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucpr.biblioteca.app.Configuracao;
import br.pucpr.biblioteca.app.PerfilExecucao;
import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.biblioteca.livro.SituacaoLivro;
import br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo;
import br.pucpr.biblioteca.biblioteca.relacionamento.Reserva;
import br.pucpr.biblioteca.persistencia.memoria.FabricaPersistenciaMemoria;
import br.pucpr.biblioteca.persistencia.memoria.RepositorioLivroMemoria;
import br.pucpr.biblioteca.persistencia.memoria.RepositorioMovimentacaoMemoria;
import br.pucpr.biblioteca.persistencia.memoria.RepositorioRelacionamentoMemoria;
import br.pucpr.biblioteca.persistencia.memoria.UnidadeDeTrabalhoMemoria;
import br.pucpr.biblioteca.persistencia.relacional.FabricaPersistenciaRelacional;
import br.pucpr.biblioteca.persistencia.relacional.RepositorioLivroRelacional;
import br.pucpr.biblioteca.persistencia.relacional.RepositorioMovimentacaoRelacional;
import br.pucpr.biblioteca.persistencia.relacional.RepositorioRelacionamentoRelacional;
import br.pucpr.biblioteca.persistencia.relacional.UnidadeDeTrabalhoRelacional;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class R08_FabricaPersistenciaTest {
    @Test
    void R08_perfilTesteFornecaApenasObjetosEmMemoria() {
        FabricaPersistencia fabrica = Configuracao.fabricaPara(PerfilExecucao.TESTE);
        UnidadeDeTrabalho unidade = fabrica.criarUnidadeDeTrabalho();

        assertInstanceOf(FabricaPersistenciaMemoria.class, fabrica);
        assertInstanceOf(UnidadeDeTrabalhoMemoria.class, unidade);
        assertInstanceOf(RepositorioLivroMemoria.class, fabrica.criarRepositorioLivro(unidade));
        assertInstanceOf(RepositorioMovimentacaoMemoria.class, fabrica.criarRepositorioMovimentacao(unidade));
        assertInstanceOf(RepositorioRelacionamentoMemoria.class, fabrica.criarRepositorioRelacionamento(unidade));
        unidade.rollback();
    }

    @Test
    void R08_perfilProducaoFornecaRepositoriosRelacionais() {
        FabricaPersistencia fabrica = Configuracao.fabricaPara(PerfilExecucao.PRODUCAO);
        UnidadeDeTrabalhoRelacional unidade = (UnidadeDeTrabalhoRelacional) fabrica.criarUnidadeDeTrabalho();

        assertInstanceOf(FabricaPersistenciaRelacional.class, fabrica);
        assertInstanceOf(RepositorioLivroRelacional.class, fabrica.criarRepositorioLivro(unidade));
        assertInstanceOf(RepositorioMovimentacaoRelacional.class, fabrica.criarRepositorioMovimentacao(unidade));
        assertInstanceOf(RepositorioRelacionamentoRelacional.class, fabrica.criarRepositorioRelacionamento(unidade));
        assertSame(unidade, ((RepositorioLivroRelacional) fabrica.criarRepositorioLivro(unidade))
                .unidadeDeTrabalho());
        assertSame(unidade, ((RepositorioMovimentacaoRelacional) fabrica.criarRepositorioMovimentacao(unidade))
                .unidadeDeTrabalho());
        assertSame(unidade, ((RepositorioRelacionamentoRelacional) fabrica.criarRepositorioRelacionamento(unidade))
                .unidadeDeTrabalho());
    }

    @Test
    void R08_naoMisturaFamilias() {
        FabricaPersistenciaMemoria memoria = new FabricaPersistenciaMemoria();
        FabricaPersistenciaRelacional relacional = new FabricaPersistenciaRelacional();
        UnidadeDeTrabalhoMemoria unidadeMemoria = memoria.criarUnidadeDeTrabalho();
        UnidadeDeTrabalhoRelacional unidadeRelacional = relacional.criarUnidadeDeTrabalho();

        assertThrows(IllegalArgumentException.class, () -> relacional.criarRepositorioLivro(unidadeMemoria));
        assertThrows(IllegalArgumentException.class, () -> relacional.criarRepositorioMovimentacao(unidadeMemoria));
        assertThrows(IllegalArgumentException.class, () -> relacional.criarRepositorioRelacionamento(unidadeMemoria));
        assertThrows(IllegalArgumentException.class, () -> memoria.criarRepositorioLivro(unidadeRelacional));
        assertThrows(IllegalArgumentException.class, () -> memoria.criarRepositorioMovimentacao(unidadeRelacional));
        assertThrows(IllegalArgumentException.class, () -> memoria.criarRepositorioRelacionamento(unidadeRelacional));
        unidadeMemoria.rollback();
    }

    @Test
    void R08_commitPublicaERollbackDescarta() {
        FabricaPersistenciaMemoria fabrica = new FabricaPersistenciaMemoria();
        Livro livro = new Livro("l1", "Livro");

        UnidadeDeTrabalhoMemoria unidadeRollback = fabrica.criarUnidadeDeTrabalho();
        RepositorioLivro repositorioRollback = fabrica.criarRepositorioLivro(unidadeRollback);
        repositorioRollback.salvar(livro);
        unidadeRollback.rollback();
        UnidadeDeTrabalhoMemoria aposRollback = fabrica.criarUnidadeDeTrabalho();
        assertFalse(fabrica.criarRepositorioLivro(aposRollback).buscarPorId("l1").isPresent());
        aposRollback.rollback();

        UnidadeDeTrabalhoMemoria unidadeCommit = fabrica.criarUnidadeDeTrabalho();
        fabrica.criarRepositorioLivro(unidadeCommit).salvar(livro);
        unidadeCommit.commit();
        UnidadeDeTrabalhoMemoria aposCommit = fabrica.criarUnidadeDeTrabalho();
        assertTrue(fabrica.criarRepositorioLivro(aposCommit).buscarPorId("l1").isPresent());
        aposCommit.rollback();
    }

    @Test
    void R08_repositorioReconstituiEstadoDoLivro() {
        FabricaPersistenciaMemoria fabrica = new FabricaPersistenciaMemoria();
        Livro livro = Livro.reconstituir("l1", "Livro", SituacaoLivro.EMPRESTADO, null, null);
        UnidadeDeTrabalhoMemoria unidade = fabrica.criarUnidadeDeTrabalho();
        fabrica.criarRepositorioLivro(unidade).salvar(livro);
        unidade.commit();

        UnidadeDeTrabalhoMemoria leitura = fabrica.criarUnidadeDeTrabalho();
        RepositorioLivro repositorio = fabrica.criarRepositorioLivro(leitura);
        Livro lido = repositorio.buscarPorId("l1").orElseThrow();
        lido.registrarDevolucao(emprestimo(), LocalDate.of(2026, 2, 20));

        assertEquals(SituacaoLivro.EMPRESTADO, repositorio.buscarPorId("l1").orElseThrow().situacao());
        leitura.rollback();
    }

    @Test
    void R08_repositoriosCompartilhamMesmaUnidadeDeTrabalho() {
        FabricaPersistenciaMemoria fabrica = new FabricaPersistenciaMemoria();
        UnidadeDeTrabalhoMemoria unidade = fabrica.criarUnidadeDeTrabalho();
        RepositorioLivro livros = fabrica.criarRepositorioLivro(unidade);
        RepositorioMovimentacao movimentacoes = fabrica.criarRepositorioMovimentacao(unidade);
        RepositorioRelacionamento relacionamentos = fabrica.criarRepositorioRelacionamento(unidade);

        livros.salvar(new Livro("l1", "Livro"));
        movimentacoes.salvar(emprestimo());
        relacionamentos.salvarReserva(new Reserva("r1", "l1", "e1", LocalDate.of(2026, 1, 10)));

        UnidadeDeTrabalhoMemoria antesDoCommit = fabrica.criarUnidadeDeTrabalho();
        assertFalse(fabrica.criarRepositorioLivro(antesDoCommit).buscarPorId("l1").isPresent());
        assertFalse(fabrica.criarRepositorioMovimentacao(antesDoCommit).buscarPorId("t1").isPresent());
        assertFalse(fabrica.criarRepositorioRelacionamento(antesDoCommit).buscarReservaAtiva("l1").isPresent());
        antesDoCommit.rollback();

        unidade.commit();
        UnidadeDeTrabalhoMemoria depoisDoCommit = fabrica.criarUnidadeDeTrabalho();
        assertTrue(fabrica.criarRepositorioLivro(depoisDoCommit).buscarPorId("l1").isPresent());
        assertTrue(fabrica.criarRepositorioMovimentacao(depoisDoCommit).buscarPorId("t1").isPresent());
        assertTrue(fabrica.criarRepositorioRelacionamento(depoisDoCommit).buscarReservaAtiva("l1").isPresent());
        depoisDoCommit.rollback();
    }

    @Test
    void R08_repositorioMovimentacaoFiltraPorLivro() {
        FabricaPersistenciaMemoria fabrica = new FabricaPersistenciaMemoria();
        UnidadeDeTrabalhoMemoria unidade = fabrica.criarUnidadeDeTrabalho();
        RepositorioMovimentacao repositorio = fabrica.criarRepositorioMovimentacao(unidade);
        repositorio.salvar(emprestimo());
        repositorio.salvar(new Emprestimo("t2", "l2", "e1", LocalDate.of(2026, 1, 10)));

        assertEquals(1, repositorio.listarPorLivro("l1").size());
        unidade.rollback();
    }

    @Test
    void R08_repositorioRelacionamentoMantemUmaReservaAtivaPorLivro() {
        FabricaPersistenciaMemoria fabrica = new FabricaPersistenciaMemoria();
        UnidadeDeTrabalhoMemoria unidade = fabrica.criarUnidadeDeTrabalho();
        RepositorioRelacionamento repositorio = fabrica.criarRepositorioRelacionamento(unidade);
        repositorio.salvarReserva(new Reserva("r1", "l1", "e1", LocalDate.of(2026, 1, 10)));
        repositorio.salvarReserva(new Reserva("r2", "l1", "e2", LocalDate.of(2026, 1, 11)));

        assertEquals("r2", repositorio.buscarReservaAtiva("l1").orElseThrow().id());
        repositorio.removerReserva("l1");
        assertFalse(repositorio.buscarReservaAtiva("l1").isPresent());
        unidade.rollback();
    }

    @Test
    void R08_unidadeEncerradaRecusaUsoPosterior() {
        FabricaPersistenciaMemoria fabrica = new FabricaPersistenciaMemoria();
        UnidadeDeTrabalhoMemoria unidade = fabrica.criarUnidadeDeTrabalho();
        unidade.commit();

        assertThrows(IllegalStateException.class, unidade::rollback);
        assertThrows(IllegalStateException.class,
                () -> fabrica.criarRepositorioLivro(unidade).listar());
    }

    private Emprestimo emprestimo() {
        return new Emprestimo("t1", "l1", "e1", LocalDate.of(2026, 1, 10));
    }
}
