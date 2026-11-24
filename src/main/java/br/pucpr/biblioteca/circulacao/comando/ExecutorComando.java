package br.pucpr.biblioteca.circulacao.comando;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ConcurrentMap;

/**
 * Executa cada comando identificado uma única vez, inclusive sob concorrência.
 *
 * <p>Padrão: R06 Command — papel: Invocador.</p>
 */
public final class ExecutorComando {
    private final ConcurrentMap<String, EntradaComando> tarefas = new ConcurrentHashMap<>();

    public <T> T executar(Comando<T> comando) {
        FutureTask<Object> tarefaNova = new FutureTask<>(comando::executar);
        EntradaComando nova = new EntradaComando(comando.assinaturaOperacao(), tarefaNova);
        EntradaComando entrada = tarefas.putIfAbsent(comando.idComando(), nova);
        if (entrada == null) {
            entrada = nova;
            entrada.tarefa().run();
        } else if (!entrada.assinatura().equals(comando.assinaturaOperacao())) {
            throw new IllegalArgumentException(
                    "O identificador do comando ja pertence a outra operacao");
        }
        return aguardarResultado(entrada.tarefa());
    }

    private static <T> T aguardarResultado(FutureTask<Object> tarefa) {
        try {
            @SuppressWarnings("unchecked")
            T resultado = (T) tarefa.get();
            return resultado;
        } catch (InterruptedException excecao) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Execucao do comando foi interrompida", excecao);
        } catch (ExecutionException excecao) {
            Throwable causa = excecao.getCause();
            if (causa instanceof RuntimeException) {
                throw (RuntimeException) causa;
            }
            if (causa instanceof Error) {
                throw (Error) causa;
            }
            throw new IllegalStateException("Falha na execucao do comando", causa);
        }
    }

    private record EntradaComando(String assinatura, FutureTask<Object> tarefa) {
    }
}
