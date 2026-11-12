package br.pucpr.biblioteca.tratadores;

import br.pucpr.biblioteca.biblioteca.acervo.consulta.MetadadosLivro;
import br.pucpr.biblioteca.biblioteca.acervo.consulta.ServicoConsulta;
import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.cadastro.CadastroLivro;
import br.pucpr.biblioteca.comum.excecoes.BibliotecaException;
import java.util.List;
import java.util.Objects;

/**
 * Traduz entradas da interface para cadastro e consulta do acervo.
 */
public final class TratadorDeAcervo {
    private final CadastroLivro cadastroLivro;
    private final ServicoConsulta consulta;

    public TratadorDeAcervo(CadastroLivro cadastroLivro, ServicoConsulta consulta) {
        this.cadastroLivro = Objects.requireNonNull(cadastroLivro);
        this.consulta = Objects.requireNonNull(consulta);
    }

    public String cadastrar(Livro livro) {
        try {
            Livro cadastrado = cadastroLivro.incluir(livro);
            return "Livro cadastrado: " + cadastrado.id();
        } catch (BibliotecaException | IllegalArgumentException excecao) {
            return "Nao foi possivel cadastrar o livro: " + excecao.getMessage();
        }
    }

    public List<MetadadosLivro> listar() {
        return consulta.listar();
    }

    public String consultar(String idLivro) {
        return consulta.consultarPorId(idLivro)
                .map(metadados -> metadados.id() + " - "
                        + metadados.titulo() + " (" + metadados.situacao() + ")")
                .orElse("Livro nao encontrado: " + idLivro);
    }
}
