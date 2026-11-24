package br.pucpr.biblioteca.rh.pessoa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

class PessoaTest {
    @Test
    void preservaDadosEImplementaRegistroCadastravel() {
        Pessoa pessoa = new Pessoa("p1", "Ana", "123", "ana@example.com");

        assertEquals("p1", pessoa.id());
        assertEquals("Ana", pessoa.nome());
        assertEquals("123", pessoa.cpf());
        assertEquals("ana@example.com", pessoa.email());
    }

    @Test
    void rejeitaDadosObrigatoriosVazios() {
        assertThrows(IllegalArgumentException.class,
                () -> new Pessoa("Ana", "", "ana@example.com", LocalDate.of(1990, 1, 1)));
    }

    @Test
    void permiteCriacaoSemIdParaAutoIncremento() {
        Pessoa pessoa = new Pessoa(
                "Ana", "123", "ana@example.com", LocalDate.of(1990, 1, 1));

        assertTrue(pessoa.id().isBlank());
        assertEquals(LocalDate.of(1990, 1, 1), pessoa.dataNascimento());
    }
}
