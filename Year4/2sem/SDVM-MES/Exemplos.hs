module Exemplos where

import Gramatica
import ParserPicoC

teste3 = codigo "if((3>2) == True)then{a=2+3;}else{a=4;}"

testeString = "x=2+2; %teste Goat% y=3+3;%yahoho%"


----------------------------------------------------------------
-- Testes para verificar se dá um unparse direito de um PicoC
----------------------------------------------------------------
examplePicoC :: PicoC
examplePicoC = PicoC
    [ COMS "oasdflksdj", Atrib "x" (Const 10),
    Atrib "y" (Add (Var "x") (Const 5)),
    COMS "asldkjf",
    While (Great (Var "y") (Const 0))
    [ COMS "alskdfj", Atrib "y" (Sub (Var "y") (Const 1)),
     Atrib "x" (Sub (Var "x") (Const 1))],
    ITE (Minor (Var "x") (Const 5))
    [ Atrib "result" (Const 1) ]
    [ Atrib "result" (Const 0) ]] 

examplePicoC2 :: PicoC
examplePicoC2 = PicoC
    [ Atrib "x" (Const 10)
    , Atrib "y" (Add (Var "x") (Const 5))
    ]

examplePicoC3 :: PicoC
examplePicoC3 = PicoC [Atrib "margem" (Add (Const 15) (Const 0)),
                       ITE (Great (Var "margem") (Mul (Const 30) (Const 1)))
                       [Atrib "margem" (Mul (Add (Const 0)(Const 4))(Add (Add (Const 23)(Const 0)) (Mul (Const 3)(Const 1))))]
                       [Atrib "margem" (Const 0)]]


examplePicoC8 :: PicoC
examplePicoC8 = PicoC[Atrib "margem" (Add (Const 15) (Const 0)),
                    ITE (Equals (Var "margem") (Verdadeiro))
                    [Atrib "margem" (Mul (Add (Const 0)(Const 4))(Add (Add (Const 23)(Const 0)) (Mul (Const 3)(Const 1))))]
                    [Atrib "margem" (Const 0)]]

exam :: PicoC
exam = PicoC [Atrib "marem" (Equals (Var "a") (Const 1))]

---------------------------------------------------------
-- Testes do eval
---------------------------------------------------------

-- pExp1 "(3 + aux1) * 5"
ast :: Exp
ast = Mul (Add (Const 3) (Var "aux1")) (Const 5)

ast2 :: Exp
ast2 = Mul (Add (Const 3) (Const 0)) (Const 5)




---------------------------------------------------------------------
-- Exemplo que nao passa para otimizações em que não seja com o fail
---------------------------------------------------------------------
examplePicoC4 :: PicoC
examplePicoC4 = PicoC [Atrib "x" (Mul (Const 1) (Add (Const 0) (Const 1)))]

examplePicoC5 :: PicoC
examplePicoC5 = PicoC [Atrib "x" (Mul (Const 2) (Mul (Mul (Const 1) (Const 1))(Const 2)))]

examplePicoC10 :: PicoC
examplePicoC10 = PicoC [Atrib "x" (Not (Not (Const 2)))]
---------------------------------------------------------------------
-- Exemplo para testar os "NOTs"
---------------------------------------------------------------------
examplePicoC6 :: PicoC
examplePicoC6 = PicoC [ITE (Not (Great (Var "x") (Const 0))) [Atrib "x" (Const 0)] [Atrib "x" (Const 1)]]


-- Estrategia 7
-- 1. Aplicar a estrategia 4
-- 2. Aplicar a estrategia 5
-- 3. Aplicar a estrategia 6
--estrategia7 :: PicoC -> PicoC
--estrategia7 p = estrategia6(estrategia5(estrategia4 p))



b = PicoC [Atrib "vbcyrgigmvzwogcumnmgr" (Const 13),While (Or (Minor (Add (Div (Const 30) (Const 8)) (Var "ywinnnatammslnqzteaaghy")) (Var "usesu")) (Equals (Var "wizpmftxuexfafbyoucqfe") (Var "pznlmwntssnspsgetddn"))) [Atrib "itkw" (Var "zmkfeiuntvtpekjdx")]]


c = PicoC [Atrib "vbcyrgigmvzwogcumnmgr" (Const 13),While (Or (Minor (Add (Div (Const 30) (Const 8)) (Var "ywinnnatammslnqzteaaghy")) (Var "usesu")) (Equals (Var "wizpmftxuexfafbyoucqfe") (Var "pznlmwntssnspsgetddn"))) [Atrib "itkw" (Var "zmkfeiuntvtpekjdx")]]



e = "vbcyrgigmvzwogcumnmgr = 13;while(30 / 8 + ywinnnatammslnqzteaaghy < usesu ){itkw = zmkfeiuntvtpekjdx;}"

f = PicoC [Atrib "vbcyrgigmvzwogcumnmgr" (Const 13),While (Minor (Add (Div (Const 30) (Const 8)) (Var "ywinnnatammslnqzteaaghy")) (Var "usesu")) [Atrib "itkw" (Var "zmkfeiuntvtpekjdx")]]


g= "vbcyrgigmvzwogcumnmgr = -13;while(-30 /8 + ywinnnatammslnqzteaaghy < usesu || wizpmftxuexfafbyoucqfe == -pznlmwntssnspsgetddn){itkw = -zmkfeiuntvtpekjdx;}"


h = "vbcyrgigmvzwogcumnmgr = (asfasfas);while(((30) / 8) + ywinnnatammslnqzteaaghy < usesu || wizpmftxuexfafbyoucqfe == (pznlmwntssnspsgetddn)){itkw = (zmkfeiuntvtpekjdx);}"


j = "if(mtsetyhqwzizrr == amnjoyekzewuraqy || aujritawqowghryvkpjjyefjr != cjxalr)then{ifxee = 28;}else{wxizrxmvswfumx = h;}bqvdtolaehnmdzb = 31;"



n = PicoC [ITE (Or (Equals (Var "mtsetyhqwzizrr") (Var "amnjoyekzewuraqy")) (Dif (Var "aujritawqowghryvkpjjyefjr") (Var "cjxalr"))) [Atrib "ifxee" (Const 28)] [Atrib "wxizrxmvswfumx" (Var "h")],Atrib "bqvdtolaehnmdzb" (Const 31)]


n2 = PicoC [ITE Falso [Atrib "muaovvfihsyl" (Mul (Const 27) (Const 16))] [Atrib "suzbqqybqxgvahcfmieaptjrd" (Var "syrbsjneixmwvguxnuboohfqnmrjcs")],Atrib "ncxz" (Var "ttmqeiwvjy")]



exam2 :: PicoC
exam2 = PicoC [PRINT (Str "ola"), Atrib "marem" (Equals (Var "a") (Const 1))]