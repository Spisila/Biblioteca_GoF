package br.pucpr.biblioteca.biblioteca.acervo.consulta;

import java.util.List;
import java.util.Optional;

/**
 * Contrato para consultas de metadados do acervo.
 *
 * <p>Padrão: R09 Decorator — papel: Componente.</p>
 */
public interface ServicoConsulta {
    Optional<MetadadosLivro> consultarPorId(String idLivro);

    List<MetadadosLivro> listar();
}
