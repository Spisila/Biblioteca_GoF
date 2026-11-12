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
    private final ConcurrentMap<String, FutureTask<Object>> tarefas = new ConcurrentHashMap<>();

    public <T> T executar(Comando<T> comando) {
        FutureTask<Object> tarefaNova = new FutureTask<>(comando::executar);
        FutureTask<Object> tarefa = tarefas.putIfAbsent(comando.idComando(), tarefaNova);
        if (tarefa == null) {
            tarefa = tarefaNova;
            tarefa.run();
        }
        return aguardarResultado(tarefa);
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
}
