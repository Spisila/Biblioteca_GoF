package br.pucpr.biblioteca.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import br.pucpr.biblioteca.academico.estudante.Estudante;
import br.pucpr.biblioteca.app.RepositorioCadastroMemoria;
import br.pucpr.biblioteca.biblioteca.acervo.consulta.ServicoConsultaOriginal;
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
    void demonstracaoMostraCadaEtapaEConcluiFluxoComMulta() {
        FabricaPersistencia fabrica = new FabricaPersistenciaMemoria();
        RepositorioLivro repositorioLivro = new RepositorioLivroDeTeste(fabrica);
        RepositorioCadastroMemoria<Pessoa> pessoas = new RepositorioCadastroMemoria<>();
        RepositorioCadastroMemoria<Estudante> estudantes = new RepositorioCadastroMemoria<>();
        RepositorioCadastroMemoria<Atendente> atendentes = new RepositorioCadastroMemoria<>();
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
        TratadorDeAcervo tratadorAcervo = new TratadorDeAcervo(
                new CadastroLivro(repositorioLivro),
                new ServicoConsultaOriginal(repositorioLivro));
        ConsoleBiblioteca console = new ConsoleBiblioteca(
                tratadorAcervo,
                new TratadorDePessoa<>(new CadastroPessoa<>(pessoas)),
                new TratadorDeEstudante<>(new CadastroEstudante<>(estudantes, pessoas)),
                new TratadorDeAtendente<>(new CadastroAtendente<>(atendentes, pessoas)),
                new TratadorDeEmprestimo(fachada, new ExecutorComando()),
                new TratadorDeDevolucao(fachada),
                new TratadorDeMulta(emissor),
                new Scanner("D\n0\n"),
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
        for (int etapa = 1; etapa <= 10; etapa++) {
            assertTrue(saida.contains("[" + etapa + "/10]"), "Etapa " + etapa + " nao exibida");
        }
        assertTrue(saida.contains("DEMONSTRACAO CONCLUIDA"));
        assertTrue(saida.contains("Devolucao registrada"));
        assertTrue(!ouvinteMulta.multas().isEmpty());
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
