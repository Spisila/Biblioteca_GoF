package br.pucpr.biblioteca.tratadores;

import br.pucpr.biblioteca.cadastro.CadastroAtendente;
import br.pucpr.biblioteca.cadastro.RegistroCadastravel;
import br.pucpr.biblioteca.comum.excecoes.BibliotecaException;
import java.util.Objects;

/**
 * Traduz entradas da interface para o template de cadastro de atendentes.
 */
public final class TratadorDeAtendente<T extends RegistroCadastravel> {
    private final CadastroAtendente<T> cadastro;

    public TratadorDeAtendente(CadastroAtendente<T> cadastro) {
        this.cadastro = Objects.requireNonNull(cadastro);
    }

    public String cadastrar(T dados) {
        try {
            T cadastrado = cadastro.incluir(dados);
            return "Atendente cadastrado: " + cadastrado.id();
        } catch (BibliotecaException | IllegalArgumentException excecao) {
            return "Nao foi possivel cadastrar o atendente: " + excecao.getMessage();
        }
    }

    public String consultar(String id) {
        return cadastro.consultarPorId(id)
                .map(registro -> "Atendente encontrado: " + registro.id())
                .orElse("Atendente nao encontrado: " + id);
    }
}
