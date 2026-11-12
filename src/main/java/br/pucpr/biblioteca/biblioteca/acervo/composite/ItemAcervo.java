package br.pucpr.biblioteca.biblioteca.acervo.composite;

import java.util.List;
import java.util.Objects;

/**
 * Folha que referencia um livro sem assumir sua propriedade.
 *
 * <p>Padrão: R10 Composite — papel: Folha.</p>
 */
public final class ItemAcervo implements ElementoAcervo {
    private final String id;
    private final String idLivro;
    private GrupoAcervo grupoPai;

    public ItemAcervo(String id, String idLivro) {
        this.id = validarTexto(id, "id");
        this.idLivro = validarTexto(idLivro, "idLivro");
    }

    @Override
    public String id() {
        return id;
    }

    public String idLivro() {
        return idLivro;
    }

    @Override
    public int contarElementos() {
        return 1;
    }

    @Override
    public List<String> listarIdsLivros() {
        return List.of(idLivro);
    }

    GrupoAcervo grupoPai() {
        return grupoPai;
    }

    void definirGrupoPai(GrupoAcervo grupoPai) {
        this.grupoPai = grupoPai;
    }

    private static String validarTexto(String valor, String nome) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(nome + " nao pode ser vazio");
        }
        return valor;
    }
}
