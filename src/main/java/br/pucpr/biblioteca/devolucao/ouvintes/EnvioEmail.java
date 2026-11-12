package br.pucpr.biblioteca.devolucao.ouvintes;

import br.pucpr.biblioteca.devolucao.evento.EventoDevolucao;

@FunctionalInterface
public interface EnvioEmail {
    void enviar(EventoDevolucao evento);
}
