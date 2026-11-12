package br.pucpr.biblioteca.app;

import br.pucpr.biblioteca.biblioteca.acervo.consulta.ServicoConsultaOriginal;
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
import br.pucpr.biblioteca.persistencia.fabrica.FabricaPersistencia;
import br.pucpr.biblioteca.tratadores.TratadorDeAcervo;
import br.pucpr.biblioteca.tratadores.TratadorDeAtendente;
import br.pucpr.biblioteca.tratadores.TratadorDeDevolucao;
import br.pucpr.biblioteca.tratadores.TratadorDeEmprestimo;
import br.pucpr.biblioteca.tratadores.TratadorDeEstudante;
import br.pucpr.biblioteca.tratadores.TratadorDeMulta;
import br.pucpr.biblioteca.tratadores.TratadorDePessoa;
import br.pucpr.biblioteca.ui.ConsoleBiblioteca;
import br.pucpr.biblioteca.ui.RegistroConsole;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        PerfilExecucao perfil = usarProducao(args)
                ? PerfilExecucao.PRODUCAO
                : PerfilExecucao.TESTE;
        FabricaPersistencia fabrica = Configuracao.fabricaPara(perfil);
        RelogioSistema relogio = new RelogioSistema();

        RepositorioLivroPorOperacao repositorioLivro = new RepositorioLivroPorOperacao(fabrica);
        TratadorDeAcervo tratadorAcervo = new TratadorDeAcervo(
                new CadastroLivro(repositorioLivro),
                new ServicoConsultaOriginal(repositorioLivro));

        RepositorioCadastroMemoria<RegistroConsole> pessoas = new RepositorioCadastroMemoria<>();
        RepositorioCadastroMemoria<RegistroConsole> estudantes = new RepositorioCadastroMemoria<>();
        RepositorioCadastroMemoria<RegistroConsole> atendentes = new RepositorioCadastroMemoria<>();
        TratadorDePessoa<RegistroConsole> tratadorPessoa = new TratadorDePessoa<>(
                new CadastroPessoa<>(pessoas));
        TratadorDeEstudante<RegistroConsole> tratadorEstudante = new TratadorDeEstudante<>(
                new CadastroEstudante<>(estudantes, registro -> true));
        TratadorDeAtendente<RegistroConsole> tratadorAtendente = new TratadorDeAtendente<>(
                new CadastroAtendente<>(atendentes, registro -> true));

        EmissorMulta emissorMulta = new EmissorMulta();
        PublicadorDevolucao publicador = criarPublicador(emissorMulta);
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

    private static PublicadorDevolucao criarPublicador(EmissorMulta emissorMulta) {
        PublicadorDevolucao publicador = new PublicadorDevolucao();
        publicador.adicionar(new OuvinteInspecaoExemplar());
        publicador.adicionar(new OuvinteMulta(emissorMulta));
        publicador.adicionar(new OuvinteIndicadores());
        publicador.adicionar(new OuvinteEmail(evento ->
                System.out.println("E-mail de devolucao enviado para " + evento.idEstudante())));
        return publicador;
    }

    private static boolean usarProducao(String[] args) {
        for (String argumento : args) {
            if ("--producao".equalsIgnoreCase(argumento)) {
                return true;
            }
        }
        return false;
    }
}

