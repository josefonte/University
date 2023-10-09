-- | State utilizado no MiniGame KillPacman

module KillPacman where 

import Data.Time.Clock.POSIX
import Control.Monad.IO.Class
import UI.NCurses
import Types
import FileUtils
import Tarefa2 
import Tarefa4 
import StatesModoNormal

-- | /Maze1/ do documento StatesModoNormal.hs
mazeKillPacman :: Maze 
mazeKillPacman = maze $ loadMaze "maps/mapModeNormal.txt"

-- | Os três /Players/ (um /Ghost/ e dois Pacmans) que fazem parte do MiniGame.
plKillPacman :: [Player]
plKillPacman = [(Ghost $ GhoState (7,(7,12),1,Null,0,0) Alive),
                (Pacman $ PacState (1,(1,1),1,R,0,3) 0 Open Normal),
                (Pacman $ PacState (2,(13,23),1,L,0,3) 0 Open Normal)]
                
-- | /State/ do jogo.                     
stateKillPacman :: State 
stateKillPacman = State mazeKillPacman plKillPacman 0 

