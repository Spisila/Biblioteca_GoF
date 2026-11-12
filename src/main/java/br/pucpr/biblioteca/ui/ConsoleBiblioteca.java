package br.pucpr.biblioteca.ui;

import br.pucpr.biblioteca.tratadores.TratadorDeAcervo;
import br.pucpr.biblioteca.tratadores.TratadorDeAtendente;
import br.pucpr.biblioteca.tratadores.TratadorDeDevolucao;
import br.pucpr.biblioteca.tratadores.TratadorDeEmprestimo;
import br.pucpr.biblioteca.tratadores.TratadorDeEstudante;
import br.pucpr.biblioteca.tratadores.TratadorDeMulta;
import br.pucpr.biblioteca.tratadores.TratadorDePessoa;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Scanner;

/**
 * Interface interativa da aplicação; todas as operações passam pelos tratadores.
 */
public final class ConsoleBiblioteca {
    private final TratadorDeAcervo acervo;
    private final TratadorDePessoa<RegistroConsole> pessoa;
    private final TratadorDeEstudante<RegistroConsole> estudante;
    private final TratadorDeAtendente<RegistroConsole> atendente;
    private final TratadorDeEmprestimo emprestimo;
    private final TratadorDeDevolucao devolucao;
    private final TratadorDeMulta multa;
    private final Scanner entrada;

    public ConsoleBiblioteca(
            TratadorDeAcervo acervo,
            TratadorDePessoa<RegistroConsole> pessoa,
            TratadorDeEstudante<RegistroConsole> estudante,
            TratadorDeAtendente<RegistroConsole> atendente,
            TratadorDeEmprestimo emprestimo,
            TratadorDeDevolucao devolucao,
            TratadorDeMulta multa,
            Scanner entrada) {
        this.acervo = Objects.requireNonNull(acervo);
        this.pessoa = Objects.requireNonNull(pessoa);
        this.estudante = Objects.requireNonNull(estudante);
        this.atendente = Objects.requireNonNull(atendente);
        this.emprestimo = Objects.requireNonNull(emprestimo);
        this.devolucao = Objects.requireNonNull(devolucao);
        this.multa = Objects.requireNonNull(multa);
        this.entrada = Objects.requireNonNull(entrada);
    }

    public void iniciar() {
        boolean continuar = true;
        while (continuar) {
            imprimirMenu();
            if (!entrada.hasNextLine()) {
                return;
            }
            String opcao = entrada.nextLine().trim();
            try {
                continuar = executar(opcao);
            } catch (RuntimeException excecao) {
                System.out.println("Entrada invalida: " + excecao.getMessage());
            }
        }
    }

    private boolean executar(String opcao) {
        switch (opcao) {
            case "1":
                cadastrarPessoa();
                break;
            case "2":
                cadastrarEstudante();
                break;
            case "3":
                cadastrarAtendente();
                break;
            case "4":
                cadastrarLivro();
                break;
            case "5":
                System.out.println(acervo.consultar(ler("Id do livro: ")));
                break;
            case "12":
                System.out.println(pessoa.consultar(ler("Id da pessoa: ")));
                break;
            case "13":
                System.out.println(estudante.consultar(ler("Id do estudante: ")));
                break;
            case "14":
                System.out.println(atendente.consultar(ler("Id do atendente: ")));
                break;
            case "6":
                System.out.println(emprestimo.reservar(
                        ler("Id da reserva: "), ler("Id do livro: "), ler("Id do estudante: ")));
                break;
            case "7":
                System.out.println(emprestimo.realizar(
                        ler("Id do emprestimo: "), ler("Id do livro: "), ler("Id do estudante: ")));
                break;
            case "8":
                renovar();
                break;
            case "9":
                devolver();
                break;
            case "10":
                System.out.println(multa.emitir(Integer.parseInt(ler("Dias de atraso: "))));
                break;
            case "11":
                System.out.println(acervo.listarComoTexto());
                break;
            case "D":
            case "d":
                demonstrarFluxo();
                break;
            case "0":
                return false;
            default:
                System.out.println("Opcao inexistente.");
        }
        return true;
    }

    private void cadastrarPessoa() {
        System.out.println(pessoa.cadastrar(new RegistroConsole(
                ler("Id da pessoa: "), ler("Descricao: "))));
    }

    private void cadastrarEstudante() {
        System.out.println(estudante.cadastrar(new RegistroConsole(
                ler("Id do estudante: "), ler("Descricao: "))));
    }

    private void cadastrarAtendente() {
        System.out.println(atendente.cadastrar(new RegistroConsole(
                ler("Id do atendente: "), ler("Descricao: "))));
    }

    private void cadastrarLivro() {
        System.out.println(acervo.cadastrar(ler("Id do livro: "), ler("Titulo: ")));
    }

    private void renovar() {
        String idComando = ler("Id unico do comando: ");
        boolean sucesso = emprestimo.renovarIdempotente(
                idComando,
                ler("Autor: "),
                ler("Id do livro: "),
                ler("Id do emprestimo: "),
                java.time.Instant.now());
        System.out.println(sucesso ? "Renovacao realizada." : "Renovacao recusada.");
    }

    private void devolver() {
        String data = ler("Data da devolucao (AAAA-MM-DD): ");
        try {
            boolean sucesso = devolucao.registrar(
                    ler("Id do livro: "),
                    ler("Id do emprestimo: "),
                    LocalDate.parse(data));
            System.out.println(sucesso
                    ? "Devolucao registrada; ouvintes notificados."
                    : "Devolucao recusada.");
        } catch (DateTimeParseException excecao) {
            System.out.println("Data invalida.");
        }
    }

    private void demonstrarFluxo() {
        System.out.println(pessoa.cadastrar(new RegistroConsole("p-demo", "Pessoa demonstracao")));
        System.out.println(estudante.cadastrar(new RegistroConsole("e-demo", "Estudante demonstracao")));
        System.out.println(atendente.cadastrar(new RegistroConsole("a-demo", "Atendente demonstracao")));
        System.out.println(acervo.cadastrar("l-demo", "Livro demonstracao"));
        System.out.println(emprestimo.reservar("r-demo", "l-demo", "e-demo"));
        System.out.println(emprestimo.realizar("t-demo", "l-demo", "e-demo"));
        System.out.println(emprestimo.renovarIdempotente(
                "cmd-demo", "a-demo", "l-demo", "t-demo", java.time.Instant.now()));
        System.out.println(emprestimo.renovarIdempotente(
                "cmd-demo", "a-demo", "l-demo", "t-demo", java.time.Instant.now()));
        System.out.println(devolucao.registrar(
                "l-demo", "t-demo", LocalDate.now().plusDays(100))
                ? "Devolucao demonstrativa registrada."
                : "Devolucao demonstrativa recusada.");
    }

    private String ler(String mensagem) {
        System.out.print(mensagem);
        return entrada.nextLine().trim();
    }

    private void imprimirMenu() {
        System.out.println();
        System.out.println("=== Biblioteca GoF ===");
        System.out.println("1 - Cadastrar pessoa");
        System.out.println("2 - Cadastrar estudante");
        System.out.println("3 - Cadastrar atendente");
        System.out.println("4 - Cadastrar livro");
        System.out.println("5 - Consultar livro");
        System.out.println("6 - Solicitar reserva");
        System.out.println("7 - Efetivar emprestimo");
        System.out.println("8 - Renovar emprestimo (Command idempotente)");
        System.out.println("9 - Registrar devolucao");
        System.out.println("10 - Emitir multa");
        System.out.println("11 - Listar livros");
        System.out.println("12 - Consultar pessoa");
        System.out.println("13 - Consultar estudante");
        System.out.println("14 - Consultar atendente");
        System.out.println("D - Executar fluxo demonstrativo");
        System.out.println("0 - Sair");
        System.out.print("Opcao: ");
    }
}
