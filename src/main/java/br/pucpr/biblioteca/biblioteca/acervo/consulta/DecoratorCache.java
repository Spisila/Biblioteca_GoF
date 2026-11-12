package br.pucpr.biblioteca.biblioteca.acervo.consulta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cacheia somente metadados das consultas.
 *
 * <p>Padrão: R09 Decorator — papel: Decorator concreto.</p>
 */
public final class DecoratorCache extends DecoratorConsulta {
    private final Map<String, Optional<MetadadosLivro>> cachePorId = new ConcurrentHashMap<>();
    private volatile List<MetadadosLivro> listaCacheada;

    public DecoratorCache(ServicoConsulta servico) {
        super(servico);
    }

    @Override
    public Optional<MetadadosLivro> consultarPorId(String idLivro) {
        return cachePorId.computeIfAbsent(idLivro, super::consultarPorId);
    }

    @Override
    public List<MetadadosLivro> listar() {
        List<MetadadosLivro> lista = listaCacheada;
        if (lista == null) {
            lista = List.copyOf(super.listar());
            listaCacheada = lista;
        }
        return new ArrayList<>(lista);
    }

    public void limpar() {
        cachePorId.clear();
        listaCacheada = null;
    }
}
