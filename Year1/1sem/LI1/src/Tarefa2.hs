-- | Nesta tarefa o __objetivo é fazer jogadas com os /Players/ (Ghosts e Pacmans)__. De modo a fazer jogadas utiliza-se a função __play__ que recebe uma __/Play/ (Move id or)__ e um __/State/ (maze players level)__, devolvendo um __/State/ (maze players level)__. 

module Tarefa2 where

import Types 
import Tarefa1


-- */State/ inicial utilizado para testes

-- | == Descrição da função  
-- __Cria um /State/ inicial típico com os /Ghosts/ dentro da casa e o Pacman abaixo da mesma.__   
-- Utiliza a função __generateMaze__ (da /Tarefa 1/) para criar o labirinto e a função __plIniciais__ para criar a sua lista de /Players/ iniciais.

-- | === Utilização
-- Utilizado nos vários testes efetuados ao longo da construção da Tarefa 2.
stateInicial :: Int -- ^ largura do Labirinto   
             -> Int -- ^ altura do labirinto
             -> Int -- ^ /seed/ para gerar as peças aleatórias
             -> State -- ^ Devolve um /State/ (um labirinto, uma lista de /Players/ e o nivel 1)
stateInicial x y sd = State (generateMaze x y sd) (plIniciais m) 1 
           where m = generateMaze x y sd 

-- | == Descrição da função
-- Cria a lista de /Players/ iniciais juntando o __pacInicial__ com os __ghostsIniciais__.

plIniciais :: Maze -> [Player]
plIniciais m = pacInicial m : ghostsInicial m

-- | == Descrição da função
-- __Cria um Pacman na posição inicial.__ Para isso, utiliza a função __coordsiniciaisPac__ que determinada a suas coordenadas iniciais. 

-- | == Exemplo
-- * @pacInicial (generateMaze 20 20 12) = Pacman (PacState (0,(__11,9__),1,Null,0,3) 0 Open Normal)@    
-- * @pacInicial (generateMaze 18 20 12) = Pacman (PacState (0,(__11,8__),1,Null,0,3) 0 Open Normal)@  

pacInicial :: Maze -> Player
pacInicial maze = Pacman (PacState (0,(x0,y0),1,Null,0,3) 0 Open Normal)
         where (x0,y0) = coordsiniciaisPac maze
              
-- | == Descrição da função 
-- Esta função recebe um labirinto e devolve uma lista com quatro /Ghosts/ com coordenadas correspondentes às suas posição dentro da casa do fantasmas da seguinda forma:  __@ MMMM @__  (caso seja par) ou  __@M M M M@__  (caso seja impar).

-- | == Exemplo               
-- * @ghostsInicial (generateMaze 20 20 12) = [Ghost  (GhoState (1,(__9,8__),1,Null,0,1) Alive),
--                                        Ghost  (GhoState (2,(__9,9__),1,Null,0,1) Alive),
--                                        Ghost  (GhoState (3,(__9,10__),1,Null,0,1) Alive),
--                                        Ghost  (GhoState (4,(__9,11__),1,Null,0,1) Alive)]@
-- * @ghostsInicial (generateMaze 15 11 2) = [Ghost  (GhoState (1,__(5,4)__,1,Null,0,1) Alive), 
--                                        Ghost  (GhoState (2,__(5,6)__,1,Null,0,1) Alive),
--                                        Ghost  (GhoState (3,__(5,8)__,1,Null,0,1) Alive),
--                                        Ghost  (GhoState (4,__(5,10)__,1,Null,0,1) Alive)]@

ghostsInicial :: Maze -> [Player]
ghostsInicial m 
    | even x && even y = [Ghost  (GhoState (1,(xE,yE1),1,Null,0,1) Alive),
                          Ghost  (GhoState (2,(xE,yE2),1,Null,0,1) Alive),
                          Ghost  (GhoState (3,(xE,yE3),1,Null,0,1) Alive),
                          Ghost  (GhoState (4,(xE,yE4),1,Null,0,1) Alive)]

    | even x && odd y = [Ghost  (GhoState (1,(xE,yO1),1,Null,0,1) Alive),
                         Ghost  (GhoState (2,(xE,yO2),1,Null,0,1) Alive),
                         Ghost  (GhoState (3,(xE,yO3),1,Null,0,1) Alive),
                         Ghost  (GhoState (4,(xE,yO4),1,Null,0,1) Alive)
                         ]

    | odd x && even y = [Ghost  (GhoState (1,(xO,yE1),1,Null,0,1) Alive),
                         Ghost  (GhoState (2,(xO,yE2),1,Null,0,1) Alive),
                         Ghost  (GhoState (3,(xO,yE3),1,Null,0,1) Alive),
                         Ghost  (GhoState (4,(xO,yE4),1,Null,0,1) Alive)
                         ]

    | odd x && odd y = [Ghost  (GhoState (1,(xO,yO1),1,Null,0,1) Alive),
                        Ghost  (GhoState (2,(xO,yO2),1,Null,0,1) Alive),
                        Ghost  (GhoState (3,(xO,yO3),1,Null,0,1) Alive),
                        Ghost  (GhoState (4,(xO,yO4),1,Null,0,1) Alive)
                        ]

  where x = length m
        y = length (head m)
        xE = (div x 2) - 1 
        xO = (div x 2)  
        yE1 = (div y 2) - 2 
        yE2 = (div y 2) - 1
        yE3 = (div y 2) 
        yE4 = (div y 2) + 1
        yO1 = (div y 2) - 3   
        yO2 = (div y 2) - 1
        yO3 = (div y 2) + 1
        yO4 = (div y 2) + 3
        

-- * Funções auxiliares

-- | == Descrição da Função
-- Troca a peça nas coordenadas indicadas por __Empty__. 
-- Funciona "saltando" corredores e quando chega ao corredor certo, invoca a função __auxemptyPeca__.  

-- | === Utilização
-- A função é utilizada para "limpar" a posição por onde o Pacman passa.
emptyPeca :: Coords -- ^ Coordenadas da posição alvo
          -> Maze -- ^ labirinto alvo
          -> Maze -- ^ devolve um labirinto com a posição __Empty__ nas coordenadas indicadas 
emptyPeca (a,b) [] = []
emptyPeca (a,b) (x:xs) 
  | a == 0 = auxemptyPeca b x : xs 
  | otherwise = x : emptyPeca (a-1,b) xs

-- | == Descrição da Função
-- A função auxiliar da função __emptyPeca__ funciona percorrendo o corredor até chegar à coordenada pertendida e altera a mesma por __Empty__.   
auxemptyPeca :: Int -- ^ Recebe a coordenada da largura
             -> [Piece] -- ^ Recebe o corredor   
             -> [Piece] -- ^ Devolve o corredor com a posição-alvo __Empty__ 
auxemptyPeca i [] = [] 
auxemptyPeca i (x:xs)
  | i == 0 = Empty : xs 
  | otherwise =  x : auxemptyPeca (i-1) xs

-- | == Descrição da função 
-- Revela a __Piece__ para onde o jogador se irá mover. A função recebe um /Move/ e um /State/ e com estas informações aplica a função __revealPiece__ às coordenadas do movimento do jogador (recolhidas através da funçao __getPlayerCoords__).
-- O seu funcinamento confere à função flexibilidade pois pode ser utilizada com qualquer /Player/ e qualquer orientação.

-- | === Utilização
-- A função revela a peça que existe na posição para onde o /Player/ se move. É util pois o Pacman e os /Ghosts/ tem reações diferentes caso a posição para onde se movem seja __Wall__, __Empty__, __Food Big__ ou __Food Little__.  

-- | === Exemplo
-- @revealPieceOr (Move 0 R) (State maze players level) = __revealPiece (1,3) maze__
--         where (1,2) = getPlayerCoords (encID 0 players)@ 
revealPieceOr :: Play -- ^ recebe uma /Play/ com uma orientação e um ID
              -> State -- ^ receve um /State/
              -> Piece  -- ^ devolve a /Piece/ na posição pretendida

revealPieceOr (Move id R) (State m p l) = revealPieceReverse m (a,b+1) 
         where (a,b) = getPlayerCoords (encID id p) 

revealPieceOr (Move id L) (State m p l) = revealPieceReverse m (a,b-1) 
         where (a,b) = getPlayerCoords (encID id p) 

revealPieceOr (Move id U) (State m p l) = revealPieceReverse m (a-1,b) 
         where (a,b) = getPlayerCoords (encID id p) 

revealPieceOr (Move id D) (State m p l) = revealPieceReverse m (a+1,b) 
         where (a,b) = getPlayerCoords (encID id p)

revealPieceOr (Move id Null) (State m p l) = revealPieceReverse m (a,b) 
         where (a,b) = getPlayerCoords (encID id p)

-- | == Descrição da função
-- Revela a __Piece__ que se encontra na __posição indicada__ do Maze recebido. A função __revealPiece__ e a sua auxiliar __auxRevealPiece__ funcionam de maneira similar à função __emptyPeca__ e __auxemptyPeca__, diferendo no que devolve. Neste caso, devolve uma /Piece/.

revealPiece :: Coords -> Maze -> Piece
revealPiece (a,b) (x:xs) 
  | b == -1 || b == length x = Empty 
  | a == 0 = auxRevealPiece b x  
  | otherwise = revealPiece (a-1,b)  xs

-- | == Descriçao da Função 
-- Função auxiliar da função __revealPiece__.   
auxRevealPiece :: Int -> [Piece] -> Piece
auxRevealPiece i (x:xs)
  | i == 0 = x 
  | otherwise = auxRevealPiece (i-1) xs

-- | == Descrição da função
-- Revela o __Player__ que se encontra na Posição para onde se move. Funciona de modo semelhante a __revealPierceOr__ mas é aplicada a função auxiliar __revealPlayer__ sobre os argumentos.

-- | === Utilização
-- A função é utilizada no confronto entre /Players/ e a sua utilidade consiste no facto de que acontencem diferentes eventos que variam consoante os /Players/ que se cruzam e os seus estados.
revealPlayerOr :: Play -> [Player] -> Player 

revealPlayerOr (Move id R) p = revealPlayer (a,b+1) p
         where (a,b) = getPlayerCoords (encID id p) 

revealPlayerOr (Move id L) p = revealPlayer (a,b-1) p
         where (a,b) = getPlayerCoords (encID id p) 

revealPlayerOr (Move id U) p = revealPlayer (a-1,b) p
         where (a,b) = getPlayerCoords (encID id p) 

revealPlayerOr (Move id D) p = revealPlayer (a+1,b) p
         where (a,b) = getPlayerCoords (encID id p)


-- | == Descrição da função
-- Função auxiliar da __revealPlayerOr__. Recebe as coordenadas, uma lista de /Players/ e devolve o /Player/ com as mesmas coordenadas. 
revealPlayer:: Coords -> [Player] -> Player
revealPlayer (x,y) (h:t)
    | (x,y) == getPlayerCoords h = h
    | otherwise = revealPlayer (x,y) t

-- | == Descrição da função
-- Como as /Pieces/ e os /Players/ operam em "Layers" diferentes, é relevante saber se para as coordenadas para onde o /Player/ se move tem /Players/ ou não. Caso tenha, utiliza a função __revealPlayerOr__ que revela a identidade do /Player/ (__Ghost__ ou __Pacman__).

nPlayer :: Player -> Play -> [Player] -> Bool
nPlayer player _ [] = True
nPlayer player (Move id or) (x:xs) = if (movimento (getPlayerCoords player) or) == getPlayerCoords x
                                     then False
                                     else nPlayer player (Move id or) xs   

-- | == Descrição da função
-- Recebendo coordenadas e uma orientação, devolve as coordenadas no final do movimento.

-- | === Utilização
-- A função é utilizada sobretudo para determinar se o /Player/ entra num dos túneis e para alterar a sua posição em outros casos. 

-- | == Exemplos
-- * @movimento (1,1) D = (2,1)@ 
-- * @movimento (1,1) R = (1,2)@

movimento :: (Int,Int) -> Orientation -> Coords
movimento (x,y) D = (x+1,y)
movimento (x,y) U = (x-1,y)
movimento (x,y) R = (x,y+1)
movimento (x,y) L = (x,y-1)
movimento (x,y) Null = (x,y)

-- | == Descrição da função
-- A função recebe um Pacman e altera a informação relativa à sua boca.

-- | == Utilização
--  Movimento da boca durante o movimento do Pacman pelo mapa. __Nota:__ Esta função ainda não é utilizada nesta fase. 

changeMouth :: Player -> Player 
changeMouth (Pacman (PacState a b Open c)) = Pacman (PacState a b Closed c)
changeMouth (Pacman (PacState a b Closed c)) = Pacman (PacState a b Open c)

-- | == Descrição da função
-- Associa a um /Player/ um inteiro. Neste caso __Pacman = 2__ && __Ghost = 1__.

-- | === Utilização 
-- Necessário nas condições das funções __playPac__ e __playGhost__. 

ghostORPacmanvalor :: Player -> Int
ghostORPacmanvalor (Ghost g) = 1
ghostORPacmanvalor (Pacman p) = 2

-- | == Descrição da função
-- Dado um /Maze/, devolve a posição inicial do Pacman. Calcula a posição para larguras e alturas pares e ímpares.  

-- | === Utilização 
-- Quando o Pacman "choca" com um Ghost e ainda tem vidas, volta para a posição inicial.  

-- | === Exemplos 
-- * @coordsiniciaisPac (generateMaze 20 20 12) = (11,9) @ 

coordsiniciaisPac :: Maze -> (Int,Int)
coordsiniciaisPac m 
    | even x && even y = (yE,xE)
    | even x && odd y = (yO,xE)
    | odd x && even y = (yE,xO)
    | odd x && odd y =(yO,xO)
  where y = length m
        x = length (head m) 
        yE = (div y 2) +1
        yO = (div y 2) +2
        xE = (div x 2) -1
        xO = (div x 2) 

-- | == Descrição da função
-- Dado um /Maze/ devolve a posição inicial do /Ghost/. Ao contrário do __coordsiniciaisPac__, os cálculos já estão feitos no __ghostsInciais__ e, por isso, apenas recolhe as coordenadas do primeiro elemento.  

-- | === Utilização 
-- Quando o Pacman no modo /Mega/ "choca" com um /Ghost/, volta para a casa dos fantasmas.  

coordsiniciaisGho :: Maze -> Coords
coordsiniciaisGho m = if even (length (head m)) 
                      then (x0,y0)  
                      else (x0,y0+1)
                  where (x0,y0) = getPlayerCoords ((ghostsInicial m)!!1)

-- | == Descrição da função
-- Recebendo um /Maze/, define uma lista de coordenadas da entrada da casa dos fantasmas.

-- | === Utilização 
-- Quando o Pacman se move pelo labirinto não pode entrar na casa dos fantasmas, logo assume o conjunto de coordenadas criado como uma __Wall__.

aberturaCasa :: Maze -> [Coords]
aberturaCasa m 
   | even x && even y = [(xE,yC),(xE,yC+1)]      
   | even x && odd y =  [(xE,yC),(xE,yC+1),(xE,yC+2)]   
   | odd x && even y =  [(xO,yC),(xO,yC+1)]      
   | odd x && odd y = [(xO,yC),(xO,yC+1),(xO,yC+2)]
    where y = length (head m)
          x = length m
          xO = (div x 2) -1 
          xE = (div x 2) -2
          yC = (div y 2) -1

-- | == Descrição da função
-- Recebe uma /__Play__/ e a lista de /__Players__/. Caso o /Player/ na cabeça da lista tenha o mesmo ID da /Play/, a sua orientação é alterada para a da /Play/.   

-- | === Utilização 
-- Quando o /Player/ muda de orientação.  

changeOr :: Play -> [Player] -> [Player]
changeOr _ [] = []
changeOr (Move id or) (x:xs) 
  | id == getPlayerID x = setPlayerOrientation x or : xs
  | otherwise = x : changeOr (Move id or) xs

-- | == Descrição da função
-- Verifica se as duas orientações de input são opostas ou não. Se forem iguais, devolve __False__, caso sejam contrárias, devolve __True__.

-- | === Utilização 
-- Quando o /Player/ efetua uma jogada e se esta tiver uma orientação diferente da do /Player/, ele apenas muda de orientação mas não troca de posição.  

opositeOr :: Orientation -> Orientation -> Bool
opositeOr or1 or2 | or1 == or2 = False
                  | otherwise = True

-- | == Descrição da função
-- Dado um /__Player__/, indica o valor da sua /health/.

-- | === Utilização 
-- Utilizada nas condições do __playPac__.

-- | Quando o Pacman "choca" com um /Ghost/, dependendo da sua /health/, terá reações diferentes. 
getPlayerHealth :: Player -> Int
getPlayerHealth (Pacman (PacState (x,y,z,t,h,l) q c d )) = l 
getPlayerHealth (Ghost (GhoState (x,y,z,t,h,l) q )) = l

-- | == Descrição da função
-- Dado um ID e uma lista de /Players/, a função encontra o jogador com o mesmo ID na lista.

-- | === Utilização 
-- Utilizada em todos os casos em que se desconhece o /Player/ que corresponde ao ID, como por exemplo, nas funções __revealPlayerOr__ e __revealPieceOr__, e nas condições do __playPac__ e __playGho__.   
encID :: Int -> [Player] -> Player 
encID id (x:xs)
   | id == getPlayerID x = x
   | otherwise = encID id xs
    
-- | == Descrição da função
-- Dado um /Player/ e uma /Orientation/, __muda a orientação do /Player/__ para a orientação de input. 

-- | === Utilização 
-- Utilizada sempre que o /Player/ muda de direção e necessita de alterar a sua orientação. 
setPlayerOrientation :: Player -> Orientation -> Player
setPlayerOrientation (Pacman (PacState (x,y,z,t,h,l) q c d )) or = Pacman (PacState (x,y,z,or,h,l) q c d )
setPlayerOrientation (Ghost (GhoState (x,y,z,t,h,l) q )) or = Ghost (GhoState (x,y,z,or,h,l) q )

-- * Jogadas dos Pacmans 

-- | == Descrição da função
-- A função __playPac__ é responsável por todo o tipo de jogadas que se executa com o __ID__ de um jogador associado a um __Pacman__.

-- A função conta com 11 guardas e 6 funções locais que garantem que, dependendo da situação em que o Pacman se encontra, cumpre as alterações corretas no /State/.

-- | == Funções locais da função __playPac__
-- __Coordenadas do /Player/ que corresponde ao ID fornecido__

-- | * @(a,b) = (getPlayerCoords (encID id players))@

-- | __Comprimento e largura do /Maze/__

-- | *  @y = length (head maze)@

-- | *  @x = length maze@

-- | __/Maze/ com a posição do Pacman "limpa"__

-- | *  @removeP = emptyPeca (a,b) maze@ 

-- | __Revelar o /Player/ que se encontra à frente__

-- | * @revealPlayer = revealPlayerOr (Move id or) players@ 

-- | __Revelar a /Piece/ que se encontra à frente__

-- | * @revP = revealPieceOr (Move id or) (State maze players level)@

-- | === Condições da função
-- * Se a orientação da /Play/ for __Null__: devolve o mesmo /State/;
-- * Se a orientação da /Play/ não for a mesma da do fantasma: utiliza a função __changeOr__ que apenas muda a orientação do /Player/ que corresponde ao ID da /Play/;  
-- * Se a /Play/ movimentar o jogador pelo __túnel__: utiliza a função __teleport__ que faz o /Player/ utilizar o túnel;
-- * Se a /Play/ movimentar o jogador para uma  __Wall__ ou para a entrada da __casa dos fantasmas__: devolve o __mesmo /State/__;
-- * Se a /Play/ movimentar o jogador para uma posição __Empty__: devolve __removeP__ (/Maze/), utiliza a função __emptyplayExp__ e o nivel mantém-se constante;
-- * Se a /Play/ movimentar o jogador para uma posição com __Food Little__: devolve um __removeP__ (/Maze/), utiliza a função __foodLittleExp__ e o nivel mantém-se. Assim sendo, o Pacman vai avançar de casa e __aumentar 1 points__;
-- * Se a /Play/ movimentar o jogador para uma posição com __Food Big__: devolve um __removeP__ (/Maze/), utiliza a função __foodBigExp__ e o nivel mantém-se. Assim sendo, o Pacman muda para o estado __Mega__ e __aumenta 5 points__;
-- * Se a /Play/ movimentar o jogador para uma posição com __Ghost__, a sua "__health = 0__" e modo __Normal__: devolve o __mesmo Maze__, utiliza a função __pacDeath__ e o nível  mantém-se. Assim sendo, o Pacman __entra no modo Dying__;
-- * Se a /Play/ movimentar o jogador para uma posição com um __Ghost__, a sua "__health > 0__" e o modo __Normal__: devolve um __removeP__ (/Maze/), utiliza a função __geralPacGhost__ e o nível mantém-se. Assim sendo, o Pacman volta à sua posição inicial, __perdendo 1 lives__;
-- * Se a /Play/ movimentar o jogador para uma posição com um __Ghost__ e o seu modo for __Mega__: dependendo de se o Ghost está numa posição que "abaixo" tem /Food Little/, /Food Big/ ou /Empty/, utiliza respetivamente a função __megaBig__, __megaLittle__ ou __nenhuma__ sobre a função __geralPacGhost__ e o nivel mantém-se. Assim sendo, o /Ghost/ volta à casa dos fantasmas no modo __Alive__ e o Pacman __contabiliza +10 points__ por eliminar um /Ghost/ (para alémde poder contablizar os pontos da possível peça "abaixo" do /Ghost/); 
-- * Se a /Play/ movimentar o jogador para uma posição com um __Pacman__: devolve __removeP__ (/Maze/), aplica a função __emptyplayExp__ e o nível mantém.
playPac :: Play -- ^ __(Move ID OR)__ Recebe o ID do jogador e a /Orientation/ da jogada 
        -> State -- ^ __(/State/ maze players level)__ Recebe um /State/ composto pelo /Maze/, a lista dos /Players/ e o /level/
        -> State -- ^ __(/State/ maze players level)__ Devolve o /State/ atualizado, o que pode envolver mudanças no maze ou na lista dos Players 
playPac (Move id or) (State maze players level)

    | or == Null = State maze (changeOr (Move id Null) players) level
    
    | opositeOr or (getPlayerOrientation (encID id players)) = State maze (changeOr (Move id or) players) level 
    
    | movimento (a,b) or == (a,y) || movimento (a,b) or == (a,-1) = State maze 
                                                                          (teleport (Move id or) maze players)
                                                                          level  

    | revP == Wall || elem (movimento (a,b) or) (aberturaCasa maze) = State maze 
                                                                            players
                                                                            level
   
    | revP == Empty && b /= -1 && b/= y && 
             nPlayer (encID id players) (Move id or) players = State removeP 
                                                                     (emptyplayExp (Move id or) players) 
                                                                     level

    | revP == (Food Little) && 
      nPlayer (encID id players) (Move id or) players = State removeP
                                                              (foodLittleExp (Move id or) players)
                                                              level

    | revP == (Food Big) &&
      nPlayer (encID id players) (Move id or) players = State removeP
                                                              (foodBigExp (Move id or) players)
                                                              level

    | elem 1 allV && 
      getPlayerHealth (encID id players) == 0 && 
      elem Alive gModes = State maze
                          (pacDeath (Move id or) players)
                          level 

    |  elem 1 allV && 
       getPlayerHealth (encID id players) > 0 &&
       elem Alive gModes = State maze 
                          (geralPacGhost (Move id or) (State maze players level) players)
                          level

    | elem 1 allV = if revP == (Food Little) 
                    then megalittle (Move id or) (State removeP
                                                (geralPacGhost (Move id or) (State maze players level) players)
                                                level)
                   else if revP == (Food Big)
                         then megaBig (Move id or) (State removeP
                                                  (geralPacGhost (Move id or) (State maze players level) players)
                                                  level) 
                         else State removeP
                             (geralPacGhost (Move id or) (State maze players level) players)
                              level
                                               
                                               
    | otherwise = State removeP 
                       (emptyplayExp (Move id or) players)
                       level 
       
   where (a,b) = (getPlayerCoords (encID id players))
         y = length (head maze)
         x = length maze
         removeP = emptyPeca (a,b) maze 
         revP = revealPieceOr (Move id or) (State maze players level)
         revealPlayer = revealPlayerOr (Move id or) players
         gModes = map getGhostMode $ rmPacmans $ (revealAllPlayerOr (Move id or) players)
         allV = map ghostORPacmanvalor (revealAllPlayerOr (Move id or) players)


-- | == Descrição da função
-- Utilizada quando o Pacman se move para coordenadas com a peça /Empty/ . Utlizando a função auxiliar __emptyplay__, altera as coordenadas do jogador do ID fornecido para a posição pretendida.

emptyplayExp :: Play -> [Player]->[Player]
emptyplayExp (Move id or) [] = []
emptyplayExp (Move id or) (x:xs)
    | id == getPlayerID x = emptyplay or x : xs 
    | otherwise = x : emptyplayExp (Move id or) xs
-- | Função auxiliar de __emptyplayExp__.
emptyplay :: Orientation -> Player -> Player
emptyplay or (Pacman (PacState(i,(x,y),v,o,p,l) t m s)) = changeMouth $ Pacman (PacState(i,(movimento (x,y) or),v,or,p,l) t m s)

-- | == Descrição da função
-- __Utilizada quando o Pacman se move para posições com /Food Little/__. Ao jogador que contêm o ID fornecido pela /Play/, altera os pontos (em __+1__) e muda as coordenadas de acordo com a orientação da /Play/.  
-- Para isto, utiliza a função auxiliar __foodLittle__.

foodLittleExp :: Play -> [Player] -> [Player]
foodLittleExp (Move id or) [] = []
foodLittleExp (Move id or) (x:xs)
    | id == getPlayerID x = foodLittle or x  : xs
    | otherwise = x : foodLittleExp (Move id or) xs
-- | Função auxiliar de __foodLittleExp__.
foodLittle :: Orientation -> Player -> Player
foodLittle or (Pacman (PacState(i,(x,y),v,o,p,l) t m s)) = changeMouth $ Pacman (PacState(i,(movimento (x,y) or),v,o,p+1,l) t m s) 

-- | == Descrição da função
-- __Utilizada quando o Pacman se move para posições com /Food Big/__. Ao jogador que contêm o ID fornecido pela /Play/ (neste caso, um Pacman), utiliza-se a função auxiliar __foodBig__ para alterar os pontos (em __+5__) e mudar as coordenadas de acordo com a orientação da /Play/.

-- | Os /Ghosts/ diminuem a sua velocidade para metade.  

pacMegaSpeed = 1.5 
pacNormalSpeed = 1 
ghostAliveSpeed = 1
ghostDeadSpeed = 0.5

foodBigExp :: Play -> [Player] -> [Player]
foodBigExp (Move id or) [] = []
foodBigExp (Move id or) ((Ghost (GhoState (i,c,v,o,p,l) m)):xs) 
    | m == Alive = (Ghost (GhoState (i,c,ghostDeadSpeed,(downs o),p,l) Dead)) : foodBigExp (Move id or) xs
    | otherwise =  (Ghost (GhoState (i,c,ghostDeadSpeed,o,p,l) Dead)) : foodBigExp (Move id or) xs
foodBigExp (Move id or) (x:xs)
    | id == getPlayerID x = foodBig or x : foodBigExp (Move id or) xs
    | otherwise = x : foodBigExp (Move id or) xs
-- | Função auxiliar de __foodBigExp__.
foodBig :: Orientation -> Player -> Player
foodBig or (Pacman (PacState (i,(x,y), v , o, p , l) t m s)) = changeMouth $ Pacman (PacState(i,(movimento (x,y) or),pacMegaSpeed,o,p+5,l) 8000 m Mega)

-- | == Descrição da função
-- Função utilizada quando o Pacman encontra um /Ghost/ e tem mais do que __0 lives__.

-- | === Condições:
-- * Se o Pacman estiver no modo __Normal__, é utilizada a função auxiliar __pacNormalGhost__;
-- * Se o Pacman estiver no modo __Mega__, é utilizada a função auxiliar __pacMegaGhost__.

geralPacGhost :: Play -> State -> [Player] -> [Player]
geralPacGhost (Move id or) s [] = []
geralPacGhost (Move id or) s (x:xs)
    
    | id == getPlayerID x && elem Alive gModes = pacNormalGhost (Move id or) s (x:xs)
    
    | id == getPlayerID x = pacMegaGhost (Move id or) s (x:xs)
    
    | otherwise = x : geralPacGhost (Move id or) s xs

  where gModes = map getGhostMode $ rmPacmans $ (revealAllPlayerOr (Move id or) (x:xs))


-- | == Descrição da Função
-- Quando o Pacman está em modo __Normal__ e tem __mais do que 0 lives__, este volta à sua posição inicial perdendo uma vida. 
pacNormalGhost :: Play -> State -> [Player] -> [Player]
pacNormalGhost (Move id or) (State maze pl level) [] = []
pacNormalGhost (Move id or) (State maze pl level) ((Pacman (PacState(i,(x,y),v,o,p,l) t m mo)):xs) 
    
    | i==id = (changeMouth $ (Pacman (PacState(id,(x0,y0),v,Null,p,l-1) 0 m mo))) : xs
    
    | otherwise = (Pacman (PacState(i,(x,y),v,o,p,l) t m mo)) : pacNormalGhost (Move id or) (State maze pl level) xs
   
   where (x0,y0) = coordsiniciaisPac maze

pacNormalGhost (Move id or) maze (x:xs) = x : pacNormalGhost (Move id or) maze xs

-- | == Descrição da função
-- Quando o Pacman está em modo __Mega__, este __ganha 10 points__ e o /Ghost/ volta à casa dos fantasmas alterando para o modo __Alive__.

pacMegaGhost :: Play -> State -> [Player] -> [Player]
pacMegaGhost (Move id or) (State maze pl level) [] = []

pacMegaGhost (Move id or) (State maze pl level) ((Pacman (PacState(i,(x,y),v,o,p,l) t m Mega)):xs) 
   
   | i==id = (changeMouth $ Pacman (PacState(i,(movimento (x,y) or),v,o,(p+nG),l) t m Mega)) : pacMegaGhost (Move id or) (State maze pl level) xs
   
   | otherwise = (Pacman (PacState(i,(x,y),v,o,p,l) t m Mega)) : pacMegaGhost (Move id or) (State maze pl level) xs
  
   where nG = (length $ rmPacmans $ revealAllPlayerOr (Move id or) pl) * 10 

pacMegaGhost (Move id or) (State maze pl level) ((Ghost (GhoState (i,(x,y),v,o,p,l) mode)):xs) 
   
   | elem i findid = (Ghost (GhoState (i,(x0,y0),ghostAliveSpeed,Null,p,l) Alive)) : pacMegaGhost (Move id or) (State maze pl level) xs --rever, mudei a velocidade para 2vzs a inicial pq muda de estado
   
   | otherwise = (Ghost (GhoState (i,(x,y),v,o,p,l) mode)) : pacMegaGhost (Move id or) (State maze pl level) xs
    
  where findid = map getPlayerID (revealAllPlayerOr (Move id or) pl)
        (x0,y0) = coordsiniciaisGho maze
  
pacMegaGhost (Move id or) (State maze pl level) ((Pacman (PacState(i,(x,y),v,o,p,l) t m Normal)):xs) = (Pacman (PacState(i,(x,y),v,o,p,l) t m Normal)) : pacMegaGhost  (Move id or) (State maze pl level) xs

           
-- | == Descrição da função
-- Função aplicada quando um /Player/ entra num dos tuneis. Aplicando a função auxiliar __teleport__, altera a sua posição para o outro lado do /Maze/, no mesmo corredor.

teleport :: Play -> Maze -> [Player] -> [Player]
teleport (Move id or) m [] = []
teleport (Move id or) m (x:xs)
   | id == getPlayerID x = teleportAux m x : xs
   | otherwise = x : teleport (Move id or) m xs


-- | Função auxiliar de __teleport__.
teleportAux :: Maze -> Player -> Player 
teleportAux maze (Pacman (PacState (i,(x,0), v, o, p, l) t m s)) = changeMouth $ Pacman (PacState (i,(x,end),v,o,p,l) t m s) 
       where end = (length (head maze)) - 1 
teleportAux maze (Pacman (PacState (i,(x,y), v, o, p, l) t m s)) = changeMouth $ Pacman (PacState (i,(x,0),v,o,p,l) t m s) 

teleportAux maze (Ghost (GhoState (i,(x,0), v, o, p, l) mo)) = Ghost (GhoState (i,(x,end),v,o,p,l) mo) 
       where end = (length (head maze)) - 1 
teleportAux maze (Ghost (GhoState  (i,(x,y), v, o, p, l) mo)) = Ghost (GhoState (i,(x,0),v,o,p,l) mo) 

-- | == Descrição da função
-- Função aplicada quando o Pacman tem __0 lives__ e está no modo __Normal__, cuja utilidade é mudar o estado do Pacman para __Dying__.

pacDeath :: Play ->[Player] -> [Player]
pacDeath (Move id or) [] = []
pacDeath (Move id or) ((Pacman (PacState(i,(x,y),v,o,p,0) t m mode)):xs)
    
    | id == i = Pacman (PacState(i,(x,y),0,o,p,0) 0 m Dying): xs
    
    | otherwise = (Pacman (PacState(i,(x,y),v,o,p,0) t m mode)) : pacDeath (Move id or) xs

pacDeath (Move id or) (x:xs) = x : pacDeath (Move id or) xs

-- * Funções auxiliares para as situações em que o Pacman "encontra" comida e fantasmas (simultaneamente).

-- | == Descrição da função
-- Esta função é utilizada quando o Pacman está no modo __Mega__ e come um /Ghost/ que tem /Food Little/ "abaixo". Para isso, aplica a função __foodLittleExp2__ que __altera os pontos em +1__ do jogador que corresponde ao ID fornecdido.

megalittle :: Play -> State -> State
megalittle (Move id or) (State maze players level) = State maze (foodLittleExp2 (Move id or) players) level 

-- | Função auxiliar de __megaLittle__.

foodLittleExp2 :: Play -> [Player] -> [Player]
foodLittleExp2 (Move id or) [] = []
foodLittleExp2 (Move id or) (x:xs)
    | id == getPlayerID x = foodLittle2 or x  : xs
    | otherwise = x : foodLittleExp2 (Move id or) xs

-- | Função auxiliar de __foodLittleExp2__.

foodLittle2 :: Orientation -> Player -> Player
foodLittle2 or (Pacman (PacState(i,(x,y),v,o,p,l) t m s)) = changeMouth $ Pacman (PacState(i,(x,y),v,o,p+1,l) t m s) 

-- | == Descrição da função
-- Esta função é aplicada quando o Pacman está no modo __Mega__ e come um /Ghost/ que tem /Food Little/ "abaixo". Para isso, aplica a função __foodBigExp2__ que __altera em +1__ os pontos do jogador que corresponde ao ID fornecdido.

megaBig :: Play -> State -> State 
megaBig (Move id or) (State maze players level) = State maze (foodBigExp2 (Move id or) players) level

-- | Função auxiliar de __megaBig__.

foodBigExp2 :: Play -> [Player] -> [Player]
foodBigExp2 (Move id or) [] = []
foodBigExp2 (Move id or) ((Ghost (GhoState (i,c,v,o,p,l) m)):xs) 
     | m == Alive = (Ghost (GhoState (i,c,ghostDeadSpeed,(downs o),p,l) Dead)) : foodBigExp2 (Move id or) xs
     | otherwise = (Ghost (GhoState (i,c,ghostDeadSpeed,o,p,l) Dead)) : foodBigExp2 (Move id or) xs
foodBigExp2 (Move id or) (x:xs)
    | id == getPlayerID x = foodBig2 or x : foodBigExp2 (Move id or) xs
    | otherwise = x : foodBigExp2 (Move id or) xs

-- | Função auxiliar de __foodBigExp2__.

foodBig2 :: Orientation -> Player -> Player
foodBig2 or (Pacman (PacState (i,(x,y), v , o, p , l) t m s)) = changeMouth $ Pacman (PacState(i,(x,y),v,o,p+5,l) 8500 m Mega) 

-- * Jogadas dos fantasmas

-- | == Descrição da função 
-- A função __playGho__ recebe um estado e uma /Play/ e usa o __ID__ e a __orientação__ contidas na /Play/ para operar nos jogadores do estado (neste caso, serão apenas os fantasmas) de acordo com as condições mais abaixo abordadas.

-- | == Funções locais da função playGho
-- __Coordenadas do /Player/ do que corresponde ao ID fornecido__

-- | * @(a,b) = (getPlayerCoords (encID id players))@

-- | __Comprimento e largura do /Maze/__

-- | *  @y = length (head maze)@

-- | *  @x = length maze@

-- | __Revelar o /Player/ que se encontra à frente__

-- | * @revealPlayer = revealPlayerOr (Move id or) players@ 

-- | __Revelar a /Piece/ que se encontra à frente__

-- | * @revP = revealPieceOr (Move id or) (State maze players level)@


-- | === Condições
-- * Se a orientação da /Play/ for __Null__: devolve o mesmo estado;
-- * Se a orientação da /Play/ não for a mesma da do fantasma: altera a orientação do fastasma para a orientação fornecida;
-- * Se a /Play/ movimentar o fantasma para uma posição que não se encontre no labirinto (__(x,-1)__ ou __(x,y)__, sendo __y__ o comprimento dos corredores), ou seja, se este entrar no __túnel__: é utilizada a função
-- __teleport__ no fastasma em questão e este transita para o túnel oposto com a mesma orientação;
-- * Se a /Play/ direcionar o fantasma para uma __/Wall/__ : devolve o mesmo estado;
-- * Se a /Play/ direcionar o fantasma para __Empty__, __Fodd Little__ ou __Food Big__ e não houver nessa coordenada nenhum jogador: é aplicada a função __ghoMoving__ e o fantasma transita para essa posição;
-- * Se a /Play/ direcionar o fantasma para a posição de um __Pacman no modo Normal__ com __0 lives__: é aplicada a função __ghoKillPacman__ e o __Pacman entra no Modo Dying__;
-- * Se a /Play/ direcionar o fantasma para a posição de um __Pacman no modo Normal__ com __mais de 0 lives__: é aplicada a função __ghoKillPacman__ o que faz com que o Pacman volte à posição com __-1 live__ e o fantasma avance para a sua posição;
-- * Se a /Play/ direcionar o fantasma para a posição de um __Pacman no modo Mega__: é aplicada a função __ghoVSMega__ que faz com que o fantasma retorne para a casa dos fantasmas, o seu estado seja trocado para __Alive__ e o Pacman ganhe __10 points__;
-- * Se a /Play/ direcionar o fantasma para a posição de um __Fastama__: o fantasma ao qual foi aplicada a /Play/ movimenta-se da para essa coordenada. 

playGho :: Play -> State -> State
playGho (Move id or) (State maze players level)
   
   | or == Null = State maze (changeOr (Move id Null) players) level 

   | opositeOr or (getPlayerOrientation (encID id players)) = State maze (changeOr (Move id or) players) level 
    
   | movimento (a,b) or == (a,y) || movimento (a,b) or == (a,-1) = State maze 
                                                                         (teleport (Move id or) maze players)
                                                                         level 
   | (revP == Wall || ((elem (movimento (getPlayerCoords (encID id players)) or) (aberturaCasa maze)) && or == D)) && 
     nPlayer (encID id players) (Move id or) players = State maze players level
    
   | (revP == Empty && b /= -1 && b /= y|| 
     revP == Food Little || 
     revP == Food Big) && nPlayer (encID id players) (Move id or) players = State maze
                                                                     (ghoMoving (Move id or) players)
                                                                     level

   | elem 2 allV && 
     modePl == Alive = State maze
                             (ghoKillPacman (Move id or) (State maze players level) players)
                             level

   | elem 2 allV &&
      getGhostMode (encID id players) == Dead = State maze
                                                 (ghoVsMega (Move id or) (State maze players level) players)
                                                 level
    
   | otherwise = State maze
                       (ghoMoving (Move id or) players)
                       level
     

    where (a,b) = (getPlayerCoords (encID id players))
          y = length (head maze)
          x = length maze
          revP = revealPieceOr (Move id or) (State maze players level)
          revealPlayer = revealPlayerOr (Move id or) players
          allV = map ghostORPacmanvalor (revealAllPlayerOr (Move id or) players)
          gModes = map getGhostMode $ rmPacmans $ (revealAllPlayers (a,b) players)
          modePl = getGhostMode $ encID id players

-- * Funções auxiliares para as __playGho__

-- | == Descrição da função
-- Esta função é usada para mover o fantasma para uma determinada coordenada sem quaisquer tipo de restrição. 

ghoMoving :: Play -> [Player] -> [Player]
ghoMoving _ [] = []
ghoMoving (Move id or) (x:xs) 
   | id == getPlayerID x = ghoMovingAux or x : xs
   | otherwise = x : ghoMoving (Move id or) xs
-- | Função auxiliar de __ghoMoving__.
ghoMovingAux :: Orientation -> Player -> Player
ghoMovingAux or (Ghost (GhoState (i,(x,y),v,o,p,l) m)) = Ghost (GhoState (i, (movimento (x,y) or),v,or,p,l) m)

-- | == Descrição da função
-- Esta função é usada para quando o fantasma encontra o __Pacman no modo Normal__ e faz com que o fantasma avance para a posição do Pacman, alterando a /heath/ do Pacman em __-1 lives__.

ghoKillPacman :: Play -> State -> [Player] -> [Player]
ghoKillPacman (Move id or) s [] = []

ghoKillPacman (Move id or) (State maze pl level) ((Pacman (PacState(i,(x,y),v,o,p,0) t m mode)):xs) -- 0 lives
    
    | elem i findid = (Pacman (PacState (i,(x,y),0,o,p,0) 0 m Dying)) :  ghoKillPacman (Move id or) (State maze pl level) xs
    
    | otherwise   = (Pacman (PacState (i,(x,y),v,o,p,0) t m mode)) : ghoKillPacman (Move id or) (State maze pl level) xs
   
   where findid = map getPlayerID (revealAllPlayerOr (Move id or) pl)
         
ghoKillPacman (Move id or) (State maze pl level) ((Pacman (PacState (i,(x,y),v,o,p,l) t m mode)):xs) -- >0 lives

   | elem i findid = (Pacman (PacState (i,(x0,y0),pacNormalSpeed,Null,p,l-1) 0 Open Normal)): ghoKillPacman (Move id or) (State maze pl level) xs
   
   | otherwise   = (Pacman (PacState (i,(x,y),v,o,p,l) t m Normal)) : ghoKillPacman (Move id or) (State maze pl level) xs
   
  where findid = map getPlayerID (revealAllPlayerOr (Move id or) pl )
        (x0,y0) = coordsiniciaisPac maze

ghoKillPacman (Move id or) (State maze pl level) ((Ghost (GhoState (i,(x,y),v,o,p,l) m)):xs) 
   | getPlayerHealth (findplayer) == 0 && id == i = (Ghost (GhoState (i,(x,y),v,o,p,l) m)) : ghoKillPacman (Move id or) (State maze pl level) xs  
   | id == i = (Ghost (GhoState (i,movimento (x,y) or ,v,o,p,l) m)) : ghoKillPacman (Move id or) (State maze pl level) xs
   | otherwise = (Ghost (GhoState (i,(x,y),v,o,p,l) m)) : ghoKillPacman (Move id or) (State maze pl level) xs
  where findplayer = revealPlayerOr (Move id or) pl

--ghoKillPacman (Move id or) s (x:xs) = x : ghoKillPacman (Move id or) s xs

-- | == Descrição da função
-- Esta função é usada quando o fantasma encontra o __Pacman no modo Mega__ e faz com que o fantasma volte para a casa dos fantasmas, mudando o seu estado para __Alive__, e o Pacman tenha a sua pontuação alterada em __+10__. 

ghoVsMega :: Play -> State -> [Player] -> [Player]
ghoVsMega (Move id or) (State maze pl level) [] = []

ghoVsMega (Move id or) (State maze pl level) ((Pacman (PacState(i,(x,y),v,o,p,l) t m Mega)):xs) 
   
   | findid == i = Pacman (PacState(i,(x,y),v,o,p+10,l) t m Mega) : ghoVsMega (Move id or) (State maze pl level) xs
   
   | otherwise   = (Pacman (PacState(i,(x,y),v,o,p,l) t m Mega)) : ghoVsMega (Move id or) (State maze pl level) xs
 where findid = getPlayerID (revealPlayerOr (Move id or) pl )

ghoVsMega (Move id or) (State maze pl level) ((Ghost (GhoState (i,(x,y),v,o,p,l) Dead)):xs) 
   
   | id == i   = (Ghost (GhoState (i,(x0,y0),ghostAliveSpeed,Null,p,l) Alive)) : ghoVsMega (Move id or) (State maze pl level) xs
   
   | otherwise = (Ghost (GhoState (i,(x,y),v,o,p,l) Dead)) : ghoVsMega (Move id or) (State maze pl level) xs
 
 where (x0,y0) = coordsiniciaisGho maze 
          
ghoVsMega (Move id or) (State maze pl level) (x:xs) = x : ghoVsMega (Move id or) (State maze pl level) xs


-- * Objetivo da Tarefa 2

-- | == Descrição da função
-- A função __play__ faz a compilação das funções __playPac__ e __playGho__, ou seja, se o ID do jogador contido na /Play/ corresponder ao de um /Pacman/,
-- será aplicada a função __playPac__ a esse jogador, caso contrário, será aplicada a função __playGho__ ao fantasma que contêm o ID da /Play/. 

play :: Play -> State -> State
play (Move id or) (State maze players level) 
  | ghostORPacmanvalor (encID id players) == 1 = playGho (Move id or) (State maze players level)
  | otherwise = playPac (Move id or) (State maze players level)  

-- * "/Quick Plays/""

-- | == Descrição da função
-- Função criada para testes. Recebe o número de movimentos, __/ID/__, __/Orientation/__ e __/State/__ e aplica a função __play__ __n__ vezes sobre um /State/, sendo __n__ o número de movimentos recebido.

-- | === Utilização
-- Em testes revelou-se muito utíl pois faz __movimentos consecutivos__ numa certa orientação.

quickP :: Int -> Int -> Orientation -> State -> State
quickP 0 _ _ st = st
quickP 1 id or s = play (Move id or) s
quickP n id or s = play (Move id or) (quickP (n-1) id or s)

--------------------------------------------------------------------------------------------

revealAllPlayerOr :: Play -> [Player] -> [Player] 

revealAllPlayerOr (Move id R) p = revealAllPlayers (a,b+1) p
         where (a,b) = getPlayerCoords (encID id p) 

revealAllPlayerOr (Move id L) p = revealAllPlayers (a,b-1) p
         where (a,b) = getPlayerCoords (encID id p) 

revealAllPlayerOr (Move id U) p = revealAllPlayers (a-1,b) p
         where (a,b) = getPlayerCoords (encID id p) 

revealAllPlayerOr (Move id D) p = revealAllPlayers (a+1,b) p
         where (a,b) = getPlayerCoords (encID id p)


revealAllPlayers :: Coords -> [Player] -> [Player]
revealAllPlayers _ [] = []
revealAllPlayers (x,y) (h:t)
    | (x,y) == getPlayerCoords h = h : revealAllPlayers (x,y) t  
    | otherwise = revealAllPlayers (x,y) t

rmPacmans :: [Player] -> [Player]
rmPacmans [] = []
rmPacmans (x:xs) = if ghostORPacmanvalor x == 2 
                   then rmPacmans xs 
                   else x : rmPacmans xs 

revealPieceReverse :: Maze -> Coords -> Piece 
revealPieceReverse m (x,y) = if elem (x,y) coordsTunel 
                             then Empty 
                             else revealPiece (x,y) m
          where coordsTunel = if even $ length m
                              then [(a,b) | a <- [midAltura-1,midAltura], b <- [-1,largura]]
                              else [(a,b) | a <- [midAltura], b <- [-1,largura]]
                midAltura = div (length m) 2 
                largura = length $ head m

rmGhosts :: [Player] -> [Player]
rmGhosts [] = []
rmGhosts (x:xs) = if ghostORPacmanvalor x == 1 
                  then rmGhosts xs 
                  else x : rmGhosts xs      

downs :: Orientation -> Orientation 
downs R = L 
downs L = R 
downs U = D 
downs D = U 
downs Null = Null

