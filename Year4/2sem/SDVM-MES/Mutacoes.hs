{-# LANGUAGE DeriveDataTypeable#-}

module Mutacoes where

import Gramatica

import Data.Generics.Zipper
import Library.StrategicData (StrategicData)
import Library.Ztrategic
--import MES_Project.Library.Ztrategic
    ( adhocTP, applyTP, failTP, full_tdTP, idTP, innermost, once_RandomTP, mutations)
import Library.StrategicData (StrategicData)
import System.Random
import Data.List
import Data.Maybe 
import Gramatica (Exp(Sub, MinorEqual, GreatEqual, Verdadeiro, Falso))
import Data.Maybe (Maybe (Just))


instance StrategicData Int
instance StrategicData PicoC
instance StrategicData Exp
instance StrategicData Inst
instance StrategicData a => StrategicData [a]


-- mutações de expressões aritméticas
mut1 :: Exp -> Maybe Exp
mut1 (Add (Const 0) e) = Just (Add e (Const 1))
mut1 (Add e (Const 0)) = Just (Add e (Const 1))
mut1 (Add (Const a) (Const b)) =Just (Const (a - b + 1))
mut1 (Mul (Const 1) e)         =Just (Const 0)
mut1 (Mul e (Const 1))         =Just (Const 0)
mut1 (Mul (Const 0) e)         =Just e
mut1 (Mul e (Const 0))         =Just e
mut1 (Div e (Const 1))         =Just (Mul e (Const 2))
mut1 (Sub e (Const 0))         =Just (Add e (Const 1))
mut1 (Sub (Const a) (Const b)) =Just (Const (b - a))
mut1 (Add e i) = Just (Sub i e)
mut1 (Mul e i) = Just (Div e i)
mut1 (Sub e i) = Just (Add e i)
mut1 (Div e i) = Just (Mul e i)
mut1 e = Nothing


-- Mutações de expressoes booleanas 
mut2 :: Exp -> Maybe Exp
mut2  (Not (Not e))     = Just (Not e)
mut2  (Not (Great e d)) = Just (Minor e d)
mut2  (Not (GreatEqual e d)) = Just (Great e d)
mut2  (Not (Minor e d)) = Just (Equals e d)
mut2  (Not (MinorEqual e d)) = Just (GreatEqual e d)
mut2  (Not (Equals e d)) = Just (Dif e d)
mut2  (Not (Dif e d)) = Just (GreatEqual e d)
mut2  (Not (And e d)) = Just (And e d)
mut2  (Not (Or e d)) = Just (Or e d)
mut2  (Not Verdadeiro) = Just Falso
mut2  (Not Falso) = Just Verdadeiro
mut2  e = Nothing



mut3 :: Exp -> Maybe Exp
mut3 (Add e i) = Just (Sub e i)
mut3 (Mul e i) = Just (Div e i)
mut3 (Sub e i) = Just (Add e i)
mut3 (Const e) = Just (Add (Const e) (Const 10))
mut3 (Great e i) = Just (MinorEqual e i)
mut3 (GreatEqual e i) = Just (Minor e i)
mut3 (Minor e i) = Just (GreatEqual e i)
mut3 (Equals e i) = Just (Dif e i)
mut3 (Dif e i) = Just (Equals e i)
mut3 (Not e) = Just e
mut3 (And e i) = Just (Or e i)

mut3 (Or e i) = Just (And e i)
mut3 Verdadeiro = Just Falso
mut3 Falso = Just Verdadeiro
mut3 _ = Nothing

aplicaMut p f = do  
        let pZipper = toZipper p
        p2 <- once_RandomTP pZipper f
        return $ fromZipper p2 


-- Função para aplicar uma mutação aleatória usando a função mutations
aplicaMutComMutations :: Int -> PicoC -> (Exp -> Maybe Exp) -> PicoC
aplicaMutComMutations seed p f =
    let allMutations = mutations p f
    in if null allMutations
       then p  -- Se não houver mutações possíveis, retorna o programa original
       else let gen = mkStdGen seed  -- Usa uma semente fixa para reprodução
                (index, _) = randomR (0, length allMutations - 1) gen
            in allMutations !! index