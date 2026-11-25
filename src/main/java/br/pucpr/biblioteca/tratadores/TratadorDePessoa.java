package br.pucpr.biblioteca.tratadores;

import br.pucpr.biblioteca.cadastro.CadastroPessoa;
import br.pucpr.biblioteca.cadastro.RegistroCadastravel;
import br.pucpr.biblioteca.comum.excecoes.BibliotecaException;
import br.pucpr.biblioteca.rh.pessoa.Pessoa;
import java.util.List;
import java.util.Objects;

/**
 * Traduz entradas da interface para o template de cadastro de pessoas.
 */
public final class TratadorDePessoa<T extends RegistroCadastravel> {
    private final CadastroPessoa<T> cadastro;

    public TratadorDePessoa(CadastroPessoa<T> cadastro) {
        this.cadastro = Objects.requireNonNull(cadastro);
    }

    public String cadastrar(T dados) {
        try {
            T cadastrado = cadastro.incluir(dados);
            return "Pessoa cadastrada: " + cadastrado.id();
        } catch (BibliotecaException | IllegalArgumentException excecao) {
            return "Nao foi possivel cadastrar a pessoa: " + excecao.getMessage();
        }
    }

    public String consultar(String id) {
        return cadastro.consultarPorId(id)
                .map(registro -> {
                    if (registro instanceof Pessoa pessoa) {
                        return "Pessoa encontrada: ID=" + pessoa.id()
                                + " | Nome=" + pessoa.nome()
                                + " | CPF=" + pessoa.cpf()
                                + " | E-mail=" + pessoa.email()
                                + " | Data de nascimento=" + pessoa.dataNascimento();
                    }
                    return "Pessoa encontrada: " + registro.id();
                })
                .orElse("Pessoa nao encontrada: " + id);
    }

    public List<T> listar() {
        return cadastro.listar();
    }
}
