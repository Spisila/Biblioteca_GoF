package br.pucpr.biblioteca.persistencia.relacional;

import br.pucpr.biblioteca.biblioteca.acervo.composite.GrupoAcervo;
import br.pucpr.biblioteca.biblioteca.acervo.composite.ItemAcervo;
import br.pucpr.biblioteca.biblioteca.acervo.composite.SecaoAcervo;
import br.pucpr.biblioteca.biblioteca.acervo.composite.UnidadeAcervo;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioCategoria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Persistência relacional da hierarquia de categorias.
 */
public final class RepositorioCategoriaRelacional implements RepositorioCategoria {
    private final UnidadeDeTrabalhoRelacional unidadeDeTrabalho;

    public RepositorioCategoriaRelacional(UnidadeDeTrabalhoRelacional unidadeDeTrabalho) {
        this.unidadeDeTrabalho = unidadeDeTrabalho;
    }

    public UnidadeDeTrabalhoRelacional unidadeDeTrabalho() {
        return unidadeDeTrabalho;
    }

    @Override
    public void carregar(UnidadeAcervo unidadeAcervo) {
        Map<String, CategoriaPersistida> categorias = lerCategorias();
        Map<String, SecaoAcervo> grupos = new HashMap<>();
        for (CategoriaPersistida categoria : categorias.values()) {
            if (unidadeAcervo.buscarGrupo(categoria.id()).isPresent()) {
                throw new IllegalStateException(
                        "Id de categoria duplicado na hierarquia: " + categoria.id());
            }
            grupos.put(categoria.id(), new SecaoAcervo(categoria.id(), categoria.nome()));
        }

        Set<String> anexados = new HashSet<>();
        while (anexados.size() < grupos.size()) {
            boolean progresso = false;
            for (CategoriaPersistida categoria : categorias.values()) {
                if (anexados.contains(categoria.id())) {
                    continue;
                }
                GrupoAcervo pai;
                if (categoria.idPai() == null) {
                    pai = unidadeAcervo;
                } else if (anexados.contains(categoria.idPai())) {
                    pai = grupos.get(categoria.idPai());
                } else {
                    continue;
                }
                pai.adicionar(grupos.get(categoria.id()));
                anexados.add(categoria.id());
                progresso = true;
            }
            if (!progresso) {
                throw new IllegalStateException("Hierarquia de categorias invalida no banco.");
            }
        }

        carregarAssociacoes(unidadeAcervo);
    }

    @Override
    public void salvarCategoria(String id, String nome, String idPai) {
        String sql = "INSERT INTO categorias (id, nome, id_categoria_pai) VALUES (?, ?, ?)";
        try (PreparedStatement statement = conexao().prepareStatement(sql)) {
            statement.setString(1, id);
            statement.setString(2, nome);
            if (idPai == null) {
                statement.setNull(3, Types.VARCHAR);
            } else {
                statement.setString(3, idPai);
            }
            statement.executeUpdate();
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha(
                    "Nao foi possivel salvar a categoria", excecao);
        }
    }

    @Override
    public void associarLivro(String idLivro, String idCategoria) {
        String sql = "INSERT INTO livros_categorias (id_livro, id_categoria) VALUES (?, ?)";
        try (PreparedStatement statement = conexao().prepareStatement(sql)) {
            statement.setString(1, idLivro);
            statement.setString(2, idCategoria);
            statement.executeUpdate();
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha(
                    "Nao foi possivel associar o livro a categoria", excecao);
        }
    }

    private Map<String, CategoriaPersistida> lerCategorias() {
        String sql = "SELECT id, nome, id_categoria_pai FROM categorias ORDER BY id";
        Map<String, CategoriaPersistida> categorias = new HashMap<>();
        try (PreparedStatement statement = conexao().prepareStatement(sql);
                ResultSet resultado = statement.executeQuery()) {
            while (resultado.next()) {
                CategoriaPersistida categoria = new CategoriaPersistida(
                        resultado.getString("id"),
                        resultado.getString("nome"),
                        resultado.getString("id_categoria_pai"));
                categorias.put(categoria.id(), categoria);
            }
            return categorias;
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha(
                    "Nao foi possivel carregar as categorias", excecao);
        }
    }

    private void carregarAssociacoes(UnidadeAcervo unidadeAcervo) {
        String sql = "SELECT id_livro, id_categoria FROM livros_categorias "
                + "ORDER BY id_categoria, id_livro";
        try (PreparedStatement statement = conexao().prepareStatement(sql);
                ResultSet resultado = statement.executeQuery()) {
            while (resultado.next()) {
                String idLivro = resultado.getString("id_livro");
                String idCategoria = resultado.getString("id_categoria");
                GrupoAcervo categoria = unidadeAcervo.buscarGrupo(idCategoria)
                        .orElseThrow(() -> new IllegalStateException(
                                "Categoria associada nao encontrada: " + idCategoria));
                categoria.adicionar(new ItemAcervo("ITEM-" + idLivro, idLivro));
            }
        } catch (SQLException excecao) {
            throw UnidadeDeTrabalhoRelacional.falha(
                    "Nao foi possivel carregar associacoes de livros", excecao);
        }
    }

    private Connection conexao() {
        return unidadeDeTrabalho.conexao();
    }

    private record CategoriaPersistida(String id, String nome, String idPai) {
    }
}
