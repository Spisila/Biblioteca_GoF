package br.pucpr.biblioteca.ui.swing;

import br.pucpr.biblioteca.biblioteca.acervo.consulta.MetadadosLivro;
import br.pucpr.biblioteca.tratadores.TratadorDeAcervo;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * Tela de cadastro, consulta e listagem de livros.
 */
public final class TelaLivrosPanel extends JPanel {
    private final TratadorDeAcervo acervo;
    private final JTextField id = new JTextField(15);
    private final JTextField titulo = new JTextField(25);
    private final JTextField idConsulta = new JTextField(15);
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Título", "Situação"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public TelaLivrosPanel(TratadorDeAcervo acervo) {
        this.acervo = Objects.requireNonNull(acervo);
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formulario = new JPanel(new GridBagLayout());
        adicionar(formulario, new JLabel("ID:"), 0, 0);
        adicionar(formulario, id, 1, 0);
        adicionar(formulario, new JLabel("Título:"), 0, 1);
        adicionar(formulario, titulo, 1, 1);

        JButton cadastrar = new JButton("Cadastrar");
        cadastrar.addActionListener(evento -> cadastrar());
        adicionar(formulario, cadastrar, 2, 0);

        JButton listar = new JButton("Listar");
        listar.addActionListener(evento -> atualizarLista());
        adicionar(formulario, listar, 2, 1);

        adicionar(formulario, new JLabel("Consultar ID:"), 0, 2);
        adicionar(formulario, idConsulta, 1, 2);
        JButton consultar = new JButton("Consultar");
        consultar.addActionListener(evento -> consultar());
        adicionar(formulario, consultar, 2, 2);

        add(formulario, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(modelo)), BorderLayout.CENTER);
    }

    private void cadastrar() {
        if (id.getText().isBlank() || titulo.getText().isBlank()) {
            erro("Informe o ID e o título do livro.");
            return;
        }
        informar(acervo.cadastrar(id.getText().trim(), titulo.getText().trim()));
        atualizarLista();
    }

    private void consultar() {
        if (idConsulta.getText().isBlank()) {
            erro("Informe o ID do livro.");
            return;
        }
        informar(acervo.consultar(idConsulta.getText().trim()));
    }

    private void atualizarLista() {
        modelo.setRowCount(0);
        List<MetadadosLivro> livros = acervo.listar();
        for (MetadadosLivro livro : livros) {
            modelo.addRow(new Object[]{livro.id(), livro.titulo(), livro.situacao()});
        }
    }

    private static void adicionar(
            JPanel painel, java.awt.Component componente, int x, int y) {
        GridBagConstraints contraints = new GridBagConstraints();
        contraints.gridx = x;
        contraints.gridy = y;
        contraints.anchor = GridBagConstraints.WEST;
        contraints.insets = new Insets(3, 3, 3, 3);
        contraints.fill = x == 1 ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;
        contraints.weightx = x == 1 ? 1 : 0;
        painel.add(componente, contraints);
    }

    private static void informar(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, "Biblioteca", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void erro(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
