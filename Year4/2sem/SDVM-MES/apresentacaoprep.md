    💡 Caros colegas, este documento vai servir para preparar a apresentação. Não sei se vai valer a pena fazermos um powerpoint, mas fica aqui o aglomerado do trabalho que fizemos.

# 1. Estrutura do projeto

Inicialmente nós tínhamos tudo num ficheiro só, isto é, no **_PicoC.hs_**. Mas nesta última fase apercebemo-nos que estava a tornar-se inviável trabalhar tudo no mesmo documento, para termos noção já contávamos com 800 linhas. Por isso, dividimos o trabalho por módulos.

Os módulos:

- **Geradores.hs**: Tem tudo sobre os geradores
- **Gramatica.hs**: Tem a gramática (datatype com as instruções e as expressões), o pretty printing e o unparser.
- **Instrumentacao.hs**: Tem tudo sobre a instrumentação (útil para posteriormente para o fault localization)
- **Mutacoes.hs**: Tem tudo sobre as mutações
- **Otimizacoes.hs**: Tem aquelas funções que transformam cada expressão numa equivalente mas mais simples sintáticamente, para além disso também aquelas funções de programação estratégica
- **Parser.hs**: É o parser desenvolvido com o stor
- **ParserPicoC.hs**: É o parser para a linguagem de programação PicoC
- **Programa.hs**: É o que tem os exemplos de programas que nos usamos para gerar mutações e estudar o fault localization
- **TestSuite.hs**: É aonde estão os nossos testes para os progamas

# 2. Sobre o Parser

Sobre aquele parser que foi desenvolvido não fizemos nenhuma alteração. Por isso é menos uma preocupação que temos de ter.

# 3. Sobre o ParserPicoC

No parser PicoC é aonde conseguimos gerar a árvore de derivação de um programa PicoC.

As funções pExp, são as funções que definem a prioridade das funções. Estas funções são importantes para garantir que um programa por exemplo 2\*4+1 executa de forma correta, ou seja, ter a capacidade de perceber que a execução da soma é realizada posteriormente à múltiplicação

### Funções

#### atribuiVar
Quando consegue consumir um pNomes(string que verifica se é um alfanúmerico - ao fim acabo é so da parse de uma palavra), seguido de um simbolo '=', deopis ve expressões e termina com um ';'

#### ifElse
faz a mesma coisa mas para um if then else

#### while
faz a mesma coisa mas para um while

#### comments
faz a mesma coisa mas para um comentario



# 4. Otimizações
As otimizações utilizamos programação estrategica, isto porque assim torna-se mais facil de aplciar todas as otimizações de codigo, a nível de refactoring. 

Para utilizar esssa programação estratégica, tivemos que desenolver aquelas funções ops.

usamos o innermost para a travessia da arvore
o failTP para executar ate "à falha", ou seja, ate nao ter mais nada para otimizar

temos as otimizações boleanas, e as de comentario. Nas de comentario, como nao da para simplesmente remover um elemento, nos so substituimos por outro e deois no pretty printing nao daos print desse "datatype"

a megaestrat faz tudo.


# 5. Geradores
fizemos um gerador para cada coisa. 

"shrinking" is a process used to simplify failing test cases. When a randomly generated test case fails, QuickCheck attempts to find a smaller, simpler version of that test case that still causes the failure. This smaller test case is usually easier to understand and debug.


# 6. Mutacações

as mutações seguem o mesmo raciocinio das otimizações, so que usamos o once_RandomTP e o mutations, este nos aplicamos uma mutação aleatória escolhida de um conjunto de todas as possíveis mutações do programa, utilizando uma semente fixa para reprodutibilidade. 

- **Uso de Aleatoriedade**:
  - `aplicaMut`: A aleatoriedade está embutida na função `once_RandomTP`.
  - `aplicaMutComMutations`: A aleatoriedade é controlada explicitamente com uma semente e uma seleção de índice.

# 7. Instrumentação
a instrumentação basicamente insere exps do tipo print, para depois conseguirmos facilmente dar debug das linhas que acabaram de ser executadas. isto é util para a fase de fault localization


# 8. TestSuite

aqui temos as cosas sobre testes. teste unitarios, e a função evaluate, que é mais ou menos uma execução do programa com os valores que passamos. 



# 9. Como correr?

- Com o stack


    stack ghci PicoC.hs

- Sem o stack 


    ghci PicoC.hs

Apos ele estar compilado, é testar

- Para geradores


    sample $ genPicoC int
esse int representa o quao grande 

- Para otimiações


    Nomeestrategia exemploPicoC

- instrumentação
    

    instrumentation programaPicoC

- mutações


    aplicamut/aplicaMutCom... programaPicoC

- evaluate


    evaluate programa teste

- fault Localization

    1. Primeiro meter o programa mutado com instrumentação
    2. depois correr correr como se fosse um teste normal com o evaluate


