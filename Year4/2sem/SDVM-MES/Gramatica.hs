{-# LANGUAGE DeriveDataTypeable #-}

module Gramatica where

import Data.Generics (Data, Typeable)
import Data.List (intercalate)

data PicoC = PicoC [Inst]
  deriving (Data, Eq, Typeable)
  --deriving (Show, Data, Eq, Typeable)

data Inst
  = Atrib String Exp
  | While Exp BlocoC
  | ITE Exp BlocoC BlocoC
  | COMS String
  | PRINT Exp
  | Eliminar
  deriving (Eq, Data, Typeable)
  --deriving (Show, Data, Eq, Typeable)

type BlocoC = [Inst]

type Inputs = [(String, Int)]

data Exp
  = Add Exp Exp
  | Mul Exp Exp
  | Sub Exp Exp
  | Div Exp Exp
  | Const Int
  | Var String
  | Great Exp Exp
  | GreatEqual Exp Exp
  | Minor Exp Exp
  | MinorEqual Exp Exp
  | Equals Exp Exp
  | Dif Exp Exp
  | Not Exp
  | And Exp Exp
  | Or Exp Exp
  | Verdadeiro
  | Falso
  | Str String
  ---deriving (Show, Eq, Data, Typeable)
  deriving (Eq, Data, Typeable)

----------------------------------------------------------
-- Unparse to prettyprinter
----------------------------------------------------------

instance Show PicoC where
 show = prettyprinting

instance Show Inst where
 show inst = prettyprintingInst 0 inst

instance Show Exp where
 show = prettyprintingExp

-- Helper function to indent lines
-- Helper function to indent lines
indent :: Int -> String -> String
indent level str = replicate (2 * level) ' ' ++ str

prettyprinting :: PicoC -> String
prettyprinting (PicoC insts) = unparseBlock 0 insts

unparseBlock :: Int -> BlocoC -> String
unparseBlock level insts = intercalate "\n" (map (prettyprintingInst level) insts)

prettyprintingInst :: Int -> Inst -> String
prettyprintingInst level (Atrib var exp) = indent level $ var ++ " = " ++ prettyprintingExp exp ++ ";"
prettyprintingInst level (While exp bloco) = indent level $ "while(" ++ prettyprintingExp exp ++ ") {\n" ++ unparseBlock (level + 1) bloco ++ "\n" ++ indent level "}"
prettyprintingInst level (ITE exp bloco1 bloco2) = indent level $ "if(" ++ prettyprintingExp exp ++ ") {\n" ++ unparseBlock (level + 1) bloco1 ++ "\n" ++ indent level "} else {\n" ++ unparseBlock (level + 1) bloco2 ++ "\n" ++ indent level "}"
prettyprintingInst level (COMS c) = indent level $ "// " ++ c
prettyprintingInst level (PRINT d) = indent level $ "print(" ++ prettyprintingExp d ++ ");"
prettyprintingInst _ Eliminar = ""

prettyprintingExp :: Exp -> String
prettyprintingExp (Add exp1 exp2) = prettyprintingExp exp1 ++ " + " ++ prettyprintingExp exp2
prettyprintingExp (Mul exp1 exp2) = prettyprintingExp exp1 ++ " * " ++ prettyprintingExp exp2
prettyprintingExp (Sub exp1 exp2) = prettyprintingExp exp1 ++ " - " ++ prettyprintingExp exp2
prettyprintingExp (Div exp1 exp2) = prettyprintingExp exp1 ++ " / " ++ prettyprintingExp exp2
prettyprintingExp (Const n) = show n
prettyprintingExp (Var var) = var
prettyprintingExp (Great exp1 exp2) = prettyprintingExp exp1 ++ " > " ++ prettyprintingExp exp2
prettyprintingExp (GreatEqual exp1 exp2) = prettyprintingExp exp1 ++ " >= " ++ prettyprintingExp exp2
prettyprintingExp (Minor exp1 exp2) = prettyprintingExp exp1 ++ " < " ++ prettyprintingExp exp2
prettyprintingExp (MinorEqual exp1 exp2) = prettyprintingExp exp1 ++ " <= " ++ prettyprintingExp exp2
prettyprintingExp (Equals exp1 exp2) = prettyprintingExp exp1 ++ " == " ++ prettyprintingExp exp2
prettyprintingExp (Dif exp1 exp2) = prettyprintingExp exp1 ++ " != " ++ prettyprintingExp exp2
prettyprintingExp (Not exp1) = "!" ++ prettyprintingExp exp1
prettyprintingExp (And exp1 exp2) = prettyprintingExp exp1 ++ " && " ++ prettyprintingExp exp2
prettyprintingExp (Or exp1 exp2) = prettyprintingExp exp1 ++ " || " ++ prettyprintingExp exp2
prettyprintingExp Verdadeiro = "True"
prettyprintingExp Falso = "False"
prettyprintingExp (Str s) = "\"" ++ s ++ "\""

--------------------------------------------------------------
-- unparse interno
--------------------------------------------------------------

-- Unparse function for PicoC
unparse :: PicoC -> String
unparse (PicoC insts) = concatMap unparseInst insts

-- Unparse function for instructions
unparseInst :: Inst -> String
unparseInst (Atrib var exp) = var ++ " = " ++ unparseExp exp ++ ";"
unparseInst (While exp bloco) = "while(" ++ unparseExp exp ++ "){" ++ concatMap unparseInst bloco ++ "}"
unparseInst (ITE exp bloco1 bloco2) =
  "if(" ++ unparseExp exp ++ ")then{" ++ concatMap unparseInst bloco1 ++ "}else{" ++ concatMap unparseInst bloco2 ++ "}"
unparseInst (COMS c) = "%" ++ c ++ "%"
unparseInst Eliminar = ""
unparseInst (PRINT exp) = "print(" ++ unparseExp exp ++ ");" -- Changed here

-- Unparse function for expressions
unparseExp :: Exp -> String
unparseExp (Add exp1 exp2) = "(" ++ unparseExp exp1 ++ ") + (" ++ unparseExp exp2 ++ ")"
unparseExp (Mul exp1 exp2) = "(" ++ unparseExp exp1 ++ ") * (" ++ unparseExp exp2 ++ ")"
unparseExp (Sub exp1 exp2) = "(" ++ unparseExp exp1 ++ ") - (" ++ unparseExp exp2 ++ ")"
unparseExp (Div exp1 exp2) = "(" ++ unparseExp exp1 ++ ") / (" ++ unparseExp exp2 ++ ")"
unparseExp (Const n) = show n
unparseExp (Var var) = var
unparseExp (Great exp1 exp2) = unparseExp exp1 ++ " > " ++ unparseExp exp2
unparseExp (GreatEqual exp1 exp2) = unparseExp exp1 ++ " >= " ++ unparseExp exp2
unparseExp (Minor exp1 exp2) = unparseExp exp1 ++ " < " ++ unparseExp exp2
unparseExp (MinorEqual exp1 exp2) = unparseExp exp1 ++ " <= " ++ unparseExp exp2
unparseExp (Equals exp1 exp2) = unparseExp exp1 ++ " == " ++ unparseExp exp2
unparseExp (Dif exp1 exp2) = unparseExp exp1 ++ " != " ++ unparseExp exp2
unparseExp (Not exp1) = "!" ++ unparseExp exp1
unparseExp (And exp1 exp2) = unparseExp exp1 ++ " && " ++ unparseExp exp2
unparseExp (Or exp1 exp2) = unparseExp exp1 ++ " || " ++ unparseExp exp2
unparseExp Verdadeiro = "True"
unparseExp Falso = "False"
unparseExp (Str s) = ['"'] ++ s ++ ['"']
