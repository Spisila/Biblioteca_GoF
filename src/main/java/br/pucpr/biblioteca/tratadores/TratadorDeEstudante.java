package br.pucpr.biblioteca.tratadores;

import br.pucpr.biblioteca.cadastro.CadastroEstudante;
import br.pucpr.biblioteca.cadastro.RegistroCadastravel;
import br.pucpr.biblioteca.comum.excecoes.BibliotecaException;
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
}
