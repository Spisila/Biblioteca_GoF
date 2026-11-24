package br.pucpr.biblioteca.app;

import br.pucpr.biblioteca.cadastro.RegistroCadastravel;
import br.pucpr.biblioteca.cadastro.RepositorioCadastro;
import br.pucpr.biblioteca.persistencia.fabrica.FabricaPersistencia;
import br.pucpr.biblioteca.persistencia.fabrica.UnidadeDeTrabalho;
import br.pucpr.biblioteca.persistencia.relacional.RepositorioCadastroRelacional;
import br.pucpr.biblioteca.persistencia.relacional.UnidadeDeTrabalhoRelacional;
import java.util.Optional;
import java.util.Objects;

final class RepositorioCadastroPorOperacao<T extends RegistroCadastravel>
        implements RepositorioCadastro<T> {
    private final FabricaPersistencia fabrica;
    private final RepositorioCadastro<T> memoria;
    private final Class<T> tipo;

    RepositorioCadastroPorOperacao(
            FabricaPersistencia fabrica,
            RepositorioCadastro<T> memoria,
            Class<T> tipo) {
        this.fabrica = Objects.requireNonNull(fabrica);
        this.memoria = memoria;
        this.tipo = Objects.requireNonNull(tipo);
    }

    @Override
    public Optional<T> buscarPorId(String id) {
        UnidadeDeTrabalho unidade = fabrica.criarUnidadeDeTrabalho();
        try {
            Optional<T> resultado = repositorio(unidade).buscarPorId(id);
            unidade.rollback();
            return resultado;
        } catch (RuntimeException excecao) {
            unidade.rollback();
            throw excecao;
        } finally {
            unidade.close();
        }
    }

    @Override
    public T salvar(T registro) {
        UnidadeDeTrabalho unidade = fabrica.criarUnidadeDeTrabalho();
        try {
            T persistido = repositorio(unidade).salvar(registro);
            unidade.commit();
            return persistido;
        } catch (RuntimeException excecao) {
            unidade.rollback();
            throw excecao;
        } finally {
            unidade.close();
        }
    }

    @SuppressWarnings("unchecked")
    private RepositorioCadastro<T> repositorio(UnidadeDeTrabalho unidade) {
        if (unidade instanceof UnidadeDeTrabalhoRelacional) {
            return new RepositorioCadastroRelacional<>(
                    (UnidadeDeTrabalhoRelacional) unidade, tipo);
        }
        return memoria;
    }
}
