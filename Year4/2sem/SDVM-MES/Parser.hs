module Parser where

import Prelude hiding ((<*>),(<$>))
import Data.Char

infixl 2 <|>
infixl 3 <*>

type Parser r = String -> [(r,String)]

symbola :: String -> [(Char,String)]
symbola []    = []
symbola (h:t) = if   h == 'a'
                then [('a',t)]
                else []

--symbol :: Char -> String -> [(Char,String)]
symbol :: Char -> Parser Char
symbol s [] = []
symbol s (h:t) = if s == h
                 then [( s , t)]
                 else []

satisfy :: (Char -> Bool) -> Parser Char
satisfy p [] = []
satisfy p (h:t) = if p h
                  then [(h,t)]
                  else []

token :: [Char] -> String -> [([Char], String)]
token t inp =
     if take (length t) inp == t
     then [(t,drop (length t) inp)]
     else []

succeed :: a -> String -> [(a,String)]
succeed r inp = [ ( r , inp)]

--
--  Combinadores de Parsers!

{-
    X -> "While"
      |  "For"
-}


(<|>) :: Parser a -> Parser a -> Parser a
(p <|> q) inp = p inp ++  q inp

pX =  token "While"
  <|> token "For"
  <|> token "while"

    

(<*>) :: Parser (a -> r) -> Parser a
      -> Parser r
(p <*> q) inp = [ ( f r ,rst') 
                | ( f   ,rst)  <- p inp 
                , (   r ,rst') <- q rst
                ]

(<$>) :: (a -> r) -> Parser a -> Parser r
(f <$> p) inp =  [ (f r,rst)
                 | (r,rst)   <- p inp
                 ]


pAs =  f <$>  symbol 'a'
   <|> g <$>  symbol 'a' <*> pAs
   where f  x   = 1
         g  x y = 1 + y



pA :: Parser Char
pA = f <$> symbol 'a' <*> symbol 'b' <*>
           symbol 'c' <*> symbol 'a'
   where f x y z w = y



pInt :: Parser Int
pInt =  f <$> pSinal <*> pDigitos <*> espacos
   where f  '-' y _ = (read ('-':y))
         f  _   y _ = read y

pSinal =   symbol '-'
      <|>  symbol '+'
      <|>  succeed '+'

pDigitos =  f <$> (satisfy isDigit)
        <|> g <$> (satisfy isDigit) <*> pDigitos
	where f d = [d]
	      g d ds = d : ds


oneOrMore p =   f <$>  p
           <|>  g  <$> p <*> (oneOrMore p)
   where f x = [x]
         g x y = x : y

pString = f <$> symbol '\"' <*>
                zeroOrMore (satisfy (/= '\"')) <*>
	        symbol '\"'
   where f a b c = b

ex = pString "\"abcd  ajajd29309283092 e\""


zeroOrMore p  =        succeed []
             <|> f <$> p <*> (zeroOrMore p)
	     where f x y = x:y

optional p =   f <$>  p
          <|>         succeed []
	  where f a = [a]

sinal =  symbol '+'
     <|> symbol '-'

pInt' =  f <$> optional (sinal) <*>
               oneOrMore (satisfy isDigit)
     where f a b = a ++ b 

separatedBy p s =  f <$> p
               <|> g <$> p  <*> s <*> (separatedBy p s)
   where f a     = [a]
         g a b c = a : c;


followedBy p s =     succeed []
              <|> f <$> p <*> s <*> (followedBy p s)
	    where f a _ b = a : b



enclosedBy a c f = (\_ b _ -> b) <$>  a <*> c <*> f



pListasIntHaskell =
     enclosedBy (symbol '[')
                (separatedBy pInt (symbol ','))
		(symbol ']')

blocoCodigoC =
     enclosedBy (symbol '{')
                (followedBy pInt (symbol ';'))
		(symbol '}')

espacos = zeroOrMore (satisfy isSpace)
symbol' a  = (\a b -> a) <$> symbol a <*> espacos
token' t   = (\a b -> a) <$> token t <*> espacos
satisfy' p = (\a b -> a) <$> satisfy p <*> espacos

pNomes    =  f <$> satisfy isLower
               <*> zeroOrMore (satisfy isAlphaNum)
	       <*> espacos
          where f a b c = a : b



---------------------------------------------
-- Reucrsividade à Esquerda Não Funciona!
-- única limitação desta técnica de parsing
--
-- Exemplo
--
--   A  -> X a
--       |  a
--   X -> A

pAre =  f  <$>  pAre <*> symbol 'a'
    <|> g  <$>  symbol 'a'
    where f a b = a ++ [b]
          g a   = [a] 