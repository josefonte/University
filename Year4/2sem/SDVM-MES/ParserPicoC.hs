{-# LANGUAGE DeriveDataTypeable #-}

module ParserPicoC where

import Gramatica
import Parser
import Prelude hiding ((<$>), (<*>))

--------------------------------------------------------
-- 1. Parser
--------------------------------------------------------


-- Parsing expressions with a lower priority (logical operations)
pExp0 :: Parser Exp
pExp0 =   f <$> pExp1 <*> token' "||" <*> pExp0
     <|> g <$> pExp1
     <|> w <$> token' "Str" <*> enclosedBy (symbol' '"') pNomes (symbol' '"')
  where
    f a _ b = Or a b
    g r = r
    w _ b = Str b

-- Parsing expressions with the next priority (logical AND)
pExp1 :: Parser Exp
pExp1 =   f <$> pExp2 <*> token' "&&" <*> pExp1
     <|> g <$> pExp2
  where
    f a _ b = And a b
    g r = r

-- Parsing expressions with the next priority (relational operations)
pExp2 :: Parser Exp
pExp2 =   f <$> pExp3 <*> symbol' '>' <*> pExp2
     <|> g <$> pExp3 <*> symbol' '<' <*> pExp2
     <|> h <$> pExp3 <*> token' ">=" <*> pExp2
     <|> i <$> pExp3 <*> token' "<=" <*> pExp2
     <|> j <$> pExp3 <*> token' "==" <*> pExp2
     <|> k <$> pExp3 <*> token' "!=" <*> pExp2
     <|> l <$> pExp3
  where
    f a _ b = Great a b
    g a _ b = Minor a b
    h a _ b = GreatEqual a b
    i a _ b = MinorEqual a b
    j a _ b = Equals a b
    k a _ b = Dif a b
    l r = r

-- Parsing expressions with the next priority (additive operations)
pExp3 :: Parser Exp
pExp3 =   f <$> pExp4 <*> symbol' '+' <*> pExp3
     <|> g <$> pExp4 <*> symbol' '-' <*> pExp3
     <|> h <$> pExp4
  where
    f a _ b = Add a b
    g a _ b = Sub a b
    h r = r

-- Parsing expressions with the next priority (multiplicative operations)
pExp4 :: Parser Exp
pExp4 =   f <$> pExp5 <*> symbol' '*' <*> pExp4
     <|> g <$> pExp5 <*> symbol' '/' <*> pExp4
     <|> h <$> pExp5
  where
    f a _ b = Mul a b
    g a _ b = Div a b
    h r = r

-- Parsing expressions with the highest priority (unary operations and parentheses)
pExp5 :: Parser Exp
pExp5 =   f <$> symbol' '!' <*> pExp5
     <|> g <$> symbol' '(' <*> pExp0 <*> symbol' ')'
     <|> h <$> pInt
     <|> i <$> pNomes
     <|> j <$> token' "True"
     <|> k <$> token' "False"
     <|> w <$> token' "Str" <*> enclosedBy (symbol' '"') pNomes (symbol' '"')
  where
    f _ a = Not a
    g _ a _ = a
    h a = Const a
    i a = Var a
    j _ = Verdadeiro
    k _ = Falso
    w _ b = Str b

-- Function to parse variable assignment
atribuiVar :: Parser [Inst]
atribuiVar = f <$> pNomes <*> symbol' '=' <*> pExp0 <*> symbol' ';'
  where
    f a _ c _ = [Atrib a c]

-- Function to parse if_then_else code
ifElse :: Parser [Inst]
ifElse =
  f
    <$> token' "if"
    <*> enclosedBy (symbol' '(') pExp0 (symbol' ')')
    <*> token' "then"
    <*> symbol' '{'
    <*> codigo
    <*> symbol' '}'
    <*> token' "else"
    <*> symbol' '{'
    <*> codigo
    <*> symbol' '}'
  where
    f _ p _ _ c _ _ _ c2 _ = [ITE p c c2]

-- Function to parse while loop
while :: Parser [Inst]
while =
  f
    <$> token' "while"
    <*> enclosedBy (symbol' '(') pExp0 (symbol' ')')
    <*> symbol' '{'
    <*> codigo
    <*> symbol' '}'
  where
    f _ e _ l _ = [While e l]

-- Function to parse comments
comments :: Parser [Inst]
comments = f <$> enclosedBy (symbol' '%') (zeroOrMore (satisfy (/= '%'))) (symbol' '%')
  where
    f a = [COMS a]

-- Parser for print statement
printStmt :: Parser [Inst]
printStmt = f <$> token' "print" <*> enclosedBy (symbol' '(') pExp0 (symbol' ')') <*> symbol' ';'
  where
    f _ exp _ = [PRINT exp]

-- Parsing order
ordem :: Parser [Inst]
ordem =
  atribuiVar
    <|> while
    <|> ifElse
    <|> comments
    <|> printStmt

-- Function to parse a piece of code
codigo :: Parser BlocoC
codigo = f <$> ordem <*> optional codigo
  where
    f b [] = b
    f b bs = b ++ last bs

-- Auxiliary function to find the element of the tree where the second tuple element is an empty string
findEmptyStrings :: [(BlocoC, String)] -> [BlocoC]
findEmptyStrings tuples =
  [x | (x, str) <- tuples, null str]

-- Parser function
parse :: String -> PicoC
parse s =
  let insts = findEmptyStrings (codigo s)
   in if null insts
        then PicoC []
        else PicoC (head insts)
