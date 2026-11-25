package br.pucpr.biblioteca.cadastro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.pucpr.biblioteca.comum.excecoes.PreRequisitoNaoAtendidoException;
import br.pucpr.biblioteca.academico.estudante.Estudante;
import br.pucpr.biblioteca.app.RepositorioCadastroMemoria;
import br.pucpr.biblioteca.rh.pessoa.Pessoa;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class R07_TemplateCadastroTest {
    @Test
    void R07_cpfCadastradoCriaEstudanteComMesmoIdDaPessoa() {
        RepositorioCadastroMemoria<Pessoa> pessoas = new RepositorioCadastroMemoria<>(Pessoa.class);
        RepositorioCadastroMemoria<Estudante> estudantes =
                new RepositorioCadastroMemoria<>(Estudante.class);
        CadastroPessoa<Pessoa> cadastroPessoa = new CadastroPessoa<>(pessoas);
        Pessoa pessoa = cadastroPessoa.incluir(new Pessoa(
                "Ana", "12345678901", "ana@teste", LocalDate.of(2000, 1, 1)));
        CadastroEstudante<Estudante> cadastroEstudante =
                new CadastroEstudante<>(estudantes, pessoas, Estudante.class);

        Estudante estudante = cadastroEstudante.incluir(new Estudante(
                "", "Ana", "12345678901", "ana@teste",
                "2026001", "BCC", 5, LocalDate.of(2000, 1, 1)));

        assertEquals(pessoa.id(), estudante.id());
    }

    @Test
    void R07_cpfInexistenteNaoGeraPessoaOuEstudante() {
        RepositorioCadastroMemoria<Pessoa> pessoas = new RepositorioCadastroMemoria<>(Pessoa.class);
        RepositorioCadastroMemoria<Estudante> estudantes =
                new RepositorioCadastroMemoria<>(Estudante.class);
        CadastroEstudante<Estudante> cadastro =
                new CadastroEstudante<>(estudantes, pessoas, Estudante.class);

        assertThrows(PreRequisitoNaoAtendidoException.class,
                () -> cadastro.incluir(new Estudante(
                        "", "Sem Pessoa", "99999999999", "sem@teste",
                        "2026999", "BCC", 5, LocalDate.of(2000, 1, 1))));
        assertFalse(pessoas.buscarPorCpf("99999999999").isPresent());
        assertFalse(estudantes.buscarPorCpf("99999999999").isPresent());
    }

    @Test
    void R07_estudanteDuplicadoPorCpfRetornaExistente() {
        RepositorioCadastroMemoria<Pessoa> pessoas = new RepositorioCadastroMemoria<>(Pessoa.class);
        RepositorioCadastroMemoria<Estudante> estudantes =
                new RepositorioCadastroMemoria<>(Estudante.class);
        Pessoa pessoa = new CadastroPessoa<Pessoa>(pessoas).incluir(new Pessoa(
                "Ana", "12345678901", "ana@teste", LocalDate.of(2000, 1, 1)));
        CadastroEstudante<Estudante> cadastro =
                new CadastroEstudante<>(estudantes, pessoas, Estudante.class);
        Estudante original = cadastro.incluir(new Estudante(
                "", "Ana", "12345678901", "ana@teste",
                "2026001", "BCC", 5, LocalDate.of(2000, 1, 1)));

        Estudante repetido = cadastro.incluir(new Estudante(
                pessoa.id(), "Nome alterado", "12345678901", "novo@teste",
                "OUTRA", "OUTRO", 9, LocalDate.of(2001, 1, 1)));

        assertSame(original, repetido);
    }
    @Test
    void R07_itemDuplicadoRetornaCadastroExistente() {
        RepositorioFake repositorio = new RepositorioFake();
        RegistroTeste existente = new RegistroTeste("p1", "existente");
        repositorio.salvar(existente);
        CadastroPessoa<RegistroTeste> cadastro = new CadastroPessoa<>(repositorio);

        RegistroTeste resultado = cadastro.incluir(new RegistroTeste("p1", "novo"));

        assertSame(existente, resultado);
        assertEquals(1, repositorio.quantidadeGravacoes);
    }

    @Test
    void R07_preRequisitoNaoAtendidoImpedeQualquerGravacao() {
        RepositorioFake repositorio = new RepositorioFake();
        CadastroEstudante<RegistroTeste> cadastro =
                new CadastroEstudante<>(repositorio, registro -> false);

        assertThrows(PreRequisitoNaoAtendidoException.class,
                () -> cadastro.incluir(new RegistroTeste("e1", "estudante")));
        assertEquals(0, repositorio.quantidadeGravacoes);
        assertEquals(0, repositorio.dados.size());
    }

    @Test
    void R07_templateExecutaEtapasNaOrdemEOMetodoNaoPodeSerSobrescrito() {
        List<String> etapas = new ArrayList<>();
        CadastradorDeTeste cadastro = new CadastradorDeTeste(etapas);

        RegistroTeste resultado = cadastro.incluir(new RegistroTeste("x1", "registro"));

        assertEquals("x1", resultado.id());
        assertEquals(List.of("buscar", "validar", "pre-requisito", "criar", "armazenar", "apresentar"),
                etapas);
    }

    private record RegistroTeste(String id, String descricao) implements RegistroCadastravel {
    }

    private static final class RepositorioFake implements RepositorioCadastro<RegistroTeste> {
        private final Map<String, RegistroTeste> dados = new HashMap<>();
        private int quantidadeGravacoes;

        @Override
        public Optional<RegistroTeste> buscarPorId(String id) {
            return Optional.ofNullable(dados.get(id));
        }

        @Override
        public RegistroTeste salvar(RegistroTeste registro) {
            quantidadeGravacoes++;
            dados.put(registro.id(), registro);
            return registro;
        }
    }

    private static final class CadastradorDeTeste extends TemplateCadastro<RegistroTeste> {
        private final List<String> etapas;

        private CadastradorDeTeste(List<String> etapas) {
            this.etapas = etapas;
        }

        @Override
        protected Optional<RegistroTeste> buscarExistente(RegistroTeste dados) {
            etapas.add("buscar");
            return Optional.empty();
        }

        @Override
        protected void validarDados(RegistroTeste dados) {
            etapas.add("validar");
        }

        @Override
        protected void verificarPreRequisitos(RegistroTeste dados) {
            etapas.add("pre-requisito");
        }

        @Override
        protected RegistroTeste criarRegistro(RegistroTeste dados) {
            etapas.add("criar");
            return dados;
        }

        @Override
        protected RegistroTeste armazenar(RegistroTeste registro) {
            etapas.add("armazenar");
            return registro;
        }

        @Override
        protected RegistroTeste apresentar(RegistroTeste registro) {
            etapas.add("apresentar");
            return registro;
        }
    }
}
