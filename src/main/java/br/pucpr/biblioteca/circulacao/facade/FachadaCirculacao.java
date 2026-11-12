package br.pucpr.biblioteca.circulacao.facade;

import br.pucpr.biblioteca.biblioteca.livro.Livro;
import br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo;
import br.pucpr.biblioteca.comum.excecoes.NaoEncontradoException;
import br.pucpr.biblioteca.comum.relogio.Relogio;
import br.pucpr.biblioteca.persistencia.fabrica.FabricaPersistencia;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioLivro;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioMovimentacao;
import br.pucpr.biblioteca.persistencia.fabrica.RepositorioRelacionamento;
import br.pucpr.biblioteca.persistencia.fabrica.UnidadeDeTrabalho;
import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Function;

/**
 * Entrada única para operações transacionais de circulação.
 *
 * <p>Padrão: R04 Facade — papel: Fachada.</p>
 */
public final class FachadaCirculacao {
    private final FabricaPersistencia fabricaPersistencia;
    private final Relogio relogio;

    public FachadaCirculacao(
            FabricaPersistencia fabricaPersistencia,
            Relogio relogio) {
        this.fabricaPersistencia = Objects.requireNonNull(
                fabricaPersistencia, "fabricaPersistencia nao pode ser nula");
        this.relogio = Objects.requireNonNull(relogio, "relogio nao pode ser nulo");
    }

    public FachadaCirculacao(FabricaPersistencia fabricaPersistencia) {
        this(fabricaPersistencia, new br.pucpr.biblioteca.comum.relogio.RelogioSistema());
    }

    /**
     * Realiza o empréstimo de um livro reservado pelo estudante informado.
     */
    public Emprestimo realizarEmprestimo(
            String idTransacao,
            String idLivro,
            String idEstudante) {
        return realizarEmprestimo(idTransacao, idLivro, idEstudante, relogio.hoje());
    }

    /**
     * Variante que permite ao chamador informar a data do empréstimo.
     */
    public Emprestimo realizarEmprestimo(
            String idTransacao,
            String idLivro,
            String idEstudante,
            LocalDate dataEmprestimo) {
        return emTransacao(repositorios -> {
            Livro livro = buscarLivro(repositorios.livros(), idLivro);
            repositorios.relacionamentos().buscarReservaAtiva(idLivro)
                    .orElseThrow(() -> new NaoEncontradoException(
                            "Reserva ativa nao encontrada para o livro " + idLivro));
            Emprestimo emprestimo = new Emprestimo(
                    idTransacao, idLivro, idEstudante, dataEmprestimo);
            livro.realizarEmprestimo(emprestimo);
            repositorios.relacionamentos().removerReserva(idLivro);
            repositorios.livros().salvar(livro);
            repositorios.movimentacoes().salvar(emprestimo);
            return emprestimo;
        });
    }

    /**
     * Renova o empréstimo atualmente associado ao livro.
     */
    public Emprestimo registrarRenovacao(String idLivro, String idTransacao) {
        return emTransacao(repositorios -> {
            Livro livro = buscarLivro(repositorios.livros(), idLivro);
            Emprestimo emprestimo = buscarEmprestimo(repositorios.movimentacoes(), idTransacao);
            livro.renovarEmprestimo(emprestimo);
            repositorios.livros().salvar(livro);
            repositorios.movimentacoes().salvar(emprestimo);
            return emprestimo;
        });
    }

    public Emprestimo renovarEmprestimo(String idLivro, String idTransacao) {
        return registrarRenovacao(idLivro, idTransacao);
    }

    /**
     * Registra a devolução e persiste o novo estado do livro.
     */
    public Livro registrarDevolucao(
            String idLivro,
            String idTransacao,
            LocalDate dataDevolucao) {
        return emTransacao(repositorios -> {
            Livro livro = buscarLivro(repositorios.livros(), idLivro);
            Emprestimo emprestimo = buscarEmprestimo(repositorios.movimentacoes(), idTransacao);
            livro.registrarDevolucao(emprestimo, dataDevolucao);
            repositorios.livros().salvar(livro);
            return livro;
        });
    }

    public Livro registrarDevolucao(String idLivro, String idTransacao) {
        return registrarDevolucao(idLivro, idTransacao, relogio.hoje());
    }

    private <T> T emTransacao(
            Function<Repositorios, T> operacao) {
        UnidadeDeTrabalho unidade = fabricaPersistencia.criarUnidadeDeTrabalho();
        Repositorios repositorios = new Repositorios(
                fabricaPersistencia.criarRepositorioLivro(unidade),
                fabricaPersistencia.criarRepositorioMovimentacao(unidade),
                fabricaPersistencia.criarRepositorioRelacionamento(unidade));
        try {
            T resultado = operacao.apply(repositorios);
            unidade.commit();
            return resultado;
        } catch (RuntimeException excecao) {
            unidade.rollback();
            throw excecao;
        } finally {
            unidade.close();
        }
    }

    private static Livro buscarLivro(RepositorioLivro repositorio, String idLivro) {
        return repositorio.buscarPorId(idLivro)
                .orElseThrow(() -> new NaoEncontradoException("Livro nao encontrado: " + idLivro));
    }

    private static Emprestimo buscarEmprestimo(
            RepositorioMovimentacao repositorio,
            String idTransacao) {
        return repositorio.buscarPorId(idTransacao)
                .filter(Emprestimo.class::isInstance)
                .map(Emprestimo.class::cast)
                .orElseThrow(() -> new NaoEncontradoException(
                        "Emprestimo nao encontrado: " + idTransacao));
    }

    private record Repositorios(
            RepositorioLivro livros,
            RepositorioMovimentacao movimentacoes,
            RepositorioRelacionamento relacionamentos) {
    }
}
