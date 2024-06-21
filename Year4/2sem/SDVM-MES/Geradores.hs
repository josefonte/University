module Geradores where

import Gramatica
import Test.QuickCheck
import Test.QuickCheck.Function
import Control.Monad
import Data.Data

---------------------------------------------------------------------
-- Geradores
---------------------------------------------------------------------

genConst :: Gen Exp
genConst = do
    x <- arbitrary `suchThat` (>0)
    return (Const x)

genBoolean :: Gen Exp
genBoolean = frequency [(50, return Verdadeiro), (50, return Falso)]

genVar :: Gen Exp
genVar = do
    x <- listOf1 (choose ('a', 'z'))
    return (Var x)

genNot :: Int -> Gen Exp
genNot n = do
    e1 <- genExp (n-1)
    return $ Not e1

genConds :: Int -> Gen Exp
genConds n = frequency [(50, liftM2 And e1 e2), (50, liftM2 Or e1 e2)]
    where e1 = frequency [(75, genOrdGrandeza (n-1)), (25, genIgualdade)]
          e2 = frequency [(75, genOrdGrandeza (n-1)), (25, genIgualdade)]

genOrdGrandeza :: Int -> Gen Exp
genOrdGrandeza n = frequency [  (25, liftM2 Great e1 e2),
                                (25, liftM2 GreatEqual e1 e2),
                                (25, liftM2 Minor e1 e2),
                                (25, liftM2 MinorEqual e1 e2)
                             ]
    where e1 = frequency [(50, genConst), (25, genVar), (25, genContas (n-1))]
          e2 = frequency [(50, genConst), (25, genVar), (25, genContas (n-1))]

genContas :: Int -> Gen Exp
genContas n = frequency [ (25, liftM2 Add e1 e2),
                          (25, liftM2 Sub e1 e2),
                          (25, liftM2 Mul e1 e2),
                          (25, liftM2 Div e1 e2)
                        ]
    where e1 = genContas2 (n-1)
          e2 = genContas2 (n-1)

genContas2 :: Int -> Gen Exp
genContas2 n = frequency [(30, genConst), (30, genVar), (20, genContas (n-1))]

genIgualdade :: Gen Exp
genIgualdade = frequency  [ (70, liftM2 Equals e1 e2),
                            (30, liftM2 Dif e1 e2)
                          ]
    where e1 = frequency [(15, genConst), (70, genVar), (5, genBoolean)]
          e2 = frequency [(15, genConst), (70, genVar), (5, genBoolean)]

genExp :: Int -> Gen Exp
genExp 0 = frequency [  (30, genConst),
                        (30, genVar),
                        (10, genBoolean)
                     ]
genExp n = frequency [  (30, genConst),
                        (30, genVar),
                        (10, genNot n),
                        (10, genBoolean),
                        (10, genConds n),
                        (10, genOrdGrandeza n),
                        (30, genContas n),
                        (10, genIgualdade)
                     ]

genString :: Gen String
genString = listOf1 (choose ('a', 'z'))

genAtrib :: Int -> Gen Inst
genAtrib n = liftM2 Atrib genString (genContas2 (n - 1))

genWhile :: Int -> Gen Inst
genWhile n = liftM2 While (genConds (n-1)) (genBlocoC (n `div` 2))

genITE :: Int -> Gen Inst
genITE n = liftM3 ITE (genConds (n - 1)) (genBlocoC (n `div` 2)) (genBlocoC (n `div` 2))

genComs :: Gen Inst
genComs = COMS <$> listOf1 (choose ('a', 'z'))

genBlocoC :: Int -> Gen BlocoC
genBlocoC n = do
    k <- choose (1, n `div` 2) 
    replicateM k (genInst (n-1))

genInst :: Int -> Gen Inst
genInst 0 = frequency [(60, genAtrib 0), (5, genComs)]
genInst n = frequency [(40, genAtrib n), (15, genWhile n), (40, genITE n), (5, genComs)]

genPicoC :: Int -> Gen PicoC
genPicoC n = do
    bloco <- genBlocoC n
    return (PicoC bloco)

----------------------------------
-- Shrinking
----------------------------------
instance Arbitrary Inst where
    arbitrary = sized genInst
    shrink (Atrib s e)    = [Atrib s e' | e' <- shrink e]
    shrink (While e b)    = [While e' b | e' <- shrink e] ++ [While e b' | b' <- shrink b]
    shrink (ITE e b1 b2)  = [ITE e' b1 b2 | e' <- shrink e] ++ [ITE e b1' b2 | b1' <- shrink b1] ++ [ITE e b1 b2' | b2' <- shrink b2]
    shrink (COMS s)       = [COMS s' | s' <- shrink s]

instance Arbitrary PicoC where
    arbitrary = sized genPicoC
    shrink (PicoC insts) = [PicoC insts' | insts' <- shrink insts]

instance Arbitrary Exp where
    arbitrary = sized genExp
    shrink (Const n)       = [Const n' | n' <- shrink n]
    shrink (Var v)         = [Var v' | v' <- shrink v]
    shrink (Add e1 e2)     = [e1, e2] ++ [Add e1' e2' | (e1', e2') <- shrink (e1, e2)]
    shrink (Sub e1 e2)     = [e1, e2] ++ [Sub e1' e2' | (e1', e2') <- shrink (e1, e2)]
    shrink (Mul e1 e2)     = [e1, e2] ++ [Mul e1' e2' | (e1', e2') <- shrink (e1, e2)]
    shrink (Div e1 e2)     = [e1, e2] ++ [Div e1' e2' | (e1', e2') <- shrink (e1, e2)]
    shrink (Great e1 e2)   = [e1, e2] ++ [Great e1' e2' | (e1', e2') <- shrink (e1, e2)]
    shrink (GreatEqual e1 e2) = [e1, e2] ++ [GreatEqual e1' e2' | (e1', e2') <- shrink (e1, e2)]
    shrink (Minor e1 e2)   = [e1, e2] ++ [Minor e1' e2' | (e1', e2') <- shrink (e1, e2)]
    shrink (MinorEqual e1 e2) = [e1, e2] ++ [MinorEqual e1' e2' | (e1', e2') <- shrink (e1, e2)]
    shrink (Equals e1 e2)  = [e1, e2] ++ [Equals e1' e2' | (e1', e2') <- shrink (e1, e2)]
    shrink (Dif e1 e2)     = [e1, e2] ++ [Dif e1' e2' | (e1', e2') <- shrink (e1, e2)]
    shrink (Not e)         = [e] ++ [Not e' | e' <- shrink e]
    shrink (And e1 e2)     = [e1, e2] ++ [And e1' e2' | (e1', e2') <- shrink (e1, e2)]
    shrink (Or e1 e2)      = [e1, e2] ++ [Or e1' e2' | (e1', e2') <- shrink (e1, e2)]
    shrink Verdadeiro      = []
    shrink Falso           = []
