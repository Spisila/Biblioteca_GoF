package br.pucpr.biblioteca.financeiro.cobranca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucpr.biblioteca.comum.dinheiro.Dinheiro;
import br.pucpr.biblioteca.financeiro.cobranca.adaptador.AdaptadorBancoA;
import br.pucpr.biblioteca.financeiro.cobranca.adaptador.AdaptadorBancoB;
import br.pucpr.biblioteca.financeiro.cobranca.bancossimulados.BancoASimulado;
import br.pucpr.biblioteca.financeiro.cobranca.bancossimulados.BancoBSimulado;
import br.pucpr.biblioteca.financeiro.cobranca.contrato.Cobranca;
import br.pucpr.biblioteca.financeiro.cobranca.contrato.ServicoCobranca;
import br.pucpr.biblioteca.financeiro.cobranca.contrato.SituacaoCobranca;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class R03_AdaptadorBancoTest {
    @Test
    void R03_mesmaCobrancaConverteSeteReaisParaCadaApi() {
        BancoASimulado bancoA = new BancoASimulado();
        BancoBSimulado bancoB = new BancoBSimulado();
        Cobranca cobrancaA = new AdaptadorBancoA(bancoA);
        Cobranca cobrancaB = new AdaptadorBancoB(bancoB);
        Dinheiro valor = Dinheiro.ofReais(7, 0);

        new ServicoCobranca(cobrancaA).emitir("m1", "DOC-1", valor);
        new ServicoCobranca(cobrancaB).emitir("m1", "DOC-1", valor);

        assertEquals(700, bancoA.ultimosCentavos());
        assertEquals(new BigDecimal("7.00"), bancoB.ultimoValorReais());
        assertEquals("A-m1", bancoA.ultimoIdentificador());
        assertEquals("B/m1", bancoB.ultimaReferencia());
    }

    @Test
    void R03_notificacaoDoBancoEConvertidaEQuitacaoDuplicadaIgnorada() {
        BancoASimulado banco = new BancoASimulado();
        AdaptadorBancoA adaptador = new AdaptadorBancoA(banco);
        adaptador.emitir("m1", "DOC-1", Dinheiro.ofReais(7, 0));

        var notificacao = banco.notificarPagamento();

        assertTrue(adaptador.receberNotificacao(notificacao));
        assertFalse(adaptador.receberNotificacao(notificacao));
        assertEquals(SituacaoCobranca.PAGA, adaptador.situacao("m1"));
    }

    @Test
    void R03_servicoOperaSomenteComContratoInstitucional() {
        BancoBSimulado banco = new BancoBSimulado();
        ServicoCobranca servico = new ServicoCobranca(new AdaptadorBancoB(banco));

        servico.emitir("m2", "DOC-2", Dinheiro.ofReais(7, 0));

        assertEquals(SituacaoCobranca.EMITIDA, servico.situacao("m2"));
    }
}
