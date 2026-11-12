package br.pucpr.biblioteca.biblioteca.acervo.consulta;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.pucpr.biblioteca.biblioteca.livro.SituacaoLivro;
import br.pucpr.biblioteca.circulacao.facade.FachadaCirculacao;
import br.pucpr.biblioteca.comum.relogio.RelogioFixo;
import br.pucpr.biblioteca.persistencia.fabrica.FabricaPersistencia;
import br.pucpr.biblioteca.persistencia.memoria.FabricaPersistenciaMemoria;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

// Cenário R9: validar extensões decoradoras de consultas ao acervo.
class R09_DecoratorConsultaTest {
    private static final LocalDate DATA = LocalDate.of(2026, 11, 12);

    @Test
    void R09_combinaCacheEMedicaoEConsultaCriticaVeEstadoReal() {
        FabricaPersistencia fabrica = new FabricaPersistenciaMemoria();
        FachadaCirculacao fachada = new FachadaCirculacao(
                fabrica, new RelogioFixo(DATA, Instant.parse("2026-11-12T17:34:00Z")));
        var unidade = fabrica.criarUnidadeDeTrabalho();
        fabrica.criarRepositorioLivro(unidade).salvar(
                new br.pucpr.biblioteca.biblioteca.livro.Livro("l1", "Livro"));
        unidade.commit();
        unidade.close();

        var unidadeConsulta = fabrica.criarUnidadeDeTrabalho();
        DecoratorMedicao medicao = new DecoratorMedicao(
                new DecoratorCache(new ServicoConsultaOriginal(
                        fabrica.criarRepositorioLivro(unidadeConsulta))));
        assertEquals(SituacaoLivro.LIVRE, medicao.consultarPorId("l1").orElseThrow().situacao());

        fachada.solicitarReserva("r1", "l1", "e1");
        fachada.realizarEmprestimo("t1", "l1", "e1");

        assertEquals(SituacaoLivro.LIVRE, medicao.consultarPorId("l1").orElseThrow().situacao());
        assertEquals(SituacaoLivro.EMPRESTADO, buscarEstadoAtual(fabrica));
        assertEquals(2, medicao.quantidadeConsultas());
        unidadeConsulta.rollback();
    }

    private static SituacaoLivro buscarEstadoAtual(FabricaPersistencia fabrica) {
        var unidade = fabrica.criarUnidadeDeTrabalho();
        SituacaoLivro situacao = fabrica.criarRepositorioLivro(unidade)
                .buscarPorId("l1").orElseThrow().situacao();
        unidade.rollback();
        return situacao;
    }
}

