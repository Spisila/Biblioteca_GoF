package br.pucpr.biblioteca.circulacao.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.biblioteca.livro.SituacaoLivro;
import br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo;
import br.pucpr.biblioteca.biblioteca.relacionamento.Reserva;
import br.pucpr.biblioteca.comum.excecoes.OperacaoRecusadaException;
import br.pucpr.biblioteca.comum.relogio.RelogioFixo;
import br.pucpr.biblioteca.persistencia.fabrica.FabricaPersistencia;
import br.pucpr.biblioteca.persistencia.memoria.FabricaPersistenciaMemoria;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

// Cenário R4: validar a fachada das operações de circulação.
class R04_FachadaCirculacaoTest {
    private static final LocalDate DATA = LocalDate.of(2026, 11, 12);

    @Test
    void R04_realizaEmprestimoCompletoAPartirDeReserva() {
        FabricaPersistencia fabrica = new FabricaPersistenciaMemoria();
        salvarReserva(fabrica);
        FachadaCirculacao fachada = new FachadaCirculacao(
                fabrica, new RelogioFixo(DATA, Instant.parse("2026-11-12T17:28:34Z")));

        Emprestimo emprestimo = fachada.realizarEmprestimo("t1", "l1", "e1");

        assertEquals("l1", emprestimo.idLivro());
        assertEquals(SituacaoLivro.EMPRESTADO, buscarLivro(fabrica).situacao());
        assertEquals(1, buscarMovimentacoes(fabrica));
        assertEquals(false, buscarReserva(fabrica));
    }

    @Test
    void R04_falhaReverteAlteracoesDaOperacao() {
        FabricaPersistencia fabrica = new FabricaPersistenciaMemoria();
        salvarReserva(fabrica);
        FachadaCirculacao fachada = new FachadaCirculacao(
                fabrica, new RelogioFixo(DATA, Instant.parse("2026-11-12T17:28:34Z")));

        assertThrows(OperacaoRecusadaException.class,
                () -> fachada.realizarEmprestimo("t1", "l1", "outro-estudante"));

        assertEquals(SituacaoLivro.RESERVADO, buscarLivro(fabrica).situacao());
        assertEquals(0, buscarMovimentacoes(fabrica));
        assertEquals(true, buscarReserva(fabrica));
    }

    private static void salvarReserva(FabricaPersistencia fabrica) {
        var unidade = fabrica.criarUnidadeDeTrabalho();
        Livro livro = new Livro("l1", "Livro");
        livro.solicitarReserva("e1");
        fabrica.criarRepositorioLivro(unidade).salvar(livro);
        fabrica.criarRepositorioRelacionamento(unidade)
                .salvarReserva(new Reserva("r1", "l1", "e1", DATA));
        unidade.commit();
        unidade.close();
    }

    private static Livro buscarLivro(FabricaPersistencia fabrica) {
        var unidade = fabrica.criarUnidadeDeTrabalho();
        Livro livro = fabrica.criarRepositorioLivro(unidade).buscarPorId("l1").orElseThrow();
        unidade.rollback();
        return livro;
    }

    private static int buscarMovimentacoes(FabricaPersistencia fabrica) {
        var unidade = fabrica.criarUnidadeDeTrabalho();
        int quantidade = fabrica.criarRepositorioMovimentacao(unidade).listarPorLivro("l1").size();
        unidade.rollback();
        return quantidade;
    }

    private static boolean buscarReserva(FabricaPersistencia fabrica) {
        var unidade = fabrica.criarUnidadeDeTrabalho();
        boolean existe = fabrica.criarRepositorioRelacionamento(unidade)
                .buscarReservaAtiva("l1").isPresent();
        unidade.rollback();
        return existe;
    }
}

