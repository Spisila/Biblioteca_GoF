package br.pucpr.biblioteca.app;

import br.pucpr.biblioteca.biblioteca.acervo.consulta.ServicoConsultaOriginal;
import br.pucpr.biblioteca.biblioteca.acervo.composite.UnidadeAcervo;
import br.pucpr.biblioteca.academico.estudante.Estudante;
import br.pucpr.biblioteca.cadastro.CadastroAtendente;
import br.pucpr.biblioteca.cadastro.CadastroEstudante;
import br.pucpr.biblioteca.cadastro.CadastroLivro;
import br.pucpr.biblioteca.cadastro.CadastroPessoa;
import br.pucpr.biblioteca.circulacao.comando.ExecutorComando;
import br.pucpr.biblioteca.circulacao.facade.FachadaCirculacao;
import br.pucpr.biblioteca.comum.relogio.RelogioSistema;
import br.pucpr.biblioteca.devolucao.ouvintes.OuvinteEmail;
import br.pucpr.biblioteca.devolucao.ouvintes.OuvinteIndicadores;
import br.pucpr.biblioteca.devolucao.ouvintes.OuvinteInspecaoExemplar;
import br.pucpr.biblioteca.devolucao.ouvintes.OuvinteMulta;
import br.pucpr.biblioteca.devolucao.publicador.PublicadorDevolucao;
import br.pucpr.biblioteca.financeiro.multa.EmissorMulta;
import br.pucpr.biblioteca.financeiro.cobranca.contrato.ServicoCobranca;
import br.pucpr.biblioteca.rh.atendente.Atendente;
import br.pucpr.biblioteca.rh.pessoa.Pessoa;
import br.pucpr.biblioteca.persistencia.fabrica.FabricaPersistencia;
import br.pucpr.biblioteca.tratadores.TratadorDeAcervo;
import br.pucpr.biblioteca.tratadores.TratadorDeAtendente;
import br.pucpr.biblioteca.tratadores.TratadorDeDevolucao;
import br.pucpr.biblioteca.tratadores.TratadorDeEmprestimo;
import br.pucpr.biblioteca.tratadores.TratadorDeEstudante;
import br.pucpr.biblioteca.tratadores.TratadorDeMulta;
import br.pucpr.biblioteca.tratadores.TratadorDePessoa;
import br.pucpr.biblioteca.ui.ConsoleBiblioteca;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        PerfilExecucao perfil = usarTeste(args) ? PerfilExecucao.TESTE : PerfilExecucao.PRODUCAO;
        FabricaPersistencia fabrica = Configuracao.fabricaPara(perfil);
        RelogioSistema relogio = new RelogioSistema();

        RepositorioLivroPorOperacao repositorioLivro = new RepositorioLivroPorOperacao(fabrica);
        TratadorDeAcervo tratadorAcervo = new TratadorDeAcervo(
                new CadastroLivro(repositorioLivro),
                Configuracao.consulta(new ServicoConsultaOriginal(repositorioLivro)),
                new UnidadeAcervo("ACERVO", "Acervo"));

        RepositorioCadastroMemoria<Pessoa> pessoasMemoria =
                new RepositorioCadastroMemoria<>(Pessoa.class);
        RepositorioCadastroMemoria<Estudante> estudantesMemoria =
                new RepositorioCadastroMemoria<>(Estudante.class);
        RepositorioCadastroMemoria<Atendente> atendentesMemoria =
                new RepositorioCadastroMemoria<>(Atendente.class);
        RepositorioCadastroPorOperacao<Pessoa> pessoas =
                new RepositorioCadastroPorOperacao<>(
                        fabrica, pessoasMemoria, Pessoa.class);
        RepositorioCadastroPorOperacao<Estudante> estudantes =
                new RepositorioCadastroPorOperacao<>(
                        fabrica, estudantesMemoria, Estudante.class);
        RepositorioCadastroPorOperacao<Atendente> atendentes =
                new RepositorioCadastroPorOperacao<>(
                        fabrica, atendentesMemoria, Atendente.class);
        TratadorDePessoa<Pessoa> tratadorPessoa = new TratadorDePessoa<>(
                new CadastroPessoa<>(pessoas));
        TratadorDeEstudante<Estudante> tratadorEstudante = new TratadorDeEstudante<>(
                new CadastroEstudante<>(estudantes, pessoas, Estudante.class));
        TratadorDeAtendente<Atendente> tratadorAtendente = new TratadorDeAtendente<>(
                new CadastroAtendente<>(atendentes, pessoas, Atendente.class));

        EmissorMulta emissorMulta = new EmissorMulta(Configuracao.politicaMulta());
        ServicoCobranca servicoCobranca = new ServicoCobranca(Configuracao.cobranca());
        PublicadorDevolucao publicador = criarPublicador(emissorMulta, servicoCobranca);
        FachadaCirculacao fachada = new FachadaCirculacao(fabrica, relogio, publicador);
        TratadorDeEmprestimo tratadorEmprestimo = new TratadorDeEmprestimo(
                fachada, new ExecutorComando());

        try (Scanner entrada = new Scanner(System.in)) {
            new ConsoleBiblioteca(
                    tratadorAcervo,
                    tratadorPessoa,
                    tratadorEstudante,
                    tratadorAtendente,
                    tratadorEmprestimo,
                    new TratadorDeDevolucao(fachada),
                    new TratadorDeMulta(emissorMulta),
                    entrada).iniciar();
        }
    }

    private static PublicadorDevolucao criarPublicador(
            EmissorMulta emissorMulta,
            ServicoCobranca servicoCobranca) {
        PublicadorDevolucao publicador = new PublicadorDevolucao();
        publicador.adicionar(new OuvinteInspecaoExemplar());
        publicador.adicionar(new OuvinteMulta(emissorMulta, servicoCobranca));
        publicador.adicionar(new OuvinteIndicadores());
        publicador.adicionar(new OuvinteEmail(evento ->
                System.out.println("E-mail de devolucao enviado para " + evento.idEstudante())));
        return publicador;
    }

    private static boolean usarTeste(String[] args) {
        for (String argumento : args) {
            if ("--teste".equalsIgnoreCase(argumento)) {
                return true;
            }
        }
        return false;
    }
}

