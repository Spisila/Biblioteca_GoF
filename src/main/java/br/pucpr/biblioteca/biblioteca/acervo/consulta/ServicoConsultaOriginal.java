package br.pucpr.biblioteca.biblioteca.acervo.consulta;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioLivro;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementação original da consulta, sempre lendo o repositório.
 *
 * <p>Padrão: R09 Decorator — papel: Componente concreto.</p>
 */
public final class ServicoConsultaOriginal implements ServicoConsulta {
    private final RepositorioLivro repositorioLivro;

    public ServicoConsultaOriginal(RepositorioLivro repositorioLivro) {
        this.repositorioLivro = Objects.requireNonNull(
                repositorioLivro, "repositorioLivro nao pode ser nulo");
    }

    @Override
    public Optional<MetadadosLivro> consultarPorId(String idLivro) {
        return repositorioLivro.buscarPorId(idLivro).map(ServicoConsultaOriginal::metadados);
    }

    @Override
    public List<MetadadosLivro> listar() {
        return repositorioLivro.listar().stream()
                .map(ServicoConsultaOriginal::metadados)
                .toList();
    }

    private static MetadadosLivro metadados(Livro livro) {
        return new MetadadosLivro(livro.id(), livro.titulo(), livro.situacao());
    }
}
