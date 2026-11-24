package br.pucpr.biblioteca.ui;

import br.pucpr.biblioteca.tratadores.TratadorDeAcervo;
import br.pucpr.biblioteca.tratadores.TratadorDeAtendente;
import br.pucpr.biblioteca.tratadores.TratadorDeDevolucao;
import br.pucpr.biblioteca.tratadores.TratadorDeEmprestimo;
import br.pucpr.biblioteca.tratadores.TratadorDeEstudante;
import br.pucpr.biblioteca.tratadores.TratadorDeMulta;
import br.pucpr.biblioteca.tratadores.TratadorDePessoa;
import br.pucpr.biblioteca.academico.estudante.Estudante;
import br.pucpr.biblioteca.rh.atendente.Atendente;
import br.pucpr.biblioteca.rh.pessoa.Pessoa;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;

/**
 * Interface interativa da aplicação; todas as operações passam pelos tratadores.
 */
public final class ConsoleBiblioteca {
    private final TratadorDeAcervo acervo;
    private final TratadorDePessoa<Pessoa> pessoa;
    private final TratadorDeEstudante<Estudante> estudante;
    private final TratadorDeAtendente<Atendente> atendente;
    private final TratadorDeEmprestimo emprestimo;
    private final TratadorDeDevolucao devolucao;
    private final TratadorDeMulta multa;
    private final Scanner entrada;

    public ConsoleBiblioteca(
            TratadorDeAcervo acervo,
            TratadorDePessoa<Pessoa> pessoa,
            TratadorDeEstudante<Estudante> estudante,
            TratadorDeAtendente<Atendente> atendente,
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
                System.out.println(pessoa.consultar(ler("Id da pessoa: ")));
                break;
            case "6":
                System.out.println(estudante.consultar(ler("Id do estudante: ")));
                break;
            case "7":
                System.out.println(atendente.consultar(ler("Id do atendente: ")));
                break;
            case "8":
                System.out.println(acervo.consultar(ler("Id do livro: ")));
                break;
            case "9":
                reservar();
                break;
            case "10":
                System.out.println(emprestimo.realizar(
                        "T-" + UUID.randomUUID(), ler("Id do livro: "),
                        ler("Id do estudante: ")));
                break;
            case "11":
                renovar();
                break;
            case "12":
                devolver();
                break;
            case "13":
                System.out.println(multa.emitir(Integer.parseInt(ler("Dias de atraso: "))));
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
        try {
            System.out.println(pessoa.cadastrar(new Pessoa(
                    ler("Nome: "),
                    ler("CPF: "),
                    ler("E-mail: "),
                    LocalDate.parse(ler("Data de nascimento (AAAA-MM-DD): ")))));
        } catch (DateTimeParseException excecao) {
            System.out.println("Data de nascimento invalida.");
        }
    }

    private void cadastrarEstudante() {
        try {
            System.out.println(estudante.cadastrar(new Estudante(
                    ler("Nome: "),
                    ler("CPF: "),
                    ler("E-mail: "),
                    LocalDate.parse(ler("Data de nascimento (AAAA-MM-DD): ")),
                    ler("Numero de matricula: "),
                    ler("Curso: "),
                    Integer.parseInt(ler("Limite de emprestimos: ")))));
        } catch (DateTimeParseException excecao) {
            System.out.println("Data de nascimento invalida.");
        } catch (NumberFormatException excecao) {
            System.out.println("Limite de emprestimos invalido.");
        }
    }

    private void cadastrarAtendente() {
        try {
            System.out.println(atendente.cadastrar(new Atendente(
                    ler("Nome: "),
                    ler("CPF: "),
                    ler("E-mail: "),
                    LocalDate.parse(ler("Data de nascimento (AAAA-MM-DD): ")),
                    ler("Turno: "),
                    ler("Registro funcional: "))));
        } catch (DateTimeParseException excecao) {
            System.out.println("Data de nascimento invalida.");
        }
    }

    private void cadastrarLivro() {
        System.out.println(acervo.cadastrar(ler("Titulo: ")));
    }

    private void reservar() {
        System.out.println(emprestimo.reservar(
                "R-" + UUID.randomUUID(),
                ler("Id do livro: "),
                ler("Id do estudante: ")));
    }

    private void renovar() {
        boolean sucesso = emprestimo.renovarIdempotente(
                "CMD-" + UUID.randomUUID(),
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
        String pessoaCriada = pessoa.cadastrar(new Pessoa(
                "Pessoa demonstracao", "00000000000", "demo@example.com",
                LocalDate.of(1990, 1, 1)));
        String estudanteCriado = estudante.cadastrar(new Estudante(
                "Estudante demonstracao", "11111111111", "estudante@example.com",
                LocalDate.of(2000, 1, 1), "MAT-DEMO", "Sistemas", 5));
        String atendenteCriado = atendente.cadastrar(new Atendente(
                "Atendente demonstracao", "22222222222", "atendente@example.com",
                LocalDate.of(1985, 1, 1), "Integral", "RF-DEMO"));
        String livroCriado = acervo.cadastrar("Livro demonstracao");
        System.out.println(pessoaCriada);
        System.out.println(estudanteCriado);
        System.out.println(atendenteCriado);
        System.out.println(livroCriado);

        String idEstudante = idGerado(estudanteCriado);
        String idLivro = idGerado(livroCriado);
        String idTransacao = "T-DEMO-" + UUID.randomUUID();
        System.out.println(emprestimo.reservar(
                "R-DEMO-" + UUID.randomUUID(), idLivro, idEstudante));
        System.out.println(emprestimo.realizar(idTransacao, idLivro, idEstudante));
        System.out.println(emprestimo.renovarIdempotente(
                "CMD-DEMO-" + UUID.randomUUID(),
                "a-demo", idLivro, idTransacao, java.time.Instant.now()));
        System.out.println(emprestimo.renovarIdempotente(
                "CMD-DEMO-" + UUID.randomUUID(),
                "a-demo", idLivro, idTransacao, java.time.Instant.now()));
        System.out.println(devolucao.registrar(
                idLivro, idTransacao, LocalDate.now().plusDays(100))
                ? "Devolucao demonstrativa registrada."
                : "Devolucao demonstrativa recusada.");
    }

    private String idGerado(String mensagem) {
        int separador = mensagem.lastIndexOf(':');
        return separador >= 0 ? mensagem.substring(separador + 1).trim()
                : "";
    }

    private String ler(String mensagem) {
        System.out.print(mensagem);
        return entrada.nextLine().trim();
    }

    private void imprimirMenu() {
        System.out.println();
        System.out.println("=== Biblioteca GoF ===");
        System.out.println("--- Cadastros ---");
        System.out.println("1 - Cadastrar pessoa");
        System.out.println("2 - Cadastrar estudante");
        System.out.println("3 - Cadastrar atendente");
        System.out.println("4 - Cadastrar livro");
        System.out.println("--- Consultas ---");
        System.out.println("5 - Consultar pessoa");
        System.out.println("6 - Consultar estudante");
        System.out.println("7 - Consultar atendente");
        System.out.println("8 - Consultar livro");
        System.out.println("--- Circulacao ---");
        System.out.println("9 - Solicitar reserva");
        System.out.println("10 - Efetivar emprestimo");
        System.out.println("11 - Renovar emprestimo (Command idempotente)");
        System.out.println("12 - Registrar devolucao");
        System.out.println("13 - Emitir multa");
        System.out.println("D - Executar fluxo demonstrativo");
        System.out.println("0 - Sair");
        System.out.print("Opcao: ");
    }
}
