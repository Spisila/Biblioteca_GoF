package br.pucpr.biblioteca.biblioteca.livro.estado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.biblioteca.livro.ResultadoInspecao;
import br.pucpr.biblioteca.biblioteca.livro.SituacaoLivro;
import br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo;
import br.pucpr.biblioteca.comum.excecoes.OperacaoRecusadaException;
import br.pucpr.biblioteca.comum.relogio.RelogioFixo;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class R01_CicloDeVidaLivroTest {
    private final RelogioFixo relogio = new RelogioFixo(
            LocalDate.of(2026, 1, 10),
            Instant.parse("2026-01-10T12:00:00Z"));

    @Test
    void R01_terceiraRenovacaoRecusada() {
        Livro livro = livroEmprestado();
        Emprestimo emprestimo = emprestimo();

        livro.renovarEmprestimo(emprestimo);
        livro.renovarEmprestimo(emprestimo);
        LocalDate vencimentoAntes = emprestimo.dataVencimento();
        int renovacoesAntes = emprestimo.numRenovacoes();

        assertThrows(OperacaoRecusadaException.class, () -> livro.renovarEmprestimo(emprestimo));
        assertEquals(vencimentoAntes, emprestimo.dataVencimento());
        assertEquals(renovacoesAntes, emprestimo.numRenovacoes());
        assertEquals(SituacaoLivro.RENOVADO, livro.situacao());
    }

    @Test
    void R01_devolvidoNaoPodeSerReservado() {
        Livro livro = livroDevolvido();

        assertThrows(OperacaoRecusadaException.class, () -> livro.solicitarReserva("e1"));
        assertEquals(SituacaoLivro.DEVOLVIDO, livro.situacao());
    }

    @Test
    void R01_fluxoCompletoLivreAteLivre() {
        Livro livro = new Livro("l1", "Livro");
        Emprestimo emprestimo = emprestimo();

        livro.solicitarReserva("e1");
        assertEquals(SituacaoLivro.RESERVADO, livro.situacao());
        livro.realizarEmprestimo(emprestimo);
        assertEquals(SituacaoLivro.EMPRESTADO, livro.situacao());
        livro.renovarEmprestimo(emprestimo);
        assertEquals(SituacaoLivro.RENOVADO, livro.situacao());
        livro.registrarDevolucao(emprestimo, relogio.hoje().plusDays(1));
        assertEquals(SituacaoLivro.DEVOLVIDO, livro.situacao());
        livro.inspecionarLivro(ResultadoInspecao.BOM_ESTADO);
        assertEquals(SituacaoLivro.LIVRE, livro.situacao());
        assertNull(livro.dataDevolucao());
    }

    @Test
    void R01_primeiraRenovacaoSomaTrintaDias() {
        Livro livro = livroEmprestado();
        Emprestimo emprestimo = emprestimo();
        LocalDate vencimentoInicial = emprestimo.dataVencimento();

        livro.renovarEmprestimo(emprestimo);

        assertEquals(vencimentoInicial.plusDays(30), emprestimo.dataVencimento());
        assertEquals(1, emprestimo.numRenovacoes());
    }

    @Test
    void R01_segundaRenovacaoPermitida() {
        Livro livro = livroEmprestado();
        Emprestimo emprestimo = emprestimo();

        livro.renovarEmprestimo(emprestimo);
        livro.renovarEmprestimo(emprestimo);

        assertEquals(2, emprestimo.numRenovacoes());
        assertEquals(SituacaoLivro.RENOVADO, livro.situacao());
    }

    @Test
    void R01_emprestimoDeOutroEstudanteRecusado() {
        Livro livro = new Livro("l1", "Livro");
        livro.solicitarReserva("e1");

        Emprestimo emprestimo = new Emprestimo("t1", "l1", "e2", relogio.hoje());

        assertThrows(OperacaoRecusadaException.class, () -> livro.realizarEmprestimo(emprestimo));
        assertEquals(SituacaoLivro.RESERVADO, livro.situacao());
    }

    @Test
    void R01_devolucaoNoPrazoSemAtraso() {
        Emprestimo emprestimo = emprestimo();

        assertEquals(0, emprestimo.diasDeAtraso(emprestimo.dataVencimento()));
    }

    @Test
    void R01_devolucaoAtrasadaCalculaDias() {
        Emprestimo emprestimo = emprestimo();

        assertEquals(4, emprestimo.diasDeAtraso(emprestimo.dataVencimento().plusDays(4)));
    }

    @Test
    void R01_inspecaoBomEstado() {
        Livro livro = livroDevolvido();

        livro.inspecionarLivro(ResultadoInspecao.BOM_ESTADO);

        assertEquals(SituacaoLivro.LIVRE, livro.situacao());
        assertNull(livro.dataDevolucao());
    }

    @Test
    void R01_inspecaoReparavelEnviaParaReparo() {
        Livro livro = livroDevolvido();

        livro.inspecionarLivro(ResultadoInspecao.DANIFICADO_REPARAVEL);

        assertEquals(SituacaoLivro.REPARANDO, livro.situacao());
    }

    @Test
    void R01_inspecaoIrreparavelIncinera() {
        Livro livro = livroDevolvido();

        livro.inspecionarLivro(ResultadoInspecao.IRREPARAVEL);

        assertEquals(SituacaoLivro.INCINERADO, livro.situacao());
    }

    @Test
    void R01_retornoDoReparoLiberaLivro() {
        Livro livro = Livro.reconstituir("l1", "Livro", SituacaoLivro.REPARANDO, null, null);

        livro.retornarDoReparo();

        assertEquals(SituacaoLivro.LIVRE, livro.situacao());
    }

    @Test
    void R01_laudoIrrecuperavelIncinera() {
        Livro livro = Livro.reconstituir("l1", "Livro", SituacaoLivro.REPARANDO, null, null);

        livro.registrarLaudoIrrecuperavel();

        assertEquals(SituacaoLivro.INCINERADO, livro.situacao());
    }

    @Test
    void R01_incineradoRecusaTodosOsEventos() {
        Livro livro = Livro.reconstituir("l1", "Livro", SituacaoLivro.INCINERADO, null, null);
        Emprestimo emprestimo = emprestimo();

        assertThrows(OperacaoRecusadaException.class, () -> livro.solicitarReserva("e1"));
        assertThrows(OperacaoRecusadaException.class, () -> livro.realizarEmprestimo(emprestimo));
        assertThrows(OperacaoRecusadaException.class, () -> livro.renovarEmprestimo(emprestimo));
        assertThrows(OperacaoRecusadaException.class,
                () -> livro.registrarDevolucao(emprestimo, relogio.hoje()));
        assertThrows(OperacaoRecusadaException.class,
                () -> livro.inspecionarLivro(ResultadoInspecao.BOM_ESTADO));
        assertThrows(OperacaoRecusadaException.class, () -> livro.enviarParaReparo(true));
        assertThrows(OperacaoRecusadaException.class, livro::retornarDoReparo);
        assertThrows(OperacaoRecusadaException.class, livro::registrarLaudoIrrecuperavel);
        assertThrows(OperacaoRecusadaException.class, livro::excluirLivro);
        assertEquals(SituacaoLivro.INCINERADO, livro.situacao());
    }

    @Test
    void R01_eventoInvalidoNaoAlteraLivro() {
        Livro livre = new Livro("l1", "Livro");
        Livro reservado = Livro.reconstituir("l1", "Livro", SituacaoLivro.RESERVADO, "e1", null);
        Livro emprestado = Livro.reconstituir("l1", "Livro", SituacaoLivro.EMPRESTADO, null, null);
        Livro renovado = Livro.reconstituir("l1", "Livro", SituacaoLivro.RENOVADO, null, null);
        Livro devolvido = livroDevolvido();
        Livro reparando = Livro.reconstituir("l1", "Livro", SituacaoLivro.REPARANDO, null, null);
        Livro incinerado = Livro.reconstituir("l1", "Livro", SituacaoLivro.INCINERADO, null, null);
        Emprestimo emprestimo = emprestimo();

        assertEventoRecusadoSemAlterar(livre, () -> livre.registrarDevolucao(emprestimo, relogio.hoje()));
        assertEventoRecusadoSemAlterar(reservado, () -> reservado.renovarEmprestimo(emprestimo));
        assertEventoRecusadoSemAlterar(emprestado, () -> emprestado.solicitarReserva("e1"));
        assertEventoRecusadoSemAlterar(renovado, () -> renovado.enviarParaReparo(true));
        assertEventoRecusadoSemAlterar(devolvido, () -> devolvido.solicitarReserva("e1"));
        assertEventoRecusadoSemAlterar(reparando, () -> reparando.realizarEmprestimo(emprestimo));
        assertEventoRecusadoSemAlterar(incinerado, incinerado::excluirLivro);
    }

    @Test
    void R01_estadoReconstituidoAPartirDaSituacao() {
        Livro livre = Livro.reconstituir("l1", "Livro", SituacaoLivro.LIVRE, null, null);
        Livro reservado = Livro.reconstituir("l1", "Livro", SituacaoLivro.RESERVADO, "e1", null);
        Livro emprestado = Livro.reconstituir("l1", "Livro", SituacaoLivro.EMPRESTADO, null, null);
        Livro renovado = Livro.reconstituir("l1", "Livro", SituacaoLivro.RENOVADO, null, null);
        Livro devolvido = Livro.reconstituir("l1", "Livro", SituacaoLivro.DEVOLVIDO, null, relogio.hoje());
        Livro reparando = Livro.reconstituir("l1", "Livro", SituacaoLivro.REPARANDO, null, null);
        Emprestimo emprestimo = emprestimo();

        livre.solicitarReserva("e1");
        reservado.realizarEmprestimo(emprestimo);
        emprestado.renovarEmprestimo(emprestimo);
        renovado.renovarEmprestimo(emprestimo);
        devolvido.inspecionarLivro(ResultadoInspecao.BOM_ESTADO);
        reparando.retornarDoReparo();

        assertEquals(SituacaoLivro.RESERVADO, livre.situacao());
        assertEquals(SituacaoLivro.EMPRESTADO, reservado.situacao());
        assertEquals(SituacaoLivro.RENOVADO, emprestado.situacao());
        assertEquals(SituacaoLivro.RENOVADO, renovado.situacao());
        assertEquals(SituacaoLivro.LIVRE, devolvido.situacao());
        assertEquals(SituacaoLivro.LIVRE, reparando.situacao());
    }

    @Test
    void R01_livreComDanoNaoIdentificadoRecusaReparo() {
        Livro livro = new Livro("l1", "Livro");

        assertThrows(OperacaoRecusadaException.class, () -> livro.enviarParaReparo(false));
        assertEquals(SituacaoLivro.LIVRE, livro.situacao());
    }

    private Emprestimo emprestimo() {
        return new Emprestimo("t1", "l1", "e1", relogio.hoje());
    }

    private Livro livroEmprestado() {
        Livro livro = new Livro("l1", "Livro");
        livro.solicitarReserva("e1");
        livro.realizarEmprestimo(emprestimo());
        return livro;
    }

    private Livro livroDevolvido() {
        Livro livro = livroEmprestado();
        Emprestimo emprestimo = emprestimo();
        livro.registrarDevolucao(emprestimo, relogio.hoje());
        return livro;
    }

    private void assertEventoRecusadoSemAlterar(Livro livro, Runnable evento) {
        SituacaoLivro situacaoAntes = livro.situacao();
        assertThrows(OperacaoRecusadaException.class, evento::run);
        assertEquals(situacaoAntes, livro.situacao());
    }
}
