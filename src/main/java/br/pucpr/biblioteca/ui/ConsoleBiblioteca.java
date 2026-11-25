package br.pucpr.biblioteca.ui;

import br.pucpr.biblioteca.academico.estudante.Estudante;
import br.pucpr.biblioteca.biblioteca.acervo.consulta.MetadadosLivro;
import br.pucpr.biblioteca.biblioteca.movimentacao.Emprestimo;
import br.pucpr.biblioteca.biblioteca.movimentacao.Transacao;
import br.pucpr.biblioteca.biblioteca.relacionamento.Reserva;
import br.pucpr.biblioteca.rh.atendente.Atendente;
import br.pucpr.biblioteca.rh.pessoa.Pessoa;
import br.pucpr.biblioteca.tratadores.TratadorDeAcervo;
import br.pucpr.biblioteca.tratadores.TratadorDeAtendente;
import br.pucpr.biblioteca.tratadores.TratadorDeDevolucao;
import br.pucpr.biblioteca.tratadores.TratadorDeEmprestimo;
import br.pucpr.biblioteca.tratadores.TratadorDeEstudante;
import br.pucpr.biblioteca.tratadores.TratadorDeMulta;
import br.pucpr.biblioteca.tratadores.TratadorDePessoa;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;

/**
 * Interface interativa da aplicação; todas as operações passam pelos tratadores.
 */
public final class ConsoleBiblioteca {
    private static final long INTERVALO_DEMO_PADRAO_MS = 450;

    private final TratadorDeAcervo acervo;
    private final TratadorDePessoa<Pessoa> pessoa;
    private final TratadorDeEstudante<Estudante> estudante;
    private final TratadorDeAtendente<Atendente> atendente;
    private final TratadorDeEmprestimo emprestimo;
    private final TratadorDeDevolucao devolucao;
    private final TratadorDeMulta multa;
    private final Scanner entrada;
    private final long intervaloDemoMillis;

    public ConsoleBiblioteca(
            TratadorDeAcervo acervo,
            TratadorDePessoa<Pessoa> pessoa,
            TratadorDeEstudante<Estudante> estudante,
            TratadorDeAtendente<Atendente> atendente,
            TratadorDeEmprestimo emprestimo,
            TratadorDeDevolucao devolucao,
            TratadorDeMulta multa,
            Scanner entrada) {
        this(acervo, pessoa, estudante, atendente, emprestimo, devolucao, multa,
                entrada, INTERVALO_DEMO_PADRAO_MS);
    }

    public ConsoleBiblioteca(
            TratadorDeAcervo acervo,
            TratadorDePessoa<Pessoa> pessoa,
            TratadorDeEstudante<Estudante> estudante,
            TratadorDeAtendente<Atendente> atendente,
            TratadorDeEmprestimo emprestimo,
            TratadorDeDevolucao devolucao,
            TratadorDeMulta multa,
            Scanner entrada,
            long intervaloDemoMillis) {
        this.acervo = Objects.requireNonNull(acervo);
        this.pessoa = Objects.requireNonNull(pessoa);
        this.estudante = Objects.requireNonNull(estudante);
        this.atendente = Objects.requireNonNull(atendente);
        this.emprestimo = Objects.requireNonNull(emprestimo);
        this.devolucao = Objects.requireNonNull(devolucao);
        this.multa = Objects.requireNonNull(multa);
        this.entrada = Objects.requireNonNull(entrada);
        if (intervaloDemoMillis < 0) {
            throw new IllegalArgumentException("O intervalo da demonstracao nao pode ser negativo");
        }
        this.intervaloDemoMillis = intervaloDemoMillis;
    }

    public void iniciar() {
        imprimirBoasVindas();
        boolean continuar = true;
        while (continuar) {
            imprimirMenu();
            if (!entrada.hasNextLine()) {
                System.out.println("\nEntrada encerrada. Finalizando a biblioteca.");
                return;
            }
            String opcao = entrada.nextLine().trim();
            try {
                continuar = executar(opcao);
            } catch (RuntimeException excecao) {
                imprimirFalha("Operacao interrompida", excecao.getMessage());
            }
        }
        System.out.println("\nSessao encerrada. Obrigado por utilizar a Biblioteca GoF.");
    }

    private boolean executar(String opcao) {
        switch (opcao.toLowerCase()) {
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
                consultarPessoa();
                break;
            case "6":
                consultarEstudante();
                break;
            case "7":
                consultarAtendente();
                break;
            case "8":
                consultarLivro();
                break;
            case "9":
                reservar();
                break;
            case "10":
                realizarEmprestimo();
                break;
            case "11":
                renovar();
                break;
            case "12":
                devolver();
                break;
            case "13":
                emitirMulta();
                break;
            case "14":
                listarAcervo();
                break;
            case "15":
                listarPessoas();
                break;
            case "16":
                listarEstudantes();
                break;
            case "17":
                listarAtendentes();
                break;
            case "18":
                listarReservas();
                break;
            case "19":
                listarMovimentacoes();
                break;
            case "d":
                demonstrarFluxo();
                break;
            case "0":
            case "s":
                return false;
            default:
                imprimirFalha("Opcao nao reconhecida",
                        "Escolha um numero do menu, D para a demonstracao ou 0 para sair.");
        }
        return true;
    }

    private void cadastrarPessoa() {
        cabecalho("Cadastro de pessoa", "Registra os dados pessoais que podem ser associados "
                + "a um estudante ou atendente.");
        try {
            String resultado = pessoa.cadastrar(new Pessoa(
                    lerObrigatorio("Nome completo: "),
                    lerObrigatorio("CPF: "),
                    lerObrigatorio("E-mail: "),
                    lerData("Data de nascimento (AAAA-MM-DD): ")));
            imprimirResultado(resultado, "Pessoa cadastrada");
        } catch (IllegalArgumentException excecao) {
            imprimirFalha("Cadastro de pessoa nao concluido", excecao.getMessage());
        }
    }

    private void cadastrarEstudante() {
        cabecalho("Cadastro de estudante",
                "O CPF precisa pertencer a uma pessoa ja cadastrada.");
        try {
            String resultado = estudante.cadastrar(new Estudante(
                    lerObrigatorio("Nome completo: "),
                    lerObrigatorio("CPF da pessoa cadastrada: "),
                    lerObrigatorio("E-mail: "),
                    lerData("Data de nascimento (AAAA-MM-DD): "),
                    lerObrigatorio("Numero de matricula: "),
                    lerObrigatorio("Curso: "),
                    lerInteiro("Limite de emprestimos: ")));
            imprimirResultado(resultado, "Estudante cadastrado");
        } catch (IllegalArgumentException excecao) {
            imprimirFalha("Cadastro de estudante nao concluido", excecao.getMessage());
        }
    }

    private void cadastrarAtendente() {
        cabecalho("Cadastro de atendente",
                "O CPF precisa pertencer a uma pessoa ja cadastrada.");
        try {
            String resultado = atendente.cadastrar(new Atendente(
                    lerObrigatorio("Nome completo: "),
                    lerObrigatorio("CPF da pessoa cadastrada: "),
                    lerObrigatorio("E-mail: "),
                    lerData("Data de nascimento (AAAA-MM-DD): "),
                    lerObrigatorio("Turno: "),
                    lerObrigatorio("Registro funcional: ")));
            imprimirResultado(resultado, "Atendente cadastrado");
        } catch (IllegalArgumentException excecao) {
            imprimirFalha("Cadastro de atendente nao concluido", excecao.getMessage());
        }
    }

    private void cadastrarLivro() {
        cabecalho("Cadastro de livro",
                "O sistema gera o identificador e inicia o livro no estado LIVRE.");
        String resultado = acervo.cadastrar(lerObrigatorio("Titulo do livro: "));
        imprimirResultado(resultado, "Livro cadastrado");
    }

    private void consultarPessoa() {
        cabecalho("Consulta de pessoa", "Informe o identificador gerado no cadastro.");
        imprimirConsulta(pessoa.consultar(lerObrigatorio("Id da pessoa: ")));
    }

    private void consultarEstudante() {
        cabecalho("Consulta de estudante", "Informe o identificador associado a Pessoa.");
        imprimirConsulta(estudante.consultar(lerObrigatorio("Id do estudante: ")));
    }

    private void consultarAtendente() {
        cabecalho("Consulta de atendente", "Informe o identificador associado a Pessoa.");
        imprimirConsulta(atendente.consultar(lerObrigatorio("Id do atendente: ")));
    }

    private void consultarLivro() {
        cabecalho("Consulta de livro",
                "Mostra os metadados atuais do acervo; a reserva sempre valida o estado real.");
        imprimirConsulta(acervo.consultar(lerObrigatorio("Id do livro: ")));
    }

    private void listarAcervo() {
        cabecalho("Catalogo do acervo", "Lista todos os dados registrados para cada livro.");
        List<List<String>> linhas = new ArrayList<>();
        for (MetadadosLivro livro : acervo.listar()) {
            linhas.add(List.of(
                    livro.id(),
                    livro.titulo(),
                    livro.situacao().toString(),
                    texto(livro.reservadoPara()),
                    texto(livro.dataDevolucao())));
        }
        imprimirTabela(
                List.of("ID", "Titulo", "Situacao", "Reservado para", "Data de devolucao"),
                linhas,
                "Nenhum livro cadastrado.");
    }

    private void listarPessoas() {
        cabecalho("Pessoas cadastradas", "Lista os dados pessoais registrados.");
        List<List<String>> linhas = new ArrayList<>();
        for (Pessoa registro : pessoa.listar()) {
            linhas.add(List.of(
                    registro.id(),
                    registro.nome(),
                    registro.cpf(),
                    registro.email(),
                    registro.dataNascimento().toString()));
        }
        imprimirTabela(
                List.of("ID", "Nome", "CPF", "E-mail", "Nascimento"),
                linhas,
                "Nenhuma pessoa cadastrada.");
    }

    private void listarEstudantes() {
        cabecalho("Estudantes cadastrados", "Lista os dados pessoais e academicos.");
        List<List<String>> linhas = new ArrayList<>();
        for (Estudante registro : estudante.listar()) {
            linhas.add(List.of(
                    registro.id(),
                    registro.nome(),
                    registro.cpf(),
                    registro.email(),
                    registro.dataNascimento().toString(),
                    registro.numeroMatricula(),
                    registro.curso(),
                    Integer.toString(registro.limiteEmprestimos())));
        }
        imprimirTabela(
                List.of("ID", "Nome", "CPF", "E-mail", "Nascimento",
                        "Matricula", "Curso", "Limite"),
                linhas,
                "Nenhum estudante cadastrado.");
    }

    private void listarAtendentes() {
        cabecalho("Atendentes cadastrados", "Lista os dados pessoais e funcionais.");
        List<List<String>> linhas = new ArrayList<>();
        for (Atendente registro : atendente.listar()) {
            linhas.add(List.of(
                    registro.id(),
                    registro.nome(),
                    registro.cpf(),
                    registro.email(),
                    registro.dataNascimento().toString(),
                    registro.turno(),
                    registro.registroFuncional()));
        }
        imprimirTabela(
                List.of("ID", "Nome", "CPF", "E-mail", "Nascimento",
                        "Turno", "Registro funcional"),
                linhas,
                "Nenhum atendente cadastrado.");
    }

    private void listarReservas() {
        cabecalho("Reservas ativas", "Lista as reservas ainda registradas no sistema.");
        List<List<String>> linhas = new ArrayList<>();
        for (Reserva reserva : emprestimo.listarReservas()) {
            linhas.add(List.of(
                    reserva.id(),
                    reserva.idLivro(),
                    reserva.idEstudante(),
                    reserva.dataReserva().toString()));
        }
        imprimirTabela(
                List.of("ID", "ID do livro", "ID do estudante", "Data da reserva"),
                linhas,
                "Nenhuma reserva ativa.");
    }

    private void listarMovimentacoes() {
        cabecalho("Movimentacoes", "Lista todos os emprestimos registrados.");
        List<List<String>> linhas = new ArrayList<>();
        for (Transacao transacao : emprestimo.listarMovimentacoes()) {
            if (transacao instanceof Emprestimo registro) {
                linhas.add(List.of(
                        registro.idTransacao(),
                        "Emprestimo",
                        registro.idLivro(),
                        registro.idEstudante(),
                        registro.dataTransacao().toString(),
                        registro.dataVencimento().toString(),
                        Integer.toString(registro.numRenovacoes())));
            } else {
                linhas.add(List.of(
                        transacao.idTransacao(),
                        transacao.getClass().getSimpleName(),
                        "-",
                        "-",
                        transacao.dataTransacao().toString(),
                        "-",
                        "-"));
            }
        }
        imprimirTabela(
                List.of("ID", "Tipo", "ID do livro", "ID do estudante",
                        "Data", "Vencimento", "Renovacoes"),
                linhas,
                "Nenhuma movimentacao cadastrada.");
    }

    private void reservar() {
        cabecalho("Solicitacao de reserva",
                "A reserva e enviada como comando e o dominio valida a disponibilidade do livro.");
        String idLivro = lerObrigatorio("Id do livro: ");
        String idEstudante = lerObrigatorio("Id do estudante: ");
        String idReserva = "R-" + UUID.randomUUID();
        System.out.println("  Identificador da solicitacao: " + idReserva);
        imprimirResultado(emprestimo.reservar(idReserva, idLivro, idEstudante),
                "Reserva solicitada");
    }

    private void realizarEmprestimo() {
        cabecalho("Efetivacao de emprestimo",
                "E necessario existir uma reserva ativa do estudante para este livro.");
        String idLivro = lerObrigatorio("Id do livro: ");
        String idEstudante = lerObrigatorio("Id do estudante: ");
        String idTransacao = "T-" + UUID.randomUUID();
        System.out.println("  Identificador da transacao: " + idTransacao);
        imprimirResultado(emprestimo.realizar(idTransacao, idLivro, idEstudante),
                "Emprestimo efetivado");
    }

    private void renovar() {
        cabecalho("Renovacao de emprestimo",
                "O comando e idempotente: repetir seu identificador nao renova novamente.");
        String autor = lerObrigatorio("Autor da solicitacao: ");
        String idLivro = lerObrigatorio("Id do livro: ");
        String idTransacao = lerObrigatorio("Id do emprestimo: ");
        String idComando = "CMD-" + UUID.randomUUID();
        Instant instanteCriacao = Instant.now();
        System.out.println("  Comando: " + idComando + " | criado em " + instanteCriacao);
        imprimirResultado(emprestimo.renovarIdempotente(
                idComando, autor, idLivro, idTransacao, instanteCriacao)
                ? "Renovacao registrada para o emprestimo " + idTransacao + "."
                : "Renovacao recusada. Verifique o estado do livro e o limite de renovacoes.",
                "Renovacao");
    }

    private void devolver() {
        cabecalho("Registro de devolucao",
                "A devolucao e confirmada antes da notificacao dos ouvintes de inspecao e multa.");
        String idLivro = lerObrigatorio("Id do livro: ");
        String idTransacao = lerObrigatorio("Id do emprestimo: ");
        LocalDate data = lerData("Data da devolucao (AAAA-MM-DD): ");
        boolean sucesso = devolucao.registrar(idLivro, idTransacao, data);
        if (sucesso) {
            imprimirSucesso("Devolucao confirmada em " + data + ".");
            System.out.println("  O estado do exemplar aguarda inspecao; ouvintes foram notificados.");
            System.out.println("  Se houve atraso, o ouvinte financeiro calculou e emitiu a multa.");
        } else {
            imprimirFalha("Devolucao recusada",
                    "Confira os identificadores e se o emprestimo permite devolucao.");
        }
    }

    private void emitirMulta() {
        cabecalho("Emissao de multa",
                "O valor e calculado pela politica administrativa atualmente selecionada.");
        int dias = lerInteiro("Dias de atraso (0 ou mais): ");
        imprimirResultado(multa.emitir(dias), "Calculo da multa");
    }

    private void demonstrarFluxo() {
        secao("DEMONSTRACAO GUIADA", "Reserva -> emprestimo -> renovacoes -> devolucao atrasada");
        System.out.println("  Cada pausa separa as etapas para facilitar a leitura.");
        pausaDemonstracao();

        String cpfEstudante = cpfDemonstracao();
        String cpfAtendente = cpfDemonstracao();
        String resultadoPessoaEstudante = executarEtapa(1, 10,
                "Preparando o cadastro base do estudante",
                "Primeiro cadastramos a Pessoa que sera associada pelo CPF.",
                () -> pessoa.cadastrar(new Pessoa(
                        "Pessoa demonstracao", cpfEstudante, "demo@example.com",
                        LocalDate.of(1990, 1, 1))));
        String idPessoaEstudante = idGerado(resultadoPessoaEstudante, "Pessoa cadastrada:");

        String resultadoEstudante = executarEtapa(2, 10,
                "Cadastrando estudante",
                "O CPF corresponde a Pessoa anterior; o cadastro reutiliza o mesmo identificador.",
                () -> estudante.cadastrar(new Estudante(
                        "Estudante demonstracao", cpfEstudante, "demo@example.com",
                        LocalDate.of(2000, 1, 1), "MAT-DEMO-" + sufixo(),
                        "Sistemas", 5)));
        String idEstudante = idGerado(resultadoEstudante, "Estudante cadastrado:");
        if (!idPessoaEstudante.equals(idEstudante)) {
            throw new IllegalStateException("Pessoa e estudante receberam identificadores diferentes");
        }

        executarEtapa(3, 10,
                "Cadastrando a Pessoa do atendente",
                "O cadastro de atendente tambem exige uma Pessoa previamente existente.",
                () -> pessoa.cadastrar(new Pessoa(
                        "Pessoa atendente demonstracao", cpfAtendente, "atendente@example.com",
                        LocalDate.of(1985, 1, 1))));
        executarEtapa(4, 10,
                "Cadastrando atendente",
                "A conta funcional passa a usar o cadastro de Pessoa feito no passo anterior.",
                () -> atendente.cadastrar(new Atendente(
                        "Atendente demonstracao", cpfAtendente, "atendente@example.com",
                        LocalDate.of(1985, 1, 1), "Integral", "RF-DEMO-" + sufixo())));

        String resultadoLivro = executarEtapa(5, 10,
                "Incluindo livro no acervo",
                "Um novo exemplar e cadastrado no estado inicial LIVRE.",
                () -> acervo.cadastrar("Livro demonstracao " + sufixo()));
        String idLivro = idGerado(resultadoLivro, "Livro cadastrado:");

        String idReserva = "R-DEMO-" + sufixo();
        executarEtapa(6, 10,
                "Solicitando reserva",
                "O tratador cria e encaminha um ComandoReserva ao executor idempotente.",
                () -> emprestimo.reservar(idReserva, idLivro, idEstudante));

        String idTransacao = "T-DEMO-" + sufixo();
        executarEtapa(7, 10,
                "Efetivando o emprestimo",
                "A fachada confirma a reserva, atualiza o livro e cria a movimentacao.",
                () -> {
                    String resultado = emprestimo.realizar(idTransacao, idLivro, idEstudante);
                    if (!resultado.startsWith("Empréstimo realizado:")) {
                        throw new IllegalStateException(resultado);
                    }
                    return resultado;
                });

        executarEtapa(8, 10,
                "Executando a primeira renovacao",
                "Um ComandoRenovacao idempotente estende o prazo por 30 dias.",
                () -> renovarDemonstracao("CMD-DEMO-1-" + sufixo(), idLivro, idTransacao));

        executarEtapa(9, 10,
                "Executando a segunda renovacao",
                "Uma nova solicitacao distinta e permitida; o dominio bloqueia uma terceira.",
                () -> renovarDemonstracao("CMD-DEMO-2-" + sufixo(), idLivro, idTransacao));

        LocalDate dataDevolucao = LocalDate.now().plusDays(100);
        executarEtapa(10, 10,
                "Registrando devolucao atrasada",
                "A devolucao e confirmada; depois, Observer aciona inspecao, multa, indicadores e e-mail.",
                () -> {
                    boolean sucesso = devolucao.registrar(idLivro, idTransacao, dataDevolucao);
                    if (!sucesso) {
                        throw new IllegalStateException("A devolucao demonstrativa foi recusada");
                    }
                    return "Devolucao registrada em " + dataDevolucao + "; ouvintes executados.";
                });

        secao("DEMONSTRACAO CONCLUIDA",
                "Pessoa/estudante/atendente cadastrados; livro reservado, emprestado, renovado e devolvido.");
        System.out.println("  Identificadores: pessoa/estudante=" + idEstudante
                + ", livro=" + idLivro + ", emprestimo=" + idTransacao);
    }

    private String executarEtapa(
            int numero,
            int total,
            String titulo,
            String descricao,
            OperacaoDemonstrativa operacao) {
        System.out.println();
        System.out.println("  [" + numero + "/" + total + "] " + titulo);
        System.out.println("      " + descricao);
        pausaDemonstracao();
        String resultado = operacao.executar();
        if (resultado == null || resultado.toLowerCase().contains("nao foi possivel")
                || resultado.toLowerCase().contains("recusad")
                || resultado.toLowerCase().contains("nao conclu")) {
            throw new IllegalStateException(
                    resultado == null ? "A operacao nao retornou um resultado." : resultado);
        }
        System.out.println("      Resultado: " + resultado);
        pausaDemonstracao();
        return resultado;
    }

    private void pausaDemonstracao() {
        if (intervaloDemoMillis == 0) {
            return;
        }
        try {
            Thread.sleep(intervaloDemoMillis);
        } catch (InterruptedException excecao) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "A demonstracao foi interrompida durante a pausa.", excecao);
        }
    }

    private String idGerado(String mensagem, String prefixo) {
        if (mensagem == null || !mensagem.startsWith(prefixo)) {
            throw new IllegalStateException(
                    "A demonstracao nao conseguiu continuar: " + mensagem);
        }
        String id = mensagem.substring(prefixo.length()).trim();
        if (id.isEmpty()) {
            throw new IllegalStateException("O cadastro nao retornou um identificador.");
        }
        return id;
    }

    private String sufixo() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private String cpfDemonstracao() {
        return Long.toString(ThreadLocalRandom.current()
                .nextLong(10_000_000_000L, 99_999_999_999L));
    }

    private String renovarDemonstracao(String idComando, String idLivro, String idTransacao) {
        boolean sucesso = emprestimo.renovarIdempotente(
                idComando, "atendente-demo", idLivro, idTransacao, Instant.now());
        if (!sucesso) {
            throw new IllegalStateException("A renovacao " + idComando + " foi recusada.");
        }
        return "Renovacao confirmada; novo prazo ampliado em 30 dias.";
    }

    private String lerObrigatorio(String mensagem) {
        while (true) {
            System.out.print("  " + mensagem);
            if (!entrada.hasNextLine()) {
                throw new IllegalStateException("A entrada foi encerrada.");
            }
            String valor = entrada.nextLine().trim();
            if (!valor.isEmpty()) {
                return valor;
            }
            imprimirFalha("Campo obrigatorio", "Informe um valor para continuar.");
        }
    }

    private LocalDate lerData(String mensagem) {
        while (true) {
            String valor = lerObrigatorio(mensagem);
            try {
                return LocalDate.parse(valor);
            } catch (DateTimeParseException excecao) {
                imprimirFalha("Data invalida", "Use o formato AAAA-MM-DD, por exemplo 2026-11-24.");
            }
        }
    }

    private int lerInteiro(String mensagem) {
        while (true) {
            String valor = lerObrigatorio(mensagem);
            try {
                int numero = Integer.parseInt(valor);
                if (numero < 0) {
                    imprimirFalha("Valor fora do intervalo", "Informe um inteiro igual ou maior que zero.");
                    continue;
                }
                return numero;
            } catch (NumberFormatException excecao) {
                imprimirFalha("Numero invalido", "Informe um numero inteiro igual ou maior que zero.");
            }
        }
    }

    private void imprimirBoasVindas() {
        System.out.println();
        System.out.println("==============================================================");
        System.out.println("                 SISTEMA BIBLIOTECA GoF");
        System.out.println("==============================================================");
        System.out.println("Bem-vindo(a)! Escolha uma operacao abaixo.");
        System.out.println("Dica: a demonstracao guiada apresenta um fluxo completo, passo a passo.");
    }

    private void imprimirMenu() {
        System.out.println();
        System.out.println("+--------------------- MENU PRINCIPAL ---------------------+");
        System.out.println("| CADASTROS                                                |");
        System.out.println("|----------------------------------------------------------|");
        System.out.println("| 1  | Cadastrar pessoa                                    |");
        System.out.println("| 2  | Cadastrar estudante                                 |");
        System.out.println("| 3  | Cadastrar atendente                                 |");
        System.out.println("| 4  | Cadastrar livro                                     |");
        System.out.println("|----------------------------------------------------------|");
        System.out.println("| CONSULTAS                                                |");
        System.out.println("|----------------------------------------------------------|");
        System.out.println("| 5  | Consultar pessoa                                    |");
        System.out.println("| 6  | Consultar estudante                                 |");
        System.out.println("| 7  | Consultar atendente                                 |");
        System.out.println("| 8  | Consultar livro                                     |");
        System.out.println("|----------------------------------------------------------|");
        System.out.println("| CIRCULACAO                                               |");
        System.out.println("|----------------------------------------------------------|");
        System.out.println("| 9  | Solicitar reserva                                   |");
        System.out.println("| 10 | Efetivar emprestimo                                 |");
        System.out.println("| 11 | Renovar emprestimo                                  |");
        System.out.println("| 12 | Registrar devolucao                                 |");
        System.out.println("| 13 | Emitir multa                                        |");
        System.out.println("|----------------------------------------------------------|");
        System.out.println("| LISTAGENS                                                |");
        System.out.println("|----------------------------------------------------------|");
        System.out.println("| 14 | Acervo                                              |");
        System.out.println("| 15 | Pessoas                                             |");
        System.out.println("| 16 | Estudantes                                          |");
        System.out.println("| 17 | Atendentes                                          |");
        System.out.println("| 18 | Reservas                                            |");
        System.out.println("| 19 | Movimentacoes                                       |");
        System.out.println("| D  | Demonstracao guiada                                 |");
        System.out.println("| 0  | Sair                                                |");
        System.out.println("+----------------------------------------------------------|");
        System.out.print("Opcao: ");
    }

    private void imprimirTabela(
            List<String> cabecalhos,
            List<List<String>> linhas,
            String mensagemVazia) {
        if (linhas.isEmpty()) {
            System.out.println("  " + mensagemVazia);
            return;
        }
        List<Integer> larguras = new ArrayList<>();
        for (int indice = 0; indice < cabecalhos.size(); indice++) {
            larguras.add(cabecalhos.get(indice).length());
        }
        for (List<String> linha : linhas) {
            for (int indice = 0; indice < linha.size(); indice++) {
                larguras.set(indice, Math.max(larguras.get(indice), linha.get(indice).length()));
            }
        }
        System.out.println("  " + bordaTabela(larguras));
        System.out.println("  " + linhaTabela(cabecalhos, larguras));
        System.out.println("  " + bordaTabela(larguras));
        for (List<String> linha : linhas) {
            System.out.println("  " + linhaTabela(linha, larguras));
        }
        System.out.println("  " + bordaTabela(larguras));
    }

    private String bordaTabela(List<Integer> larguras) {
        StringBuilder borda = new StringBuilder("+");
        for (int largura : larguras) {
            borda.append("-".repeat(largura + 2)).append('+');
        }
        return borda.toString();
    }

    private String linhaTabela(List<String> valores, List<Integer> larguras) {
        StringBuilder linha = new StringBuilder("|");
        for (int indice = 0; indice < valores.size(); indice++) {
            String valor = valores.get(indice);
            linha.append(' ').append(valor)
                    .append(" ".repeat(larguras.get(indice) - valor.length() + 1))
                    .append('|');
        }
        return linha.toString();
    }

    private String texto(Object valor) {
        return valor == null ? "-" : valor.toString();
    }

    private void cabecalho(String titulo, String descricao) {
        System.out.println();
        System.out.println("--------------------------------------------------------------");
        System.out.println("  " + titulo.toUpperCase());
        System.out.println("  " + descricao);
        System.out.println("--------------------------------------------------------------");
    }

    private void secao(String titulo, String descricao) {
        System.out.println();
        System.out.println("==============================================================");
        System.out.println("  " + titulo);
        System.out.println("  " + descricao);
        System.out.println("==============================================================");
    }

    private void imprimirConsulta(String resultado) {
        if (resultado.toLowerCase().contains("nao encontrado")) {
            imprimirFalha("Consulta sem resultados", resultado);
        } else {
            System.out.println("  Resultado: " + resultado);
        }
    }

    private void imprimirResultado(String resultado, String contexto) {
        if (resultado == null || resultado.toLowerCase().contains("nao foi possivel")
                || resultado.toLowerCase().contains("recusad")) {
            imprimirFalha(contexto + " nao concluido", resultado);
        } else {
            imprimirSucesso(resultado);
        }
    }

    private void imprimirSucesso(String mensagem) {
        System.out.println();
        System.out.println("  [OK] " + mensagem);
    }

    private void imprimirFalha(String titulo, String detalhe) {
        System.out.println();
        System.out.println("  [!] " + titulo + (detalhe == null || detalhe.isBlank()
                ? "." : ": " + detalhe));
    }

    @FunctionalInterface
    private interface OperacaoDemonstrativa {
        String executar();
    }
}
