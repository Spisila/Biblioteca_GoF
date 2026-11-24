package br.pucpr.biblioteca.app;

import br.pucpr.biblioteca.persistencia.fabrica.FabricaPersistencia;
import br.pucpr.biblioteca.persistencia.memoria.FabricaPersistenciaMemoria;
import br.pucpr.biblioteca.persistencia.relacional.FabricaPersistenciaRelacional;
import java.util.Objects;
import br.pucpr.biblioteca.financeiro.multa.PoliticaMulta;
import br.pucpr.biblioteca.financeiro.multa.PoliticaOriginal;
import br.pucpr.biblioteca.financeiro.multa.PoliticaComTeto;
import br.pucpr.biblioteca.financeiro.multa.PoliticaComTolerancia;
import br.pucpr.biblioteca.financeiro.cobranca.contrato.Cobranca;
import br.pucpr.biblioteca.financeiro.cobranca.adaptador.AdaptadorBancoA;
import br.pucpr.biblioteca.financeiro.cobranca.adaptador.AdaptadorBancoB;
import br.pucpr.biblioteca.financeiro.cobranca.bancossimulados.BancoASimulado;
import br.pucpr.biblioteca.financeiro.cobranca.bancossimulados.BancoBSimulado;
import br.pucpr.biblioteca.biblioteca.acervo.consulta.ServicoConsulta;
import br.pucpr.biblioteca.biblioteca.acervo.consulta.ServicoConsultaOriginal;
import br.pucpr.biblioteca.biblioteca.acervo.consulta.DecoratorCache;
import br.pucpr.biblioteca.biblioteca.acervo.consulta.DecoratorMedicao;

public final class Configuracao {
    private Configuracao() {
    }

    public static FabricaPersistencia fabricaPara(PerfilExecucao perfil) {
        switch (Objects.requireNonNull(perfil, "perfil nao pode ser nulo")) {
            case TESTE:
                return new FabricaPersistenciaMemoria();
            case PRODUCAO:
                return new FabricaPersistenciaRelacional();
            default:
                throw new IllegalStateException("Perfil de execução não suportado");
        }
    }

    public static PoliticaMulta politicaMulta() {
        String nome = propriedade(
                "biblioteca.multa.politica", "BIBLIOTECA_MULTA_POLITICA", "original").trim();
        return switch (nome.toLowerCase()) {
            case "teto" -> new PoliticaComTeto();
            case "tolerancia" -> new PoliticaComTolerancia();
            default -> new PoliticaOriginal();
        };
    }

    public static Cobranca cobranca() {
        String nome = propriedade(
                "biblioteca.cobranca.banco", "BIBLIOTECA_COBRANCA_BANCO", "A").trim();
        return "B".equalsIgnoreCase(nome)
                ? new AdaptadorBancoB(new BancoBSimulado())
                : new AdaptadorBancoA(new BancoASimulado());
    }

    public static ServicoConsulta consulta(ServicoConsultaOriginal original) {
        ServicoConsulta consulta = original;
        if (Boolean.parseBoolean(propriedade(
                "biblioteca.consulta.cache", "BIBLIOTECA_CONSULTA_CACHE", "false"))) {
            consulta = new DecoratorCache(consulta);
        }
        if (Boolean.parseBoolean(propriedade(
                "biblioteca.consulta.medicao", "BIBLIOTECA_CONSULTA_MEDICAO", "false"))) {
            consulta = new DecoratorMedicao(consulta);
        }
        return consulta;
    }

    private static String propriedade(String chave, String ambiente, String padrao) {
        String valor = System.getProperty(chave);
        if (valor != null && !valor.isBlank()) {
            return valor;
        }
        valor = System.getenv(ambiente);
        return valor == null || valor.isBlank() ? padrao : valor;
    }
}
