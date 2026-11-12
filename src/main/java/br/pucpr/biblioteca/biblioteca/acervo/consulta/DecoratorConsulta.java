package br.pucpr.biblioteca.biblioteca.acervo.consulta;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Base para extensões combináveis do serviço de consulta.
 *
 * <p>Padrão: R09 Decorator — papel: Decorator abstrato.</p>
 */
public abstract class DecoratorConsulta implements ServicoConsulta {
    protected final ServicoConsulta servico;

    protected DecoratorConsulta(ServicoConsulta servico) {
        this.servico = Objects.requireNonNull(servico, "servico nao pode ser nulo");
    }

    @Override
    public Optional<MetadadosLivro> consultarPorId(String idLivro) {
        return servico.consultarPorId(idLivro);
    }

    @Override
    public List<MetadadosLivro> listar() {
        return servico.listar();
    }
}
