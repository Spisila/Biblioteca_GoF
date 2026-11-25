package br.pucpr.biblioteca.tratadores;

import br.pucpr.biblioteca.cadastro.CadastroEstudante;
import br.pucpr.biblioteca.cadastro.RegistroCadastravel;
import br.pucpr.biblioteca.comum.excecoes.BibliotecaException;
import br.pucpr.biblioteca.academico.estudante.Estudante;
import java.util.List;
import java.util.Objects;

/**
 * Traduz entradas da interface para o template de cadastro de estudantes.
 */
public final class TratadorDeEstudante<T extends RegistroCadastravel> {
    private final CadastroEstudante<T> cadastro;

    public TratadorDeEstudante(CadastroEstudante<T> cadastro) {
        this.cadastro = Objects.requireNonNull(cadastro);
    }

    public String cadastrar(T dados) {
        try {
            T cadastrado = cadastro.incluir(dados);
            return "Estudante cadastrado: " + cadastrado.id();
        } catch (BibliotecaException | IllegalArgumentException excecao) {
            return "Nao foi possivel cadastrar o estudante: " + excecao.getMessage();
        }
    }

    public String consultar(String id) {
        return cadastro.consultarPorId(id)
                .map(registro -> {
                    if (registro instanceof Estudante estudante) {
                        return "Estudante encontrado: ID=" + estudante.id()
                                + " | Nome=" + estudante.nome()
                                + " | CPF=" + estudante.cpf()
                                + " | E-mail=" + estudante.email()
                                + " | Data de nascimento=" + estudante.dataNascimento()
                                + " | Matricula=" + estudante.numeroMatricula()
                                + " | Curso=" + estudante.curso()
                                + " | Limite de emprestimos=" + estudante.limiteEmprestimos();
                    }
                    return "Estudante encontrado: " + registro.id();
                })
                .orElse("Estudante nao encontrado: " + id);
    }

    public List<T> listar() {
        return cadastro.listar();
    }
}
