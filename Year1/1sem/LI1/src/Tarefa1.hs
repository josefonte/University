-- | __Nesta tarefa, o objetivo foi criar a função "@generateMaze :: Int -> Int -> Int -> Maze@" que gera um labirinto aletatório com dimensões definidas pelo usuário e com padrões prédefinidos.__

{-# OPTIONS_HADDOCK prune #-}

module Tarefa1 where

import System.Random
import Data.Char
import Types

geraAleatorios :: Int -> Int -> [Int]
geraAleatorios n seed = let gen = mkStdGen seed 
                        in take n $ randomRs (0,99) gen 

subLista :: Int -> [a] -> [[a]]
subLista _ [] = []
subLista n l = take n l: subLista n (drop n l)

convertePeca :: Int -> Piece
convertePeca p  | p == 3 = Food Big
                | p>=0 && p<=70 = Food Little
                | p>=71 && p<=99 = Wall

printCorridor :: [Piece] -> String
printCorridor [] = "\n"
printCorridor (h:t) = (show h) ++ (printCorridor t)  


converteCorredor :: [Int] -> Corridor
converteCorredor [] = []
converteCorredor (x:xs) = (convertePeca x):(converteCorredor xs)

converteLabirinto :: [[Int]] -> Maze
converteLabirinto [] = []
converteLabirinto (nrs) = map (\c -> converteCorredor c) nrs

-- * Funções (auxiliares) usadas na contrução das funções __"allMaze"__ e __"generateMaze"__, apresentadas no final da documentação desta tarefa.  

-- | == Descrição da função
-- A função __telhado__ é responsável pela criação do primeiro e último corredor. Esta recebe um valor numérico do tipo /Int/ e, sendo __n__ o valor que a função recebe,
-- o resultado de "@telhado n@" é uma lista de /Wall/ com n elementos.  

-- | == Exemplos
-- * @telhado 0 = []@
-- * @telhado 1 = [Wall]@
-- * @telhado 15 = [Wall,Wall,Wall,Wall,Wall,Wall,Wall,Wall,Wall,Wall,Wall,Wall,Wall,Wall,Wall]@

telhado :: Int -> Corridor 
telhado x = replicate x Wall

-- | == Descrição da função 
-- Esta função, a partir do número de corredores fornecido como argumento, calcula o número de corredores acima da casa dos fantasmas, desconsiderando o corredor logo acima da casa e o primeiro corredor do labirinto
-- dado que esse é gerado pela função __telhado__. 

-- | == Exemplos 
-- * @numberOfUpWalls 10 = 1@
-- * @numberOfUpWalls 21 = 7@

numberOfUpWalls :: Int -> Int 
numberOfUpWalls y = if even y
                    then (div y 2) - 4
                    else (div y 2) - 3

-- | == Descrição da função 
-- O papel desta função é juntar uma /Wall/ no início e no final de cada corridor do /Maze/ fornecido.

-- | == Exemplos 
-- * @juntaWalls [[Empty,Empty],[Food Big,Empty]] = [[__Wall__,Empty,Empty,__Wall__],[__Wall__,Food Big,Empty,__Wall__]]@
-- * @juntaWalls [[Wall,Empty,Food Little],[Food Little,Food Big,Wall],[Empty,Food Little,Empty]] = [[__Wall__,Wall,Empty,Food Little,__Wall__],[__Wall__,Food Little,Food Big,Wall,__Wall__],[__Wall__,Empty,Food Little,Empty,__Wall__]]@

juntaWalls :: Maze -> Maze 
juntaWalls [] = []
juntaWalls (x:xs) = ([Wall] ++ x ++ [Wall]) : juntaWalls xs


-- * Funções utilizadas para construir os corredores que contêm a casa dos fantasmas, o corredor que a antecede e o que a sucede 

-- | == Descrição da função
-- Esta função, a partir do comprimento dos corredores do labirinto (fornecido como um valor numérico do tipo /Int/), calcula o número de peças aleatórias que existirão entre a casa dos fantasmas e um dos limites do labirinto. Ou seja, considerando o corredor central de um labirinto com um número impar de corredores, 
-- o número de peças aleatórias que esta função calcula corresponde às peças que estão entre o /Empty/ do tunel esquerdo, por exemplo, e o /Empty/ antes da parede da casa, que é predefinido.

-- | == Exemplos
-- * @numberofColAle 15 = 1@
-- * @numberofColAle 50 = 19@

numberofColAle :: Int -> Int 
numberofColAle x = if even x 
                   then div (x - 12) 2
                   else div (x - 13) 2     

linhas125 :: Int -> Int -> Maze
linhas125 x sd 
      | even x = [[Wall] ++ p0 ++ take 10 (repeat (Empty)) ++ p1 ++ [Wall]] ++
                 [[Wall] ++ p2 ++ [Empty] ++  take 3 (repeat (Wall)) ++ take 2 (repeat (Empty)) ++ take 3 (repeat (Wall)) ++ [Empty] ++ p3 ++ [Wall]] ++
                 [[Wall] ++ p8 ++ take 10 (repeat (Empty)) ++ p9 ++ [Wall]] 

      | otherwise = [[Wall] ++ p0 ++ take 11 (repeat (Empty)) ++ p1 ++ [Wall]] ++ 
                    [[Wall] ++ p2 ++ [Empty] ++  take 3 (repeat (Wall)) ++ take 3 (repeat (Empty)) ++ take 3 (repeat (Wall)) ++ [Empty] ++ p3 ++ [Wall]] ++                 
                    [[Wall] ++ p8 ++ take 11 (repeat (Empty)) ++ p9 ++ [Wall]]

     where p0 = (parteem10 x sd)!!0
           p1 = (parteem10 x sd)!!1
           p2 = (parteem10 x sd)!!2
           p3 = (parteem10 x sd)!!3
           p8 = (parteem10 x sd)!!8
           p9 = (parteem10 x sd)!!9             

linha34 :: Int -> Int -> Int -> Maze
linha34 x y sd 
        | even x && even y = [[Empty] ++ p4 ++ [Empty] ++ [Wall] ++ take 6 (repeat (Empty)) ++ [Wall] ++ [Empty] ++ p5 ++ [Empty]] ++
                             [[Empty] ++ p6 ++ [Empty] ++ take 8 (repeat (Wall)) ++ [Empty] ++ p7 ++ [Empty]]
        | even x && odd y = [[Empty] ++ p4 ++ [Empty] ++ [Wall] ++ take 6 (repeat (Empty)) ++ [Wall] ++ [Empty] ++ p5 ++ [Empty]] ++
                            [[Wall] ++ p6 ++ [Empty] ++ take 8 (repeat (Wall)) ++ [Empty] ++ p7 ++ [Wall]]
        | odd x && even y = [[Empty] ++ p4 ++ [Empty] ++ [Wall] ++ take 7 (repeat (Empty)) ++ [Wall] ++ [Empty] ++ p5 ++ [Empty]] ++
                            [[Empty] ++ p6 ++ [Empty] ++ take 9 (repeat (Wall)) ++ [Empty] ++ p7 ++ [Empty]]
        | odd x && odd y = [[Empty] ++ p4 ++ [Empty] ++ [Wall] ++ take 7 (repeat (Empty)) ++ [Wall] ++ [Empty] ++ p5 ++ [Empty]] ++
                           [[Wall] ++ p6 ++ [Empty] ++ take 9 (repeat (Wall)) ++ [Empty] ++ p7 ++ [Wall]]
       where p4 = (parteem10 x sd)!!4
             p5 = (parteem10 x sd)!!5
             p6 = (parteem10 x sd)!!6
             p7 = (parteem10 x sd)!!7

-- | == Descrição da função
-- Esta função usa o comprimeiro de um corredor (primeiro argumento) e uma /seed/ (segundo argumento)
-- para gerar uma lista de peças (/Corridor/) usando as funções: __numberofColAle__;__geraAleatorios__ e __converteCorredor__.

-- | A função __geraAleatorios__ recebe dois valores numéricos do tipo /Int/ e devolve uma lista de /Int/. Sendo __n__ e __sd__ os argumentos da função (por esta ordem),
-- a função cria uma lista de __n__ números inteiros com a /seed/ __sd__.

-- | A função __converteCorredor__ recebe uma lista de /Int/ e, aplicando os parâmetros da função __convertePeca__ (que recebe um número inteiro de __0__ a __99__ e associa uma peça a esse número),
-- devolve um /Corridor/ com as peças correspondes aos valores.

-- | Para manter a aleatoriedade, é usada a expressão __@(sd + 2)@__ de forma a não haja padrões entre estas peças e os corredores acima e abaixo da casa dos fantamas.
    
-- | == Exemplos
-- * @alminicorredor 10 1 = []@
-- * @alminicorredor 15 0 = [Food Little,Wall,Food Little,Food Little,Food Litte,Wall,Food Little,Wall,Food Little,Food Little]@

-- | __Nota:__ O comprimento do labirinto será no mínimo __15__. 

alminicorredor :: Int -> Int -> Corridor 
alminicorredor x sd = converteCorredor (geraAleatorios (10*(numberofColAle x)) (sd+2)) 

-- | == Descrição da função 
-- A função __parteem10__ gera uma lista de listas de peças (/Maze/) de tamanhos iguais que corresponde aos dez espaços que requerem peças aleatórias nos corredores da casa dos fantasmas, assim como no corredor logo acima e no corredor logo abaixo.
-- A necessidade de criar dez listas de peças deve-se ao facto de que o /Maze/ requer peças aleatórias do lado esquedo e direito do "bloco" onde fica a casa, que se encontra em cinco corredores consecutivos.

-- | Esta função tem papel fundamental nas funções __linhas125__ e __linhas34__, responsáveis por gerar os cinco corredores correspondentes à casa dos fantasmas.

-- | == Exemplos 
-- * @parteem10 10 1 = []@
-- * @parteem10 15 0 = [[Food Little],[Wall],[Food Little],[Food Little],[Food Litte],[Wall],[Food Little],[Wall],[Food Little],[Food Little]]@

parteem10 :: Int -> Int -> Maze 
parteem10 x sd = subLista (numberofColAle x) (alminicorredor x sd)  

-- | == Descrição da função
-- Esta função é responsável por gerar os cinco corredores que contêm a casa dos fantasmas e as peças /Empty/ que o rodeiam da seguinte forma: "@middle x y sd = [l1] ++ [l2] ++ [l3] ++ [l4] ++ [l5]@", sendo __l1__,__l2__,__l3__,__l4__ e __l5__ os corredores extraídos
-- das funções __linhas125__ e __linhas34__. 

middle :: Int -> Int -> Int -> Maze 
middle x y sd = [l1] ++ [l2] ++ [l3] ++ [l4] ++ [l5]
       where l1 = (linhas125 x sd)!!0
             l2 = (linhas125 x sd)!!1
             l3 = (linha34 x y sd)!!0
             l4 = (linha34 x y sd)!!1
             l5 = (linhas125 x sd)!!2

-- * Função usada para gerar os corredores acima e abaixo dos corredores da casa dos fantasmas

-- | == Descrição da função
-- A função __allMaze__, a partir da largura do labirinto, altura e uma /seed/ (os três argumentos do tipo /Int/, por esta ordem), gera os corredores que devem ser apresentados acima e abaixo dos corredores gerados pela função __middle__.

-- | O resultado apresentado é do tipo "@(Maze,Maze)@", sendo que o primeiro elemento serão os corredores acima e o segundo, os corredores abaixo (dos corredores que englobam a casa dos fantasmas).

allMaze :: Int -> Int -> Int -> (Maze,Maze) 
allMaze x y sd = splitAt (number) (juntaWalls (converteLabirinto (subLista (x-2) (geraAleatorios ((x-2)*(y-7)) sd))))
      where  number = numberOfUpWalls y

-- * __Objetivo final da Tarefa 3__  

-- | == Descrição da função
-- Como função objetivo desta tarefa, a __generateMaze__ constroi todo o labirinto (com as suas restrições predefinidas e dimensões e /seed/ fornecidas pelo usuário) da seguinte forma: 

-- | __"@[telhado x] ++ up ++ (middle x y sd) ++ down ++ [telhado x]@"__, sendo __x__,__y__ e __sd__ os argumentos da função (seguindo esta ordem) e __(up,down)__ = "@allMaze x y sd@". 

-- | __Nota:__ Caso a largura do labirinto seja inferior a __15__ ou o comprimento seja inferior a __10__, a função devolve: @error "Digite um labirinto com dimencoes iguais ou superiores a 15 por 10"@.

generateMaze :: Int -> Int -> Int -> Maze
generateMaze x y sd 
  | x >= 15 && y >= 10 = [telhado x] ++ up ++ (middle x y sd) ++ down ++ [telhado x]
  | otherwise = error "Digite um labirinto com dimencoes iguais ou superiores a 15 por 10"
     where (up,down) = allMaze x y sd

-- * "/Print/" do labirinto

-- | == Descrição da função
-- Para facilitar a visulização da função em testes efuados, foi criada a função __printAllMaze__ que imprime na tela (do terminal) as peças do labirinto da forma correta e ordenada.

printAllMaze :: Int -> Int -> Int -> IO () 
printAllMaze x y sd = putMaze (generateMaze x y sd)

putMaze m = putStr (printMaze m)

