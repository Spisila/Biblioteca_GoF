# Pontifícia Universidade Católica do Paraná
**Bacharelado em Ciência da Computação**  
**Modelagem de Sistemas Computacionais**  
**Prof. Edson Emílio Scalabrin**  
**Turma:** A-2S-2026  
**Trabalho:** Implementação do sistema de empréstimo de livros com padrões GoF  
**Sistema de gestão de biblioteca**

---

| $N^{\circ}$ | Situação (lista de problemas) | Padrão mínimo |
| :---: | :--- | :--- |
| 1 | Ciclo de vida do livro | **State**, com máquina de estados alinhada ao diagrama de classes |
| 2 | Políticas de cálculo de multa | **Strategy** |
| 3 | Integração com bancos incompatíveis | **Adapter** |
| 4 | Entrada única para as operações de circulação | **Facade** |
| 5 | Reações independentes à devolução | **Observer** |
| 6 | Solicitações de circulação como objetos | **Command** |
| 7 | Rotina comum para cadastros | **Template Method** |
| 8 | Famílias compatíveis de acesso aos dados | **Abstract Factory** |
| 9 | Serviço de consulta com recursos opcionais | **Decorator** |
| 10 | Organização hierárquica do acervo | **Composite** |

Trabalho prático em equipe para estudantes de Ciência da Computação. Continuação do Trabalho 02 e da lista "Problemas de aplicação dos padrões GoF": as dez situações modeladas passam a ser implementadas, testadas e demonstradas.

---

## Objetivo
Implementar uma versão executável do sistema de cadastro, reserva, empréstimo, renovação e devolução de livros, aplicando, no mínimo, os dez padrões GoF indicados acima, cada um na situação para a qual foi proposto. O código deve corresponder aos modelos UML da equipe, e cada padrão deve ser comprovado por meio de um cenário de verificação executado na demonstração.

---

## Contexto comum
A empresa iB2B utiliza um sistema desenvolvido pela BCC2022. Os componentes Acadêmico, RH, Biblioteca e Financeiro comunicam-se por meio de interfaces; o Banco é externo. Preserve os tratadores, a delegação e os pontos internos de acesso Acervo, Relacionamento e Movimentacao previstos no Trabalho 02. As premissas de interpretação da lista continuam válidas, em especial a guarda `numRenovacoes < 2` e o evento de laudo irrecuperável que leva `REPARANDO` a `INCINERADO`.

---

## Requisitos mínimos
Cada linha da tabela é obrigatória. As seções seguintes detalham o que implementar, o cenário a ser executado e o que será perguntado sobre cada padrão.

*Outros padrões podem ser usados quando justificados, mas não substituem os da tabela.*

---

## Equipes e prazos
* **Equipes:** até 4 integrantes.
* **Entrega do repositório:** até 27/11/2026, às 09h40.
* **Demonstrações:** em 27/11/2026, com 5 minutos por equipe, conforme ordem divulgada pelo professor.

---

## Requisitos técnicos
* **Linguagem:** Java 17 ou superior, com Maven ou Gradle. Outra linguagem orientada a objetos, apenas com autorização prévia do professor.
* **Interface:** web ou linha de comando. A interface chama apenas os tratadores e não contém regras do domínio.
* **Persistência:** duas famílias — uma em memória para os testes e outra relacional (MySQL ou equivalente aprovado) para o perfil de produção, ambas criadas pela fábrica do requisito 8.
* **Bancos A e B:** podem ser simulados por classes que imitam as APIs incompatíveis da lista; o importante é que o adaptador realize a conversão.
* **Testes:** um teste automatizado (JUnit ou equivalente) para cada cenário de verificação, com um nome que identifique o requisito, por exemplo, `R01_terceiraRenovacaoRecusada`.
* **Execução:** o projeto compila e os testes rodam com um único comando descrito no `README`, sem necessidade de configuração manual além do banco de dados.

---

## Uso de LLMs
Cada equipe pode usar qualquer LLM para gerar código, testes e documentação. O uso é livre, mas a responsabilidade é da equipe:
* O `README` informa quais ferramentas e modelos foram usados e em quais partes do sistema.
* O arquivo `REGISTRO_LLM.md` resume os principais pedidos feitos ao modelo e o que a equipe aceitou, corrigiu ou descartou.
* Código que nenhum integrante consegue explicar é considerado não implementado.
* Durante a demonstração, cada integrante deve ser capaz de responder qualquer pergunta sobre qualquer parte do código e das funcionalidades, não apenas sobre a parte que desenvolveu. Não há divisão de responsabilidade na arguição.

---

## Entregáveis
1. Repositório com o código-fonte, os testes e o `README` (com instruções sobre como compilar, executar e rodar os testes).
2. Modelos UML atualizados no Astah (`.asta`) e exportados em PDF: diagrama de classes de cada padrão, a máquina de estados de Livro e os diagramas de sequência dos cenários.
3. Tabela de rastreabilidade: para cada requisito, o padrão, as classes que desempenham cada papel e o teste que comprova o cenário.
4. `REGISTRO_LLM.md`, conforme a seção anterior.

---

## Demonstração e arguição
1. Execução dos testes dos dez cenários de verificação, todos aprovados.
2. Demonstração, pela interface, de pelo menos um fluxo completo: reserva, empréstimo, renovação, devolução atrasada e multa.
3. Arguição individual: o professor escolhe quem responde, aponta um trecho do código e pergunta o que ele faz, qual papel o padrão desempenha e o que aconteceria se fosse alterado.
4. Alteração ao vivo: um integrante escolhido pelo professor faz uma pequena extensão, por exemplo, uma nova política de multa ou um novo ouvinte da devolução, explicando por que o padrão permite fazê-la sem alterar as classes existentes.

---

## Critérios de avaliação

| Critério | Peso |
| :--- | :---: |
| Aplicação correta dos dez padrões, com papéis identificáveis no código | 40% |
| Cenários de verificação automatizados e aprovados | 20% |
| Coerência entre código e modelos UML, incluindo a máquina de estados | 15% |
| Separação de responsabilidades, tratamento de falhas e preservação das regras do domínio | 15% |
| Documentação: README, rastreabilidade e registro de uso de LLM | 10% |

> A nota individual é a nota da equipe multiplicada pelo fator de arguição do integrante:
> * **1,0:** quando explica com segurança;
> * **0,7:** quando explica com lacunas;
> * **0,4:** quando explica apenas a parte que desenvolveu;
> * **0:** quando não consegue explicar o código apresentado.

---

## Fonte
Trabalho 02 – Modelagem dinâmica e arquitetural do sistema de empréstimo de livros, e lista "Problemas de aplicação dos padrões GoF". Prof. Edson Emilio Scalabrin, PUCPR, $2^{\circ}$ semestre de 2026.

---

# Detalhamento dos Requisitos

## Requisito 1: Ciclo de vida do livro (`State`)

* **O que implementar:** `Livro` delega cada evento a um objeto de estado. Há uma classe para cada um dos sete estados (`LIVRE`, `RESERVADO`, `EMPRESTADO`, `RENOVADO`, `DEVOLVIDO`, `REPARANDO`, `INCINERADO`), e cada uma implementa apenas as operações válidas; as demais são recusadas sem alterar o livro. Os tratadores não testam a situação do livro com condicionais. Inclua o evento de laudo irrecuperável em `REPARANDO`.
* **Cenário de verificação:** Uma terceira renovação deve ser recusada sem alterar o vencimento. Um livro devolvido ainda não inspecionado não pode ser reservado.
* **Alinhamento com o modelo:** Cada transição da máquina de estados corresponde a um método da classe do estado de origem; guardas e ações aparecem no corpo desse método.
* **Na arguição, saiba explicar:** Onde está a guarda da renovação e por que ela é avaliada antes de alterar o vencimento; o que acontece quando um evento chega a um estado que não o aceita; como o estado é gravado e reconstituído na persistência.

---

## Requisito 2: Políticas de cálculo de multa (`Strategy`)

* **O que implementar:** Uma interface de política de multa e três implementações: a original (R\$ 1,00 por dia), com teto de R\$ 20,00 e com tolerância de dois dias. A política vigente é definida pela configuração administrativa. A multa grava o valor e a política aplicada na emissão, e a emissão de boletos não muda.
* **Cenário de verificação:** Para cinco dias de atraso, obtenha R\$ 5,00 na política original e R\$ 3,00 na política com tolerância. Sem atraso, o valor deve ser zero.
* **Na arguição, saiba explicar:** Como uma nova política é incluída sem alterar o serviço de multas; por que uma troca de política não recalculas multas já emitidas; como os dias de atraso e os valores monetários são calculados.

---

## Requisito 3: Integração com bancos incompatíveis (`Adapter`)

* **O que implementar:** Um contrato institucional de cobrança e um adaptador para cada banco, que converte valores, documentos, identificadores e situações. As notificações de pagamento também são traduzidas pelo adaptador. O serviço de multas não contém condicionais por banco, e uma notificação repetida não gera segunda quitação.
* **Cenário de verificação:** A mesma solicitação de R\$ 7,00 deve produzir 700 centavos no banco A e 7,00 no banco B, sem condicionais por banco no serviço de multas.
* **Na arguição, saiba explicar:** Onde ficam as conversões de cada banco; como o banco usado pela instalação é escolhido; como a quitação é protegida contra notificações repetidas.

---

## Requisito 4: Entrada única para as operações de circulação (`Facade`)

* **O que implementar:** Uma fachada de circulação que coordena `Relacionamento`, `Movimentacao`, `Acervo` e `Livro` em operações completas e transacionais, devolvendo comprovantes em vez de coleções. Os tratadores delegam a ela, e a interface depende apenas dos tratadores.
* **Cenário de verificação:** A efetivação de empréstimo deve validar a reserva, remover a reserva ativa, criar o empréstimo e atualizar `Livro` sem expor coleções à interface.
* **Na arguição, saiba explicar:** Quais classes a fachada coordena e em que ordem; o que é desfeito se um passo falhar no meio da operação; por que a interface não acessa `Movimentacao` diretamente.

---

## Requisito 5: Reações independentes à devolução (`Observer`)

* **O que implementar:** Um publicador de devolução com ouvintes registrados para inspeção, multa, indicadores e e-mail. O evento contém os dados necessários: vencimento, data de devolução e estudante. A publicação ocorre após a confirmação da devolução, e a falha de um ouvinte não afeta os demais nem desfaz a devolução.
* **Cenário de verificação:** Uma devolução atrasada deve alimentar a inspeção, a multa e os indicadores. Uma devolução no prazo não deve gerar multa. O livro permanece `DEVOLVIDO` até a decisão da inspeção.
* **Na arguição, saiba explicar:** Como um novo ouvinte é incluído sem alterar o tratador; o que acontece quando o envio de e-mail falha; por que o evento leva os dados em vez de o ouvinte consultá-los depois.

---

## Requisito 6: Solicitações de circulação como objetos (`Command`)

* **O que implementar:** Solicitações de reserva e renovação como objetos com identificador, autor, instante de criação e operação de execução. As telas criam a solicitação e a encaminham a um executor, que garante que o mesmo identificador não seja executado duas vezes. As regras continuam no domínio.
* **Cenário de verificação:** Uma solicitação de renovação executada duas vezes com o mesmo identificador deve acrescentar apenas 30 dias. Solicitações diferentes continuam sujeitas ao limite de duas renovações.
* **Na arguição, saiba explicar:** Quem cria, quem invoca e quem executa a solicitação; como a repetição é detectada mesmo com duas submissões simultâneas; por que não há operação de desfazer.

---

## Requisito 7: Rotina comum para cadastros (`Template Method`)

* **O que implementar:** Uma classe abstrata com o método-modelo de inclusão, que não pode ser sobrescrito, fixando a ordem: buscar existente, validar, verificar pré-requisitos, criar, armazenar e apresentar. `Livro`, `Pessoa`, `Estudante` e `Atendente` implementam os passos variáveis, preservando as interfaces `iLivro`, `iPessoa`, `Estudante` e `iAtendente`.
* **Cenário de verificação:** Incluir um estudante duplicado deve retornar o cadastro existente. Incluir um novo estudante associado a CPF não cadastrado deve falhar antes de qualquer gravação.
* **Na arguição, saiba explicar:** Quais passos são fixos e quais variam; o que é um gancho e onde ele é usado; por que a falha do pré-requisito não permite gravação parcial.

---

## Requisito 8: Famílias compatíveis de acesso aos dados (`Abstract Factory`)

* **O que implementar:** Uma fábrica abstrata que cria a unidade de trabalho e os repositórios de `Livro`, `Movimentacao` e `Relacionamento`, com uma fábrica para a família em memória e outra para a família relacional. A fábrica é escolhida uma única vez pelo perfil de execução. O Oracle continua réplica e não recebe escritas da aplicação.
* **Cenário de verificação:** Uma configuração de teste deve fornecer apenas objetos em memória. Uma configuração de produção deve fornecer repositórios MySQL que compartilham a mesma unidade de trabalho.
* **Na arguição, saiba explicar:** Onde a família é escolhida; como os repositórios compartilham a mesma transação; o que impede misturar um repositório em memória com outro repositório relacional.

---

## Requisito 9: Serviço de consulta com recursos opcionais (`Decorator`)

* **O que implementar:** Um contrato de consulta ao acervo, a implementação original sem alterações e decoradores de medição e de cache que podem ser combinados conforme a instalação. O cache armazena apenas metadados; a reserva consulta sempre o estado atual do `Livro`.
* **Cenário de verificação:** Uma segunda consulta de metadados deve poder usar cache e, ainda assim, gerar medição. Um resultado antigo do cache nunca pode autorizar a reserva de um livro que deixou de estar `LIVRE`.
* **Na arguição, saiba explicar:** Como a cadeia de decoradores é montada e por que a ordem importa; o que acontece se o cache falhar; por que o cache não participa da decisão de reserva.

---

## Requisito 10: Organização hierárquica do acervo (`Composite`)

* **O que implementar:** Unidades, seções e subseções como grupos de uma árvore de profundidade variável, com itens que referenciam livros pelo identificador. Grupos e itens respondem às mesmas operações de contagem e listagem. O grupo recusa ciclos e livros que já pertencem a outro grupo.
* **Cenário de verificação:** Uma unidade com duas seções, contendo três e dois livros, deve informar um total de cinco livros. Mover um livro entre seções não deve alterar seu id, estado nem status de empréstimo.
* **Na arguição, saiba explicar:** Como a contagem percorre a árvore; quais regras impedem ciclos e livros em dois grupos; por que mover um item não toca no `Livro` nem na `Movimentacao`.