package br.pucpr.biblioteca.biblioteca.acervo.composite;

import java.util.List;

/**
 * Operações uniformes para folhas e grupos do acervo.
 *
 * <p>Padrão: R10 Composite — papel: Componente.</p>
 */
public interface ElementoAcervo {
    String id();

    int contarElementos();

    List<String> listarIdsLivros();
}
