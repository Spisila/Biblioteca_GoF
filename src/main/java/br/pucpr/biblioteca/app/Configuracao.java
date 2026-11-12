package br.pucpr.biblioteca.app;

import br.pucpr.biblioteca.persistencia.fabrica.FabricaPersistencia;
import br.pucpr.biblioteca.persistencia.memoria.FabricaPersistenciaMemoria;
import br.pucpr.biblioteca.persistencia.relacional.FabricaPersistenciaRelacional;
import java.util.Objects;

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
}
