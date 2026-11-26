package br.pucpr.biblioteca.biblioteca.acervo.consulta;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Cacheia temporariamente metadados das consultas.
 *
 * <p>Padrão: R09 Decorator — papel: Decorator concreto.</p>
 */
public final class DecoratorCache extends DecoratorConsulta {
    private static final Duration DURACAO_PADRAO = Duration.ofSeconds(30);
    private final Map<String, CacheEntry<Optional<MetadadosLivro>>> cachePorId =
            new HashMap<>();
    private final long duracaoCacheNanos;
    private CacheEntry<List<MetadadosLivro>> listaCacheada;

    public DecoratorCache(ServicoConsulta servico) {
        this(servico, DURACAO_PADRAO);
    }

    public DecoratorCache(ServicoConsulta servico, Duration duracaoCache) {
        super(servico);
        Objects.requireNonNull(duracaoCache, "duracaoCache nao pode ser nula");
        if (duracaoCache.isZero() || duracaoCache.isNegative()) {
            throw new IllegalArgumentException("duracaoCache deve ser positiva");
        }
        this.duracaoCacheNanos = duracaoCache.toNanos();
    }

    @Override
    public synchronized Optional<MetadadosLivro> consultarPorId(String idLivro) {
        Objects.requireNonNull(idLivro, "idLivro nao pode ser nulo");
        CacheEntry<Optional<MetadadosLivro>> entrada = cachePorId.get(idLivro);
        if (entrada != null && !expirada(entrada)) {
            System.out.println("[CACHE] HIT - Resultado recuperado do cache.");
            return entrada.valor();
        }

        registrarAusencia(entrada);
        Optional<MetadadosLivro> resultado = Objects.requireNonNull(
                super.consultarPorId(idLivro), "A consulta nao pode retornar nulo");
        if (resultado.isPresent()) {
            cachePorId.put(idLivro, novaEntrada(resultado));
        } else {
            cachePorId.remove(idLivro);
        }
        return resultado;
    }

    @Override
    public synchronized List<MetadadosLivro> listar() {
        if (listaCacheada != null && !expirada(listaCacheada)) {
            System.out.println("[CACHE] HIT - Resultado recuperado do cache.");
            return new ArrayList<>(listaCacheada.valor());
        }

        registrarAusencia(listaCacheada);
        List<MetadadosLivro> resultado = List.copyOf(Objects.requireNonNull(
                super.listar(), "A consulta nao pode retornar nulo"));
        listaCacheada = novaEntrada(resultado);
        return new ArrayList<>(resultado);
    }

    public synchronized void limpar() {
        cachePorId.clear();
        listaCacheada = null;
    }

    private boolean expirada(CacheEntry<?> entrada) {
        return System.nanoTime() - entrada.instanteCriacaoNanos() >= duracaoCacheNanos;
    }

    private void registrarAusencia(CacheEntry<?> entrada) {
        if (entrada == null) {
            System.out.println("[CACHE] MISS - Consulta nao encontrada no cache.");
        } else {
            System.out.println("[CACHE] EXPIRED - Resultado expirado, realizando nova consulta.");
        }
    }

    private <T> CacheEntry<T> novaEntrada(T valor) {
        return new CacheEntry<>(valor, System.nanoTime());
    }

    private record CacheEntry<T>(T valor, long instanteCriacaoNanos) {
    }
}
