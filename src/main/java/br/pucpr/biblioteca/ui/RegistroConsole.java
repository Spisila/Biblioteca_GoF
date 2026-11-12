package br.pucpr.biblioteca.ui;

import br.pucpr.biblioteca.cadastro.RegistroCadastravel;

/**
 * Dados de entrada da CLI para os cadastros ainda sem entidade de domínio.
 */
public record RegistroConsole(String id, String descricao) implements RegistroCadastravel {
}
