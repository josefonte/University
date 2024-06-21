{-# LANGUAGE DeriveDataTypeable#-}

module Otimizacoes where

import Gramatica

import Data.Generics.Zipper
import Data.Maybe
import Library.StrategicData (StrategicData)
import Library.Ztrategic
--import MES_Project.Library.Ztrategic
    ( adhocTP, applyTP, failTP, full_tdTP, idTP, innermost )
import Library.StrategicData (StrategicData)

import Data.List
import Gramatica (PicoC)

instance StrategicData Int
instance StrategicData PicoC
instance StrategicData Exp
instance StrategicData Inst
instance StrategicData a => StrategicData [a]


---------------------------------------------------------
-- 3.Funções para gerar o resultado das contas (isto faz recursivamente)
--------------------------------------------------------

--eval :: Exp -> [(String,Int)] -> Int
--eval (Const i) _ = i
--eval (Var   n) c = fromJust (lookup n c)
--eval (Add e d) c = eval e c + eval d c
--eval (Mul e d) c = eval e c * eval d c
--eval (Sub e d) c = eval e c - eval d c
--eval (Div e d) c = div (eval e c) (eval d c)

eval2 :: Exp -> [(String,Int)] -> Bool
eval2 (Great e d) c = eval e c > eval d c
eval2 (GreatEqual e d) c = eval e c >= eval d c
eval2 (Minor e d) c = eval e c < eval d c
eval2 (MinorEqual e d) c = eval e c <= eval d c
eval2 (Equals e d) c = eval e c == eval d c
eval2 Verdadeiro _ = True 
eval2 Falso _ = False


eval :: Exp -> Inputs -> Int
eval (Const i) _ = i
eval (Var n) c = fromJust (lookup n c)
eval (Add e d) c = eval e c + eval d c
eval (Mul e d) c = eval e c * eval d c
eval (Sub e d) c = eval e c - eval d c
eval (Div e d) c = div (eval e c) (eval d c)
eval (Great e d) c = if eval e c > eval d c then 1 else 0
eval (GreatEqual e d) c = if eval e c >= eval d c then 1 else 0
eval (Minor e d) c = if eval e c < eval d c then 1 else 0
eval (MinorEqual e d) c = if eval e c <= eval d c then 1 else 0
eval (Equals e d) c = if eval e c == eval d c then 1 else 0
eval (Dif e d) c = if eval e c /= eval d c then 1 else 0
eval (Not e) c = if eval e c == 0 then 1 else 0
eval (And e d) c = if eval e c /= 0 && eval d c /= 0 then 1 else 0
eval (Or e d) c = if eval e c /= 0 || eval d c /= 0 then 1 else 0
eval Verdadeiro _ = 1
eval Falso _ = 0

evalS :: Exp -> Inputs -> String
evalS (Str e) _ = e
----------------------------------------------------------
-- Otimizações 
----------------------------------------------------------

-- Otimizações básicas
opt :: Exp -> Maybe Exp
opt (Add (Const 0) e)         = Just e
opt (Add e (Const 0))         =Just e
opt (Add (Const a) (Const b)) =Just (Const (a + b))
opt (Mul (Const 1) e)         =Just e
opt (Mul e (Const 1))         =Just e
opt (Mul (Const 0) _)         =Just (Const 0)
opt (Mul _ (Const 0))         =Just (Const 0)
opt (Div e (Const 1))         =Just e
opt (Sub e (Const 0))         =Just e
opt (Sub (Const a) (Const b)) =Just (Const (a - b))
opt e = Just e


etiquetaVar :: Exp -> Maybe Exp
etiquetaVar (Var s) = Just (Var ("v_"++s))
etiquetaVar e = Just e

etiquetaAtrib :: Inst -> Maybe Inst
etiquetaAtrib (Atrib s x) = Just (Atrib ("v_"++s) x)
etiquetaAtrib e = Just e

-- Esta função é melhor porque nao fica presa num loop 
etiquetaAtrib2 :: Inst -> Maybe Inst
etiquetaAtrib2 (Atrib s x) = if s `isPrefixOf` "v_" then Nothing else Just (Atrib ("v_"++s) x)
etiquetaAtrib2 e = Just e

-- Otimizações de expressões aritméticas
optAritmeticas :: Exp -> Maybe Exp
optAritmeticas (Add (Const 0) e)         = Just e
optAritmeticas (Add e (Const 0))         =Just e
optAritmeticas (Add (Const a) (Const b)) =Just (Const (a + b))
optAritmeticas (Mul (Const 1) e)         =Just e
optAritmeticas (Mul e (Const 1))         =Just e
optAritmeticas (Mul (Const 0) _)         =Just (Const 0)
optAritmeticas (Mul _ (Const 0))         =Just (Const 0)
optAritmeticas (Div e (Const 1))         =Just e
optAritmeticas (Sub e (Const 0))         =Just e
optAritmeticas (Sub (Const a) (Const b)) =Just (Const (a - b))
optAritmeticas e = Nothing

-- optimizaçoes de expressoes booleanas 
optBooleanas :: Exp -> Maybe Exp
optBooleanas  (Not (Not e))     = Just e
optBooleanas  (Not (Great e d)) = Just (MinorEqual e d)
optBooleanas  (Not (GreatEqual e d)) = Just (Minor e d)
optBooleanas  (Not (Minor e d)) = Just (GreatEqual e d)
optBooleanas  (Not (MinorEqual e d)) = Just (Great e d)
optBooleanas  (Not (Equals e d)) = Just (Dif e d)
optBooleanas  (Not (Dif e d)) = Just (Equals e d)
optBooleanas  (Not (And e d)) = Just (Or e d)
optBooleanas  (Not (Or e d)) = Just (And e d)
optBooleanas  (Not Verdadeiro) = Just Falso
optBooleanas  (Not Falso) = Just Verdadeiro
optBooleanas  e = Nothing

optTrue :: Exp -> Maybe Exp
optTrue (Equals e Verdadeiro) = Just e
optTrue e = Nothing

optComs :: Inst -> Maybe Inst
optComs (COMS e) = Just Eliminar
optComs e = Nothing

----------------------------------------------------------------------
-- Estrategias para aplicar as otimizações
----------------------------------------------------------------------
-- Otimizaçoes de atribuiçoes e variáveis
etiquetaVars :: PicoC -> PicoC
etiquetaVars p =
        let pZipper = toZipper p
            Just newP = applyTP(full_tdTP step) pZipper
            step = idTP `adhocTP` etiquetaVar `adhocTP` etiquetaAtrib
        in fromZipper newP


estAritmeticasfraca :: PicoC -> PicoC
estAritmeticasfraca p =
    let pZipper = toZipper p
        Just newP = applyTP (full_tdTP  step) pZipper
        step = idTP `adhocTP` opt
    in fromZipper newP


-- Estratégias com otimizaçoes de expressoes aritmeticas
estAritmeticas :: PicoC -> PicoC
estAritmeticas p =
    let pZipper = toZipper p
        Just newP = applyTP (innermost  step) pZipper
        step = failTP `adhocTP` optAritmeticas
    in fromZipper newP


-- Estratégias com otimizações de expressões booleanas
estBooleanas :: PicoC -> PicoC
estBooleanas p =
    let pZipper = toZipper p
        Just newP = applyTP (innermost  step) pZipper
        step = failTP `adhocTP` optBooleanas
    in fromZipper newP



estTrue :: PicoC -> PicoC
estTrue p =
    let pZipper = toZipper p
        Just newP = applyTP(innermost step) pZipper
        step = failTP `adhocTP` optTrue
    in fromZipper  newP


-- Estratégias com otimizações de comentários
estComs :: PicoC -> PicoC
estComs p =
    let pZipper = toZipper p
        maybeNewP = applyTP (innermost step) pZipper
        newP = case maybeNewP of
            Just zipper -> fromZipper zipper
            Nothing     -> p
        step = failTP `adhocTP` optComs
    in newP


megaEstrat :: PicoC -> PicoC
megaEstrat p = {- estComs $-} estTrue $ estBooleanas $ estAritmeticas $ estAritmeticasfraca p
