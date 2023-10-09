{- | 

== Neste documento são carregados todos os /Mazes/ feitos pelo grupo e os /States/ derivados dos mesmos. Para além disso, conta também com a função __levelUp__.

Em todos os /States/ é usada a função __plIniciais__ que dado um /Maze/ determina onde devem ficar os fantasmas e o Pacman.-}

{-# OPTIONS_HADDOCK prune #-}

module StatesModoNormal where 

import FileUtils
import Types
import Tarefa2

-- * /States/ 

-- | O __state1__ contém o /Maze/ mais pequeno e simples que foi bastante usado para testes do código.
state1 :: State 
state1 = State maze1 (plIniciais maze1) 1

maze1 :: Maze 
maze1 = maze $ loadMaze "maps/mapModeNormal.txt"

-- | O __state3tuneis__ é aquele que mais difere da estrutura dos restantes por permitir ao utilizador teletransportar-se por diferentes zonas do labirinto.
state3tuneis :: State 
state3tuneis = State maze3tuneis (plIniciais maze3tuneis) 1 

maze3tuneis :: Maze 
maze3tuneis = maze $ loadMaze "maps/mapa3tuneis.txt"

-- | O __state67__, apesar de parecer aletatório em certos lugares, tem algumas mensagens escondidas. São usados códigos Morse e binário e a informação foi consultada na Wikipédia e profcardy.com. 

-- | === /Easter Eggs/
-- * Na zona de cima, no centro estão seguidos os seguintes conjuntos de /Pieces/: __[Wall,Food Little,Wall,Wall,Food Big,Wall,Wall,Food Little,Wall], [Wall,Empty,Wall,Wall], [Wall,Wall,Food Little,Wall,Food Little,Wall,Wall,Food Little,Wall]__. Se considerarmos "#" como um sinal curto e "##" como sinal longo, então, usando o código Morse obtemos "PAC";
-- * Na zona de cima, perto do tunel direito, estão apresentados: __[Wall,Wall] , [Wall]__. Se considerarmos "#"" um sinal longo e "##" dois sinal curtos, obtemos, em códido Morse "EI" (Enganheria Informática).
-- * Na zona de baixo, perto do tunel esquerdo, aparece: __[Wall,Food Little,Wall,Food Little,Food Little,Food Little] , [Wall,Food Little,Wall,Food Little,Wall,Food Little]__. Ao passar as /Walls/ para '1' e as /Foods/ para '0', obtemos: "10100" "10101", que, em código binário representam "2020" e "2021", respetivamente. 
-- * Por fim, na zona de baixo, ao centro, aparacem, também seguidos, as /Pieces/: __[Wall,Food Little,Food Little,Food Little,Food Little,Wall,Wall]__. Seguindo a mesma lógica da anterior, obtemos "1000011", que em binário representa "67" (o número do nosso grupo).

state67 :: State  
state67 = State maze67 (plIniciais maze67) 1 

maze67 :: Maze 
maze67 = maze $ loadMaze "maps/mapaEasterEggs.txt"

-- | /State/ utilizado no MiniGame __PacDungeon__. 
stateDungeon :: State
stateDungeon = State mazeDungeon (plIniciais mazeDungeon) 1 

mazeDungeon :: Maze 
mazeDungeon = replaceElemInMaze (6,14) Empty (maze $ loadMaze "maps/mapMG.txt") 

maze2 :: Maze 
maze2 = maze $ loadMaze "maps/labaleatorio.txt"

-- | Segundo /State/ do jogo.
state2 :: State
state2 = State maze2 (plIniciais maze1) 1 

maze3 :: Maze 
maze3 = maze $ loadMaze "maps/4tuneis.txt"

-- | Terceiro /State/ do jogo.
state3 :: State
state3 = State maze3 (plIniciais maze1) 1

maze4 :: Maze 
maze4 = maze $ loadMaze "maps/maze4.txt" 

-- | Quarto e último /State/ do jogo. 
state4 :: State 
state4 = State maze4 (plIniciais maze4) 1 

-- * Level Up

-- | Esta função faz com que os /Players/ voltem a ter 1 de velocidade; passem para Normal/Alive e os /Pacmans/ voltem a ter Time Mega = 0.
backToIn :: [Player] -> [Player]
backToIn [] = []
backToIn ((Pacman (PacState (x,y,z,t,h,l) q c d)):ps) = (Pacman (PacState (x,y,1,t,h,l) 0 c Normal )) : backToIn ps
backToIn ((Ghost (GhoState (x,y,z,t,h,l) q )):ps) = (Ghost (GhoState (x,y,1,t,h,l) Alive)) : backToIn ps

replaceGho :: Maze -> Player -> Player
replaceGho maze p = setPlayerCoords p (coordsIni!!(idGho-1)) 
         where coordsIni = map getPlayerCoords posInic
               posInic = ghostsInicial maze
               idGho = getPlayerID p 

-- | A __replacePlayers__ faz com que todos os /Players/ voltem para as posições iniciais.
replacePlayers :: Maze -> [Player] -> [Player]
replacePlayers maze [] = []
replacePlayers maze (x:xs) = if ghostORPacmanvalor x == 1 
                             then replaceGho maze x : replacePlayers maze xs 
                             else setPlayerCoords x (coordsiniciaisPac maze) : replacePlayers maze xs

-- | Esta função engloba todas as restantes e faz com que os /Players/ voltem as posições e modos iniciais.
levelUp :: State -> Maze -> State 
levelUp (State maze players lvl) mazeInicial = State mazeInicial (backToIn $ replacePlayers maze players) (lvl+1)