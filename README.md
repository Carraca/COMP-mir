**PROJECT TITLE: mir

**GROUP: 5F

NAME1: Ana Margarida Cardoso Carraca, NR1: ei10001, GRADE1: 18, CONTRIBUTION: 33

NAME2: Bruno Miguel Mendonça Maia, NR2: ei09095, GRADE2: 18, CONTRIBUTION: 33

NAME3: Francisco Paulo Alves Ferreira, NR3: ei09075, GRADE3: 18, CONTRIBUTION: 33

** SUMMARY: 
Elaboramos uma ferramenta gráfica que interpreta um programa (i.e. um
conjunto de funções) escrito com recurso a 3-address-code no formato MIR, 
para depois o analisar. O resultado final é a geração e visualização de vários 
elementos:
- Árvore de Sintaxe Abstracta
- Control Flow Graph, devidamente anotado e com possibilidade de ser exportado
  para dotty
- Grafo de interferências
- Alocação de registos, com recurso a graph coloring, sendo que o número de
  registos disponíveis está devidamente parametrizado
- Relatório sobre lifeness das variáveis:
	- Onde começa
	- Onde acaba
	- Que registo lhe está atribuído

**DEALING WITH SYNTACTIC ERRORS: 
Depois do utilizador carregar o código de um programa e o compilar, o primeiro
passo é a análise sintática.
Nesta fase, caso sejam detectados erros, é feito o seguinte:
- A análise pára.
- Na área de logging, aparece uma mensagem indicativa da causa do erro e linha
  onde este ocorreu.
- A linha é realçada a vermelho no visualizador de código 

**SEMANTIC ANALYSIS: 
As verificações semânticas realizadas são as seguintes:
- Verificação da integridade dos saltos:
		Cada "goto" tem de levar a uma label válida.
- Verificação da integridade das chamadas de funções:
		Cada instrução "call" (chamada de função) tem de ter um destino válido
		(i.e. a função de destino existe) e o número de parâmetros correcto.
- Verificação da declaração das variáveis:
		Para uma variável poder ser lida, primeiro tem de ter sido escrita.
- Verificação de existência de um entry point:
		Tem de existir uma função chamada "main".
- Verificação de inexistência de funções anónimas
		Todas as funções devem ser precedidas por uma label que denota o nome
		destas.
No caso de violação das regras de semântica, o procedimento utilizado é o
mesmo que para erros sintácticos:
- Parar a análise.

**INTERMEDIATE REPRESENTATIONS (IRs): 
A geração de uma AST "prática" de utilizar, juntamente com as características
da linguagem permite que a representação intermédia utilizada não seja mais 
do que a AST anotada com atributos relevantes (p.ex., se o nó for uma variável,
interessa saber se o acesso à variável é de leitura ou escrita).

**CODE GENERATION:


**OVERVIEW:


**TESTSUITE AND TEST INFRASTRUCTURE: 
Os testes não foram automatizados; Para o nosso caso, a automatização teria
poucas vantagens quando comparada com o tempo dispendido a implementá-la. 
Temos alguns programas exemplo que implementam algoritmos simples e
que abrangem grande parte da especificação da linguagem.
Estes exemplos sofriam uma introdução de erros deliberados quando era necessário
verificar o correcto funcionamento dos mecanismos de detecção de erros.
Os nossos visualizadores gráficos, em conjunto com o logger, tornavam o 
processo de verificação de funcionamento de acordo com o esperado simples e
rápido.

**TASK DISTRIBUTION:
Torna-se complicado apontar quem fez o que, visto que todos os elementos
participaram em todas as fases do projecto e 90% do trabalho foi realizado e
conjunto. A distribuição da carga de trabalho foi equilibrada.

**PROS: (Identify the most positive aspects of your tool)

**CONS: (Identify the most negative aspects of your tool) 
