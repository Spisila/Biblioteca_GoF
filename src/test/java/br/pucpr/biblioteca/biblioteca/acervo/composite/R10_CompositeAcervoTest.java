package br.pucpr.biblioteca.biblioteca.acervo.composite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.biblioteca.livro.SituacaoLivro;
import br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

// Cenário R10: validar a composição hierárquica dos elementos do acervo.
class R10_CompositeAcervoTest {
    @Test
    void R10_contaRecursivamenteUnidadeSecoesESubsecoes() {
        UnidadeAcervo unidade = new UnidadeAcervo("u1", "Biblioteca");
        SecaoAcervo humanas = new SecaoAcervo("s1", "Humanas");
        SecaoAcervo historia = new SecaoAcervo("ss1", "Historia");
        SecaoAcervo tecnologia = new SecaoAcervo("s2", "Tecnologia");
        humanas.adicionar(new ItemAcervo("i1", "l1"));
        historia.adicionar(new ItemAcervo("i2", "l2"));
        historia.adicionar(new ItemAcervo("i3", "l3"));
        tecnologia.adicionar(new ItemAcervo("i4", "l4"));
        humanas.adicionar(historia);
        unidade.adicionar(humanas);
        unidade.adicionar(tecnologia);

        assertEquals(4, unidade.contarElementos());
        assertEquals(4, unidade.listarIdsLivros().size());
        assertEquals(4, unidade.listarIdsLivros().stream().distinct().count());
    }

    @Test
    void R10_recusaCiclosEDuplicidadeDeLivro() {
        UnidadeAcervo unidade = new UnidadeAcervo("u1", "Biblioteca");
        SecaoAcervo secao = new SecaoAcervo("s1", "Secao");
        SecaoAcervo subsecao = new SecaoAcervo("ss1", "Subsecao");
        unidade.adicionar(secao);
        secao.adicionar(subsecao);
        subsecao.adicionar(new ItemAcervo("i1", "l1"));

        assertThrows(IllegalArgumentException.class, () -> subsecao.adicionar(unidade));
        assertThrows(IllegalArgumentException.class,
                () -> secao.adicionar(new ItemAcervo("i2", "l1")));
    }

    @Test
    void R10_moverLivroPreservaIdentificadorEEstadoDoLivro() {
        UnidadeAcervo unidade = new UnidadeAcervo("u1", "Biblioteca");
        SecaoAcervo origem = new SecaoAcervo("s1", "Origem");
        SecaoAcervo destino = new SecaoAcervo("s2", "Destino");
        Livro livro = new Livro("l1", "Livro");
        livro.solicitarReserva("e1");
        Emprestimo emprestimo = new Emprestimo("t1", "l1", "e1", LocalDate.of(2026, 11, 12));
        livro.realizarEmprestimo(emprestimo);
        origem.adicionar(new ItemAcervo("i1", livro.id()));
        unidade.adicionar(origem);
        unidade.adicionar(destino);

        unidade.moverLivro("l1", destino);

        assertEquals("l1", livro.id());
        assertEquals(SituacaoLivro.EMPRESTADO, livro.situacao());
        assertEquals(List.of("l1"), destino.listarIdsLivros());
    }
}

