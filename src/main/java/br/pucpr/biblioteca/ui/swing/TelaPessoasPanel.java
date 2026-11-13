package br.pucpr.biblioteca.ui.swing;

import br.pucpr.biblioteca.academico.estudante.Estudante;
import br.pucpr.biblioteca.rh.atendente.Atendente;
import br.pucpr.biblioteca.rh.pessoa.Pessoa;
import br.pucpr.biblioteca.tratadores.TratadorDeAtendente;
import br.pucpr.biblioteca.tratadores.TratadorDeEstudante;
import br.pucpr.biblioteca.tratadores.TratadorDePessoa;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Tela de cadastro e consulta de pessoas, estudantes e atendentes.
 */
public final class TelaPessoasPanel extends JPanel {
    private final TratadorDePessoa<Pessoa> pessoa;
    private final TratadorDeEstudante<Estudante> estudante;
    private final TratadorDeAtendente<Atendente> atendente;

    public TelaPessoasPanel(
            TratadorDePessoa<?> pessoa,
            TratadorDeEstudante<?> estudante,
            TratadorDeAtendente<?> atendente) {
        this.pessoa = pessoaTipada(pessoa);
        this.estudante = estudanteTipado(estudante);
        this.atendente = atendenteTipado(atendente);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        javax.swing.JTabbedPane abas = new javax.swing.JTabbedPane();
        abas.addTab("Pessoa", formularioPessoa());
        abas.addTab("Estudante", formularioEstudante());
        abas.addTab("Atendente", formularioAtendente());
        add(abas, BorderLayout.CENTER);
    }

    private JPanel formularioPessoa() {
        JTextField id = campo();
        JTextField nome = campo();
        JTextField cpf = campo();
        JTextField email = campo();
        JPanel painel = formulario(
                new String[]{"ID", "Nome", "CPF", "E-mail"},
                new JTextField[]{id, nome, cpf, email});
        JButton cadastrar = new JButton("Cadastrar pessoa");
        cadastrar.addActionListener(evento -> {
            try {
                informar(pessoa.cadastrar(new Pessoa(
                        valor(id), valor(nome), valor(cpf), valor(email))));
            } catch (IllegalArgumentException excecao) {
                erro(excecao.getMessage());
            }
        });
        painel.add(cadastrar);
        adicionarConsulta(painel, pessoa::consultar);
        return painel;
    }

    private JPanel formularioEstudante() {
        JTextField id = campo();
        JTextField nome = campo();
        JTextField cpf = campo();
        JTextField email = campo();
        JTextField matricula = campo();
        JTextField curso = campo();
        JTextField limite = campo();
        JPanel painel = formulario(
                new String[]{"ID", "Nome", "CPF", "E-mail", "Matrícula", "Curso", "Limite"},
                new JTextField[]{id, nome, cpf, email, matricula, curso, limite});
        JButton cadastrar = new JButton("Cadastrar estudante");
        cadastrar.addActionListener(evento -> {
            try {
                informar(estudante.cadastrar(new Estudante(
                        valor(id), valor(nome), valor(cpf), valor(email),
                        valor(matricula), valor(curso), Integer.parseInt(valor(limite)))));
            } catch (IllegalArgumentException excecao) {
                erro(excecao.getMessage());
            }
        });
        painel.add(cadastrar);
        adicionarConsulta(painel, estudante::consultar);
        return painel;
    }

    private JPanel formularioAtendente() {
        JTextField id = campo();
        JTextField nome = campo();
        JTextField cpf = campo();
        JTextField email = campo();
        JTextField turno = campo();
        JTextField registro = campo();
        JPanel painel = formulario(
                new String[]{"ID", "Nome", "CPF", "E-mail", "Turno", "Registro funcional"},
                new JTextField[]{id, nome, cpf, email, turno, registro});
        JButton cadastrar = new JButton("Cadastrar atendente");
        cadastrar.addActionListener(evento -> {
            try {
                informar(atendente.cadastrar(new Atendente(
                        valor(id), valor(nome), valor(cpf), valor(email),
                        valor(turno), valor(registro))));
            } catch (IllegalArgumentException excecao) {
                erro(excecao.getMessage());
            }
        });
        painel.add(cadastrar);
        adicionarConsulta(painel, atendente::consultar);
        return painel;
    }

    private static JPanel formulario(String[] rotulos, JTextField[] campos) {
        JPanel painel = new JPanel(new GridLayout(0, 2, 6, 6));
        for (int indice = 0; indice < rotulos.length; indice++) {
            painel.add(new JLabel(rotulos[indice] + ":"));
            painel.add(campos[indice]);
        }
        return painel;
    }

    private static void adicionarConsulta(JPanel painel, java.util.function.Function<String, String> consulta) {
        JTextField id = campo();
        painel.add(new JLabel("Consultar ID:"));
        painel.add(id);
        JButton consultar = new JButton("Consultar");
        consultar.addActionListener(evento -> informar(consulta.apply(valor(id))));
        painel.add(consultar);
        painel.add(new JLabel());
    }

    private static JTextField campo() {
        return new JTextField(20);
    }

    private static String valor(JTextField campo) {
        return campo.getText().trim();
    }

    @SuppressWarnings("unchecked")
    private static TratadorDePessoa<Pessoa> pessoaTipada(TratadorDePessoa<?> tratador) {
        return (TratadorDePessoa<Pessoa>) Objects.requireNonNull(tratador);
    }

    @SuppressWarnings("unchecked")
    private static TratadorDeEstudante<Estudante> estudanteTipado(TratadorDeEstudante<?> tratador) {
        return (TratadorDeEstudante<Estudante>) Objects.requireNonNull(tratador);
    }

    @SuppressWarnings("unchecked")
    private static TratadorDeAtendente<Atendente> atendenteTipado(TratadorDeAtendente<?> tratador) {
        return (TratadorDeAtendente<Atendente>) Objects.requireNonNull(tratador);
    }

    private static void informar(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, "Biblioteca", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void erro(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
