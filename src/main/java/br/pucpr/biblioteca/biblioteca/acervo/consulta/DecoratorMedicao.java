package br.pucpr.biblioteca.biblioteca.acervo.consulta;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Mede quantidade e duração das consultas sem alterar seus resultados.
 *
 * <p>Padrão: R09 Decorator — papel: Decorator concreto.</p>
 */
public final class DecoratorMedicao extends DecoratorConsulta {
    private final AtomicLong quantidadeConsultas = new AtomicLong();
    private final AtomicLong totalNanos = new AtomicLong();

    public DecoratorMedicao(ServicoConsulta servico) {
        super(servico);
    }

    @Override
    public Optional<MetadadosLivro> consultarPorId(String idLivro) {
        long inicio = System.nanoTime();
        try {
            return super.consultarPorId(idLivro);
        } finally {
            registrar(inicio);
        }
    }

    @Override
    public List<MetadadosLivro> listar() {
        long inicio = System.nanoTime();
        try {
            return super.listar();
        } finally {
            registrar(inicio);
        }
    }

    public long quantidadeConsultas() {
        return quantidadeConsultas.get();
    }

    public long totalNanos() {
        return totalNanos.get();
    }

    private void registrar(long inicio) {
        quantidadeConsultas.incrementAndGet();
        totalNanos.addAndGet(System.nanoTime() - inicio);
    }
}
