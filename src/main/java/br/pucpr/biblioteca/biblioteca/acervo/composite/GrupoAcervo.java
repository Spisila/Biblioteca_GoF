package br.pucpr.biblioteca.biblioteca.acervo.composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Composite para unidades, seções e subseções do acervo.
 *
 * <p>Padrão: R10 Composite — papel: Composite.</p>
 */
public class GrupoAcervo implements ElementoAcervo {
    private final String id;
    private final String nome;
    private final List<ElementoAcervo> elementos = new ArrayList<>();
    private GrupoAcervo grupoPai;

    public GrupoAcervo(String id, String nome) {
        this.id = validarTexto(id, "id");
        this.nome = validarTexto(nome, "nome");
    }

    @Override
    public String id() {
        return id;
    }

    public String nome() {
        return nome;
    }

    public List<ElementoAcervo> elementos() {
        return Collections.unmodifiableList(elementos);
    }

    public void adicionar(ElementoAcervo elemento) {
        Objects.requireNonNull(elemento, "elemento nao pode ser nulo");
        if (elemento == this) {
            throw new IllegalArgumentException("Um grupo nao pode conter a si mesmo");
        }
        if (elemento instanceof GrupoAcervo) {
            GrupoAcervo grupo = (GrupoAcervo) elemento;
            if (grupo.contemGrupo(this)) {
                throw new IllegalArgumentException("A adicao criaria um ciclo no acervo");
            }
            if (grupo.grupoPai != null) {
                throw new IllegalArgumentException("O grupo ja pertence a outro grupo");
            }
        } else if (elemento instanceof ItemAcervo
                && ((ItemAcervo) elemento).grupoPai() != null) {
            throw new IllegalArgumentException("O item ja pertence a outro grupo");
        }
        if (raiz().contemIdLivro(elemento)) {
            throw new IllegalArgumentException("O livro ja pertence a este acervo");
        }
        elementos.add(elemento);
        definirPai(elemento, this);
    }

    public void remover(ElementoAcervo elemento) {
        if (!elementos.remove(elemento)) {
            throw new IllegalArgumentException("Elemento nao pertence a este grupo");
        }
        definirPai(elemento, null);
    }

    public void moverLivro(String idLivro, GrupoAcervo destino) {
        Objects.requireNonNull(destino, "destino nao pode ser nulo");
        ItemAcervo item = buscarItem(idLivro);
        if (item == null) {
            throw new IllegalArgumentException("Livro nao encontrado no acervo: " + idLivro);
        }
        if (item.grupoPai() == destino) {
            return;
        }
        if (destino.contemGrupo(item.grupoPai())) {
            throw new IllegalArgumentException("O destino nao pode estar dentro da origem");
        }
        if (destino.contemIdLivro(idLivro)) {
            throw new IllegalArgumentException("O livro ja pertence ao grupo de destino");
        }
        GrupoAcervo origem = item.grupoPai();
        origem.remover(item);
        try {
            destino.adicionar(item);
        } catch (RuntimeException excecao) {
            origem.adicionar(item);
            throw excecao;
        }
    }

    @Override
    public int contarElementos() {
        return elementos.stream().mapToInt(ElementoAcervo::contarElementos).sum();
    }

    @Override
    public List<String> listarIdsLivros() {
        List<String> ids = new ArrayList<>();
        elementos.forEach(elemento -> ids.addAll(elemento.listarIdsLivros()));
        return List.copyOf(ids);
    }

    GrupoAcervo grupoPai() {
        return grupoPai;
    }

    private ItemAcervo buscarItem(String idLivro) {
        for (ElementoAcervo elemento : elementos) {
            if (elemento instanceof ItemAcervo
                    && ((ItemAcervo) elemento).idLivro().equals(idLivro)) {
                return (ItemAcervo) elemento;
            }
            if (elemento instanceof GrupoAcervo) {
                ItemAcervo item = ((GrupoAcervo) elemento).buscarItem(idLivro);
                if (item != null) {
                    return item;
                }
            }
        }
        return null;
    }

    private boolean contemIdLivro(ElementoAcervo elemento) {
        for (String idLivro : elemento.listarIdsLivros()) {
            if (contemIdLivro(idLivro)) {
                return true;
            }
        }
        return false;
    }

    private boolean contemIdLivro(String idLivro) {
        return listarIdsLivros().contains(idLivro);
    }

    private boolean contemGrupo(GrupoAcervo procurado) {
        if (this == procurado) {
            return true;
        }
        return grupoPai != null && grupoPai.contemGrupo(procurado);
    }

    private GrupoAcervo raiz() {
        return grupoPai == null ? this : grupoPai.raiz();
    }

    private static void definirPai(ElementoAcervo elemento, GrupoAcervo pai) {
        if (elemento instanceof GrupoAcervo) {
            ((GrupoAcervo) elemento).grupoPai = pai;
        } else {
            ((ItemAcervo) elemento).definirGrupoPai(pai);
        }
    }

    private static String validarTexto(String valor, String nome) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(nome + " nao pode ser vazio");
        }
        return valor;
    }
}
