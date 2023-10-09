{- |

= Introdução 

O objetivo desta tarefa foi fazer com os /Ghosts/ se movimentassem pelo /Maze/ de forma independente do utilizador e que perseguissem o(s) Pacman(s). 

= Desenvolvimento 

Para implementar este /bot/ usamos quatro maneiras distintas para o /Ghost/ agir: 

 * Chase Mode: sempre que o fantasma está em modo Alive e a mais de quatro peças de distância de todos os Pacmans, este decide a jogada que deve 
   executar quando à sua fente (seguinto a sua orientação) se encontra uma /Wall/. Para isto, calcula a distância entre todas as opções aos Pacmans
   pela métrica de Manhattan, sendo que só reccore à orientação oposta se não houver qualquer outra opção.

 * Chase Mode "four or less": Ao contrário do anterior, este modo é aplicado a cada instante do jogo quando o /Ghost/ está
   próximo de um Pacman. A sua funcionalidade é aumentar a eficácia do /Ghost/ e impedir situações em que o /Ghost/ se movia 
   próximo ao Pacman e não ia ao encontro do mesmo.

 * Scatter Mode: Este modo de agir, invocado quando o /Ghost/ passa para modo /Dead/, faz com que o mesmo, ao encontrar uma /Wall/, se mova para a 
   sua direita (relativa à orientação). Em alternativa, se a direita não constituir uma opção, o /Ghost/ pode virar-se para a esquerda relativa
   e, em último caso, para trás. O /bot/, quando se encontra em Scatter Mode, tende a rodear objetos no sentido horário, sendo este o efeito objetivo
   deste modo.

 * Leave Home: Para evitar que os /Ghosts/ fiquem dentro casa no início e decorrer do jogo, foi criado o modo Leave Home que faz com que eles se retirem
   da casa caso nao haja um /Ghost/ com maior ID dentro da mesma. Esta última condição faz com que saia um de cada vez e evita, na maior parte da vezes,
   que dois /Ghosts/ fiquem na mesma coordenada.    

= Conclusão 

Esta Tarefa exigiu bastantes testes para perceber qual seria a melhor forma de cada /Ghost/ agir consoante as diversas situações em que se encontrava. 
Após resolver todos os pequenos problemas que foram surgindo no decorrer do desenvolvimento desta /bot/, pensamos ter atingido aquilo
que esperavamos.
-}

{-# OPTIONS_HADDOCK prune #-}

module Tarefa5 where 

import Types
import Tarefa2 

-- | Calcula todas as coordenadas vizinhas de uma determinada coordenada, isto é, as coordenadas que estão acima, abaixo, à direita e à esquerda dessa coordenada.
neighborhoodCoords :: Coords -> [Coords] 
neighborhoodCoords (x,y) = [(x+1,y),(x-1,y),(x,y+1),(x,y-1)]

-- | Dada uma coordenada, revela as peças que se encontram na sua vizinhança.
neighborhoodPieces :: Coords -> Maze -> [Piece]
neighborhoodPieces (x,y) maze = map (revealPieceReverse maze) (neighborhoodCoords (x,y))

-- | Dada uma lista de tuplos (/Coords/,/Piece/), remove os tuplos cuja segunda componente seja uma /Wall/.
rmWalls :: Maze -> [(Coords,Piece)] -> [(Coords,Piece)]
rmWalls _ [] = []
rmWalls m (x:xs) = if snd x == Wall || (elem (fst x) $ aberturaCasa m)  
                   then rmWalls m xs 
                   else x : rmWalls m xs  

-- | Usa todas as funções anteriores como auxiliares e, a partir de uma coordenada e um /Maze/, calcula a lista de coordenadas para o qual o /Player/
-- se pode movimentar.
choices :: Coords -> Maze -> [Coords]
choices (x,y) maze = map fst $ rmWalls maze $ zip (neighborhoodCoords (x,y)) (neighborhoodPieces (x,y) maze)

-- | Calcula distâncias entre coordenadas segundo a métrica de Manhattan.
dist :: Coords -> Coords -> Int 
dist (a,b) (c,d) = abs (a-c) + abs (b-d)

-- | Extrai as coordenadas dos Pacmans num determinado instante.
pacmansCoords :: [Player] -> [Coords]
pacmansCoords [] = []
pacmansCoords (x:xs) = if ghostORPacmanvalor x == 2 
                       then getPlayerCoords x : pacmansCoords xs 
                       else pacmansCoords xs 

-- | A partir das coordenadas possíveis do /bot/ e das coordenadas dos adversários, calcula a lista de possibilidades como tuplos constituidos
-- por uma determinada opção do /bot/ e a coordenada de um adversário.
allOptions :: [Coords] -> [Coords] -> [(Coords,Coords)]
allOptions choices pacs = [(a,b) | a <- choices, b <- pacs]  

smallerDistAux :: [(Coords,Coords)] -> Coords
smallerDistAux [(a,b)] = a
smallerDistAux ((a,b):(c,d):xs) = if dist a b > dist c d 
                                  then smallerDistAux ((c,d):xs)
                                  else smallerDistAux ((a,b):xs)

-- | Recebe dois conjuntos de /Coords/ como input (que representam as opções do /bot/ e as coordenadas
-- dos adversários) e seleciona a opção do /bot/ que lhe permite ficar mais próximo ao adversário.
smallerDist :: [Coords] -> [Coords] -> Coords
smallerDist choices pacs = smallerDistAux (allOptions choices pacs) 

-- | Esta função testa se a coordenada seguinte do /bot/ (considerando a sua orientação) tem uma /Wall/ ou pertence 
-- ao conjunto de coordenadas da abertura da casa. Em caso afirmativo, move-se para outra direção de forma a ficar mais próximo ao adversário (usando a __smallerDist__ como auxiliar).
-- Caso não se verifique essa condição, continua a mover-se de acordo com a sua orientação.
chaseMode :: State -> Int -> Play 
chaseMode st@(State maze players level) id 
    | ch == [] = Move id Null
    | (condition1 || condition2) && smallerDist ch pacs == (x-1,y) = Move id U 
    | (condition1 || condition2) && smallerDist ch pacs == (x+1,y) = Move id D 
    | (condition1 || condition2) && smallerDist ch pacs == (x,y-1) = Move id L 
    | (condition1 || condition2) && smallerDist ch pacs == (x,y+1) = Move id R 
    | otherwise = Move id orGho 
   where ch = choices (x,y) maze 
         (x,y) = getPlayerCoords plgho
         pacs = pacmansCoords players  
         plgho = encID id players
         orGho = getPlayerOrientation plgho
         condition1 = revealPieceOr (Move id orGho) st == Wall
         condition2 = elem (movimento (x,y) orGho) (aberturaCasa maze)

-- | A estruta desta função é semelhante à anterior, porém, não existem restrições quando à informação próxima /Piece/. Como tal, o /Ghost/
-- fica mais eficiente quando estiver próximo do seu alvo. 
chaseMode4orless :: State -> Int -> Play 
chaseMode4orless st@(State maze players level) id 
    | choices (x,y) maze == [] = Move id Null
    | ch == [] = Move id (downs orGho)
    | smallerDist ch pacs == (x-1,y) = Move id U 
    | smallerDist ch pacs == (x+1,y) = Move id D 
    | smallerDist ch pacs == (x,y-1) = Move id L 
    | smallerDist ch pacs == (x,y+1) = Move id R 
    | otherwise = Move id orGho 
   where ch = filter (/= coordback plgho) $ choices (x,y) maze 
         (x,y) = getPlayerCoords plgho
         pacs = pacmansCoords players  
         plgho = encID id players
         orGho = getPlayerOrientation plgho

-- | Calcula a coordenada atrás do /Player/ relativamente à sua orientação.
coordback :: Player -> Coords 
coordback p 
   | orI == U = (x+1,y)
   | orI == D = (x-1,y)
   | orI == R = (x,y-1)
   | orI == L = (x,y+1)
   | orI == Null = (x,y)
       where (x,y) = getPlayerCoords p 
             orI = getPlayerOrientation p  

-- | Calcula a direita relativa do /Player/.
rights :: Orientation -> Orientation 
rights R = D 
rights L = U 
rights U = R 
rights D = L 
rights Null = Null

-- | Calcula a esquerda relativa do /Player.
lefts :: Orientation -> Orientation 
lefts R = U 
lefts L = D 
lefts U = L 
lefts D = R 
lefts Null = Null

-- | Compila as funções anteriores e a função __downs__ (Tarefa 2) para calcular a orientação relativa de um /Player/, dada uma orientação (orientação relativa que
-- queremos determinar) e um /Player/.
relativeOr :: Orientation -> Player -> Play 
relativeOr or p 
    | or == R = Move id $ rights orI  
    | or == L = Move id $ lefts orI 
    | or == D = Move id $ downs orI
       where orI = getPlayerOrientation p
             id = getPlayerID p

-- | Esta função cria um modo de se movimentar de forma a que, cada vez que o /bot/ se depare com uma parede, se mova para a direita (caso esta não contenha 
-- também uma parede, nesse caso, escolherá a esquerda relativa ou voltar para trás, por esta ordem).
scatterMode :: State -> Int -> Play
scatterMode st@(State maze players lvl) id 
     | revealPieceOr (Move id orI) st /= Wall = Move id orI 
     | revealPieceOr (relativeOr R gho) st /= Wall = relativeOr R gho
     | revealPieceOr (relativeOr L gho) st /= Wall = relativeOr L gho
     | revealPieceOr (relativeOr D gho) st /= Wall = relativeOr D gho
     | otherwise = Move id Null 
    where gho = encID id players
          orI = getPlayerOrientation gho 

-- | Dado um /Maze/, calcula a lista de coordenadas que constituem a casa dos fantasmas e a sua abertura.
homeCoords :: Maze -> [Coords]  
homeCoords m = if even y 
               then aberturaCasa m ++ zip (repeat (a+1)) [ym-3,ym-2,ym,ym,ym+1,ym+2]
               else aberturaCasa m ++ zip (repeat (a+1)) [ym-3,ym-2,ym-1,ym,ym+1,ym+2,ym+3]
      where (a,b) = (aberturaCasa m)!!1 
            x = length m 
            y = length $ head m
            ym = div y 2 

-- | Recebe um /Maze/ e uma lista de /Players/ e testa se algum deles está dentro da casa dos fantasmas.
insideHome :: Maze -> [Player] -> Bool 
insideHome _ [] = False 
insideHome m (x:xs) = if elem (getPlayerCoords x) $ homeCoords m
                      then True 
                      else insideHome m xs   

-- | A partir de um /Int/ (que representa um ID) e uma lista de /Players/, devolve uma lista com todos os /Players/ que têm ID superior ao 
-- valor fornecido como primeiro argumento. No caso de não existirem /Players/ com essa condição, devolve a lista vazia. 
ghoMaiorID :: Int -> [Player] -> [Player]
ghoMaiorID _ [] = []
ghoMaiorID id (x:xs) = if getPlayerID x > id 
                       then x : ghoMaiorID id xs 
                       else ghoMaiorID id xs 

leaveHomeOnePlayer :: Player -> Maze -> Play 
leaveHomeOnePlayer (Ghost (GhoState (i,(x,y),v,o,xp,hp) mode)) maze 
   | revealPiece (x-1,y) maze == Empty = Move i U 
   | elem (x-1,y+1) (aberturaCasa maze) || elem (x-1,y+2) (aberturaCasa maze) = Move i R 
   | elem (x-1,y-1) (aberturaCasa maze) || elem (x-1,y-2) (aberturaCasa maze) = Move i L

-- | Recebe um /State/ e um valor /Int/ e testa se existe /Ghost/ com ID maior do que o valor so segundo argumento.
-- No caso de não se verificar a condição, o /bot/ que tem como ID o segundo argumento move-se de forma a sair da casa dos fantasmas.
-- Pelo contrário, se a condição se verificar, o /bot/ em questão relaliza apenas a /Play/ "Move id Null" (sendo "id", o ID do /bot/), não se movendo.
leaveHome :: State -> Int -> Play 
leaveHome (State maze players level) id = if insideHome maze ghosmaiorID
                                          then Move id Null 
                                          else leaveHomeOnePlayer (encID id players) maze
              where ghosmaiorID = ghoMaiorID id $ rmPacmans players  

-- | Calcula a distância entre o /bot/ e o adversário mais próximo dele.
minDist :: Coords -> [Coords] -> Int 
minDist coord pacs = dist coord (pacmorecloser coord pacs)
    where pacmorecloser _ [c] = c 
          pacmorecloser coord (c1:c2:cs) = if dist c1 coord > dist c2 coord 
                                           then pacmorecloser coord (c2:cs)
                                           else pacmorecloser coord (c1:cs) 

-- | Dado um /State/ e um /Int/ calcula a /Play/ que o /bot/ cujo ID coincide com o segundo argumento deve realizar, tendo em conta todos os modos 
-- de se comportar acima descritos.
ghostPlayOnePlayer :: State -> Int -> Play  
ghostPlayOnePlayer st@(State maze players level) id  
      | elem (getPlayerCoords plgho) hCoords = leaveHome st id 
      | getPlayerOrientation plgho == Null = Move id D
      | getGhostMode plgho == Alive && minDist coord paccoords <= 4 = chaseMode4orless st id
      | getGhostMode plgho == Alive && id == 2 = chaseMode4orless st id -- isto aumenta a eficiencia no chase
      | getGhostMode plgho == Alive = chaseMode st id 
      | getGhostMode plgho == Dead = scatterMode st id 
    where plgho = encID id players 
          hCoords = homeCoords maze 
          paccoords = map getPlayerCoords $ rmGhosts players 
          coord = getPlayerCoords plgho

-- | Esta função tem a mesma estrura da função __ghostPlayOnePlayer__, porém, não usa usa o modo "Leave Home" para que o /bot/ se possa comportar em /Mazes/
-- que não têm a mesma estruta que os /Mazes/ da Tarefa 1.  
ghostPlayOnePlayerAnyMaps :: State -> Int -> Play 
ghostPlayOnePlayerAnyMaps st@(State maze players level) id  
      | getGhostMode plgho == Alive && minDist coord paccoords <= 4 = chaseMode4orless st id
      | getGhostMode plgho == Alive && id == 2 = chaseMode4orless st id 
      | getGhostMode plgho == Alive = chaseMode st id 
      | getGhostMode plgho == Dead = scatterMode st id 
    where plgho = encID id players 
          hCoords = homeCoords maze 
          paccoords = map getPlayerCoords $ rmGhosts players 
          coord = getPlayerCoords plgho

-- | Esta é a função objetivo da Tarefa 5 e calcula a lista de /Plays/ de todos os /Ghosts/ num determinado /State/.
ghostPlay :: State -> [Play]
ghostPlay st@(State maze players level) = map (ghostPlayOnePlayer st) ids 
      where ids = map getPlayerID (rmPacmans players) 
