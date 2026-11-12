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

    public String cadastrar(String id, String titulo) {
        return cadastrar(new Livro(id, titulo));
    }

    public List<MetadadosLivro> listar() {
        return consulta.listar();
    }

    public String listarComoTexto() {
        StringBuilder resultado = new StringBuilder();
        for (MetadadosLivro metadados : listar()) {
            if (resultado.length() > 0) {
                resultado.append(System.lineSeparator());
            }
            resultado.append(metadados.id())
                    .append(" - ")
                    .append(metadados.titulo())
                    .append(" (")
                    .append(metadados.situacao())
                    .append(')');
        }
        return resultado.length() == 0 ? "Nenhum livro cadastrado." : resultado.toString();
    }

    public String consultar(String idLivro) {
        return consulta.consultarPorId(idLivro)
                .map(metadados -> metadados.id() + " - "
                        + metadados.titulo() + " (" + metadados.situacao() + ")")
                .orElse("Livro nao encontrado: " + idLivro);
    }
}
