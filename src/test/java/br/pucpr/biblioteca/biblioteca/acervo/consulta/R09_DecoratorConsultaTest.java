package br.pucpr.biblioteca.biblioteca.acervo.consulta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucpr.biblioteca.biblioteca.livro.SituacaoLivro;
import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.app.Configuracao;
import br.pucpr.biblioteca.circulacao.facade.FachadaCirculacao;
import br.pucpr.biblioteca.comum.relogio.RelogioFixo;
import br.pucpr.biblioteca.persistencia.fabrica.FabricaPersistencia;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioLivro;
import br.pucpr.biblioteca.persistencia.memoria.FabricaPersistenciaMemoria;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

// Cenário R9: validar extensões decoradoras de consultas ao acervo.
class R09_DecoratorConsultaTest {
    private static final LocalDate DATA = LocalDate.of(2026, 11, 12);
    private static final MetadadosLivro LIVRO = new MetadadosLivro(
            "l1", "Livro", SituacaoLivro.LIVRE);

    @Test
    void R09_medicaoPreservaResultadoEApresentaDuracaoReal() {
        AtomicInteger chamadas = new AtomicInteger();
        Optional<MetadadosLivro> resultadoEsperado = Optional.of(LIVRO);
        ServicoConsulta original = consulta(
                id -> {
                    chamadas.incrementAndGet();
                    return resultadoEsperado;
                },
                List::of);
        DecoratorMedicao medicao = new DecoratorMedicao(original);

        String saida = capturarSaida(() ->
                assertSame(resultadoEsperado, medicao.consultarPorId("l1")));

        assertEquals(1, chamadas.get());
        assertTrue(saida.matches(
                "(?s).*\\[MEDICAO\\] Consulta executada em \\d+\\.\\d{2} ms\\R"));
        assertEquals(1, medicao.quantidadeConsultas());
        assertTrue(medicao.totalNanos() >= 0);
    }

    @Test
    void R09_medicaoEhFinalizadaQuandoConsultaFalha() {
        RuntimeException falha = new IllegalStateException("falha de consulta");
        ServicoConsulta original = consulta(id -> {
            throw falha;
        }, List::of);
        DecoratorMedicao medicao = new DecoratorMedicao(original);

        String saida = capturarSaida(() ->
                assertSame(falha, assertThrows(
                        RuntimeException.class, () -> medicao.consultarPorId("l1"))));

        assertTrue(saida.contains("[MEDICAO] Consulta executada em "));
        assertEquals(1, medicao.quantidadeConsultas());
    }

    @Test
    void R09_cacheReutilizaResultadosEUsaChavePorId() {
        AtomicInteger chamadas = new AtomicInteger();
        ServicoConsulta original = consulta(id -> {
            chamadas.incrementAndGet();
            return Optional.of(new MetadadosLivro(id, "Livro " + id, SituacaoLivro.LIVRE));
        }, List::of);
        DecoratorCache cache = new DecoratorCache(original);

        String saida = capturarSaida(() -> {
            assertTrue(cache.consultarPorId("l1").isPresent());
            assertTrue(cache.consultarPorId("l1").isPresent());
            assertTrue(cache.consultarPorId("l2").isPresent());
        });

        assertEquals(2, chamadas.get());
        assertTrue(saida.contains("[CACHE] MISS"));
        assertTrue(saida.contains("[CACHE] HIT"));
        assertEquals(2, ocorrencias(saida, "[CACHE] MISS"));
    }

    @Test
    void R09_cacheNaoArmazenaResultadosVaziosOuFalhas() {
        AtomicInteger chamadasVazias = new AtomicInteger();
        ServicoConsulta consultaVazia = consulta(id -> {
            if (chamadasVazias.incrementAndGet() == 1) {
                return Optional.empty();
            }
            return Optional.of(LIVRO);
        }, List::of);
        DecoratorCache cacheVazio = new DecoratorCache(consultaVazia);

        capturarSaida(() -> {
            assertFalse(cacheVazio.consultarPorId("l1").isPresent());
            assertTrue(cacheVazio.consultarPorId("l1").isPresent());
        });
        assertEquals(2, chamadasVazias.get());

        AtomicInteger chamadasComFalha = new AtomicInteger();
        ServicoConsulta consultaComFalha = consulta(id -> {
            if (chamadasComFalha.incrementAndGet() == 1) {
                throw new IllegalStateException("falha");
            }
            return Optional.of(LIVRO);
        }, List::of);
        DecoratorCache cacheComFalha = new DecoratorCache(consultaComFalha);

        capturarSaida(() -> {
            assertThrows(IllegalStateException.class, () -> cacheComFalha.consultarPorId("l1"));
            assertTrue(cacheComFalha.consultarPorId("l1").isPresent());
        });
        assertEquals(2, chamadasComFalha.get());
    }

    @Test
    void R09_cacheExpiraEDevolveCopiasDefensivasDaLista() throws InterruptedException {
        AtomicInteger chamadasLista = new AtomicInteger();
        ServicoConsulta original = consulta(
                id -> Optional.of(LIVRO),
                () -> {
                    chamadasLista.incrementAndGet();
                    return List.of(LIVRO);
                });
        DecoratorCache cache = new DecoratorCache(original, Duration.ofMillis(1));

        String saida = capturarSaida(() -> {
            List<MetadadosLivro> primeira = cache.listar();
            primeira.clear();
            assertEquals(1, cache.listar().size());
        });
        Thread.sleep(5);
        saida += capturarSaida(cache::listar);

        assertEquals(2, chamadasLista.get());
        assertTrue(saida.contains("[CACHE] HIT"));
        assertTrue(saida.contains("[CACHE] EXPIRED"));
    }

    @Test
    void R09_medicaoExternaIncluiCacheEHitNaoConsultaNovamente() {
        AtomicInteger chamadas = new AtomicInteger();
        ServicoConsulta original = consulta(id -> {
            chamadas.incrementAndGet();
            return Optional.of(LIVRO);
        }, List::of);
        DecoratorMedicao medicao = new DecoratorMedicao(new DecoratorCache(original));

        String saida = capturarSaida(() -> {
            assertTrue(medicao.consultarPorId("l1").isPresent());
            assertTrue(medicao.consultarPorId("l1").isPresent());
        });

        assertEquals(1, chamadas.get());
        assertEquals(2, medicao.quantidadeConsultas());
        assertEquals(2, ocorrencias(saida, "[MEDICAO] Consulta executada em "));
        assertTrue(saida.contains("[CACHE] HIT"));
    }

    @Test
    void R09_composicaoConfiguradaUsaCacheMedicaoEInvalidacao() {
        String cacheAnterior = System.getProperty("biblioteca.consulta.cache");
        String medicaoAnterior = System.getProperty("biblioteca.consulta.medicao");
        System.setProperty("biblioteca.consulta.cache", "true");
        System.setProperty("biblioteca.consulta.medicao", "true");
        AtomicInteger chamadas = new AtomicInteger();
        ServicoConsulta original = consulta(id -> {
            chamadas.incrementAndGet();
            return Optional.of(LIVRO);
        }, List::of);
        Configuracao.ConsultaConfigurada configurada;
        try {
            configurada = Configuracao.consultaConfigurada(original);
        } finally {
            restaurarPropriedade("biblioteca.consulta.cache", cacheAnterior);
            restaurarPropriedade("biblioteca.consulta.medicao", medicaoAnterior);
        }

        String saida = capturarSaida(() -> {
            assertTrue(configurada.servico().consultarPorId("l1").isPresent());
            assertTrue(configurada.servico().consultarPorId("l1").isPresent());
            configurada.invalidarCache().run();
            assertTrue(configurada.servico().consultarPorId("l1").isPresent());
        });

        assertEquals(2, chamadas.get());
        assertEquals(2, ocorrencias(saida, "[CACHE] MISS"));
        assertEquals(1, ocorrencias(saida, "[CACHE] HIT"));
        assertEquals(3, ocorrencias(saida, "[MEDICAO] Consulta executada em "));
    }

    @Test
    void R09_invalidacaoAposMovimentacaoMantemConsultaAtualizada() {
        FabricaPersistencia fabrica = new FabricaPersistenciaMemoria();
        var unidade = fabrica.criarUnidadeDeTrabalho();
        fabrica.criarRepositorioLivro(unidade).salvar(
                new br.pucpr.biblioteca.biblioteca.livro.Livro("l1", "Livro"));
        unidade.commit();
        unidade.close();

        DecoratorCache cache = new DecoratorCache(new ServicoConsultaOriginal(
                repositorioPorOperacao(fabrica)));
        DecoratorMedicao medicao = new DecoratorMedicao(cache);
        FachadaCirculacao fachada = new FachadaCirculacao(
                fabrica,
                new RelogioFixo(DATA, Instant.parse("2026-11-12T17:34:00Z")),
                null,
                cache::limpar);

        assertEquals(SituacaoLivro.LIVRE, medicao.consultarPorId("l1").orElseThrow().situacao());
        assertEquals(SituacaoLivro.LIVRE, medicao.consultarPorId("l1").orElseThrow().situacao());

        fachada.solicitarReserva("r1", "l1", "e1");
        fachada.realizarEmprestimo("t1", "l1", "e1");

        assertEquals(SituacaoLivro.EMPRESTADO, medicao.consultarPorId("l1").orElseThrow().situacao());
        assertEquals(SituacaoLivro.EMPRESTADO, buscarEstadoAtual(fabrica));
        assertEquals(3, medicao.quantidadeConsultas());
    }

    private static SituacaoLivro buscarEstadoAtual(FabricaPersistencia fabrica) {
        var unidade = fabrica.criarUnidadeDeTrabalho();
        SituacaoLivro situacao = fabrica.criarRepositorioLivro(unidade)
                .buscarPorId("l1").orElseThrow().situacao();
        unidade.rollback();
        return situacao;
    }

    private static RepositorioLivro repositorioPorOperacao(FabricaPersistencia fabrica) {
        return new RepositorioLivro() {
            @Override
            public Livro salvar(Livro livro) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Optional<Livro> buscarPorId(String id) {
                return executar(fabrica, repositorio -> repositorio.buscarPorId(id));
            }

            @Override
            public List<Livro> listar() {
                return executar(fabrica, RepositorioLivro::listar);
            }

            @Override
            public void remover(String id) {
                throw new UnsupportedOperationException();
            }
        };
    }

    private static <T> T executar(
            FabricaPersistencia fabrica,
            Function<RepositorioLivro, T> operacao) {
        var unidade = fabrica.criarUnidadeDeTrabalho();
        try {
            return operacao.apply(fabrica.criarRepositorioLivro(unidade));
        } finally {
            unidade.rollback();
            unidade.close();
        }
    }

    private static ServicoConsulta consulta(
            java.util.function.Function<String, Optional<MetadadosLivro>> porId,
            java.util.function.Supplier<List<MetadadosLivro>> listar) {
        return new ServicoConsulta() {
            @Override
            public Optional<MetadadosLivro> consultarPorId(String idLivro) {
                return porId.apply(idLivro);
            }

            @Override
            public List<MetadadosLivro> listar() {
                return listar.get();
            }
        };
    }

    private static String capturarSaida(Runnable execucao) {
        PrintStream saidaOriginal = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (PrintStream saida = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            System.setOut(saida);
            execucao.run();
        } finally {
            System.setOut(saidaOriginal);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static int ocorrencias(String texto, String trecho) {
        return (int) texto.lines().filter(linha -> linha.contains(trecho)).count();
    }

    private static void restaurarPropriedade(String nome, String valorAnterior) {
        if (valorAnterior == null) {
            System.clearProperty(nome);
        } else {
            System.setProperty(nome, valorAnterior);
        }
    }
}

