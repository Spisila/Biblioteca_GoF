package br.pucpr.biblioteca.academico.estudante;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class EstudanteTest {
    @Test
    void preservaDadosAcademicosDoEstudante() {
        Estudante estudante = new Estudante(
                "e1", "Bia", "456", "bia@example.com", "M1", "Computacao", 3);

        assertEquals("e1", estudante.id());
        assertEquals("M1", estudante.numeroMatricula());
        assertEquals("Computacao", estudante.curso());
        assertEquals(3, estudante.limiteEmprestimos());
    }

    @Test
    void rejeitaLimiteDeEmprestimosNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Estudante(
                        "e1", "Bia", "456", "bia@example.com", "M1", "Computacao", -1));
    }
}
