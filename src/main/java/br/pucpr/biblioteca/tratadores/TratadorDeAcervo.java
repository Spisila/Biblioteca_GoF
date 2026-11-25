package br.pucpr.biblioteca.tratadores;

import br.pucpr.biblioteca.biblioteca.acervo.consulta.MetadadosLivro;
import br.pucpr.biblioteca.biblioteca.acervo.consulta.ServicoConsulta;
import br.pucpr.biblioteca.biblioteca.acervo.composite.GrupoAcervo;
import br.pucpr.biblioteca.biblioteca.acervo.composite.ItemAcervo;
import br.pucpr.biblioteca.biblioteca.acervo.composite.SecaoAcervo;
import br.pucpr.biblioteca.biblioteca.acervo.composite.UnidadeAcervo;
import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.cadastro.CadastroLivro;
import br.pucpr.biblioteca.comum.excecoes.BibliotecaException;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioCategoria;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Traduz entradas da interface para cadastro e consulta do acervo.
 */
public final class TratadorDeAcervo {
    private final CadastroLivro cadastroLivro;
    private final ServicoConsulta consulta;
    private final UnidadeAcervo unidadeAcervo;
    private final Optional<RepositorioCategoria> repositorioCategoria;

    public TratadorDeAcervo(CadastroLivro cadastroLivro, ServicoConsulta consulta) {
        this(cadastroLivro, consulta, new UnidadeAcervo("ACERVO", "Acervo"));
    }

    public TratadorDeAcervo(
            CadastroLivro cadastroLivro,
            ServicoConsulta consulta,
            UnidadeAcervo unidadeAcervo) {
        this(cadastroLivro, consulta, unidadeAcervo, Optional.empty());
    }

    public TratadorDeAcervo(
            CadastroLivro cadastroLivro,
            ServicoConsulta consulta,
            UnidadeAcervo unidadeAcervo,
            RepositorioCategoria repositorioCategoria) {
        this(cadastroLivro, consulta, unidadeAcervo,
                Optional.of(Objects.requireNonNull(repositorioCategoria)));
    }

    private TratadorDeAcervo(
            CadastroLivro cadastroLivro,
            ServicoConsulta consulta,
            UnidadeAcervo unidadeAcervo,
            Optional<RepositorioCategoria> repositorioCategoria) {
        this.cadastroLivro = Objects.requireNonNull(cadastroLivro);
        this.consulta = Objects.requireNonNull(consulta);
        this.unidadeAcervo = Objects.requireNonNull(unidadeAcervo);
        this.repositorioCategoria = repositorioCategoria;
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

    public String cadastrar(String titulo) {
        return cadastrar(new Livro(titulo));
    }

    public List<MetadadosLivro> listar() {
        return consulta.listar();
    }

    public List<Livro> listarLivros() {
        return cadastroLivro.listar();
    }

    public String criarCategoria(String id, String idPai, String nome) {
        if (unidadeAcervo.buscarGrupo(id).isPresent()) {
            throw new IllegalArgumentException("Ja existe uma categoria com o id: " + id);
        }
        GrupoAcervo pai = idPai == null || idPai.isBlank()
                ? unidadeAcervo
                : buscarGrupo(idPai);
        SecaoAcervo categoria = new SecaoAcervo(id, nome);
        pai.adicionar(categoria);
        try {
            repositorioCategoria.ifPresent(repositorio ->
                    repositorio.salvarCategoria(id, nome,
                            idPai == null || idPai.isBlank() ? null : idPai));
        } catch (RuntimeException excecao) {
            pai.remover(categoria);
            throw excecao;
        }
        return "Categoria criada: " + categoria.id();
    }

    public String associarLivro(String idLivro, String idCategoria) {
        if (!consulta.consultarPorId(idLivro).isPresent()) {
            throw new IllegalArgumentException("Livro nao encontrado: " + idLivro);
        }
        GrupoAcervo categoria = buscarGrupo(idCategoria);
        ItemAcervo item = new ItemAcervo("ITEM-" + UUID.randomUUID(), idLivro);
        categoria.adicionar(item);
        try {
            repositorioCategoria.ifPresent(repositorio ->
                    repositorio.associarLivro(idLivro, idCategoria));
        } catch (RuntimeException excecao) {
            categoria.remover(item);
            throw excecao;
        }
        return "Livro " + idLivro + " associado a categoria " + categoria.id();
    }

    public List<MetadadosLivro> listarLivrosDaCategoria(String idCategoria) {
        GrupoAcervo categoria = buscarGrupo(idCategoria);
        return categoria.listarIdsLivros().stream()
                .map(idLivro -> consulta.consultarPorId(idLivro)
                        .orElseThrow(() -> new IllegalStateException(
                                "Livro associado a categoria nao encontrado: " + idLivro)))
                .toList();
    }

    public int contarLivrosDaCategoria(String idCategoria) {
        return buscarGrupo(idCategoria).contarElementos();
    }

    public String listarCategoriasComoTexto() {
        StringBuilder resultado = new StringBuilder();
        adicionarCategoriaComoTexto(unidadeAcervo, 0, resultado);
        return resultado.toString();
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
                .map(metadados -> "Livro encontrado: ID=" + metadados.id()
                        + " | Titulo=" + metadados.titulo()
                        + " | Situacao=" + metadados.situacao()
                        + " | Reservado para=" + texto(metadados.reservadoPara())
                        + " | Data de devolucao=" + texto(metadados.dataDevolucao()))
                .orElse("Livro nao encontrado: " + idLivro);
    }

    private String texto(Object valor) {
        return valor == null ? "-" : valor.toString();
    }

    private GrupoAcervo buscarGrupo(String idCategoria) {
        return unidadeAcervo.buscarGrupo(idCategoria)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Categoria nao encontrada: " + idCategoria));
    }

    private void adicionarCategoriaComoTexto(
            GrupoAcervo grupo,
            int nivel,
            StringBuilder resultado) {
        for (int indice = 0; indice < nivel; indice++) {
            resultado.append("  ");
        }
        resultado.append(grupo.id()).append(" - ").append(grupo.nome())
                .append(System.lineSeparator());
        for (var elemento : grupo.elementos()) {
            if (elemento instanceof GrupoAcervo) {
                adicionarCategoriaComoTexto((GrupoAcervo) elemento, nivel + 1, resultado);
            }
        }
    }
}
