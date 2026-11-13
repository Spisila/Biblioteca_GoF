package br.pucpr.biblioteca.ui.swing;

import br.pucpr.biblioteca.tratadores.TratadorDeAcervo;
import br.pucpr.biblioteca.tratadores.TratadorDeAtendente;
import br.pucpr.biblioteca.tratadores.TratadorDeDevolucao;
import br.pucpr.biblioteca.tratadores.TratadorDeEmprestimo;
import br.pucpr.biblioteca.tratadores.TratadorDeEstudante;
import br.pucpr.biblioteca.tratadores.TratadorDePessoa;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

/**
 * Janela principal da interface gráfica da biblioteca.
 *
 * <p>A janela apenas compõe telas que delegam as operações aos tratadores.</p>
 */
public final class BibliotecaSwingFrame extends JFrame {
    public BibliotecaSwingFrame(
            TratadorDeAcervo acervo,
            TratadorDePessoa<?> pessoa,
            TratadorDeEstudante<?> estudante,
            TratadorDeAtendente<?> atendente,
            TratadorDeEmprestimo emprestimo,
            TratadorDeDevolucao devolucao) {
        super("Biblioteca GoF");
        Objects.requireNonNull(acervo);
        Objects.requireNonNull(pessoa);
        Objects.requireNonNull(estudante);
        Objects.requireNonNull(atendente);
        Objects.requireNonNull(emprestimo);
        Objects.requireNonNull(devolucao);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        setLayout(new BorderLayout(8, 8));

        JLabel titulo = new JLabel("Sistema de empréstimo de livros", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(titulo, BorderLayout.NORTH);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Livros", new TelaLivrosPanel(acervo));
        abas.addTab("Pessoas", new TelaPessoasPanel(pessoa, estudante, atendente));
        abas.addTab("Circulação", new TelaCirculacaoPanel(emprestimo, devolucao));
        add(abas, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }
}
