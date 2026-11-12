package br.pucpr.biblioteca.cadastro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.pucpr.biblioteca.comum.excecoes.PreRequisitoNaoAtendidoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class R07_TemplateCadastroTest {
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
        public void salvar(RegistroTeste registro) {
            quantidadeGravacoes++;
            dados.put(registro.id(), registro);
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
        protected void armazenar(RegistroTeste registro) {
            etapas.add("armazenar");
        }

        @Override
        protected RegistroTeste apresentar(RegistroTeste registro) {
            etapas.add("apresentar");
            return registro;
        }
    }
}
