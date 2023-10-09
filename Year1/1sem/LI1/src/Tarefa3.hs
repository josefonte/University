-- | __Nesta tarefa, o objetivo foi criar um conjunto de funções com a finalidade de converter o labirinto para um conjunto de instruções (menor possível).__ 

{-# OPTIONS_HADDOCK prune #-}

module Tarefa3 where

import Tarefa2 
import Tarefa1 
import Types

instrucaoValida :: Maze -> Instruction -> Bool
instrucaoValida m (Instruct []) = True
instrucaoValida m (Instruct ((n,p):xs)) = if n > x
                                          then False
                                          else instrucaoValida m (Instruct xs)
                                        where x = length (head m)

-- * Funções usadas para converter peças em instruções

-- | == Descrição da função
-- Esta função é utilizada para converter uma peça numa instrução e constitui o primeiro passo para converter todo o labirinto em /Instructions/.

-- | == Exemplos
-- * @fromPieceToInst Wall = Instruct [(1,Wall)]@
-- * @fromPieceToInst Empty = Instruct [(1,Empty)]@

fromPieceToInst :: Piece -> Instruction 
fromPieceToInst p = Instruct [(1,p)]

-- | == Descrição da função
-- Nesta função, é usada a função anterior em todos os elementos do corredor e devolve uma lista de /Instruction/ que será a base das funções posteriores para comprimir a informação.

-- | == Exemplos
-- * @fromCorridorToInst [Wall,Wall] = [Instruct [(1,Wall)],Instruct [(1,Wall)]]@
-- * @fromCorridorToInst [Food Big,Empty,Food Little] = [Instruct [(1,Food Big)],Instruct [(1,Empty)],Instruct [(1,Food Little)]]@

fromCorridorToInst :: Corridor -> [Instruction] 
fromCorridorToInst p = map (fromPieceToInst) p

aux2 :: [Instruction] -> [(Int,Piece)]
aux2 [] = []
aux2 ((Instruct [(a,b)]):xs) = (a,b) : aux2 xs

-- * Funções utilizadas para compactar as instruções de um corredor e criar uma lista de /Instrution/ correspondente a todos os corredores do labirinto

-- | == Descrição da função 
-- Esta função usa as funções __fromCorridorToInst__; __agrupaIguais__ (tal como o nome sugere, agrupa instruções iguais); __compactaInstAux__ (recebe uma lista de listas e une as listas (com instruções iguais) do tipo "@Instruct [(1,Piece)]@", criando, desta forma, instruçoẽs do tipo "@Instruct [(n,Piece)]@", 
-- sendo n-1 o número de vezes que a peça se repete seguidamente num determinado segmento) e __aux2__ que usa o output anterior como argumento para criar listas de tuplos do tipo @[(Int,Piece)]@, complilando todos os tuplos que recebe numa só lista. Finalmente, é adicionado o "@Instruct@" no início para corresponder ao tipo do output da função __compactCorridor__.   

-- | == Exemplos
-- * @compactCorridor [Wall,Wall,Wall] = Instruct [(3,Wall)]@
-- * @compactCorridor [Wall,Empty,Empty,Food Little] = Instruct [(1,Wall),(2,Empty),(1,Food Little)]@

compactCorridor  :: Corridor -> Instruction 
compactCorridor c = Instruct (aux2 $ compactaInstAux $ agrupaIguais $ fromCorridorToInst c)

compactaInstAux :: [[Instruction]] -> [Instruction]
compactaInstAux [x] = [Instruct [((length x),(fromIntrToPice (head x)))]] 
compactaInstAux (x:xs) = compactaInstAux [x] ++ compactaInstAux xs

agrupaIguais :: [Instruction] -> [Instructions]
agrupaIguais [] = []
agrupaIguais [x] = [[x]]
agrupaIguais (x:xs) = if fromIntrToPice x == fromIntrToPice (head xs) 
                      then (x:c1) : c2
                      else [x] : (c1:c2)
                   where (c1:c2) = agrupaIguais xs 

fromIntrToPice :: Instruction -> Piece
fromIntrToPice (Instruct [(n,p)]) = p

-- | == Descrição da função 
-- Esta função aplica a função __compactCorridor__ a todos os corredores do /Maze/ fornecido e devolve, portanto, uma lista de /Intruction/ em que cada elmento corresponde a um corredor (pela ordem como estão dispostos no labirinto).

-- | == Exemplos
-- * @compactMazeBeta [[Wall,Wall,Wall],[Empty,Food Little,Empty],[Wall,Wall,Wall]] = [Instruct [(3,Wall)],Instruct [(1,Empty),(1,Food Little),(1,Empty)],Instruct [(3,Wall)]]@
-- * @compactMazeBeta [[Wall,Wall,Wall,Wall],[Wall,Food Big,Food Little,Wall],[Wall,Wall,Wall,Wall]] = [Instruct [(4,Wall)],Instruct [(1,Wall),(1,Food Big),(1,Food Little),(1,Wall)],Instruct [(4,Wall)]]@

compactMazeBeta :: Maze -> [Instruction] 
compactMazeBeta m = map (compactCorridor) m 

-- * Função utilizada para retirar /Intructions/ repetidas e trocá-las por @"Repeat n"@ (sendo n a posição na lista onde uma determinada /Instruction/ aparece pela primeira vez)

-- | == Descrição da função
-- A funçao __repeats__ recebe uma lista do tipo /Instructions/ e um acumulador (que será __0__ no contexto deste problema) e testa, primeiramente, se o primeiro elemento da /Instructions/ (ou seja, da lista de /Instruction/) pertence à /tail/ da lista e se não é do tipo "@Repeat n@". 
-- __Nota:__ o primeiro elemento nunca será do tipo "@Repeat n@" se o primeiro argumento da função for a aplicação da função __compactMazeBeta__ a 
-- um determinado labirinto. 

-- | === Se ambas as condições forem satisfeitas: 
-- A função é aplicada à expressão: "__@([x] ++ (trocaNasLinhasN (Repeat ac) (mapearIguais x xs) xs))@__" (sendo __@(x:xs)@__ a lista de /Instruction/ e __@ac@__ o acumulador do tipo /Int/)  
-- utilizando o mesmo acumulador como segundo argumento. 

-- | A função __trocaNasLinhasN__ recebe um determinado elemento e duas listas (sendo a primeira, uma lista de inteiros), e devolve uma lista. Admitindo que os argumentos desta função são __@a@__, __@l1@__ e __@l2@__, a função
-- __trocaNasLinhasN__, troca o elemento __@a@__ por todos os elementos da lista __@l2@__ cujo valor da posição na lista esteja contido na lista __@l1@__.

-- | A função __mapearIguais__ recebe um elemento e uma lista de elementos, e devolve uma lista de inteiros. Sendo __@a@__ e __@l@__ os argumentos da função (por esta ordem), a função __mapearIguais__ devolve uma lista com as posições dos elementos da lista __@l@__ que são iguais a iguais a __@a@__.

-- | === Se umas das condições for falsa:
-- A funcão irá calcular o resultado da expressão: "__@x : repeats xs (ac+1)@__". __Nota:__ o resultado final é apresentado pela função quando nesta não contiver quaisquer expressões do tipo @"Instruct i"@ iguais, sendo __i__ uma lista de __@(Int,Piece)@__.

-- | == Exemplos
-- * @repeats __[Instruct [(3,Wall)]__,Instruct [(1,Empty),(1,Food Little),(1,Empty)],__Instruct [(3,Wall)]__] 0 = [Instruct [(3,#)],Instruct [(1,Empty),(1,Food Little),(1,Empty)],__Repeat 0__]@
-- * @repeats __[Instruct [(4,Wall)]__,__Instruct [(1,Wall),(2,Empty),(1,Wall)]__,__Instruct [(1,Wall),(2,Empty),(1,Wall)]__,__Instruct [(4,Wall)]]__ 0 = [Instruct [(4,Wall)],Instruct [(1,Wall),(2,Empty),(1,Wall)],__Repeat 1__,__Repeat 0__]@

-- | __Nota:__ Nos exemplos acima apresentados, foram deixadas a negrito as /Instruction/ iguais que a função recebeu e os "@Repeat n@" no seu resultado.

repeats :: Instructions -> Int -> Instructions 
repeats [] ac = []
repeats (x:xs) ac = if elem x xs && notRepeat x
                    then repeats ([x] ++ (trocaNasLinhasN (Repeat ac) (mapearIguais x xs) xs)) ac 
                    else x : repeats xs (ac+1)

notRepeat :: Instruction -> Bool
notRepeat (Repeat n) = False
notRepeat _ = True 

trocaNasLinhasN :: a -> [Int] -> [a] -> [a]
trocaNasLinhasN a (x:xs) [] = [] 
trocaNasLinhasN a [] l = l
trocaNasLinhasN a (x:xs) l = trocaNaLinhaN a x (trocaNasLinhasN a xs l) 

trocaNaLinhaN :: a -> Int -> [a] -> [a]
trocaNaLinhaN _ _ [] = []
trocaNaLinhaN a 0 (x:xs) = a:xs
trocaNaLinhaN a n (x:xs) = x : trocaNaLinhaN a (n-1) xs

mapearIguais :: Eq a => a -> [a] -> [Int]
mapearIguais a [] = []
mapearIguais a l = mIAux a l 0 

mIAux :: Eq a => a -> [a] -> Int -> [Int]
mIAux a [] ac = []
mIAux a (x:xs) ac = if a == x 
                    then ac : mIAux a xs (ac+1)
                    else mIAux a xs (ac+1)

-- * __Objetivo final da Tarefa 3__

-- | == Descrição da função
-- Esta função aplica a função __compactMazeBeta__ ao /Maze/ que recebe como input 
-- e usa o resultado como primeiro argumento da função __repeats__, sendo o segundo argumento __0__. Simplificando, a função faz a seguinte operação: "__@repeats (compactMazeBeta m) 0@__" (sendo __m__ o /Maze/ que a função __compactMaze__ recebe).

-- | == Exemplos 
-- * @compactMaze [__[Wall,Wall,Wall]__,[Wall,Empty,Wall],__[Wall,Wall,Wall]__] = [Instruct [(3,Wall)],Instruct [(1,Wall),(1,Empty),(1,Wall)],__Repeat 0__]@
-- * @compactMaze [__[Wall,Wall,Wall,Wall]__,__[Wall,Empty,Food Little,Wall]__,[Wall,Food Big,Empty,Wall],__[Wall,Empty,Food Little,Wall]__,[Wall,Empty,Empty,Wall],__[Wall,Wall,Wall,Wall]__] = [Instruct [(4,Wall)],Instruct [(1,Wall),(1,Empty),(1,Food Little),(1,Wall)],Instruct [(1,Wall),(1,Food Big),(1,Empty),(1,Wall)],__Repeat 1__,Instruct [(1,Wall),(2,Empty),(1,Wall)],__Repeat 0__]@


compactMaze :: Maze -> Instructions 
compactMaze m = repeats (compactMazeBeta m) 0

--sizeInstructions :: Instructions -> Int
--sizeInstructions ins = length ins 



