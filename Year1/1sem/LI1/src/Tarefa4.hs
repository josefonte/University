{- |

= Introdução 

Nesta Tarefa, o objetivo foi fazer com que todos os jogadores reagissem à passagem do tempo e executassem jogadas (definidas na Tarefa 2). 

= Desenvolvimento

Neste documento, para além das funções da Tarefa 4, estão também as funções do MiniGame __PacDungeon__ uma vez que este MiniGame usa funções da Tarefa 4 no seu /passTime/
e a Tarefa 4 passou também a envolver funções do miniGame para que o jogo funcionasse com todos os seus modos. Desta forma, como se criaria ciclo de /imports/ entre 
a Tarefa 4 e o __PacDungeon__, decidimos juntar o Minigame ao documento da Tarefa.

A Tarefa 4 foi progamada de forma a que o /Player/ a ser controlado se mova para a direção que consta na sua informação do /PalayerState/ uma casa (caso tenha velocidade de
de uma jogada por iteração) e os restantes ajam como predefinido nas Tarefas seguintes (/bots/).

Esta Tarefa revelou-se desafiadora pois exigiu de nós uma maior capacidade para pensar sobre os tempos e velocidades e como usar todas essas variáveis para
chegar à resolução do problema, no caso, a passagem do tempo. 

O __PacDungeon__ foi um dos MiniGames que o grupo planeava e tencionava fazer. O seu objetivo é comer todas as comidas do mapa e salvar o Pacman preso entre /Walls/ num mapa
com outros seis /Ghosts/ presos.
Para aumentar a dificuldade, a cada dez segundos (quatro iterações) novos /Ghosts/ são libertados, sendo que dentro do último conjunto de /Walls/ há uma
comida grande, o que torna impossível acabar o jogo antes de todos os fantasmas serem libertados.

O grupo concordou que, entre os dois, este era o MiniGame mais completo uma vez que tem o seu próprio /passTime/ e um /bot/.

= Conclusão 

Tanto a Tarefa 4 quanto o MiniGame tiveram os seus aspetos mais complicados mas tivemos sucesso em executar o que tinhamos em mente.

Esta Tarefa foi sendo atualizada a cada Tarefa posterior concluída e este é o resultado final:

-}

{-# OPTIONS_HADDOCK prune #-}

module Tarefa4 where 

import Types
import Tarefa2
import Tarefa5
import Tarefa6
import StatesModoNormal
import FileUtils

defaultDelayTime = 250 -- 250 ms

-- * Tarefa 4

-- | Dado um ID, remove um /Player/ de uma lista de /Players/.
rmPlayer :: Int -> [Player] -> [Player]
rmPlayer _ [] = []
rmPlayer id (x:xs) = if getPlayerID x == id 
                     then xs
                     else x : rmPlayer id xs

-- | Obtém o valor correspondente à velocidade do /Player/.
getPlayerSpeed :: Player -> Double 
getPlayerSpeed (Pacman (PacState (x,y,z,t,h,l) q c d )) = z
getPlayerSpeed (Ghost (GhoState (x,y,z,t,h,l) q )) = z

-- | A partir de um /Double/ que representa a velocidade de um /Player/, produz um par de /Int/ que determinam o número de jogadas que esse /Player/ 
-- fará em cada iteração.
nPlayPerStep :: Double -- velocidade 
             -> (Int,Int) -- (jogadas em step par, jogadas em step impar)
nPlayPerStep 0 = (0,0)             
nPlayPerStep 0.5 = (1,0)
nPlayPerStep 1 = (1,1)
nPlayPerStep 1.5 = (2,1)
nPlayPerStep 2 = (2,2) 

-- | Testa se não existe qualquer tipo de comida no /Maze/.
cleanedMaze :: Maze -> Bool 
cleanedMaze maze = and $ map cleanedLine maze

cleanedLine :: Corridor -> Bool 
cleanedLine [] = True 
cleanedLine (x:xs) = if x == Food Big || x == Food Little 
                     then False
                     else cleanedLine xs 
                                           
-- | Faz com que todos os /Ghosts/ fiquem em modo /Alive/ e com velocidade de 1.
backGhostToAlive :: [Player] -> [Player] 
backGhostToAlive [] = []
backGhostToAlive ((Ghost (GhoState (i,c,v,or,q,z) Dead)):xs) = Ghost (GhoState (i,c,1,or,q,z) Alive) : backGhostToAlive xs 
backGhostToAlive (x:xs) = x : backGhostToAlive xs

lostTimeMegaOnePlayer :: Double -> Player -> Player 
lostTimeMegaOnePlayer x (Pacman (PacState (i,(x1,y), v, o, p, l) tM mo Mega)) = if x < tM  
                                                                                then Pacman $ PacState (i,(x1,y), v, o, p, l) (tM - x) mo Mega 
                                                                                else Pacman $ PacState (i,(x1,y), 1, o, p, l) 0 mo Normal 
lostTimeMegaOnePlayer _ gho = gho

lostTimeMega :: Double -> [Player] -> [Player]
lostTimeMega _ [] = []
lostTimeMega x (p:ps) = if ghostORPacmanvalor p == 2 && getTimeMega p /= 0
                        then lostTimeMegaOnePlayer x p : lostTimeMega x ps 
                        else p : (lostTimeMega x ps)

-- | Regula todo o sistema de tempo do /Mega/ e modos dos /Players/ em função disso. Se um Pacman está em modo /Mega/, faz com que o mesmo
-- perca 250 unidades (ms) a cada iteração e quando não existem Pacmans no modo Mega, todos os /Ghosts/ passam para o modo /Alive/.                         
lostTimeMegaST :: State -> State 
lostTimeMegaST (State maze players level) = if elem Dead allGModes && timesMega == replicate (length timesMega) 0    
                                            then State maze (backGhostToAlive $ lostTimeMega 250 players) level 
                                            else State maze (lostTimeMega 250 players) level                                          
       where allGModes = map getGhostMode $ rmPacmans players
             allPModes = map getPacmanMode $ rmGhosts players 
             timesMega = map getTimeMega $ rmGhosts players 

-- | Função auxiliar do __passTime__ que aplica a função __play__ (Tarefa 2) ao /Player/ que o utilizador está a controlar.
passTimeOnePlayerPID :: Int -- ID 
                     -> Int -> State -> State 
passTimeOnePlayerPID _ 0 st = st                  
passTimeOnePlayerPID id nStep st@(State maze players level) = if even nStep 
                                                              then quickP evenStep id plOr st 
                                                              else quickP oddStep id plOr st
                                     where player = encID id players
                                           (evenStep,oddStep) = nPlayPerStep $ getPlayerSpeed player
                                           plOr = getPlayerOrientation player    

-- | Função auxiliar do __passTime__ que produz os movimentos dos /Ghosts/. Esta função recorre à função __ghostPlayOnePlayer__ da Tarefa 5.
passTimeOnePlayerBotGhost :: Int -> Int -> State -> State 
passTimeOnePlayerBotGhost _ 0 st = st
passTimeOnePlayerBotGhost id nStep st@(State maze players level) = if even nStep 
                                                                   then quickP evenStep id or st 
                                                                   else quickP oddStep id or st
                                     where player = encID id players
                                           (evenStep,oddStep) = nPlayPerStep $ getPlayerSpeed player
                                           (Move idGho or) = ghostPlayOnePlayer st id 

-- | Esta função determina as duas jogadas consecutivas do Pacman quando este se comporta como /bot/ e, em algumas iterações faz duas jogadas seguidas (quando 
-- se encontra no modo /Mega/).                                           
twoPlaysBotPac :: Int -> State -> State  
twoPlaysBotPac id st = play mov2 (play mov1 st)
           where mov1 = botMaybeToPlay id st 
                 futureSt = play mov1 st 
                 mov2 = botMaybeToPlay id futureSt   

-- | Função auxiliar do __passTime__ que usa a função __botMaybeToPlay__ da Tarefa 6 e a função __twoPlaysBotPac__ para movimentar o /bot/ Pacman.
passTimeOnePlayerBotPac :: Int -> Int -> State -> State 
passTimeOnePlayerBotPac _ 0 st = st
passTimeOnePlayerBotPac id nStep st@(State maze players level) 
    | even nStep && evenStep == 2 = twoPlaysBotPac id st 
    | even nStep = quickP evenStep id or st 
    | odd nStep && oddStep == 2 = twoPlaysBotPac id st 
    | otherwise = quickP oddStep id or st
   where player = encID id players
         (evenStep,oddStep) = nPlayPerStep $ getPlayerSpeed player
         (Move idPac or) = botMaybeToPlay id st

-- | Calcula, em função do PID e tipo de /Player/ (/Ghost/ ou Pacman), as ações de cada /Player/ numa determindada iteração. 
passTimeAux :: Int -> Int -> State -> [Int] -> State 
passTimeAux x pid st@(State maze players level) [] = st 
passTimeAux x pid st@(State maze players level) (id:ids) = if cleanedMaze maze 
                                                           then levelUp st maze1 
                                                           else if id == pid 
                                                                then passTimeAux x pid (passTimeOnePlayerPID id x st) ids
                                                                else if (ghostORPacmanvalor $ encID id players) == 1
                                                                     then passTimeAux x pid (passTimeOnePlayerBotGhost id x st) ids
                                                                     else passTimeAux x pid (passTimeOnePlayerBotPac id x st) ids 

pacsfst :: [Player] -> [Player]
pacsfst [] = []
pacsfst (x:xs) = if ghostORPacmanvalor x == 1 
                 then pacsfst xs ++ [x]
                 else x : pacsfst xs

-- | Recebe a função anterior para completar, desta forma, a Tarefa 4 e ainda é utilizada uma condição para poder usar o __passTime__ no MiniGame __PacDungeon__, uma vez
-- que a interface do jogo completo podia apenas ter um tipo de __passTime__. 
passTime :: Int -> Int -> State -> State
passTime x pid st@(State maze players level) = if (length maze) == 23
                                               then passTimePacDungeon x pid st 
                                               else lostTimeMegaST $ passTimeAux x pid (State maze (pacsfst players) level) ids
        where ids = map getPlayerID $ pacsfst players 

-------------------------------------------------------------------------------------------------------------------------------------

-- * PacDungeon

-- | Lista de /Players/ que o /State/ do jogo contém.
playersPacDungeon :: [Player]
playersPacDungeon = [(Pacman $ PacState (0,(13,14),1,Null,0,3) 0 Open Normal), --Player
                     (Ghost $ GhoState (1,(11,11),1,Null,0,0) Alive), --home
                     (Ghost $ GhoState (2,(11,13),1,Null,0,0) Alive), --home 
                     (Ghost $ GhoState (3,(11,15),1,Null,0,0) Alive), --home 
                     (Ghost $ GhoState (4,(11,17),1,Null,0,0) Alive), --home
                     (Ghost $ GhoState (5,(3,3),1,D,0,0) Alive), --jail NO
                     (Ghost $ GhoState (6,(3,25),1,D,0,0) Alive), --jail NE
                     (Ghost $ GhoState (7,(19,3),1,U,0,0) Alive), --jail SO
                     (Ghost $ GhoState (8,(19,25),1,U,0,0) Alive), --jail SE 
                     (Ghost $ GhoState (9,(6,13),1,R,0,0) Alive), --jail Mid
                     (Ghost $ GhoState (10,(6,15),1,L,0,0) Alive), --jail Mid
                     (Pacman $ PacState (99,(16,14),1,Null,0,0) 0 Open Normal) --pacinjail
                    ] 

-- | /State/ do jogo com o /Maze/ desenhado para o mesmo e a lista de /Players/ anterior (e nível igual a 0).
statePacDungeon :: State
statePacDungeon = State (maze $ loadMaze "maps/mapMG.txt") playersPacDungeon 0 

dimMaze = (23,29)

jailNO :: [Coords]
jailNO = [(3,4),
          (3,2),
          (4,3),
          (2,3)]

jailNE :: [Coords]
jailNE = [(3,26),
          (3,24),
          (4,25),
          (2,25)]

jailSO :: [Coords]
jailSO = [(19,4),
          (19,2),
          (20,3),
          (18,3)]

jailSE :: [Coords]
jailSE = [(19,26),
          (19,24),
          (20,25),
          (18,25)]

jailMid :: [Coords]
jailMid = [(6,12),
           (6,16)]

jailPac :: [Coords]
jailPac = [(16,15),
           (16,13),
           (17,14),
           (15,14)]

-- | Função utilizada para remover simultaneamente todas as /Walls/ que prender os /Players/ dentro de um determinado bloco.
openJail :: [Coords] -> Maze -> Maze
openJail [] m = m 
openJail (x:xs) m = openJail xs $ replaceElemInMaze x Empty m 


-- | /Bot/ desenvolvido para questões de interface e eventos após o final do MiniGame.
botPacFriend :: Coords -> Play
botPacFriend (a,b)
   | elem b [14,15] = Move 99 R  --mover 2 para R 
   | elem a [14..16] = Move 99 U --mover 3 para U 
   | elem b [16..18] = Move 99 R -- mover 3 para R 
   | elem a [12,13] = Move 99 U -- mover 2 para U 
   | otherwise = Move 99 R -- ir para o túnel 

-- | Esta função torna possível a movimentação do /bot/ anteriormente referido.
passTimeOnePlayerBot99 :: Int -> Int -> State -> State 
passTimeOnePlayerBot99 _ 0 st = st
passTimeOnePlayerBot99 id nStep st@(State maze players level) = if even nStep 
                                                                then quickP evenStep id or st 
                                                                else quickP oddStep id or st
                                     where player = encID id players
                                           (evenStep,oddStep) = nPlayPerStep $ getPlayerSpeed player
                                           (Move idGho or) = botPacFriend (getPlayerCoords player) 

passTimeAuxPD :: Int -> Int -> State -> [Int] -> State 
passTimeAuxPD x pid st@(State maze players level) [] = st 
passTimeAuxPD x pid st@(State maze players level) (id:ids) = if id == pid 
                                                             then passTimeAuxPD x pid (passTimeOnePlayerPID id x st) ids
                                                             else if (ghostORPacmanvalor $ encID id players) == 1
                                                                then passTimeAuxPD x pid (passTimeOnePlayerBotGhost id x st) ids
                                                                else if id == 99 
                                                                     then passTimeAuxPD x pid (passTimeOnePlayerBot99 id x st) ids
                                                                     else passTimeAux x pid st ids

-- | Função utilizada para trocar o mapa de um /State/ e usada em conjunto com a função __openJail__.
changeMaze :: Maze -> State -> State 
changeMaze maze (State m pl l) = State maze pl l 

-- | Função auxiliar que remove os /Ghosts/ de um /State/.
rmGhostsState :: State -> State 
rmGhostsState (State maze pl lvl) = State maze (rmGhosts pl) lvl

-- | Esta função é o que permite todo o MiniGame acontecer com todas as suas restrições específicas e consta na função __passTime__ para ser usada 
-- no jogo. 
passTimePacDungeon :: Int -> Int -> State -> State
passTimePacDungeon x pid st@(State maze players level) 
   | x == 40 = changeMaze (openJail jailNO maze) $ lostTimeMegaST $ passTimeAuxPD x pid st ids
   | x == 80 = changeMaze (openJail jailSE maze) $ lostTimeMegaST $ passTimeAuxPD x pid st ids
   | x == 120 = changeMaze (openJail jailNE maze) $ lostTimeMegaST $ passTimeAuxPD x pid st ids
   | x == 160 = changeMaze (openJail jailSO maze) $ lostTimeMegaST $ passTimeAuxPD x pid st ids
   | x == 200 = changeMaze (openJail jailMid maze) $ lostTimeMegaST $ passTimeAuxPD x pid st ids 
   | coords99 == Just (11,28) = lostTimeMegaST $ passTimeAuxPD x pid (State maze [playerPid] level) [pid]
   | cleanedMaze maze = rmGhostsState $ changeMaze (openJail jailPac maze) $ lostTimeMegaST $ passTimeAuxPD x pid st ids 
   | length players == 1 = lostTimeMegaST $ passTimeAuxPD x pid st ids
   | otherwise = lostTimeMegaST $ passTimeAuxPD x pid st ids
        where ids = map getPlayerID players 
              playerPid = encID pid players 
              coords99 :: Maybe Coords 
              coords99 = if elem 99 ids  
                         then Just $ getPlayerCoords $ encID 99 players
                         else Nothing 