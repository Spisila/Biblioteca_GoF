package br.pucpr.biblioteca.devolucao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.biblioteca.livro.ResultadoInspecao;
import br.pucpr.biblioteca.biblioteca.livro.SituacaoLivro;
import br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo;
import br.pucpr.biblioteca.devolucao.evento.EventoDevolucao;
import br.pucpr.biblioteca.devolucao.ouvintes.OuvinteEmail;
import br.pucpr.biblioteca.devolucao.ouvintes.OuvinteIndicadores;
import br.pucpr.biblioteca.devolucao.ouvintes.OuvinteInspecaoExemplar;
import br.pucpr.biblioteca.devolucao.ouvintes.OuvinteMulta;
import br.pucpr.biblioteca.devolucao.publicador.PublicadorDevolucao;
import br.pucpr.biblioteca.financeiro.multa.EmissorMulta;
import br.pucpr.biblioteca.financeiro.cobranca.adaptador.AdaptadorBancoA;
import br.pucpr.biblioteca.financeiro.cobranca.bancossimulados.BancoASimulado;
import br.pucpr.biblioteca.financeiro.cobranca.contrato.ServicoCobranca;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class R05_ObserverDevolucaoTest {
    @Test
    void R05_devolucaoAtrasadaGeraMultaInspecaoEIndicadores() {
        Livro livro = livroEmprestado();
        Emprestimo emprestimo = emprestimo();
        PublicadorDevolucao publicador = new PublicadorDevolucao();
        OuvinteInspecaoExemplar inspecao = new OuvinteInspecaoExemplar();
        OuvinteMulta multa = new OuvinteMulta(new EmissorMulta());
        OuvinteIndicadores indicadores = new OuvinteIndicadores();
        publicador.adicionar(inspecao);
        publicador.adicionar(multa);
        publicador.adicionar(indicadores);

        EventoDevolucao evento = publicador.confirmarDevolucao(
                livro,
                emprestimo,
                emprestimo.dataVencimento().plusDays(5));

        assertEquals(SituacaoLivro.DEVOLVIDO, livro.situacao());
        assertEquals(5, evento.diasDeAtraso());
        assertEquals(1, multa.multas().size());
        assertEquals(500, multa.multas().get(0).valor().centavos());
        assertEquals(1, inspecao.pendentes().size());
        assertEquals(1, indicadores.totalDevolucoes());
        assertEquals(1, indicadores.totalDevolucoesAtrasadas());

        inspecao.inspecionar(livro, ResultadoInspecao.BOM_ESTADO);
        assertEquals(SituacaoLivro.LIVRE, livro.situacao());
    }

    @Test
    void R05_devolucaoNoPrazoNaoGeraMulta() {
        Livro livro = livroEmprestado();
        Emprestimo emprestimo = emprestimo();
        PublicadorDevolucao publicador = new PublicadorDevolucao();
        OuvinteMulta multa = new OuvinteMulta(new EmissorMulta());
        OuvinteIndicadores indicadores = new OuvinteIndicadores();
        publicador.adicionar(multa);
        publicador.adicionar(indicadores);

        publicador.confirmarDevolucao(livro, emprestimo, emprestimo.dataVencimento());

        assertTrue(multa.multas().isEmpty());
        assertEquals(1, indicadores.totalDevolucoes());
        assertEquals(0, indicadores.totalDevolucoesAtrasadas());
        assertEquals(SituacaoLivro.DEVOLVIDO, livro.situacao());
    }

    @Test
    void R05_falhaDeOuvinteNaoImpedeOsDemaisNemDesfazDevolucao() {
        Livro livro = livroEmprestado();
        Emprestimo emprestimo = emprestimo();
        PublicadorDevolucao publicador = new PublicadorDevolucao();
        OuvinteIndicadores indicadores = new OuvinteIndicadores();
        publicador.adicionar(new OuvinteEmail(evento -> {
            throw new IllegalStateException("e-mail indisponível");
        }));
        publicador.adicionar(indicadores);

        publicador.confirmarDevolucao(livro, emprestimo, emprestimo.dataVencimento());

        assertEquals(SituacaoLivro.DEVOLVIDO, livro.situacao());
        assertEquals(1, indicadores.totalDevolucoes());
        assertFalse(publicador.falhasDosOuvintes().isEmpty());
    }

    @Test
    void R05_multaAtrasadaEmiteCobrancaPeloAdapter() {
        Livro livro = livroEmprestado();
        Emprestimo emprestimo = emprestimo();
        BancoASimulado banco = new BancoASimulado();
        OuvinteMulta multa = new OuvinteMulta(
                new EmissorMulta(),
                new ServicoCobranca(new AdaptadorBancoA(banco)));
        PublicadorDevolucao publicador = new PublicadorDevolucao();
        publicador.adicionar(multa);

        publicador.confirmarDevolucao(
                livro,
                emprestimo,
                emprestimo.dataVencimento().plusDays(5));

        assertEquals(1, multa.multas().size());
        assertEquals(1, multa.cobrancas().size());
        assertEquals(500, banco.ultimosCentavos());
        assertEquals("e1", banco.ultimoDocumento());
        assertEquals("A-multa-l1-2026-02-14", banco.ultimoIdentificador());
    }

    private Livro livroEmprestado() {
        Livro livro = new Livro("l1", "Livro");
        livro.solicitarReserva("e1");
        livro.realizarEmprestimo(emprestimo());
        return livro;
    }

    private Emprestimo emprestimo() {
        return new Emprestimo("t1", "l1", "e1", LocalDate.of(2026, 1, 10));
    }
}
