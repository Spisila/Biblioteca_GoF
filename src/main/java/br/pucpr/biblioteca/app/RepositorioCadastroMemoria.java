package br.pucpr.biblioteca.app;

import br.pucpr.biblioteca.cadastro.RegistroCadastravel;
import br.pucpr.biblioteca.cadastro.RepositorioCadastro;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.UUID;
import java.util.Objects;
import br.pucpr.biblioteca.academico.estudante.Estudante;
import br.pucpr.biblioteca.rh.atendente.Atendente;
import br.pucpr.biblioteca.rh.pessoa.Pessoa;

/**
 * Repositório simples para os registros ainda não modelados no domínio.
 */
public final class RepositorioCadastroMemoria<T extends RegistroCadastravel>
        implements RepositorioCadastro<T> {
    private final ConcurrentMap<String, T> registros = new ConcurrentHashMap<>();
    private final Class<T> tipo;

    public RepositorioCadastroMemoria(Class<T> tipo) {
        this.tipo = Objects.requireNonNull(tipo);
    }

    @Override
    public Optional<T> buscarPorId(String id) {
        return Optional.ofNullable(registros.get(id));
    }

    @Override
    public List<T> listar() {
        return registros.values().stream()
                .sorted((primeiro, segundo) -> primeiro.id().compareTo(segundo.id()))
                .toList();
    }

    @Override
    public Optional<T> buscarPorCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return Optional.empty();
        }
        return registros.values().stream()
                .filter(registro -> registro instanceof Pessoa pessoa
                        && pessoa.cpf().equals(cpf))
                .findFirst();
    }

    @Override
    public T salvar(T registro) {
        if (!registro.id().isBlank()) {
            registros.put(registro.id(), registro);
            return registro;
        }
        if (registro instanceof Pessoa pessoa) {
            RegistroCadastravel persistido;
            if (pessoa instanceof Estudante estudante) {
                persistido = new Estudante(
                        "E-" + UUID.randomUUID(), estudante.nome(), estudante.cpf(),
                        estudante.email(), estudante.numeroMatricula(), estudante.curso(),
                        estudante.limiteEmprestimos(), estudante.dataNascimento());
            } else if (pessoa instanceof Atendente atendente) {
                persistido = new Atendente(
                        "A-" + UUID.randomUUID(), atendente.nome(), atendente.cpf(),
                        atendente.email(), atendente.turno(), atendente.registroFuncional(),
                        atendente.dataNascimento());
            } else {
                persistido = new Pessoa(
                        "P-" + UUID.randomUUID(), pessoa.nome(), pessoa.cpf(),
                        pessoa.email(), pessoa.dataNascimento());
            }
            T persistidoTipado = tipo.cast(persistido);
            registros.put(persistidoTipado.id(), persistidoTipado);
            return persistidoTipado;
        }
        throw new IllegalArgumentException("Registro sem ID nao pode ser persistido em memoria");
    }
}
