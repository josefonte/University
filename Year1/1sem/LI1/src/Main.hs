{-# OPTIONS_HADDOCK prune #-}

module Main where

import Data.Time.Clock.POSIX
import Control.Monad.IO.Class
import UI.NCurses
import Types
import FileUtils
import Tarefa2
import Tarefa4
import Tarefa5
import Tarefa6
import StatesModoNormal 

data Manager = Manager 
    {   
        state   :: State
    ,    pid    :: Int
    ,    step   :: Int
    ,    before :: Integer
    ,    delta  :: Integer
    ,    delay  :: Integer
    }

instance Show Manager where 
  show (Manager st pid step before delta delay) = show st ++ "\n\n" ++ "before:" ++ show before ++ "\ndelta:" ++ show delta ++ "\ndelay:" ++ show delay ++ "\n" 

loadManager :: Manager
loadManager = ( Manager state1 0 0 0 0 defaultDelayTime )
            
-- | Dada uma /Key/ e um /Manager/, esta função troca a orientação do /Player/ cujo ID corresponda ao PID.

-- | Caso a /Key/ seja inválida, devolve o mesmo /Manager/.
updateControlledPlayer :: Key -> Manager -> Manager
updateControlledPlayer key  (Manager (State maze players level) pid step bf delt del ) 
    | key == KeyRightArrow = Manager (State maze (changeOr (Move pid R) players) level) pid step bf delt del
    | key == KeyLeftArrow = Manager (State maze (changeOr (Move pid L) players) level) pid step bf delt del
    | key == KeyUpArrow =  Manager (State maze (changeOr (Move pid U) players) level) pid step bf delt del
    | key == KeyDownArrow =  Manager (State maze (changeOr (Move pid D) players) level) pid step bf delt del
    | otherwise = Manager (State maze players level) pid step bf delt del

updateScreen :: Window  -> ColorID -> Manager -> Curses ()
updateScreen w a man =
                  do
                    updateWindow w $ do
                      clear
                      setColor a
                      moveCursor 0 0 
                      drawString $ show (state man)
                    render
     
currentTime :: IO Integer
currentTime = fmap ( round . (* 1000) ) getPOSIXTime

-- | Atualiza o /before/ para o tempo que consta no primeiro argumento e aumenta o /delay/
updateTime :: Integer -> Manager -> Manager
updateTime now (Manager s p st b dlt dly) = Manager s p st now (dlt + now - b) dly

-- | Atualiza o /delay/ para 0.
resetTimer :: Integer -> Manager -> Manager
resetTimer now (Manager s p st b dlt dly) = Manager s p st now 0 dly

-- | Usa a função __resetTime__ para deixar o /delay/ a 0, aplica __passTime__ (Tarefa 4) ao /State/ e aumenta o /step/ em 1. 
nextFrame :: Integer -> Manager -> Manager
nextFrame now man@(Manager s pid step bf delt del) = resetTimer now $ Manager (passTime step pid s) pid (step+1) bf delt del 

loop :: Window -> Manager -> Curses ()
loop w man@(Manager s pid step bf delt del ) = do                         
  color_schema <- newColorID ColorBlue ColorWhite  10
  now <- liftIO  currentTime
  updateScreen w  color_schema man
  if ( delt > del )
    then loop w $ nextFrame now man
    else do
          ev <- getEvent w $ Just 0
          case ev of
                Nothing -> loop w (updateTime now man)
                Just (EventSpecialKey arrow ) -> loop w $ updateControlledPlayer arrow (updateTime now man)
                Just ev' ->
                  if (ev' == EventCharacter 'q')
                    then return ()
                    else loop w (updateTime now man)

main :: IO ()
main =
  runCurses $ do
    setEcho False
    setCursorMode CursorInvisible
    w <- defaultWindow
    loop w loadManager