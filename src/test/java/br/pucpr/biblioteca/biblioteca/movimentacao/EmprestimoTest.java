package br.pucpr.biblioteca.biblioteca.movimentacao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class EmprestimoTest {
    @Test
    void R01_criaEmprestimoComPrazoEZeroRenovacoes() {
        LocalDate data = LocalDate.of(2026, 1, 10);

        Emprestimo emprestimo = new Emprestimo("t1", "l1", "e1", data);

        assertEquals("t1", emprestimo.idTransacao());
        assertEquals(data, emprestimo.dataTransacao());
        assertEquals("l1", emprestimo.idLivro());
        assertEquals("e1", emprestimo.idEstudante());
        assertEquals(data.plusDays(30), emprestimo.dataVencimento());
        assertEquals(0, emprestimo.numRenovacoes());
    }

    @Test
    void R01_renovarAtualizaPrazoENumeroDeRenovacoesSemValidarLimite() {
        Emprestimo emprestimo = new Emprestimo("t1", "l1", "e1", LocalDate.of(2026, 1, 10));

        emprestimo.renovar();
        emprestimo.renovar();
        emprestimo.renovar();

        assertEquals(3, emprestimo.numRenovacoes());
        assertEquals(LocalDate.of(2026, 5, 10), emprestimo.dataVencimento());
    }

    @Test
    void R01_calculaDiasDeAtrasoSomenteDepoisDoVencimento() {
        Emprestimo emprestimo = new Emprestimo("t1", "l1", "e1", LocalDate.of(2026, 1, 10));

        assertEquals(0, emprestimo.diasDeAtraso(LocalDate.of(2026, 2, 9)));
        assertEquals(5, emprestimo.diasDeAtraso(LocalDate.of(2026, 2, 14)));
        assertEquals(50, emprestimo.diasDeAtraso(LocalDate.of(2026, 3, 31)));
    }
}
