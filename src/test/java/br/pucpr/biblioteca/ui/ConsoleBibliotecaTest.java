package br.pucpr.biblioteca.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucpr.biblioteca.academico.estudante.Estudante;
import br.pucpr.biblioteca.app.RepositorioCadastroMemoria;
import br.pucpr.biblioteca.biblioteca.acervo.consulta.DecoratorMedicao;
import br.pucpr.biblioteca.biblioteca.acervo.consulta.ServicoConsultaOriginal;
import br.pucpr.biblioteca.biblioteca.acervo.composite.UnidadeAcervo;
import br.pucpr.biblioteca.cadastro.CadastroAtendente;
import br.pucpr.biblioteca.cadastro.CadastroEstudante;
import br.pucpr.biblioteca.cadastro.CadastroLivro;
import br.pucpr.biblioteca.cadastro.CadastroPessoa;
import br.pucpr.biblioteca.circulacao.comando.ExecutorComando;
import br.pucpr.biblioteca.circulacao.facade.FachadaCirculacao;
import br.pucpr.biblioteca.comum.relogio.RelogioFixo;
import br.pucpr.biblioteca.devolucao.ouvintes.OuvinteEmail;
import br.pucpr.biblioteca.devolucao.ouvintes.OuvinteIndicadores;
import br.pucpr.biblioteca.devolucao.ouvintes.OuvinteInspecaoExemplar;
import br.pucpr.biblioteca.devolucao.ouvintes.OuvinteMulta;
import br.pucpr.biblioteca.devolucao.publicador.PublicadorDevolucao;
import br.pucpr.biblioteca.financeiro.cobranca.adaptador.AdaptadorBancoA;
import br.pucpr.biblioteca.financeiro.cobranca.bancossimulados.BancoASimulado;
import br.pucpr.biblioteca.financeiro.cobranca.contrato.ServicoCobranca;
import br.pucpr.biblioteca.financeiro.multa.EmissorMulta;
import br.pucpr.biblioteca.persistencia.fabrica.FabricaPersistencia;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioLivro;
import br.pucpr.biblioteca.persistencia.fabrica.UnidadeDeTrabalho;
import br.pucpr.biblioteca.persistencia.memoria.FabricaPersistenciaMemoria;
import br.pucpr.biblioteca.rh.atendente.Atendente;
import br.pucpr.biblioteca.rh.pessoa.Pessoa;
import br.pucpr.biblioteca.tratadores.TratadorDeAcervo;
import br.pucpr.biblioteca.tratadores.TratadorDeAtendente;
import br.pucpr.biblioteca.tratadores.TratadorDeDevolucao;
import br.pucpr.biblioteca.tratadores.TratadorDeEmprestimo;
import br.pucpr.biblioteca.tratadores.TratadorDeEstudante;
import br.pucpr.biblioteca.tratadores.TratadorDeMulta;
import br.pucpr.biblioteca.tratadores.TratadorDePessoa;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class ConsoleBibliotecaTest {
    @Test
    void consultasEListagensMostramDadosCompletosEFluxoConcluiComMulta() {
        FabricaPersistencia fabrica = new FabricaPersistenciaMemoria();
        RepositorioLivro repositorioLivro = new RepositorioLivroDeTeste(fabrica);
        RepositorioCadastroMemoria<Pessoa> pessoas = new RepositorioCadastroMemoria<>(Pessoa.class);
        RepositorioCadastroMemoria<Estudante> estudantes =
                new RepositorioCadastroMemoria<>(Estudante.class);
        RepositorioCadastroMemoria<Atendente> atendentes =
                new RepositorioCadastroMemoria<>(Atendente.class);
        pessoas.salvar(new Pessoa(
                "P-CONSULTA", "Nome Pessoa", "11111111111", "pessoa@example.com",
                LocalDate.of(1980, 2, 3)));
        estudantes.salvar(new Estudante(
                "E-CONSULTA", "Nome Estudante", "22222222222", "estudante@example.com",
                "MAT-CONSULTA", "Biblioteconomia", 4, LocalDate.of(2000, 4, 5)));
        atendentes.salvar(new Atendente(
                "A-CONSULTA", "Nome Atendente", "33333333333", "atendente@example.com",
                "Tarde", "RF-CONSULTA", LocalDate.of(1990, 6, 7)));
        EmissorMulta emissor = new EmissorMulta();
        ServicoCobranca cobranca = new ServicoCobranca(
                new AdaptadorBancoA(new BancoASimulado()));
        PublicadorDevolucao publicador = new PublicadorDevolucao();
        OuvinteMulta ouvinteMulta = new OuvinteMulta(emissor, cobranca);
        publicador.adicionar(new OuvinteInspecaoExemplar());
        publicador.adicionar(ouvinteMulta);
        publicador.adicionar(new OuvinteIndicadores());
        publicador.adicionar(new OuvinteEmail(evento -> { }));
        FachadaCirculacao fachada = new FachadaCirculacao(
                fabrica,
                new RelogioFixo(LocalDate.now(), Instant.now()),
                publicador);
        DecoratorMedicao medicao = new DecoratorMedicao(
                new ServicoConsultaOriginal(repositorioLivro));
        UnidadeAcervo unidadeAcervo = new UnidadeAcervo("ACERVO", "Acervo");
        TratadorDeAcervo tratadorAcervo = new TratadorDeAcervo(
                new CadastroLivro(repositorioLivro),
                medicao,
                unidadeAcervo);
        tratadorAcervo.cadastrar("L-CONSULTA", "Livro para consulta");
        String livroReservado = tratadorAcervo.cadastrar("Livro reservado para listagem")
                .substring("Livro cadastrado: ".length());
        fachada.solicitarReserva("R-TABELA", livroReservado, "E-TABELA");
        ConsoleBiblioteca console = new ConsoleBiblioteca(
                tratadorAcervo,
                new TratadorDePessoa<>(new CadastroPessoa<>(pessoas)),
                new TratadorDeEstudante<>(
                        new CadastroEstudante<>(estudantes, pessoas, Estudante.class)),
                new TratadorDeAtendente<>(
                        new CadastroAtendente<>(atendentes, pessoas, Atendente.class)),
                new TratadorDeEmprestimo(fachada, new ExecutorComando()),
                new TratadorDeDevolucao(fachada),
                new TratadorDeMulta(emissor),
                new Scanner("20\nCAT-FIC\n\nFiccao\n"
                        + "20\nCAT-FANT\nCAT-FIC\nFantasia\n"
                        + "21\nL-CONSULTA\nCAT-FANT\n"
                        + "22\nCAT-FIC\n23\nCAT-FIC\n24\n"
                        + "5\nP-CONSULTA\n6\nE-CONSULTA\n7\nA-CONSULTA\n"
                        + "8\nL-CONSULTA\nD\n14\n15\n16\n17\n18\n19\n0\n"),
                0);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream saidaOriginal = System.out;

        try (PrintStream captura = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            System.setOut(captura);
            console.iniciar();
        } finally {
            System.setOut(saidaOriginal);
        }

        String saida = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(saida.contains("Nome=Nome Pessoa"));
        assertTrue(saida.contains("CPF=11111111111"));
        assertTrue(saida.contains("  Resultado:" + System.lineSeparator()
                + "    Pessoa encontrada:" + System.lineSeparator()
                + "      ID=P-CONSULTA" + System.lineSeparator()
                + "      Nome=Nome Pessoa"));
        assertTrue(saida.contains("Data de nascimento=1980-02-03"));
        assertTrue(saida.contains("Matricula=MAT-CONSULTA"));
        assertTrue(saida.contains("Curso=Biblioteconomia"));
        assertTrue(saida.contains("Limite de emprestimos=4"));
        assertTrue(saida.contains("Turno=Tarde"));
        assertTrue(saida.contains("Registro funcional=RF-CONSULTA"));
        assertTrue(saida.contains("ID=L-CONSULTA"));
        assertTrue(saida.contains("Titulo=Livro para consulta"));
        assertTrue(saida.contains("Situacao=LIVRE"));
        assertTrue(saida.contains("Reservado para=-"));
        assertTrue(saida.contains("Data de devolucao=-"));
        for (int etapa = 1; etapa <= 13; etapa++) {
            assertTrue(saida.contains("[" + etapa + "/13]"), "Etapa " + etapa + " nao exibida");
        }
        assertTrue(saida.contains("DEMONSTRACAO CONCLUIDA"));
        assertTrue(saida.contains("Devolucao registrada"));
        assertTrue(saida.contains("Reservado para"));
        assertTrue(saida.contains("Data de devolucao"));
        assertTrue(saida.contains("Pessoa demonstracao"));
        assertTrue(saida.contains("MAT-DEMO-"));
        assertTrue(saida.contains("Pessoa atendente demonstracao"));
        assertTrue(saida.contains("RF-DEMO-"));
        assertTrue(saida.contains("Livro demonstracao"));
        assertTrue(saida.contains("R-TABELA"));
        assertTrue(saida.contains("E-TABELA"));
        assertTrue(saida.contains("Livro reservado para listagem"));
        assertTrue(saida.contains("ID do estudante"));
        assertTrue(saida.contains("T-DEMO-"));
        assertTrue(saida.contains("Consulta imediata:" + System.lineSeparator()
                + "        Pessoa encontrada:" + System.lineSeparator() + "          ID="));
        assertTrue(saida.contains("Consulta imediata:" + System.lineSeparator()
                + "        Estudante encontrado:" + System.lineSeparator() + "          ID="));
        assertTrue(saida.contains("Consulta recursiva da categoria CAT-DEMO-"));
        assertEquals(13, saida.split("Consulta imediata:", -1).length - 1);
        assertTrue(saida.contains("Quantidade de livros: 1"));
        assertTrue(saida.contains("CAT-FIC - Ficcao"));
        assertTrue(saida.contains("CAT-FANT - Fantasia"));
        assertEquals(2, unidadeAcervo.contarElementos());
        assertEquals(12, medicao.quantidadeConsultas());
        assertEquals(1, ouvinteMulta.multas().size());
        assertTrue(saida.contains("Multa emitida: " + ouvinteMulta.multas().get(0).valor()));
    }

    private static final class RepositorioLivroDeTeste implements RepositorioLivro {
        private final FabricaPersistencia fabrica;

        private RepositorioLivroDeTeste(FabricaPersistencia fabrica) {
            this.fabrica = fabrica;
        }

        @Override
        public br.pucpr.biblioteca.biblioteca.livro.Livro salvar(
                br.pucpr.biblioteca.biblioteca.livro.Livro livro) {
            return executar(repo -> repo.salvar(livro), true);
        }

        @Override
        public Optional<br.pucpr.biblioteca.biblioteca.livro.Livro> buscarPorId(String id) {
            return executar(repo -> repo.buscarPorId(id), false);
        }

        @Override
        public List<br.pucpr.biblioteca.biblioteca.livro.Livro> listar() {
            return executar(RepositorioLivro::listar, false);
        }

        @Override
        public void remover(String id) {
            executar(repo -> {
                repo.remover(id);
                return null;
            }, true);
        }

        private <T> T executar(Function<RepositorioLivro, T> operacao, boolean gravacao) {
            UnidadeDeTrabalho unidade = fabrica.criarUnidadeDeTrabalho();
            try {
                T resultado = operacao.apply(fabrica.criarRepositorioLivro(unidade));
                if (gravacao) {
                    unidade.commit();
                } else {
                    unidade.rollback();
                }
                return resultado;
            } catch (RuntimeException excecao) {
                unidade.rollback();
                throw excecao;
            } finally {
                unidade.close();
            }
        }
    }
}
