package br.pucpr.biblioteca.ui.swing;

import br.pucpr.biblioteca.tratadores.TratadorDeDevolucao;
import br.pucpr.biblioteca.tratadores.TratadorDeEmprestimo;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 * Tela para reservas, empréstimos, renovações e devoluções.
 */
public final class TelaCirculacaoPanel extends JPanel {
    private final TratadorDeEmprestimo emprestimo;
    private final TratadorDeDevolucao devolucao;

    public TelaCirculacaoPanel(
            TratadorDeEmprestimo emprestimo,
            TratadorDeDevolucao devolucao) {
        this.emprestimo = Objects.requireNonNull(emprestimo);
        this.devolucao = Objects.requireNonNull(devolucao);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Reserva e empréstimo", painelEmprestimo());
        abas.addTab("Renovação", painelRenovacao());
        abas.addTab("Devolução", painelDevolucao());
        add(abas, BorderLayout.CENTER);
    }

    private JPanel painelEmprestimo() {
        JTextField idReserva = campo();
        JTextField idLivro = campo();
        JTextField idEstudante = campo();
        JTextField idTransacao = campo();
        JPanel painel = new JPanel(new GridLayout(0, 2, 6, 6));
        adicionar(painel, "ID reserva", idReserva);
        adicionar(painel, "ID livro", idLivro);
        adicionar(painel, "ID estudante", idEstudante);
        JButton reservar = new JButton("Solicitar reserva");
        reservar.addActionListener(evento -> informar(emprestimo.reservar(
                valor(idReserva), valor(idLivro), valor(idEstudante))));
        painel.add(reservar);
        painel.add(new JLabel());
        adicionar(painel, "ID transação", idTransacao);
        JButton realizar = new JButton("Efetivar empréstimo");
        realizar.addActionListener(evento -> informar(emprestimo.realizar(
                valor(idTransacao), valor(idLivro), valor(idEstudante))));
        painel.add(realizar);
        painel.add(new JLabel());
        return painel;
    }

    private JPanel painelRenovacao() {
        JTextField idComando = campo();
        JTextField autor = campo();
        JTextField idLivro = campo();
        JTextField idTransacao = campo();
        JPanel painel = new JPanel(new GridLayout(0, 2, 6, 6));
        adicionar(painel, "ID do comando", idComando);
        adicionar(painel, "Autor", autor);
        adicionar(painel, "ID livro", idLivro);
        adicionar(painel, "ID transação", idTransacao);
        JButton renovar = new JButton("Renovar (idempotente)");
        renovar.addActionListener(evento -> informar(
                emprestimo.renovarIdempotente(
                        valor(idComando), valor(autor), valor(idLivro), valor(idTransacao),
                        java.time.Instant.now())
                        ? "Renovação realizada."
                        : "Renovação recusada."));
        painel.add(renovar);
        painel.add(new JLabel());
        return painel;
    }

    private JPanel painelDevolucao() {
        JTextField idLivro = campo();
        JTextField idTransacao = campo();
        JTextField data = campo();
        JPanel painel = new JPanel(new GridLayout(0, 2, 6, 6));
        adicionar(painel, "ID livro", idLivro);
        adicionar(painel, "ID transação", idTransacao);
        adicionar(painel, "Data (AAAA-MM-DD)", data);
        JButton devolver = new JButton("Registrar devolução");
        devolver.addActionListener(evento -> registrarDevolucao(
                valor(idLivro), valor(idTransacao), valor(data)));
        painel.add(devolver);
        painel.add(new JLabel());
        return painel;
    }

    private void registrarDevolucao(String idLivro, String idTransacao, String data) {
        try {
            boolean sucesso = devolucao.registrar(
                    idLivro, idTransacao, LocalDate.parse(data));
            informar(sucesso
                    ? "Devolução registrada; multas e ouvintes processados."
                    : "Devolução recusada.");
        } catch (DateTimeParseException excecao) {
            erro("Informe a data no formato AAAA-MM-DD.");
        }
    }

    private static void adicionar(JPanel painel, String rotulo, JTextField campo) {
        painel.add(new JLabel(rotulo + ":"));
        painel.add(campo);
    }

    private static JTextField campo() {
        return new JTextField(20);
    }

    private static String valor(JTextField campo) {
        return campo.getText().trim();
    }

    private static void informar(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, "Biblioteca", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void erro(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
