{- | 

= Introdução

Nesta Tarefa, o objetivo foi criar um /bot/ que fosse o mais eficiente possível a obter pontos.

= Desenvolvimento

Para que o /bot/ se adaptasse a cada situação do jogo, criamos três modos diferentes que ele pode adotar:

 * Escape Mode: quando existem /Ghosts/ próximos do /bot/, este vai se mover de forma a aumentar a distância entre ele e os /Ghosts/.

 * Chase Mode: quando o /bot/ encontra uma /Food Big/, começa a perseguir um dos /Ghosts/ até o conseguir comer.

 * Look For Food Mode: quando nenhumas das condições que invocam os modos acimas são satisfeitas, o /bot/ apenas procura por /Foods/. 

= Conclusão

Consideramos que o /bot/, após comer o primeiro fantasma, devesse sair do Chase Mode uma vez que haver /Ghosts/ no modo /Alive/
o deixa vulnerável.

Esta Tarefa, apesar de exigir bastantes testes, não revelou muita dificuldade ao grupo, uma vez que já tinhamos entendido a lógica
da Tarefa 5. 

-} 

{-# OPTIONS_HADDOCK prune #-}

module Tarefa6 where 

import Types
import Tarefa2
import Tarefa5

-- | Dado uma lista de /Players/, devolve as coordenadas de todos os /Ghosts/.
ghostCoords :: [Player] -> [Coords]
ghostCoords [] = []
ghostCoords (x:xs) = if ghostORPacmanvalor x == 1 
                     then getPlayerCoords x : ghostCoords xs 
                     else ghostCoords xs 

biggestDistAux :: [(Coords,Coords)] -> Coords
biggestDistAux [(a,b)] = a
biggestDistAux ((a,b):(c,d):xs) = if dist a b < dist c d 
                                  then biggestDistAux ((c,d):xs)
                                  else biggestDistAux ((a,b):xs)

-- | Ao contrário da função __smallerDist__ (Tarefa 4), esta função calcula a coordenada para o qual o /bot/
-- se deverá mover para aumentar a distância aos adversários.
biggestDist :: [Coords] -> [Coords] -> Coords
biggestDist choices pacs = biggestDistAux (allOptions choices pacs) 

-- | Função responsável pelo Escape Mode. 

-- | Determinada a /Play/ que o /bot/ terá que executar para aumentar a distância aos /Ghosts/.
escapeGhostsMode :: Int -> State -> Maybe Play 
escapeGhostsMode id st@(State maze players level)  
    | choices (x,y) maze == [] = Nothing
    | ch == [] = Just $ Move id $ downs orPac
    | biggestDist ch ghos == (x-1,y) = Just $ Move id U 
    | biggestDist ch ghos == (x+1,y) = Just $ Move id D 
    | biggestDist ch ghos == (x,y-1) = Just $ Move id L 
    | biggestDist ch ghos == (x,y+1) = Just $ Move id R 
    | otherwise = Just $ Move id orPac 
   where ch =  {- filter (/= coordback pac) $ -} choices (x,y) maze 
         (x,y) = getPlayerCoords pac
         ghos = map getPlayerCoords $ rmPacmans players  
         pac = encID id players
         orPac = getPlayerOrientation pac

-- | Função responsável pelo Chase Mode.

-- | Usa a função __smallerDist__ da Tarefa 4 como auxiliar para determinar que /Play/ o /bot/ terá de executar para 
-- que diminua a sua distância aos /Ghosts/.
chaseGhostsMode :: Int -> State -> Maybe Play 
chaseGhostsMode id st@(State maze players level)  
    | choices (x,y) maze == [] = Nothing
    | ch == [] = Just $ Move id (downs orPac)
    | smallerDist ch pacs == (x-1,y) = Just $ Move id U 
    | smallerDist ch pacs == (x+1,y) = Just $ Move id D 
    | smallerDist ch pacs == (x,y-1) = Just $ Move id L 
    | smallerDist ch pacs == (x,y+1) = Just $ Move id R 
    | otherwise = Just $ Move id orPac 
   where ch = filter (/= coordback pac) $ choices (x,y) maze 
         (x,y) = getPlayerCoords pac
         pacs = ghostCoords players  
         pac = encID id players
         orPac = getPlayerOrientation pac

coordsWithFoodAux :: Maze -> [Coords] -> [Coords]
coordsWithFoodAux _ [] = []
coordsWithFoodAux m (x:xs) = if revealPiece x m == Food Big || revealPiece x m == Food Little 
                             then x : coordsWithFoodAux m xs 
                             else coordsWithFoodAux m xs
                             
corridorToCoords :: Int -> Corridor -> [Coords]
corridorToCoords line c = [(line,s) | s <- [0..((length c)-1)]]

-- | Dado um /Maze/, calcula todas as coordenadas que lhe pertencem (Esta função recebe um acumulador que deverá ser colocado a 0).
mazeToCoords :: Int -> Maze -> [Coords]
mazeToCoords _ [] = []
mazeToCoords ac (x:xs) = (corridorToCoords ac x) ++ mazeToCoords (ac+1) xs

-- | Dado um /Maze/, determina a lista de coordenadas que contêm /Foods/ usando a função anterior como auxiliar. 
coordsWithFood :: Maze -> [Coords]
coordsWithFood m = coordsWithFoodAux m $ mazeToCoords 0 m

-- | Função que representa o Look For Food Mode. 

-- | Esta função usa também a função __smallerDist__ (Tarefa 4) para determinar a /Play/ que deverá executar para minimizar a distância às /Foods/. 
lookForFoodMode :: Int -> State -> Maybe Play 
lookForFoodMode id st@(State maze players lvl)  
    | choices (x,y) maze == [] = Nothing
    | ch == [] = Just $ Move id (downs orPac)
    | smallerDist ch foods == (x-1,y) = Just $ Move id U 
    | smallerDist ch foods == (x+1,y) = Just $ Move id D 
    | smallerDist ch foods == (x,y-1) = Just $ Move id L 
    | smallerDist ch foods == (x,y+1) = Just $ Move id R 
    | otherwise = Just $ Move id orPac
   where ch = filter (/= coordback pac) $ choices (x,y) maze 
         (x,y) = getPlayerCoords pac
         foods = coordsWithFood maze  
         pac = encID id players
         orPac = getPlayerOrientation pac

-- | Dada uma coordenada, devolve uma lista de coordenadas que constituem um "bloco" 5 por 5 em que o centro é a coordenada de input.
coordsProx :: Coords -> [Coords]
coordsProx (a,b) = [(c,d) | c <- [a-4..a+4] , d <- [b-4..b+4]]

-- | Determina se pertencem /Ghosts/ às coordenadas próximas do /bot/ usando a função __coordsProx__ como auxiliar.
elemGhostProx :: Coords -> [Player] -> Bool 
elemGhostProx _ [] = False 
elemGhostProx coordPac (x:xs) = if elem (getPlayerCoords x) proxPac 
                                then True 
                                else elemGhostProx coordPac xs
      where proxPac = coordsProx coordPac 

numberOfFoodsCorridor :: Corridor -> Int 
numberOfFoodsCorridor [] = 0 
numberOfFoodsCorridor (x:xs) = if x == Food Little || x == Food Big
                               then 1 + numberOfFoodsCorridor xs 
                               else numberOfFoodsCorridor xs 

-- | Calcula o número de /Foods/ de um /Maze/.
numberOfFoodsMaze :: Maze -> Int 
numberOfFoodsMaze m = sum $ map numberOfFoodsCorridor m 

-- | Testa se, num /Maze, existe apenas uma /Food/ (ou zero).
thereIsOneFood :: Maze -> Bool 
thereIsOneFood m = numberOfFoodsMaze m <= 1 

-- | Esta é a função objetivo desta Tarefa.

-- | A função __bot__ compila todos os modos de agir do Pacman com as suas restrições. Uma vez que, no nosso MiniGame, __KillPacman__, havia dois Pacmans, 
-- então, quando um deles comia a última /Food/ e o outro não recebia coordenadas algumas sobre posições de /Foods/, o que constituia uma excessão.
-- Para dar a volta a esse problema, adicionamos a restrição no __bot__ para que, quando sobra apenas uma /Food/, a função retorna __Nothing__. 
bot :: Int -> State -> Maybe Play
bot id st@(State maze players lvl)
      | thereIsOneFood maze = Nothing
      | elem Alive ghoModes && elemGhostProx paccoord (rmPacmans players) = escapeGhostsMode id st
      | ghoModes == replicate (length ghoModes) Dead = chaseGhostsMode id st
      | otherwise = lookForFoodMode id st
   where ghosmorecl = rmPacmans players  
         ghoModes = map getGhostMode ghosmorecl 
         paccoord = getPlayerCoords $ encID id players
         ghocoords = map getPlayerCoords ghosmorecl

-- | Para que pudessemos utilizar a função __bot__ no __passTime__ da Tarefa 4, tivemos que criar uma função que convertesse __Maybe Play__ para __Play__

-- | No caso da função __bot__ devolver __Nothing__, a função  __botMaybeToPlay__ devolve __Move id Null__, sendo __id__ o ID do /bot/.
botMaybeToPlay :: Int -> State -> Play 
botMaybeToPlay id st = case (bot id st) of 
                          Just p -> p 
                          Nothing -> Move id Null
