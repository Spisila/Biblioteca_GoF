package br.pucpr.biblioteca.biblioteca.livro;

import br.pucpr.biblioteca.biblioteca.livro.estado.EstadoLivro;
import br.pucpr.biblioteca.biblioteca.livro.estado.EstadosLivro;
import br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Contexto do ciclo de vida de um livro.
 *
 * <p>Padrão: R01 State — papel: Contexto.</p>
 */
public final class Livro {
    private final String id;
    private final String titulo;
    private EstadoLivro estado;
    private String reservadoPara;
    private LocalDate dataDevolucao;

    public Livro(String id, String titulo) {
        this.id = validarTexto(id, "id");
        this.titulo = validarTexto(titulo, "titulo");
        this.estado = EstadosLivro.de(SituacaoLivro.LIVRE);
    }

    public static Livro reconstituir(
            String id,
            String titulo,
            SituacaoLivro situacao,
            String reservadoPara,
            LocalDate dataDevolucao) {
        Livro livro = new Livro(id, titulo);
        livro.estado = EstadosLivro.de(Objects.requireNonNull(situacao, "situacao nao pode ser nula"));
        livro.reservadoPara = reservadoPara;
        livro.dataDevolucao = dataDevolucao;
        return livro;
    }

    public SituacaoLivro situacao() {
        return estado.situacao();
    }

    public void solicitarReserva(String idEstudante) {
        estado.solicitarReserva(this, idEstudante);
    }

    public void realizarEmprestimo(Emprestimo emprestimo) {
        estado.realizarEmprestimo(this, emprestimo);
    }

    public void renovarEmprestimo(Emprestimo emprestimo) {
        estado.renovarEmprestimo(this, emprestimo);
    }

    public void registrarDevolucao(Emprestimo emprestimo, LocalDate dataDevolucao) {
        estado.registrarDevolucao(this, emprestimo, dataDevolucao);
    }

    public void inspecionarLivro(ResultadoInspecao resultado) {
        estado.inspecionarLivro(this, resultado);
    }

    public void enviarParaReparo(boolean danoIdentificado) {
        estado.enviarParaReparo(this, danoIdentificado);
    }

    public void retornarDoReparo() {
        estado.retornarDoReparo(this);
    }

    public void registrarLaudoIrrecuperavel() {
        estado.registrarLaudoIrrecuperavel(this);
    }

    public void excluirLivro() {
        estado.excluirLivro(this);
    }

    /**
     * Usado somente pelos estados para alterar o estado corrente.
     */
    public void transitarPara(EstadoLivro estado) {
        this.estado = Objects.requireNonNull(estado, "estado nao pode ser nulo");
    }

    /**
     * Usado somente pelos estados para consultar a reserva corrente.
     */
    public String reservadoPara() {
        return reservadoPara;
    }

    /**
     * Usado somente pelos estados para registrar ou limpar uma reserva.
     */
    public void definirReservadoPara(String reservadoPara) {
        this.reservadoPara = reservadoPara;
    }

    /**
     * Usado somente pelos estados para registrar a data de devolucao.
     */
    public void definirDataDevolucao(LocalDate dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public String id() {
        return id;
    }

    public String titulo() {
        return titulo;
    }

    public LocalDate dataDevolucao() {
        return dataDevolucao;
    }

    private static String validarTexto(String valor, String nome) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(nome + " nao pode ser vazio");
        }
        return valor;
    }
}
