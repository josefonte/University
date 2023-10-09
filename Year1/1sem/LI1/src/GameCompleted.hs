{- | 

= Introdução 

Este é o documento principal que engloba as Tarefas 2, 4, 5 e 6 e a Interface Gráfica do projeto.

Desde o início do projeto que tivemos sempre em mente a realização de uma boa interface e MiniGames no jogo e após bastante 
esforço tornamos isso possível apesar de todas as dificuldades que surgiram.

= Desenvolvimento

Decimos construir gradualmente esta interface, pelo que começamos por criar um "data World", que envolve um /State/, um PID e um /step/.

Após isso, tentamos desenhar na /display/ um /World/ que continha o __state1__ do documento __StatesModoNormal.hs__. 
Esta fase permitiu-nos ampliar bastante os conhecimentos das bibliotecas /Gloss/ sobre as funções __scale__;__translate__ e __pictures__, que usamos
para desenhar, com o máximo rigor, o /World/.

Em seguida, usamos a função __nextFrame__ (que definimos no documento __Main.hs__, fornecido pela equipa docente) como base para criar a função __timeChangeWorld__,
que faz com que todo o jogo dentro do Modo Normal e MiniGames aconteça.

Concluída esta fase, criamos um "data Game" que envolve um /world/, um /menu/, uma /optionSelected/ e um /currentWorld/ para nos permitir
criar todos os menus do jogo e navegar entre eles.

De seguida, criamos a função __timeChange__ que faz com que o jogo decorra sem precisar que o utilizador pressione alguma tecla. Esta 
função envolve a função __timeChangeWorld__ e é usada quando o utilizador está a jogar o Modo Normal ou MiniGames, e aquando da apresentação
da "Love Story" no __PacDungeon__.

Para tornar possível a interação com o programa, nomeadamente a nevegação entre menus, criamos a função __ eventChange__ que usa a função __eventChangeWorld__ como auxiliar
em situações em que o utilizador está a jogar no Modo Normal e MiniGames (excetuando a interação com o menu de pausa).

Finalmente, criamos a função __drawGame__ que mostra o "Screen Saver", os menus, a "Love Story" e o utiliza a função __drawWorld__ 
para mostrar o /world/ atual quando o utilizador está a jogar.

= Objetivos 

== Criar uma Interface

Desde o primeiro momento que acreditavamos que uma boa interface faria toda a diferença. 
De acordo com a projeção mental que tinhamos do jogo, consideramos que adotar um sistema de menus seria o mais adequado. 
Assim sendo, utilizando a plataforma web __Figma Design__, criamos todas as janelas necessárias de modo a tornar uma /User Experience/ sem qualquer tipo de atrito.
Quanto à __/User Interface/__, a linha condutora do /design/ foi-se criando ao longo do desenvolvimento das várias janelas. Partimos de funções básicas para iterações cada vez mais complexas, 
das quais podemos realçar: a barra decrescente do /Time Mega/; o /score/ a mudar os zeros, os menus de pausa e a "joia da coroa", "a cereja no topo do bolo", a "Love Story" no final do MiniGame __PacDungeon__.

== Criar MiniGames

Para o nosso projeto, decimos expandir os modos de jogo e colocar dois Minigames: __PacDungeon__ e __KillPacman__.

===PacDungeon

Este MiniGame imerge o jogador numa masmorra cada vez mais caótica, em que ele terá de sobreviver a 10 /Ghosts/ para poder salvar a PacPrincess. 
No final desenrola-se uma pequena história da fuga das masmorras do castelo. 
Apesar de ser uma ideia bastante complicada ao início, tanto em termos de código com na parte gráfica, o desafio motivou-nos a fazer algo totalmente diferente. 
O resultado superou imenso as expectativas iniciais e consideramos o PacDungeon o melhor modo de jogo do trabalho. 

=== KillPacman

Este MiniGame troca os papéis entre o caçador e a presa. O jogador passa a controlar o /Ghost/ tentando impedir os Pacmans de comer todas as /Foods/ do labirinto.
O MiniGame oferece uma nova perspetiva sobre o jogo que consideramos bastante interessante.

= Conclusão

Apesar deste projeto envolver bastante trabalho, o seu resultado foi bastante compensador e impulsionou o nosso desenvolvimento na àrea da programação.

Conseguimos ultrapassar todas as adversidades, atingir o resultado esperado e até mesmo superar as nossas expectativas.
 
Concluindo, o desenvolvimento deste projeto revelou-se bastante divertido, principalmente na fase final em vimos "as coisas a acontecer". Permitiu-nos expandir do propósito
inicial e criar novas funcionalidades como menus e modos de jogo. Quanto à linguagem de programação utilizada, o Haskell, consideramos que não é a melhor linguagem para
desenvolvimento de jogos, porém preencheu as nossas necessidades em relação à lógica e interface.

-}

{-# OPTIONS_HADDOCK prune #-}

module Main where 

import Tarefa2
import Tarefa4
import Tarefa5
import Tarefa6
import Types
import FileUtils
import StatesModoNormal 
import Graphics.Gloss
import Graphics.Gloss.Interface.Pure.Game
import Graphics.Gloss.Juicy
import Data.Maybe
import Data.Time.Clock.POSIX
import Data.Char
import KillPacman

-- | Envolve um /State / um PID e um /step/ 
data World = World  
    {   
        state   :: State
    ,    pid    :: Int
    ,    step   :: Int
    }

-- | Envolve todas as informções de um estado do jogo relativas ao /world/, menu e opção em que o utilizador se encontra e o último /Maze/ que o jogador escolheu. 
-- No caso de não ter selecionado um, o predefinido é o /One/.
data Game = Game 
    { 
      world :: World 
    , menu :: Menu
    , optionSelected :: Options
    , currentWorld :: WorldSelected
    }

-- | Todos os menus do jogo, "Screen Saver" e HLOVE ("Love Story").
data Menu = ScreenSaver | MainMenu | GameOverModeNormal GOMN | GameOverMiniGames GOMN | NoMenu | PauseMenu | HLOVE FrameLove 

-- | Opções em que o utilizador se pode encontrar e o /EasterEgg/ dos /developers/.
data Options = NothingSelected | OpPlay IsSelected | OpPlayAgain Minigames | OpQuit Minigames
                               | OpMapas Maps 
                               | OpMinigames Minigames
                               | OpCreditos IsSelected
                               | EasterEgg
                               | Resume | BackToMenu

-- | /Mazes/ que o utilizador pode escolher para jogar no Modo Normal.
data Maps = Maze1 | Maze2 | Maze3 | Maze4 | NoMaze 

-- | MiniGames e "NoMiniGame" para distinguir em que modo de jogo o utilizador se encontra.
data Minigames = KillPacman | PacDungeon | NoMiniGame

-- | Usado para entrar nos Modos Normal e MiniGames.
data IsSelected = SelectedNormal | NotSelected | SelectedKP | SelectedPD

-- | Usado na distinção entre /Win/ e /Lose/ nos menus de fim de jogo.
data GOMN = Win | Lose 

-- | Escolha de /Mazes/ na aba dos "Maps" e distinção ao entrar nos MiniGames.
data WorldSelected = One | Two | Three | Four | MazeKillPacman | MazePacDungeon deriving (Eq,Ord)

-- | /Frames/ da "Love Story".
data FrameLove = F1 | F2 | F3 | F4 | F5 | F6 | F7 | F8 | F9 | F10 
               | F11 | F12 | F13 | F14 | F15 | F16 | F17 | F18 | F19 | F20
               | F21 | F22 | F23 | F24 | F25 | F26 | F27 | F28 | F29 | F30
               | F31 | F32 | F33 | F34 | F35 | F36 | F37 | F38 | F39 | F40
               | F41 | F42 | F43 | F44 | F45 | F46 | F47 | F48 | F49 | F50
               | F51 | F52 | F53 | F54 | F55 | F56 | F57 | F58 | F59 | F60
               
-- | Display do jogo com as dimensões e nome do jogo.
displayMode :: Display
displayMode = InWindow "Pacman67 LI1 by Eduardo Rocha & Jose Fonte | 2020/21" (1000,750) (0,0)

-- | Esta função carrega todas as 177 imagens utilizadas na interface gráfica.
loadImg :: IO [Picture]
loadImg = do pacCloseL <- loadJuicy "ImagensParaPacman/pacmanCloseLeft.png" 
             pacCloseR <- loadJuicy "ImagensParaPacman/pacmanCloseRight.png"
             pacCloseU <- loadJuicy "ImagensParaPacman/pacmanCloseUp.png"
             pacCloseD <- loadJuicy "ImagensParaPacman/pacmanCloseDown.png"
             pacOpenL <- loadJuicy "ImagensParaPacman/pacmanOPENLeft.png"
             pacOpenR <- loadJuicy "ImagensParaPacman/pacmanOPENRight.png"
             pacOpenU <- loadJuicy "ImagensParaPacman/pacmanOPENUp.png"
             pacOpenD <- loadJuicy "ImagensParaPacman/pacmanOPENdown.png"
             ghoAliveRedLeft <- loadJuicy "ImagensParaPacman/GhostRedLeft.png"
             ghoAliveRedRight <- loadJuicy "ImagensParaPacman/GhostRedRight.png"
             ghoAliveRedUp <- loadJuicy "ImagensParaPacman/GhostRedUp.png"
             ghoAliveRedDown <- loadJuicy "ImagensParaPacman/GhostRedDown.png"
             ghoAlivePinkLeft <- loadJuicy "ImagensParaPacman/GhostPinkLeft.png"
             ghoAlivePinkRight <- loadJuicy "ImagensParaPacman/GhostPinkRight.png"
             ghoAlivePinkUp <- loadJuicy "ImagensParaPacman/GhostPinkUP.png"
             ghoAlivePinkDown <- loadJuicy "ImagensParaPacman/GhostPinkDown.png"
             ghoAliveYellowLeft <- loadJuicy "ImagensParaPacman/GhostAmareloLeft.png"
             ghoAliveYellowRight <- loadJuicy "ImagensParaPacman/GhostAmareloRight.png"
             ghoAliveYellowUp <- loadJuicy "ImagensParaPacman/GhostAmareloUp.png"
             ghoAliveYellowDown <- loadJuicy "ImagensParaPacman/GhostAmareloDown.png"
             ghoAliveBlueLeft <- loadJuicy "ImagensParaPacman/GhostBlueLeft.png"
             ghoAliveBlueRigth <- loadJuicy "ImagensParaPacman/GhostBlueRight.png"
             ghoAliveBlueUp <- loadJuicy "ImagensParaPacman/GhostBlueUp.png"
             ghoAliveBlueDown <- loadJuicy "ImagensParaPacman/GhostBlueDown.png"
             ghoDead <- loadJuicy "ImagensParaPacman/Ghostdead.png"
             wall <- loadJuicy "ImagensParaPacman/paredeUniforme.png"
             foodL <- loadJuicy "ImagensParaPacman/m&m.png"
             foodB <- loadJuicy "ImagensParaPacman/fastfood.png"
             pacDead <- loadJuicy "ImagensParaPacman/pacmanDead.png"
             heart <- loadJuicy "ImagensParaPacman/heart.png"
             noTimeBar <- loadJuicy "ImagensParaPacman/vazia.png"
             bar1 <- loadJuicy "ImagensParaPacman/prog1.png"
             bar2 <- loadJuicy "ImagensParaPacman/prog2.png"
             bar3 <- loadJuicy "ImagensParaPacman/prog3.png"
             bar4 <- loadJuicy "ImagensParaPacman/prog4.png" 
             bar5 <- loadJuicy "ImagensParaPacman/prog5.png"
             bar6 <- loadJuicy "ImagensParaPacman/prog6.png"
             bar7 <- loadJuicy "ImagensParaPacman/prog7.png"
             bar8 <- loadJuicy "ImagensParaPacman/prog8.png"
             n0 <- loadJuicy "ImagensParaPacman/NumsBrancos/num0.png"
             n1 <- loadJuicy "ImagensParaPacman/NumsBrancos/num1.png"
             n2 <- loadJuicy "ImagensParaPacman/NumsBrancos/num2.png"
             n3 <- loadJuicy "ImagensParaPacman/NumsBrancos/num3.png"
             n4 <- loadJuicy "ImagensParaPacman/NumsBrancos/num4.png"
             n5 <- loadJuicy "ImagensParaPacman/NumsBrancos/num5.png"
             n6 <- loadJuicy "ImagensParaPacman/NumsBrancos/num6.png"
             n7 <- loadJuicy "ImagensParaPacman/NumsBrancos/num7.png"
             n8 <- loadJuicy "ImagensParaPacman/NumsBrancos/num8.png"
             n9 <- loadJuicy "ImagensParaPacman/NumsBrancos/num9.png"
             score <- loadJuicy "ImagensParaPacman/scorewhite.png"
             barraInfo <- loadJuicy "ImagensParaPacman/barrainfo.png"
             header <- loadJuicy "ImagensParaPacman/header.png" 
             nB0 <- loadJuicy "ImagensParaPacman/NumsPretos/numblack0.png"
             nB1 <- loadJuicy "ImagensParaPacman/NumsPretos/numblack1.png"
             nB2 <- loadJuicy "ImagensParaPacman/NumsPretos/numblack2.png"
             nB3 <- loadJuicy "ImagensParaPacman/NumsPretos/numblack3.png"
             nB4 <- loadJuicy "ImagensParaPacman/NumsPretos/numblack4.png"
             nB5 <- loadJuicy "ImagensParaPacman/NumsPretos/numblack5.png"
             nB6 <- loadJuicy "ImagensParaPacman/NumsPretos/numblack6.png"
             nB7 <- loadJuicy "ImagensParaPacman/NumsPretos/numblack7.png"
             nB8 <- loadJuicy "ImagensParaPacman/NumsPretos/numblack8.png"
             nB9 <- loadJuicy "ImagensParaPacman/NumsPretos/numblack9.png"
             wordDead <- loadJuicy "ImagensParaPacman/wordDead.png"
             lastChance <- loadJuicy "ImagensParaPacman/lastchance.png"
             pacRedOpenR <- loadJuicy "ImagensParaPacman/pacmanOPENRight3.png"
             pacRedOpenL <- loadJuicy "ImagensParaPacman/pacmanOPENLeft3.png"
             pacRedOpenU <- loadJuicy "ImagensParaPacman/pacmanOPENUp3.png"
             pacRedOpenD <- loadJuicy "ImagensParaPacman/pacmanOPENdown3.png"
             pacRedCloseR <- loadJuicy "ImagensParaPacman/pacmanCloseRight3.png"
             pacRedCloseL <- loadJuicy "ImagensParaPacman/pacmanCloseLeft3.png"
             pacRedCloseU <- loadJuicy "ImagensParaPacman/pacmanCloseUp3.png"
             pacRedCloseD <- loadJuicy "ImagensParaPacman/pacmanCloseDown3.png"
             pacRedDead <- loadJuicy "ImagensParaPacman/pacDead3.png"
             pacBlueOpenR <- loadJuicy "ImagensParaPacman/pacmanOPENRight2.png"
             pacBlueOpenL <- loadJuicy "ImagensParaPacman/pacmanOPENLeft2.png"
             pacBlueOpenU <- loadJuicy "ImagensParaPacman/pacmanOPENUp2.png"
             pacBlueOpenD <- loadJuicy "ImagensParaPacman/pacmanOPENdown2.png"
             pacBlueCloseR <- loadJuicy "ImagensParaPacman/pacmanCloseRight2.png"
             pacBlueCloseL <- loadJuicy "ImagensParaPacman/pacmanCloseLeft2.png"
             pacBlueCloseU <- loadJuicy "ImagensParaPacman/pacmanCloseUp2.png"
             pacBlueCloseD <- loadJuicy "ImagensParaPacman/pacmanCloseDown2.png"
             pacBlueDead <- loadJuicy "ImagensParaPacman/pacDead2.png"
             mainInc <- loadJuicy "ImagensParaPacman/Menus/MainInc.png"
             screenSaver <- loadJuicy "ImagensParaPacman/Menus/screenSaver.png"
             mapsButton <- loadJuicy "ImagensParaPacman/Menus/mapsMENUBUTTON.png"
             minigButton <- loadJuicy "ImagensParaPacman/Menus/MiniGamesMenu.png"
             developersButton <- loadJuicy "ImagensParaPacman/Menus/developersMENUBUTTON.png" 
             mapM1 <- loadJuicy "ImagensParaPacman/Menus/MapMenuMap1.png"
             mapM2 <- loadJuicy "ImagensParaPacman/Menus/MapMenuMap2.png"
             mapM3 <- loadJuicy "ImagensParaPacman/Menus/MapMenuMap3.png"
             mapM4 <- loadJuicy "ImagensParaPacman/Menus/MapMenuMap4.png"
             miniGPD <- loadJuicy "ImagensParaPacman/Menus/MiniGamesPacDungeon.png"
             miniGKP <- loadJuicy "ImagensParaPacman/Menus/MiniGamesKillPacman.png"
             developers <- loadJuicy "ImagensParaPacman/Menus/DevelopersMenu.png"
             mainMenuNS <- loadJuicy "ImagensParaPacman/Menus/MainMenuNS.png"
             pacLadyOpenR <- loadJuicy "ImagensParaPacman/ladyPacOPENRight.png"
             pacLadyOpenL <- loadJuicy "ImagensParaPacman/ladyPacOPENLeft.png"
             pacLadyOpenU <- loadJuicy "ImagensParaPacman/ladyPacOPENUp.png"
             pacLadyOpenD <- loadJuicy "ImagensParaPacman/ladyPacOPENDown.png"
             pacLadyCloseR <- loadJuicy "ImagensParaPacman/ladyPacClose.png"
             pacLadyCloseL <- loadJuicy "ImagensParaPacman/ladyPacCloseLeft.png"
             pacLadyCloseU <- loadJuicy "ImagensParaPacman/ladyPacCloseUp.png"
             pacLadyCloseD <- loadJuicy "ImagensParaPacman/ladyPacCloseDown.png"
             menuLosePA <- loadJuicy "ImagensParaPacman/Menus/LOSEplayagain.png"
             menuLoseBM <- loadJuicy "ImagensParaPacman/Menus/LOSEbacktomenu.png"
             pacdunegonWinPA <- loadJuicy "ImagensParaPacman/Menus/pacdungeonWINplayagain.png"
             pacdunegonWinBM <- loadJuicy "ImagensParaPacman/Menus/pacdungeonWINbacktoMenu.png"
             pacdunegonLosePA <- loadJuicy "ImagensParaPacman/Menus/pacdungeonLOSEplayagain.png"
             pacdunegonLoseBM <- loadJuicy "ImagensParaPacman/Menus/pacdungeonLOSEbacktoMenu.png"
             killPWinPA <- loadJuicy "ImagensParaPacman/Menus/killPacmanWINplayagain.png"
             killPWinBM <- loadJuicy "ImagensParaPacman/Menus/killPacmanWINbacktomenu.png"
             killPLosePA <- loadJuicy "ImagensParaPacman/Menus/killPacmanLOSEplayagain.png"
             killPLoseBM <- loadJuicy "ImagensParaPacman/Menus/killPacmanLOSEbacktomenu.png"
             pointStats <- loadJuicy "ImagensParaPacman/points.png"
             eastereggIdentidade <- loadJuicy "ImagensParaPacman/Menus/MarcadorEasterEgg.png"
             pauseResume <- loadJuicy "ImagensParaPacman/Menus/PauseMENUresume.png"
             pauseBM <- loadJuicy "ImagensParaPacman/Menus/PauseMENUbacktomenu.png" 
             f1 <- loadJuicy "ImagensParaPacman/LoveStory/Frame1.png"
             f2 <- loadJuicy "ImagensParaPacman/LoveStory/Frame2.png"
             f3 <- loadJuicy "ImagensParaPacman/LoveStory/Frame3.png"
             f4 <- loadJuicy "ImagensParaPacman/LoveStory/Frame4.png"
             f5 <- loadJuicy "ImagensParaPacman/LoveStory/Frame5.png"
             f6 <- loadJuicy "ImagensParaPacman/LoveStory/Frame6.png"
             f7 <- loadJuicy "ImagensParaPacman/LoveStory/Frame7.png"
             f8 <- loadJuicy "ImagensParaPacman/LoveStory/Frame8.png"
             f9 <- loadJuicy "ImagensParaPacman/LoveStory/Frame9.png"
             f10 <- loadJuicy "ImagensParaPacman/LoveStory/Frame10.png"
             f11 <- loadJuicy "ImagensParaPacman/LoveStory/Frame11.png"
             f12 <- loadJuicy "ImagensParaPacman/LoveStory/Frame12.png"
             f13 <- loadJuicy "ImagensParaPacman/LoveStory/Frame13.png"
             f14 <- loadJuicy "ImagensParaPacman/LoveStory/Frame14.png"
             f15 <- loadJuicy "ImagensParaPacman/LoveStory/Frame15.png"
             f16 <- loadJuicy "ImagensParaPacman/LoveStory/Frame16.png"
             f17 <- loadJuicy "ImagensParaPacman/LoveStory/Frame17.png"
             f18 <- loadJuicy "ImagensParaPacman/LoveStory/Frame18.png"
             f19 <- loadJuicy "ImagensParaPacman/LoveStory/Frame19.png"
             f20 <- loadJuicy "ImagensParaPacman/LoveStory/Frame20.png"
             f21 <- loadJuicy "ImagensParaPacman/LoveStory/Frame21.png"
             f22 <- loadJuicy "ImagensParaPacman/LoveStory/Frame22.png"
             f23 <- loadJuicy "ImagensParaPacman/LoveStory/Frame23.png"
             f24 <- loadJuicy "ImagensParaPacman/LoveStory/Frame24.png"
             f25 <- loadJuicy "ImagensParaPacman/LoveStory/Frame25.png"
             f26 <- loadJuicy "ImagensParaPacman/LoveStory/Frame26.png"
             f27 <- loadJuicy "ImagensParaPacman/LoveStory/Frame27.png"
             f28 <- loadJuicy "ImagensParaPacman/LoveStory/Frame28.png"
             f29 <- loadJuicy "ImagensParaPacman/LoveStory/Frame29.png"
             f30 <- loadJuicy "ImagensParaPacman/LoveStory/Frame30.png"
             f31 <- loadJuicy "ImagensParaPacman/LoveStory/Frame31.png"
             f32 <- loadJuicy "ImagensParaPacman/LoveStory/Frame32.png"
             f33 <- loadJuicy "ImagensParaPacman/LoveStory/Frame33.png"
             f34 <- loadJuicy "ImagensParaPacman/LoveStory/Frame34.png"
             f35 <- loadJuicy "ImagensParaPacman/LoveStory/Frame35.png"
             f36 <- loadJuicy "ImagensParaPacman/LoveStory/Frame36.png"
             f37 <- loadJuicy "ImagensParaPacman/LoveStory/Frame37.png"
             f38 <- loadJuicy "ImagensParaPacman/LoveStory/Frame38.png"
             f39 <- loadJuicy "ImagensParaPacman/LoveStory/Frame39.png"
             f40 <- loadJuicy "ImagensParaPacman/LoveStory/Frame40.png"
             f41 <- loadJuicy "ImagensParaPacman/LoveStory/Frame41.png"
             f42 <- loadJuicy "ImagensParaPacman/LoveStory/Frame42.png"
             f43 <- loadJuicy "ImagensParaPacman/LoveStory/Frame43.png"
             f44 <- loadJuicy "ImagensParaPacman/LoveStory/Frame44.png"
             f45 <- loadJuicy "ImagensParaPacman/LoveStory/Frame45.png"
             f46 <- loadJuicy "ImagensParaPacman/LoveStory/Frame46.png" 
             f47 <- loadJuicy "ImagensParaPacman/LoveStory/Frame47.png"
             f48 <- loadJuicy "ImagensParaPacman/LoveStory/Frame48.png"
             f49 <- loadJuicy "ImagensParaPacman/LoveStory/Frame49.png"
             f50 <- loadJuicy "ImagensParaPacman/LoveStory/Frame50.png"
             f51 <- loadJuicy "ImagensParaPacman/LoveStory/Frame51.png"
             f52 <- loadJuicy "ImagensParaPacman/LoveStory/Frame52.png"
             f53 <- loadJuicy "ImagensParaPacman/LoveStory/Frame53.png"
             f54 <- loadJuicy "ImagensParaPacman/LoveStory/Frame54.png"
             f55 <- loadJuicy "ImagensParaPacman/LoveStory/Frame55.png"
             f56 <- loadJuicy "ImagensParaPacman/LoveStory/Frame56.png"
             f57 <- loadJuicy "ImagensParaPacman/LoveStory/Frame57.png"
             f58 <- loadJuicy "ImagensParaPacman/LoveStory/Frame58.png"
             f59 <- loadJuicy "ImagensParaPacman/LoveStory/Frame59.png"
             f60 <- loadJuicy "ImagensParaPacman/LoveStory/Frame60.png"
             return (map fromJust [pacCloseL,           --00
                                   pacCloseR,           --01 
                                   pacCloseU,           --02 
                                   pacCloseD,           --03
                                   pacOpenL,            --04 
                                   pacOpenR,            --05 
                                   pacOpenU,            --06 
                                   pacOpenD,            --07 
                                   ghoAliveRedLeft,     --08
                                   ghoAliveRedRight,    --09 
                                   ghoAliveRedUp,       --10
                                   ghoAliveRedDown,     --11
                                   ghoAlivePinkLeft,    --12
                                   ghoAlivePinkRight,   --13 
                                   ghoAlivePinkUp,      --14 
                                   ghoAlivePinkDown,    --15 
                                   ghoAliveYellowLeft,  --16
                                   ghoAliveYellowRight, --17
                                   ghoAliveYellowUp,    --18 
                                   ghoAliveYellowDown,  --19 
                                   ghoAliveBlueLeft,    --20
                                   ghoAliveBlueRigth,   --21
                                   ghoAliveBlueUp,      --22
                                   ghoAliveBlueDown,    --23
                                   ghoDead,             --24
                                   wall,                --25
                                   foodL,               --26
                                   foodB,               --27
                                   pacDead,             --28 
                                   heart,               --29
                                   noTimeBar,           --30
                                   bar1,                --31
                                   bar2,                --32
                                   bar3,                --33
                                   bar4,                --34
                                   bar5,                --35
                                   bar6,                --36
                                   bar7,                --37
                                   bar8,                --38
                                   n0,                  --39
                                   n1,                  --40 
                                   n2,                  --41
                                   n3,                  --42
                                   n4,                  --43
                                   n5,                  --44
                                   n6,                  --45
                                   n7,                  --46
                                   n8,                  --47
                                   n9,                  --48
                                   score,               --49
                                   barraInfo,           --50
                                   header,              --51
                                   nB0,                 --52
                                   nB1,                 --53
                                   nB2,                 --54
                                   nB3,                 --55
                                   nB4,                 --56
                                   nB5,                 --57
                                   nB6,                 --58
                                   nB7,                 --59
                                   nB8,                 --60
                                   nB9,                 --61
                                   wordDead,            --62
                                   lastChance,          --63
                                   pacRedCloseR,        --64
                                   pacRedCloseL,        --65
                                   pacRedCloseU,        --66
                                   pacRedCloseD,        --67
                                   pacRedOpenR,         --68
                                   pacRedOpenL,         --69
                                   pacRedOpenU,         --70
                                   pacRedOpenD,         --71
                                   pacRedDead,          --72
                                   pacBlueCloseR,       --73
                                   pacBlueCloseL,       --74
                                   pacBlueCloseU,       --75
                                   pacBlueCloseD,       --76
                                   pacBlueOpenR,        --77
                                   pacBlueOpenL,        --78
                                   pacBlueOpenU,        --79
                                   pacBlueOpenD,        --80
                                   pacBlueDead,         --81
                                   mainInc,             --82
                                   screenSaver,         --83
                                   mapsButton,          --84
                                   minigButton,         --85
                                   developersButton,    --86
                                   mapM1,               --87 
                                   mapM2,               --88
                                   mapM3,               --89
                                   mapM4,               --90
                                   miniGPD,             --91
                                   miniGKP,             --92
                                   developers,          --93
                                   mainMenuNS,          --94
                                   pacLadyOpenR,        --95
                                   pacLadyOpenL,        --96
                                   pacLadyOpenU,        --97
                                   pacLadyOpenD,        --98
                                   pacLadyCloseR,       --99
                                   pacLadyCloseL,       --100
                                   pacLadyCloseU,       --101
                                   pacLadyCloseD,       --102
                                   menuLosePA,          --103
                                   menuLoseBM,          --104
                                   pacdunegonWinPA,     --105
                                   pacdunegonWinBM,     --106
                                   pacdunegonLosePA,    --107
                                   pacdunegonLoseBM,    --108
                                   killPWinPA,          --109
                                   killPWinBM,          --110
                                   killPLosePA,         --111
                                   killPLoseBM,         --112
                                   pointStats,          --113
                                   eastereggIdentidade, --114
                                   pauseResume,         --115
                                   pauseBM,             --116
                                   f1,                  --117 
                                   f2,                  --118 
                                   f3,                  --119
                                   f4,                  --120
                                   f5,                  --121
                                   f6,                  --122                 
                                   f7,                  --123                 
                                   f8,                  --124
                                   f9,                  --125
                                   f10,                 --126
                                   f11,                 --127
                                   f12,                 --128
                                   f13,                 --129
                                   f14,                 --130                
                                   f15,                 --131
                                   f16,                 --132
                                   f17,                 --133
                                   f18,                 --134
                                   f19,                 --135
                                   f20,                 --136
                                   f21,                 --137                
                                   f22,                 --138
                                   f23,                 --139
                                   f24,                 --140
                                   f25,                 --141                
                                   f26,                 --142
                                   f27,                 --143
                                   f28,                 --144
                                   f29,                 --145
                                   f30,                 --146
                                   f31,                 --147
                                   f32,                 --148
                                   f33,                 --149
                                   f34,                 --150
                                   f35,                 --151
                                   f36,                 --152
                                   f37,                 --153
                                   f38,                 --154
                                   f39,                 --155
                                   f40,                 --156
                                   f41,                 --157
                                   f42,                 --158
                                   f43,                 --159
                                   f44,                 --160
                                   f45,                 --161
                                   f46,                 --162
                                   f47,                 --163
                                   f48,                 --164
                                   f49,                 --165
                                   f50,                 --166
                                   f51,                 --167
                                   f52,                 --168
                                   f53,                 --169
                                   f54,                 --170
                                   f55,                 --171
                                   f56,                 --172                
                                   f57,                 --173
                                   f58,                 --174
                                   f59,                 --175
                                   f60                  --176
                                   ])

-- | A partir de uma /Piece/ (incluindo /Players/) e de uma lista de /Pictures/, faz um bijeção que atribui a cada /Piece/ uma /Picture/ e atribui-lhe
-- uma escala de acordo com o tamanho que deve adotar no jogo.
pieceToPic :: Piece -> [Picture] -> Picture
pieceToPic Empty pics = Blank  
pieceToPic Wall pics = pics!!25 
pieceToPic (Food Little) pics = scale 0.1 0.1 (pics!!26) 
pieceToPic (Food Big) pics = scale 0.6 0.6 (pics!!27)
pieceToPic (PacPlayer (Pacman (PacState (a,b,c,or,i,l) o m Dying))) pics | a == 0 = scale 0.9 0.9 (pics!!28)
                                                                         | a == 1 = scale 0.9 0.9 (pics!!72)
                                                                         | a == 2 = scale 0.9 0.9 (pics!!81)

pieceToPic (PacPlayer (Pacman (PacState (a,b,c,R,i,l) _ Open m))) pics | a == 0 = scale 0.9 0.9 (pics!!5)
                                                                       | a == 1 = scale 0.9 0.9 (pics!!68)
                                                                       | a == 2 = scale 0.9 0.9 (pics!!77)
                                                                       | a == 99 = scale 0.9 0.9 (pics!!95)

pieceToPic (PacPlayer (Pacman (PacState (a,b,c,L,i,l) _ Open m))) pics | a == 0 = scale 0.9 0.9 (pics!!4)
                                                                       | a == 1 = scale 0.9 0.9 (pics!!69)
                                                                       | a == 2 = scale 0.9 0.9 (pics!!78)
                                                                       | a == 99 = scale 0.9 0.9 (pics!!96)

pieceToPic (PacPlayer (Pacman (PacState (a,b,c,U,i,l) _ Open m))) pics | a == 0 = scale 0.9 0.9 (pics!!6)
                                                                       | a == 1 = scale 0.9 0.9 (pics!!70)
                                                                       | a == 2 = scale 0.9 0.9 (pics!!79)
                                                                       | a == 99 = scale 0.9 0.9 (pics!!97)
                                                                        
pieceToPic (PacPlayer (Pacman (PacState (a,b,c,D,i,l) _ Open m))) pics | a == 0 = scale 0.9 0.9 (pics!!7) 
                                                                       | a == 1 = scale 0.9 0.9 (pics!!71)
                                                                       | a == 2 = scale 0.9 0.9 (pics!!80)
                                                                       | a == 99 = scale 0.9 0.9 (pics!!98)

pieceToPic (PacPlayer (Pacman (PacState (a,b,c,R,i,l) _ Closed m))) pics | a == 0 = scale 0.9 0.9 (pics!!1) 
                                                                         | a == 1 = scale 0.9 0.9 (pics!!64)
                                                                         | a == 2 = scale 0.9 0.9 (pics!!73)
                                                                         | a == 99 = scale 0.9 0.9 (pics!!99)

pieceToPic (PacPlayer (Pacman (PacState (a,b,c,L,i,l) _ Closed m))) pics | a == 0 = scale 0.9 0.9 (pics!!0)
                                                                         | a == 1 = scale 0.9 0.9 (pics!!65)
                                                                         | a == 2 = scale 0.9 0.9 (pics!!74)
                                                                         | a == 99 = scale 0.9 0.9 (pics!!100)
                                                                          
pieceToPic (PacPlayer (Pacman (PacState (a,b,c,U,i,l) _ Closed m))) pics | a == 0 = scale 0.9 0.9 (pics!!2) 
                                                                         | a == 1 = scale 0.9 0.9 (pics!!66)
                                                                         | a == 2 = scale 0.9 0.9 (pics!!75)
                                                                         | a == 99 = scale 0.9 0.9 (pics!!101)

pieceToPic (PacPlayer (Pacman (PacState (a,b,c,D,i,l) _ Closed m))) pics | a == 0 = scale 0.9 0.9 (pics!!3)
                                                                         | a == 1 = scale 0.9 0.9 (pics!!67)
                                                                         | a == 2 = scale 0.9 0.9 (pics!!76)
                                                                         | a == 99 = scale 0.9 0.9 (pics!!102)
                                                                          
pieceToPic (PacPlayer (Pacman (PacState (a,b,c,Null,i,l) _ Open m))) pics | a == 0 = scale 0.9 0.9 (pics!!5)
                                                                          | a == 1 = scale 0.9 0.9 (pics!!68)
                                                                          | a == 2 = scale 0.9 0.9 (pics!!77)
                                                                          | a == 99 = scale 0.9 0.9 (pics!!95)

pieceToPic (PacPlayer (Pacman (PacState (a,b,c,Null,i,l) _ Closed m))) pics | a == 0 = scale 0.9 0.9 (pics!!1)
                                                                            | a == 1 = scale 0.9 0.9 (pics!!64)
                                                                            | a == 2 = scale 0.9 0.9 (pics!!73)
                                                                            | a == 99 = scale 0.0 0.9 (pics!!95)
                                               
pieceToPic (PacPlayer (Ghost (GhoState s Dead))) pics = scale 0.9 0.9 (pics!!24) 
pieceToPic (PacPlayer (Ghost (GhoState (a,b,c,R,i,l) Alive))) pics | elem a [1,5..12] = scale 0.9 0.9 (pics!!9)
                                                                   | elem a [2,6..12] = scale 0.9 0.9 (pics!!13)
                                                                   | elem a [3,7..12] = scale 0.9 0.9 (pics!!17)
                                                                   | elem a [4,8..12] = scale 0.9 0.9 (pics!!21)

pieceToPic (PacPlayer (Ghost (GhoState (a,b,c,L,i,l) Alive))) pics | elem a [1,5..12] = scale 0.9 0.9 (pics!!8)
                                                                   | elem a [2,6..12] = scale 0.9 0.9 (pics!!12)
                                                                   | elem a [3,7..12] = scale 0.9 0.9 (pics!!16)
                                                                   | elem a [4,8..12] = scale 0.9 0.9 (pics!!20)

pieceToPic (PacPlayer (Ghost (GhoState (a,b,c,U,i,l) Alive))) pics | elem a [1,5..12] = scale 0.9 0.9 (pics!!10)
                                                                   | elem a [2,6..12] = scale 0.9 0.9 (pics!!14)
                                                                   | elem a [3,7..12] = scale 0.9 0.9 (pics!!18)
                                                                   | elem a [4,8..12] = scale 0.9 0.9 (pics!!22)

pieceToPic (PacPlayer (Ghost (GhoState (a,b,c,D,i,l) Alive))) pics | elem a [1,5..12] = scale 0.9 0.9 (pics!!11)
                                                                   | elem a [2,6..12] = scale 0.9 0.9 (pics!!15)
                                                                   | elem a [3,7..12] = scale 0.9 0.9 (pics!!19)
                                                                   | elem a [4,8..12] = scale 0.9 0.9 (pics!!23)

pieceToPic (PacPlayer (Ghost (GhoState (a,b,c,Null,i,l) Alive))) pics | elem a [1,5..12] = scale 0.9 0.9 (pics!!10)
                                                                      | elem a [2,6..12] = scale 0.9 0.9 (pics!!14)
                                                                      | elem a [3,7..12] = scale 0.9 0.9 (pics!!18)
                                                                      | elem a [4,8..12] = scale 0.9 0.9 (pics!!22)

corridorToPic :: Corridor -> [Picture] -> Int -> [Picture]
corridorToPic [] _ _ = []
corridorToPic (h:t) pics x = (Translate (fromIntegral(x*95)) 0 ((scale 1 1 (pieceToPic h pics))):(corridorToPic t pics (x+1)))

mazeToPic :: Maze -> [Picture] -> (Int,Int) -> [Picture]
mazeToPic [] _ _ = []
mazeToPic (h:t) pics (x,y) = (Translate 0 (fromIntegral(y*(-95))) (Pictures (corridorToPic h pics x))):(mazeToPic t pics (x,y+1))

-- | Atribui uma /Picture/ a um /World/, desenhando todo o /Maze/ e jogadores dentro do mesmo.
drawMazeOnePic :: [Picture] -> World -> Picture
drawMazeOnePic img (World (State maze players lvl) pid step) 
     | length maze == 23 = translate (-279) 237 (scale 0.21 0.21 (pictures $ mazeToPic mazeWPl img (0,0))) 
     | otherwise = translate (-370) 230 (scale 0.32 0.32 (pictures $ mazeToPic mazeWPl img (0,0)))
           where mazeWPl = placePlayersOnMap players maze 

-- | Passa de um número para uma lista com todos os seus dígitos, pela ordem que aparecem.
numToDigits :: Int -> [Int]
numToDigits n = nums 
      where stringNum = show n
            nums = map digitToInt stringNum  

-- | Usa a função anterior e, sendo __n__ o número de dígitos do input, adiciona __7-n__ zeros no início da lista gerada pela função __numToDigits__.
numToDigitsWZero :: Int -> [Int]
numToDigitsWZero n = zerosRestantes ++ l 
         where l = numToDigits n 
               comp = length l 
               numzeros = 7 - comp  
               zerosRestantes = replicate numzeros 0  

-- | Converte um dígito numa /Picture/.
numToPic :: Int -> [Picture] -> Picture 
numToPic n pics | n == 0 = scale 0.06 0.06 (pics!!39)
                | n == 1 = scale 0.06 0.06 (pics!!40)
                | n == 2 = scale 0.06 0.06 (pics!!41)
                | n == 3 = scale 0.06 0.06 (pics!!42)
                | n == 4 = scale 0.06 0.06 (pics!!43)
                | n == 5 = scale 0.06 0.06 (pics!!44)
                | n == 6 = scale 0.06 0.06 (pics!!45)
                | n == 7 = scale 0.06 0.06 (pics!!46)
                | n == 8 = scale 0.06 0.06 (pics!!47)
                | n == 9 = scale 0.06 0.06 (pics!!48)

-- | Esta função carrega diferentes /Pictures/ relativas a Pacmans e é responsável pela imagem do Pacman que aparece nos 
-- /stats/ do jogador, abaixo do /Maze/.
drawPacsId :: Int -> PacMode -> [Picture] -> Picture
drawPacsId 0 Dying pics = scale 0.25 0.25 (pics!!28) 
drawPacsId 1 Dying pics = scale 0.25 0.25 (pics!!72) 
drawPacsId 2 Dying pics = scale 0.25 0.25 (pics!!81) 
drawPacsId 0 _ pics = scale 0.25 0.25 (pics!!5) 
drawPacsId 1 _ pics = scale 0.25 0.25 (pics!!68)
drawPacsId 2 _ pics = scale 0.25 0.25 (pics!!77)
drawPacsId 99 _ pics = scale 0.25 0.25 (pics!!95)

drawLivesAux :: Int -> Int -> [Picture] -> [Picture]
drawLivesAux 0 _ _ = []
drawLivesAux n ac pics = (scale 0.05 0.05 (Translate (fromIntegral(ac*900)) 0 (pics!!29))) : (drawLivesAux (n-1) (ac+1) pics)

-- | Cria uma /Picture/ com um número de corações igual ao número de vidas de um Pacman. 

-- |  Caso o Pacman tenha 0 vidas aparace uma imagem com a frase: __LAST CHANCE__. 

-- | Caso o Pacman esteja em Modo __Dying__, aparece uma imagem com a palavra: __DEAD__.
drawLives :: Int -> [Picture] -> Picture
drawLives n pics = pictures $ drawLivesAux n 0 pics

-- | Dado um determinado __Time Mega__, atribui uma /Picture/ de barra correspondente ao tempo que resta. Caso tenha __Time Mega__ de 0 aparece a imagem com
-- a frase: __NO TIME MEGA__.
drawTimeMega :: Double -> [Picture] -> Picture 
drawTimeMega 0 pics = scale 0.93 0.93 (pics!!30)
drawTimeMega n pics | elem n [250,500,750,1000]    = scale 0.93 0.93 (pics!!31)
                    | elem n [1250,1500,1750,2000] = scale 0.93 0.93 (pics!!32)
                    | elem n [2250,2500,2750,3000] = scale 0.93 0.93 (pics!!33)
                    | elem n [3250,3500,3750,4000] = scale 0.93 0.93 (pics!!34)
                    | elem n [4250,4500,4750,5000] = scale 0.93 0.93 (pics!!35)
                    | elem n [5250,5500,5750,6000] = scale 0.93 0.93 (pics!!36)
                    | elem n [6250,6500,6750,7000] = scale 0.93 0.93 (pics!!37)
                    | elem n [7250,7500,7750,8000] = scale 0.93 0.93 (pics!!38)

drawScoreAux :: [Int] -> Float -> [Picture] -> [Picture] 
drawScoreAux [] ac pics = []
drawScoreAux (x:xs) ac pics = (Translate (ac*12) 0 numPic) : (drawScoreAux xs (ac+1) pics)
       where numPic = numToPic x pics

-- | A partir dos pontos de um determinado Pacman e da função __numsToDigitsWZero__, produz uma imagens corresponde aos pontos do Pacman com sete dígitos.
drawScore :: Int -> [Picture] -> Picture 
drawScore n pics = pictures [picScore,picPoints,(Translate 55 0 (pictures $ drawScoreAux nums 0 pics))] 
    where nums = numToDigitsWZero n  
          picScore = Translate 0 1 (scale 0.13 0.13 (pics!!49)) 
          picPoints = Translate 190 1 (scale 0.07 0.07 (pics!!113))

-- | Cria a imagem acima do /Maze/ com __Level__ e: __Grupo 67__. 
drawHeader :: Int -> [Picture] -> Picture
drawHeader lvl pics = pictures [headerImg,numsImgs] 
       where headerImg = scale 0.4 0.4 (pics!!51)
             numsLvl = numToDigits lvl  
             numsImgs = scale 1.15 1.15 (Translate 305 0 (pictures $ drawScoreAux numsLvl 0 pics))

-- | Cria a imagem de todas as /stats/ de um Pacman.             
drawStatsOnePlayer :: Player -> [Picture] -> Picture 
drawStatsOnePlayer player pics = pictures [barrainf,pac,drawlives,drawscore,drawtimemega]
        where paclives = getPlayerHealth player   
              drawlives = if pacmode == Dying 
                          then scale 0.77 0.77 (Translate 105 0 (pics!!62))
                          else if paclives == 0 
                               then scale 0.77 0.77 (Translate 105 0 (pics!!63))
                               else Translate 35 0 (drawLives paclives pics) 
              score = getPlayerPoints player 
              drawscore = Translate 225 0 (drawScore score pics)
              timemega = getTimeMega player 
              drawtimemega = Translate 582 0 (drawTimeMega timemega pics) 
              barrainf = Translate 315 0 (scale 0.4 0.4 (pics!!50))
              pac = Translate (-32) 0 (drawPacsId (getPlayerID player) (getPacmanMode player) pics)
              pacmode = getPacmanMode player 

drawStatsAux :: [Player] -> Float -> [Picture] -> [Picture] 
drawStatsAux [] ac  pics = []
drawStatsAux (x:xs) ac pics = (Translate 0 (ac*(-38)) (drawStatsOnePlayer x pics)) : (drawStatsAux xs (ac+1) pics) 

-- | Generaliza a função anterior para todos os Pacmans.
drawStats :: [Player] -> [Picture] -> Picture
drawStats players pics = pictures $ drawStatsAux pacs 0 pics 
              where pacs = rmGhosts players

-- | Com as funções __drawMazeOnePic__, __drawStats__ e __drawHeader__, cria toda a imagem dentro do Modo Normal e MiniGames.
drawWorld :: [Picture] -> World -> Picture 
drawWorld pics w@(World (State maze players lvl) pid step) = pictures [imageMaze,imageStats,imageHeader]  
             where imageMaze = drawMazeOnePic pics w
                   imageStats = Translate (-320) (-243) (drawStats players pics)
                   imageHeader = Translate (-5) 270 (drawHeader lvl pics) 

-- | Primeiro /world/ do jogo.
world1 :: World 
world1 = World state1 0 0 

-- | Segundo /world/ do jogo.
world2 :: World 
world2 = World state2 0 0 

-- | Terceiro /world/ do jogo.
world3 :: World 
world3 = World state3 0 0 

-- | Quarta /world/ do jogo.
world4 :: World 
world4 = World state4 0 0 

-- | /world/ do __KillPacman__.
wordlKillPacman :: World 
wordlKillPacman = World stateKillPacman 7 0 

-- | /world/ do __PacDungeon__. 
wordlPacDungeon :: World 
wordlPacDungeon = World statePacDungeon 0 0 

-- | Esta função utiliza as funções de reagir à passagem do tempo da Tarefa 4 e atualiza o /World/ a cada iteração quando o utilizador está no Modo Normal ou MiniGames.
timeChangeWorld :: Float -> World -> World
timeChangeWorld f w@(World s pid step) = if (length $ maze $ s) == 23 
                                         then World (passTimePacDungeon step pid s) pid (step+1)
                                         else World (passTime step pid s) pid (step+1)

--  | A __timeChange__ permite usar a função anterior para situações de jogo e é usada também para fazer as passagens dos /frames/ na "Love Story".
-- Tentamos também fazer com que os menus de fim de jogo aparecessem quando o jogo terminasse mas infelizmente tivemos que usar o __eventChange__.
timeChange :: Float -> Game -> Game 
timeChange f (Game w NoMenu (OpPlay SelectedNormal) s) = Game (timeChangeWorld f w) NoMenu (OpPlay SelectedNormal) s
timeChange f (Game w NoMenu (OpPlay SelectedPD) s) = Game (timeChangeWorld f w) NoMenu (OpPlay SelectedPD) s
timeChange f (Game w NoMenu (OpPlay SelectedKP) s) = Game (timeChangeWorld f w) NoMenu (OpPlay SelectedKP) s
{-----------------------------------------------------------------
timeChange f (Game w@(World (State m pl lvl) p step) NoMenu (OpPlay SelectedKP) MazeKillPacman) | thereIsOneFood m = Game w (GameOverMiniGames Lose) (OpPlayAgain KillPacman) MazeKillPacman
                                                                                                | not $ (elem Normal allPModes || elem Mega allPModes) = Game w (GameOverMiniGames Win) (OpPlayAgain KillPacman) MazeKillPacman
                                                                where allPModes = map getPacmanMode (rmGhosts pl)

timeChange f (Game w@(World (State m pl lvl) p step) NoMenu (OpPlay SelectedNormal) s) | not $ (elem Mega allPModes || elem Normal allPModes) = Game w (GameOverModeNormal Lose) (OpPlayAgain NoMiniGame) s
                                                                                 where allPModes = map getPacmanMode (rmGhosts pl)  

timeChange f (Game w@(World (State m pl lvl) p step) NoMenu (OpPlay SelectedPD) MazePacDungeon) | not $ elem 99 ids = Game w (HLOVE F1) NothingSelected One 
                                                                                                   | pacMode == Dying = Game w (GameOverMiniGames Lose) (OpPlayAgain PacDungeon) MazePacDungeon  
                                                                                                 where pac = encID 0 pl 
                                                                                                       pacMode = getPacmanMode pac     
                                                                                                       ids = map getPlayerID pl                                                                                         

------------------------------------------------------------------}
timeChange _ (Game w (HLOVE F1) NothingSelected s) = (Game world1 (HLOVE F2) NothingSelected One) 
timeChange _ (Game w (HLOVE F2) NothingSelected s) = (Game world1 (HLOVE F3) NothingSelected s) 
timeChange _ (Game w (HLOVE F3) NothingSelected s) = (Game world1 (HLOVE F4) NothingSelected s) 
timeChange _ (Game w (HLOVE F4) NothingSelected s) = (Game world1 (HLOVE F5) NothingSelected s) 
timeChange _ (Game w (HLOVE F5) NothingSelected s) = (Game world1 (HLOVE F6) NothingSelected s) 
timeChange _ (Game w (HLOVE F6) NothingSelected s) = (Game world1 (HLOVE F7) NothingSelected s) 
timeChange _ (Game w (HLOVE F7) NothingSelected s) = (Game world1 (HLOVE F8) NothingSelected s) 
timeChange _ (Game w (HLOVE F8) NothingSelected s) = (Game world1 (HLOVE F9) NothingSelected s) 
timeChange _ (Game w (HLOVE F9) NothingSelected s) = (Game world1 (HLOVE F10) NothingSelected s) 
timeChange _ (Game w (HLOVE F10) NothingSelected s) = (Game world1 (HLOVE F11) NothingSelected s) 
timeChange _ (Game w (HLOVE F11) NothingSelected s) = (Game world1 (HLOVE F12) NothingSelected s) 
timeChange _ (Game w (HLOVE F12) NothingSelected s) = (Game world1 (HLOVE F13) NothingSelected s) 
timeChange _ (Game w (HLOVE F13) NothingSelected s) = (Game world1 (HLOVE F14) NothingSelected s) 
timeChange _ (Game w (HLOVE F14) NothingSelected s) = (Game world1 (HLOVE F15) NothingSelected s) 
timeChange _ (Game w (HLOVE F15) NothingSelected s) = (Game world1 (HLOVE F16) NothingSelected s) 
timeChange _ (Game w (HLOVE F16) NothingSelected s) = (Game world1 (HLOVE F17) NothingSelected s) 
timeChange _ (Game w (HLOVE F17) NothingSelected s) = (Game world1 (HLOVE F18) NothingSelected s) 
timeChange _ (Game w (HLOVE F18) NothingSelected s) = (Game world1 (HLOVE F19) NothingSelected s) 
timeChange _ (Game w (HLOVE F19) NothingSelected s) = (Game world1 (HLOVE F20) NothingSelected s) 
timeChange _ (Game w (HLOVE F20) NothingSelected s) = (Game world1 (HLOVE F21) NothingSelected s) 
timeChange _ (Game w (HLOVE F21) NothingSelected s) = (Game world1 (HLOVE F22) NothingSelected s) 
timeChange _ (Game w (HLOVE F22) NothingSelected s) = (Game world1 (HLOVE F23) NothingSelected s) 
timeChange _ (Game w (HLOVE F23) NothingSelected s) = (Game world1 (HLOVE F24) NothingSelected s) 
timeChange _ (Game w (HLOVE F24) NothingSelected s) = (Game world1 (HLOVE F25) NothingSelected s) 
timeChange _ (Game w (HLOVE F25) NothingSelected s) = (Game world1 (HLOVE F26) NothingSelected s) 
timeChange _ (Game w (HLOVE F26) NothingSelected s) = (Game world1 (HLOVE F27) NothingSelected s) 
timeChange _ (Game w (HLOVE F27) NothingSelected s) = (Game world1 (HLOVE F28) NothingSelected s) 
timeChange _ (Game w (HLOVE F28) NothingSelected s) = (Game world1 (HLOVE F29) NothingSelected s) 
timeChange _ (Game w (HLOVE F29) NothingSelected s) = (Game world1 (HLOVE F30) NothingSelected s) 
timeChange _ (Game w (HLOVE F30) NothingSelected s) = (Game world1 (HLOVE F31) NothingSelected s) 
timeChange _ (Game w (HLOVE F31) NothingSelected s) = (Game world1 (HLOVE F32) NothingSelected s) 
timeChange _ (Game w (HLOVE F32) NothingSelected s) = (Game world1 (HLOVE F33) NothingSelected s) 
timeChange _ (Game w (HLOVE F33) NothingSelected s) = (Game world1 (HLOVE F34) NothingSelected s) 
timeChange _ (Game w (HLOVE F34) NothingSelected s) = (Game world1 (HLOVE F35) NothingSelected s) 
timeChange _ (Game w (HLOVE F35) NothingSelected s) = (Game world1 (HLOVE F36) NothingSelected s) 
timeChange _ (Game w (HLOVE F36) NothingSelected s) = (Game world1 (HLOVE F37) NothingSelected s) 
timeChange _ (Game w (HLOVE F37) NothingSelected s) = (Game world1 (HLOVE F38) NothingSelected s) 
timeChange _ (Game w (HLOVE F38) NothingSelected s) = (Game world1 (HLOVE F39) NothingSelected s) 
timeChange _ (Game w (HLOVE F39) NothingSelected s) = (Game world1 (HLOVE F40) NothingSelected s) 
timeChange _ (Game w (HLOVE F40) NothingSelected s) = (Game world1 (HLOVE F41) NothingSelected s) 
timeChange _ (Game w (HLOVE F41) NothingSelected s) = (Game world1 (HLOVE F42) NothingSelected s) 
timeChange _ (Game w (HLOVE F42) NothingSelected s) = (Game world1 (HLOVE F43) NothingSelected s) 
timeChange _ (Game w (HLOVE F43) NothingSelected s) = (Game world1 (HLOVE F44) NothingSelected s) 
timeChange _ (Game w (HLOVE F44) NothingSelected s) = (Game world1 (HLOVE F45) NothingSelected s) 
timeChange _ (Game w (HLOVE F45) NothingSelected s) = (Game world1 (HLOVE F46) NothingSelected s) 
timeChange _ (Game w (HLOVE F46) NothingSelected s) = (Game world1 (HLOVE F47) NothingSelected s) 
timeChange _ (Game w (HLOVE F47) NothingSelected s) = (Game world1 (HLOVE F48) NothingSelected s) 
timeChange _ (Game w (HLOVE F48) NothingSelected s) = (Game world1 (HLOVE F49) NothingSelected s) 
timeChange _ (Game w (HLOVE F49) NothingSelected s) = (Game world1 (HLOVE F50) NothingSelected s) 
timeChange _ (Game w (HLOVE F50) NothingSelected s) = (Game world1 (HLOVE F51) NothingSelected s) 
timeChange _ (Game w (HLOVE F51) NothingSelected s) = (Game world1 (HLOVE F52) NothingSelected s) 
timeChange _ (Game w (HLOVE F52) NothingSelected s) = (Game world1 (HLOVE F53) NothingSelected s) 
timeChange _ (Game w (HLOVE F53) NothingSelected s) = (Game world1 (HLOVE F54) NothingSelected s) 
timeChange _ (Game w (HLOVE F54) NothingSelected s) = (Game world1 (HLOVE F55) NothingSelected s) 
timeChange _ (Game w (HLOVE F55) NothingSelected s) = (Game world1 (HLOVE F56) NothingSelected s) 
timeChange _ (Game w (HLOVE F56) NothingSelected s) = (Game world1 (HLOVE F57) NothingSelected s) 
timeChange _ (Game w (HLOVE F57) NothingSelected s) = (Game world1 (HLOVE F58) NothingSelected s) 
timeChange _ (Game w (HLOVE F58) NothingSelected s) = (Game world1 (HLOVE F59) NothingSelected s) 
timeChange _ (Game w (HLOVE F59) NothingSelected s) = (Game world1 (HLOVE F60) NothingSelected s) 
timeChange _ (Game w (HLOVE F60) NothingSelected s) = Game world1 (GameOverMiniGames Win) (OpPlayAgain PacDungeon) MazePacDungeon  

timeChange f game = game 

-- | Estado incial do /Game/.
startGame :: Game 
startGame = Game world1 ScreenSaver NothingSelected One  

-- | Função baseada na __updateControlledPlayer__ do documento __Main.hs__ fornecido pela aquipa docente e generalizada para o /World/.
eventChangeWorld :: Event -> World -> World 
eventChangeWorld (EventKey (SpecialKey KeyRight) Down _ _) (World (State maze players level) pid step) = World (State maze (changeOr (Move pid R) players) level) pid step 
eventChangeWorld (EventKey (SpecialKey KeyLeft) Down _ _) (World (State maze players level) pid step) = World (State maze (changeOr (Move pid L) players) level) pid step 
eventChangeWorld (EventKey (SpecialKey KeyUp) Down _ _) (World (State maze players level) pid step) = World (State maze (changeOr (Move pid U) players) level) pid step 
eventChangeWorld (EventKey (SpecialKey KeyDown) Down _ _) (World (State maze players level) pid step) = World (State maze (changeOr (Move pid D) players) level) pid step 
eventChangeWorld _ w = w

-- | Esta função regula toda a interação do utilizador com a interface.

-- | Permite que o mesmo navegue entre menus, escolha o /Maze/ com que deseja jogar e entre no modo de jogo que pretende explorar.
eventChange :: Event -> Game -> Game  

--- ScreenSaver ---
eventChange (EventKey (SpecialKey KeyEnter) Down _ _) (Game w ScreenSaver NothingSelected s) = Game w MainMenu NothingSelected s
--- MainMenu ---
eventChange (EventKey (SpecialKey KeyDown) Down _ _) (Game w MainMenu NothingSelected s) = Game w MainMenu (OpPlay NotSelected) s 
eventChange (EventKey (SpecialKey KeyDown) Down _ _) (Game w MainMenu (OpPlay NotSelected) s) = Game w MainMenu (OpMapas NoMaze) s 
eventChange (EventKey (SpecialKey KeyDown) Down _ _) (Game w MainMenu (OpMapas NoMaze) s) = Game w MainMenu (OpMinigames NoMiniGame) s  
eventChange (EventKey (SpecialKey KeyDown) Down _ _) (Game w MainMenu (OpMinigames NoMiniGame) s) = Game w MainMenu (OpCreditos NotSelected) s 
eventChange (EventKey (SpecialKey KeyUp) Down _ _) (Game w MainMenu (OpCreditos NotSelected) s) = Game w MainMenu (OpMinigames NoMiniGame) s
eventChange (EventKey (SpecialKey KeyUp) Down _ _) (Game w MainMenu (OpMinigames NoMiniGame) s) = Game w MainMenu (OpMapas NoMaze) s
eventChange (EventKey (SpecialKey KeyUp) Down _ _) (Game w MainMenu (OpMapas NoMaze) s) = Game w MainMenu (OpPlay NotSelected) s
--- Mapas --
eventChange (EventKey (SpecialKey KeyRight) Down _ _) (Game w MainMenu (OpMapas NoMaze) s) = Game w MainMenu (OpMapas Maze1) s
eventChange (EventKey (SpecialKey KeyLeft) Down _ _) (Game w MainMenu (OpMapas Maze1) s) = Game w MainMenu (OpMapas NoMaze) s
eventChange (EventKey (SpecialKey KeyLeft) Down _ _) (Game w MainMenu (OpMapas Maze3) s) = Game w MainMenu (OpMapas NoMaze) s
eventChange (EventKey (SpecialKey KeyRight) Down _ _) (Game w MainMenu (OpMapas Maze1) s) = Game w MainMenu (OpMapas Maze2) s
eventChange (EventKey (SpecialKey KeyDown) Down _ _) (Game w MainMenu (OpMapas Maze1) s) = Game w MainMenu (OpMapas Maze3) s
eventChange (EventKey (SpecialKey KeyLeft) Down _ _) (Game w MainMenu (OpMapas Maze2) s) = Game w MainMenu (OpMapas Maze1) s
eventChange (EventKey (SpecialKey KeyDown) Down _ _) (Game w MainMenu (OpMapas Maze2) s) = Game w MainMenu (OpMapas Maze4) s
eventChange (EventKey (SpecialKey KeyUp) Down _ _) (Game w MainMenu (OpMapas Maze3) s) = Game w MainMenu (OpMapas Maze1) s 
eventChange (EventKey (SpecialKey KeyRight) Down _ _) (Game w MainMenu (OpMapas Maze3) s) = Game w MainMenu (OpMapas Maze4) s
eventChange (EventKey (SpecialKey KeyUp) Down _ _) (Game w MainMenu (OpMapas Maze4) s) = Game w MainMenu (OpMapas Maze2) s 
eventChange (EventKey (SpecialKey KeyLeft) Down _ _) (Game w MainMenu (OpMapas Maze4) s) = Game w MainMenu (OpMapas Maze3) s
--- MiniGames ---
eventChange (EventKey (SpecialKey KeyRight) Down _ _) (Game w MainMenu (OpMinigames NoMiniGame) s) = Game w MainMenu (OpMinigames PacDungeon) s
eventChange (EventKey (SpecialKey KeyLeft) Down _ _) (Game w MainMenu (OpMinigames _) s) = Game w MainMenu (OpMinigames NoMiniGame) s
eventChange (EventKey (SpecialKey KeyDown) Down _ _) (Game w MainMenu (OpMinigames PacDungeon) s) = Game w MainMenu (OpMinigames KillPacman) s
eventChange (EventKey (SpecialKey KeyUp) Down _ _) (Game w MainMenu (OpMinigames KillPacman) s) = Game w MainMenu (OpMinigames PacDungeon) s
--- Creditos ---
--eventChange (EventKey (SpecialKey KeyEnter) Down _ _) (Game w MainMenu (OpCreditos NotSelected) s) = Game w MainMenu (OpCreditos Selected) s
--eventChange (EventKey (Char 'q') Down _ _) (Game w MainMenu (OpCreditos Selected) s) = Game w MainMenu (OpCreditos NotSelected) s 

--- Pausar o Jogo ---
eventChange (EventKey (Char 'p') Down _ _) (Game w NoMenu (OpPlay SelectedNormal) s) = Game w PauseMenu Resume s   
eventChange (EventKey (Char 'p') Down _ _) (Game w NoMenu (OpPlay SelectedPD) s) = Game w PauseMenu Resume MazePacDungeon
eventChange (EventKey (Char 'p') Down _ _) (Game w NoMenu (OpPlay SelectedKP) s) = Game w PauseMenu Resume MazeKillPacman
eventChange (EventKey (SpecialKey KeyRight) Down _ _) (Game w PauseMenu Resume s) = Game w PauseMenu BackToMenu s 
eventChange (EventKey (SpecialKey KeyLeft) Down _ _) (Game w PauseMenu BackToMenu s) = Game w PauseMenu Resume s 
eventChange (EventKey (SpecialKey KeyEnter) Down _ _) (Game w PauseMenu Resume s) | s == MazePacDungeon = Game w NoMenu (OpPlay SelectedPD) MazePacDungeon
                                                                         | s == MazeKillPacman = Game w NoMenu (OpPlay SelectedKP) MazeKillPacman 
                                                                         | otherwise = Game w NoMenu (OpPlay SelectedNormal) s 
eventChange (EventKey (SpecialKey KeyEnter) Down _ _) (Game w PauseMenu BackToMenu s) | (s == MazePacDungeon || s == MazeKillPacman || s == One) = Game world1 MainMenu NothingSelected One                                                                                         
                                                                                      | s == Two = Game world2 MainMenu NothingSelected Two 
                                                                                      | s == Three = Game world3 MainMenu NothingSelected Three 
                                                                                      | otherwise = Game world4 MainMenu NothingSelected Four 

--- entrar na Play ---
eventChange (EventKey (SpecialKey KeyEnter) Down _ _) (Game w MainMenu (OpPlay NotSelected) s) | s == One = Game world1 NoMenu (OpPlay SelectedNormal) s 
                                                                                               | s == Two = Game world2 NoMenu (OpPlay SelectedNormal) s
                                                                                               | s == Three = Game world3 NoMenu (OpPlay SelectedNormal) s
                                                                                               | s == Four = Game world4 NoMenu (OpPlay SelectedNormal) s

eventChange key (Game w@(World (State m pl lvl) p step) NoMenu (OpPlay SelectedKP) MazeKillPacman) = if thereIsOneFood m 
                                                                                                     then Game w (GameOverMiniGames Lose) (OpPlayAgain KillPacman) MazeKillPacman
                                                                                                     else if not $ (elem Normal allPModes || elem Mega allPModes)
                                                                                                          then Game w (GameOverMiniGames Win) (OpPlayAgain KillPacman) MazeKillPacman
                                                                                                          else Game (eventChangeWorld key w) NoMenu (OpPlay SelectedKP) MazeKillPacman
                                                                                               where allPModes = map getPacmanMode (rmGhosts pl)

eventChange key (Game w@(World (State m pl lvl) p step) NoMenu (OpPlay SelectedPD) MazePacDungeon) = if not $ elem 99 ids
                                                                                                     then Game w (HLOVE F1) NothingSelected One 
                                                                                                     else if pacMode == Dying
                                                                                                          then Game w (GameOverMiniGames Lose) (OpPlayAgain PacDungeon) MazePacDungeon  
                                                                                                          else Game (eventChangeWorld key w) NoMenu (OpPlay SelectedPD) MazePacDungeon
                                                                                                 where pac = encID 0 pl 
                                                                                                       pacMode = getPacmanMode pac     
                                                                                                       ids = map getPlayerID pl                                                                                         

--- JogarModoNormal ---
eventChange key (Game w@(World (State m pl lvl) p step) NoMenu (OpPlay SelectedNormal) s)  = if elem Mega allPModes || elem Normal allPModes 
                                                                                             then Game (eventChangeWorld key w) NoMenu (OpPlay SelectedNormal) s
                                                                                             else Game w (GameOverModeNormal Lose) (OpPlayAgain NoMiniGame) s
                                                                                 where allPModes = map getPacmanMode (rmGhosts pl)                                                                                    
--- GameOver ---
eventChange (EventKey (SpecialKey KeyEnter) Down _ _) (Game w (GameOverModeNormal _) (OpPlayAgain NoMiniGame) One) = Game world1 NoMenu (OpPlay SelectedNormal) One 
eventChange (EventKey (SpecialKey KeyEnter) Down _ _) (Game w (GameOverModeNormal _) (OpPlayAgain NoMiniGame) Two) = Game world2 NoMenu (OpPlay SelectedNormal) Two 
eventChange (EventKey (SpecialKey KeyEnter) Down _ _) (Game w (GameOverModeNormal _) (OpPlayAgain NoMiniGame) Three) = Game world3 NoMenu (OpPlay SelectedNormal) Three 
eventChange (EventKey (SpecialKey KeyEnter) Down _ _) (Game w (GameOverModeNormal _) (OpPlayAgain NoMiniGame) Four) = Game world4 NoMenu (OpPlay SelectedNormal) Four
eventChange (EventKey (SpecialKey KeyEnter) Down _ _) (Game w (GameOverModeNormal _) (OpQuit NoMiniGame) s) = Game world1 MainMenu NothingSelected s 
eventChange (EventKey (SpecialKey KeyRight) Down _ _) (Game w (GameOverModeNormal Win) (OpPlayAgain NoMiniGame) s) = Game world1 (GameOverModeNormal Win) (OpQuit NoMiniGame) s
eventChange (EventKey (SpecialKey KeyLeft) Down _ _) (Game w (GameOverModeNormal Win) (OpQuit NoMiniGame) s) = Game world1 (GameOverModeNormal Win) (OpPlayAgain NoMiniGame) s
eventChange (EventKey (SpecialKey KeyRight) Down _ _) (Game w (GameOverModeNormal Lose) (OpPlayAgain NoMiniGame) s) = Game world1 (GameOverModeNormal Lose) (OpQuit NoMiniGame) s
eventChange (EventKey (SpecialKey KeyLeft) Down _ _) (Game w (GameOverModeNormal Lose) (OpQuit NoMiniGame) s) = Game world1 (GameOverModeNormal Lose) (OpPlayAgain NoMiniGame) s
--- Mazes selecionados--- 
eventChange (EventKey (SpecialKey KeyEnter) Down _ _) (Game w MainMenu (OpMapas Maze1) s) = Game world1 MainMenu (OpMapas NoMaze) One 
eventChange (EventKey (SpecialKey KeyEnter) Down _ _) (Game w MainMenu (OpMapas Maze2) s) = Game world2 MainMenu (OpMapas NoMaze) Two  
eventChange (EventKey (SpecialKey KeyEnter) Down _ _) (Game w MainMenu (OpMapas Maze3) s) = Game world3 MainMenu (OpMapas NoMaze) Three 
eventChange (EventKey (SpecialKey KeyEnter) Down _ _) (Game w MainMenu (OpMapas Maze4) s) = Game world4 MainMenu (OpMapas NoMaze) Four
--- Minigames Selecionados --- 
eventChange (EventKey (SpecialKey KeyEnter) Down _ _) (Game w MainMenu (OpMinigames KillPacman) s) = Game wordlKillPacman NoMenu (OpPlay SelectedKP) MazeKillPacman
eventChange (EventKey (SpecialKey KeyEnter) Down _ _) (Game w MainMenu (OpMinigames PacDungeon) s) = Game wordlPacDungeon NoMenu (OpPlay SelectedPD) MazePacDungeon
--- GameOver Minigames ---
eventChange (EventKey (SpecialKey KeyRight) Down _ _) (Game w (GameOverMiniGames worL) (OpPlayAgain KillPacman) s) = Game w (GameOverMiniGames worL) (OpQuit KillPacman) s
eventChange (EventKey (SpecialKey KeyRight) Down _ _) (Game w (GameOverMiniGames worL) (OpPlayAgain PacDungeon) s) = Game w (GameOverMiniGames worL) (OpQuit PacDungeon) s
eventChange (EventKey (SpecialKey KeyLeft) Down _ _) (Game w (GameOverMiniGames worL) (OpQuit KillPacman) s) = Game w (GameOverMiniGames worL) (OpPlayAgain KillPacman) s 
eventChange (EventKey (SpecialKey KeyLeft) Down _ _) (Game w (GameOverMiniGames worL) (OpQuit PacDungeon) s) = Game w (GameOverMiniGames worL) (OpPlayAgain PacDungeon) s 
eventChange (EventKey (SpecialKey KeyEnter) Down _ _) (Game w (GameOverMiniGames _) (OpPlayAgain KillPacman) s) = Game wordlKillPacman NoMenu (OpPlay SelectedKP) s
eventChange (EventKey (SpecialKey KeyEnter) Down _ _) (Game w (GameOverMiniGames _) (OpPlayAgain PacDungeon) s) = Game wordlPacDungeon NoMenu (OpPlay SelectedPD) s
eventChange (EventKey (SpecialKey KeyEnter) Down _ _) (Game w (GameOverMiniGames _) (OpQuit _) s) = Game world1 MainMenu NothingSelected One
--- Easter Egg --- 
eventChange (EventKey (Char 'j') Down _ _) (Game w MainMenu (OpCreditos NotSelected) s) = Game w MainMenu EasterEgg s 
eventChange (EventKey (SpecialKey KeyEnter) Down _ _) (Game w MainMenu EasterEgg s) = Game w MainMenu (OpCreditos NotSelected) s

--- Ultimo Caso
eventChange _ game = game 

-- | Cria as duas /Pictures/ do menu de pausa.
pauseMenu :: Game -> [Picture] -> Picture 
pauseMenu (Game _ PauseMenu Resume _) pics = Translate (-05) 17 (scale 0.5 0.5 (pics!!115))
pauseMenu (Game _ PauseMenu BackToMenu _) pics = Translate (-05) 17 (scale 0.5 0.5 (pics!!116))

-- | Usa todas as funções que criam /Pictures/ como auxiliares e cria, para cada estado do /Game/, uma Picture.
drawGame :: [Picture] -> Game -> Picture 
--- ScreenSaver ---
drawGame pics (Game _ ScreenSaver _ _) = pics!!83
--- MainMenu ---
drawGame pics (Game _ MainMenu NothingSelected _) = pics!!94
drawGame pics (Game _ MainMenu (OpPlay NotSelected) _) = pics!!82
drawGame pics (Game _ MainMenu (OpMapas NoMaze) _) = pics!!84
drawGame pics (Game _ MainMenu (OpMinigames NoMiniGame) _) = pics!!85 
drawGame pics (Game _ MainMenu (OpCreditos NotSelected) _ ) = pics!!93
--- Mapas ---
drawGame pics (Game _ MainMenu (OpMapas Maze1) _) = pics!!87
drawGame pics (Game _ MainMenu (OpMapas Maze2) _) = pics!!88
drawGame pics (Game _ MainMenu (OpMapas Maze3) _) = pics!!89
drawGame pics (Game _ MainMenu (OpMapas Maze4) _) = pics!!90
--- Escolher Minigame ---
drawGame pics (Game _ MainMenu (OpMinigames PacDungeon) _) = pics!!91
drawGame pics (Game _ MainMenu (OpMinigames KillPacman) _) = pics!!92
drawGame pics (Game w NoMenu (OpPlay SelectedNormal) _) = drawWorld pics w
--- jogar minigames --- 
drawGame pics (Game w NoMenu (OpPlay SelectedPD) _) = drawWorld pics w 
drawGame pics (Game w NoMenu (OpPlay SelectedKP) _) = drawWorld pics w 
--- MenuGameOverModeNormal ---
drawGame pics (Game _ (GameOverModeNormal Lose) (OpPlayAgain NoMiniGame) _) = pics!!103
drawGame pics (Game _ (GameOverModeNormal Lose) (OpQuit NoMiniGame) _) = pics!!104
--- GameOverPacDungeon ---
drawGame pics (Game _ (GameOverMiniGames Win) (OpPlayAgain PacDungeon) MazePacDungeon) = pics!!105
drawGame pics (Game _ (GameOverMiniGames Win) (OpQuit PacDungeon) MazePacDungeon) = pics!!106
drawGame pics (Game _ (GameOverMiniGames Lose) (OpPlayAgain PacDungeon) MazePacDungeon) = pics!!107
drawGame pics (Game _ (GameOverMiniGames Lose) (OpQuit PacDungeon) MazePacDungeon) = pics!!108
--- GameOverKillPacman ---
drawGame pics (Game _ (GameOverMiniGames Win) (OpPlayAgain KillPacman) MazeKillPacman) = pics!!109
drawGame pics (Game _ (GameOverMiniGames Win) (OpQuit KillPacman) MazeKillPacman) = pics!!110
drawGame pics (Game _ (GameOverMiniGames Lose) (OpPlayAgain KillPacman) MazeKillPacman) = pics!!111
drawGame pics (Game _ (GameOverMiniGames Lose) (OpQuit KillPacman) MazeKillPacman) = pics!!112
--- Menu de Pausa ---
drawGame pics g@(Game w PauseMenu Resume _) = pictures [worldD,menuD]
                         where worldD = drawWorld pics w   
                               menuD = pauseMenu g pics 
drawGame pics g@(Game w PauseMenu BackToMenu _) = pictures [worldD,menuD]
                         where worldD = drawWorld pics w
                               menuD = pauseMenu g pics  
--- EasterEgg ---
drawGame pics (Game _ MainMenu EasterEgg _) = pics!!114
--- Love Story ---
drawGame pics (Game _ (HLOVE F1) _ _) = pics!!117
drawGame pics (Game _ (HLOVE F2) _ _) = pics!!118
drawGame pics (Game _ (HLOVE F3) _ _) = pics!!119
drawGame pics (Game _ (HLOVE F4) _ _) = pics!!120
drawGame pics (Game _ (HLOVE F5) _ _) = pics!!121
drawGame pics (Game _ (HLOVE F6) _ _) = pics!!122
drawGame pics (Game _ (HLOVE F7) _ _) = pics!!123
drawGame pics (Game _ (HLOVE F8) _ _) = pics!!124
drawGame pics (Game _ (HLOVE F9) _ _) = pics!!125
drawGame pics (Game _ (HLOVE F10) _ _) = pics!!126
drawGame pics (Game _ (HLOVE F11) _ _) = pics!!127
drawGame pics (Game _ (HLOVE F12) _ _) = pics!!128
drawGame pics (Game _ (HLOVE F13) _ _) = pics!!129
drawGame pics (Game _ (HLOVE F14) _ _) = pics!!130
drawGame pics (Game _ (HLOVE F15) _ _) = pics!!131
drawGame pics (Game _ (HLOVE F16) _ _) = pics!!132
drawGame pics (Game _ (HLOVE F17) _ _) = pics!!133
drawGame pics (Game _ (HLOVE F18) _ _) = pics!!134
drawGame pics (Game _ (HLOVE F19) _ _) = pics!!135
drawGame pics (Game _ (HLOVE F20) _ _) = pics!!136
drawGame pics (Game _ (HLOVE F21) _ _) = pics!!137
drawGame pics (Game _ (HLOVE F22) _ _) = pics!!138
drawGame pics (Game _ (HLOVE F23) _ _) = pics!!139
drawGame pics (Game _ (HLOVE F24) _ _) = pics!!140
drawGame pics (Game _ (HLOVE F25) _ _) = pics!!141
drawGame pics (Game _ (HLOVE F26) _ _) = pics!!142
drawGame pics (Game _ (HLOVE F27) _ _) = pics!!143
drawGame pics (Game _ (HLOVE F28) _ _) = pics!!144
drawGame pics (Game _ (HLOVE F29) _ _) = pics!!145
drawGame pics (Game _ (HLOVE F30) _ _) = pics!!146
drawGame pics (Game _ (HLOVE F31) _ _) = pics!!147
drawGame pics (Game _ (HLOVE F32) _ _) = pics!!148
drawGame pics (Game _ (HLOVE F33) _ _) = pics!!149
drawGame pics (Game _ (HLOVE F34) _ _) = pics!!150
drawGame pics (Game _ (HLOVE F35) _ _) = pics!!151
drawGame pics (Game _ (HLOVE F36) _ _) = pics!!152
drawGame pics (Game _ (HLOVE F37) _ _) = pics!!153
drawGame pics (Game _ (HLOVE F38) _ _) = pics!!154
drawGame pics (Game _ (HLOVE F39) _ _) = pics!!155
drawGame pics (Game _ (HLOVE F40) _ _) = pics!!156
drawGame pics (Game _ (HLOVE F41) _ _) = pics!!157
drawGame pics (Game _ (HLOVE F42) _ _) = pics!!158
drawGame pics (Game _ (HLOVE F43) _ _) = pics!!159
drawGame pics (Game _ (HLOVE F44) _ _) = pics!!160
drawGame pics (Game _ (HLOVE F45) _ _) = pics!!161
drawGame pics (Game _ (HLOVE F46) _ _) = pics!!162
drawGame pics (Game _ (HLOVE F47) _ _) = pics!!163
drawGame pics (Game _ (HLOVE F48) _ _) = pics!!164
drawGame pics (Game _ (HLOVE F49) _ _) = pics!!165
drawGame pics (Game _ (HLOVE F50) _ _) = pics!!166
drawGame pics (Game _ (HLOVE F51) _ _) = pics!!167
drawGame pics (Game _ (HLOVE F52) _ _) = pics!!168
drawGame pics (Game _ (HLOVE F53) _ _) = pics!!169
drawGame pics (Game _ (HLOVE F54) _ _) = pics!!170
drawGame pics (Game _ (HLOVE F55) _ _) = pics!!171
drawGame pics (Game _ (HLOVE F56) _ _) = pics!!172
drawGame pics (Game _ (HLOVE F57) _ _) = pics!!173
drawGame pics (Game _ (HLOVE F58) _ _) = pics!!174
drawGame pics (Game _ (HLOVE F59) _ _) = pics!!175
drawGame pics (Game _ (HLOVE F60) _ _) = pics!!176

-- | Função principal que carrega todas as imagens importadas e usa as funções __eventChange__ e __timeChange__ para operar sobre o __startGame__. 
main :: IO ()
main = do { loadedIMG <- loadImg ;
             Graphics.Gloss.Interface.Pure.Game.play 
             displayMode 
             black
             4 
             startGame
             (drawGame loadedIMG) 
             eventChange 
             timeChange } 