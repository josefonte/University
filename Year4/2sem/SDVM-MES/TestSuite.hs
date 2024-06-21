module TestSuite where

import Gramatica
import Otimizacoes
import Programas
import Instrumentacao

-- import Debug.Trace (trace)
import System.IO.Unsafe (unsafePerformIO)

teste11 :: Inputs
teste11 = [("g",0),("result",0),("r",3)]
teste12 :: Inputs
teste12 = [("g",0),("result",0),("r",4)]
teste13 :: Inputs
teste13 = [("g",0),("result",0),("r",5)]
teste14 :: Inputs
teste14 = [("g",0),("result",0),("r",0)]

testSuitePrograma1 :: [(Inputs,Int)]
testSuitePrograma1 = [(teste11,8),(teste12,16),(teste13,32),(teste14,1)]

runTestSuitePrograma1 = runTestSuite programa1 testSuitePrograma1





teste21 :: Inputs 
teste21 = [("g",2),("result",0),("n",9)]
teste22 :: Inputs 
teste22 = [("g",3),("result",0),("n",5)]
teste23 :: Inputs 
teste23 = [("g",4),("result",0),("n",-1)]
teste24 :: Inputs 
teste24 = [("g",1),("result",0),("n",20)]
teste25 :: Inputs 
teste25 = [("g",-1),("result",0),("n",12)]

testSuitePrograma2 :: [(Inputs,Int)]
testSuitePrograma2 = [(teste21,9),(teste22,3),(teste23,4),(teste24,20),(teste25,12)]

runTestSuitePrograma2 = runTestSuite programa2 testSuitePrograma2




teste31 :: Inputs 
teste31 = [("g",2),("result",0),("r",2)]
teste32 :: Inputs 
teste32 = [("g",1),("result",0),("r",20)]
teste33 :: Inputs 
teste33 = [("g",5),("result",0),("r",-10)]
teste34 :: Inputs 
teste34 = [("g",6),("result",0),("r",2)]

testSuitePrograma3 :: [(Inputs,Int)]
testSuitePrograma3 = [(teste31,12),(teste32,104),(teste33,-6),(teste34,4)]

runTestSuitePrograma3 = runTestSuite programa3 testSuitePrograma3

-- 1 (eval!)

evaluate :: PicoC -> Inputs -> Int 
evaluate (PicoC []) i = getResult i 
evaluate (PicoC (h:t)) i = evaluate (PicoC t) (evaluateInst h i)


getResult :: Inputs -> Int 
getResult [] = 0
getResult (h:t) 
                | fst h == "result" = snd h
                | otherwise = getResult t

atribAux :: Inst -> Inputs -> Inputs -> Inputs
atribAux e@(Atrib str expp) (h:t) original 
                                                | fst h == str = (fst h, eval expp original) : t 
                                                | otherwise = h : atribAux e t original


evaluateInst :: Inst -> Inputs -> Inputs
evaluateInst e@(Atrib str expp) original = atribAux e original original


evaluateInst e@(ITE expp bloco1 bloco2) original 
                                                | eval2 expp original = evaluateBloco (PicoC bloco1) original
                                                | otherwise = evaluateBloco (PicoC bloco2) original

evaluateInst e@(While expp bloco) original 
                                                | eval2 expp original = evaluateInst e (evaluateBloco (PicoC bloco) original) 
                                                | otherwise = original

evaluateInst (PRINT exp) original
                                                | isStrExp exp = trace ("PRINT: " ++ evalS exp original) original
                                                | otherwise = trace ("PRINT: " ++ show (eval exp original)) original

trace :: String -> a -> a
trace s x = unsafePerformIO $ x `seq` putStrLn s >> return x

isStrExp :: Exp -> Bool
isStrExp (Str _) = True
isStrExp _ = False

evaluateBloco :: PicoC -> Inputs -> Inputs 
evaluateBloco (PicoC []) i = i
evaluateBloco (PicoC (h:t)) i = evaluateBloco (PicoC t) (evaluateInst h i)



-- 2

runTest :: PicoC -> (Inputs, Int) -> Bool 
runTest x (inp, n) 
                | evaluate x inp == n = True 
                | otherwise = False


-- 3 

runTestSuite :: PicoC -> [(Inputs, Int)] -> Bool
runTestSuite x [] = True
runTestSuite x (h:t) 
                | runTest x h == True = runTestSuite x t
                | otherwise = False


-- 4

testesUnitarios :: IO ()
testesUnitarios = do
  let teste1 = runTestSuite programa1 testSuitePrograma1
  let teste2 = runTestSuite programa2 testSuitePrograma2
  let teste3 = runTestSuite programa3 testSuitePrograma3
  
  putStrLn $ "Test Programa1: " ++ if teste1 then "Passou" else "Falhou"
  putStrLn $ "Test Programa2: " ++ if teste2 then "Passou" else "Falhou"
  putStrLn $ "Test Programa3: " ++ if teste3 then "Passou" else "Falhou"

-- 5
-- Está no ficheiro mutacoes.hs


-- 6

-- aplicaMutComMutations seed programa mut3

programa1mut = PicoC [Atrib "result" (Const 1),While (GreatEqual (Var "g") (Var "r")) [PRINT (Str "Dentro do ciclo While(g>r)"),Atrib "result" (Mul (Var "result") (Const 2)),PRINT (Str "Atualizei o Result:"),PRINT (Var "result"),Atrib "g" (Add (Var "g") (Const 1)),PRINT (Str "Atualizei o g:"),PRINT (Var "g")],PRINT (Str "------------------------FIM DO PROGRAMA------------------------")]
programa2mut = PicoC [
                    ITE (Great (Add (Var "g") (Add (Const 5) (Const 10))) (Var "n"))
                    [Atrib "result" (Var "g")]
                    [Atrib "result" (Var "n")]
                ]
programa3mut = PicoC [While (Minor (Var "g") (Const 10)) [PRINT (Var "g"),ITE (MinorEqual (Var "g") (Const 5)) [PRINT (Str "Entrei na condicao if(g>5)"),Atrib "result" (Add (Var "result") (Const 1))] [PRINT (Str "Entrei na condicao else(g<=5)"),Atrib "result" (Add (Var "result") (Var "r"))],Atrib "g" (Add (Var "g") (Const 1))]]

-- 7

-- Estender a linguagem com prints:
alinea7 = evaluate programa1 teste11

-- 8 

alinea8 = evaluate (instrumentation programa2) teste21


-- 9

runInstrumentedTest :: PicoC -> (Inputs, Int) -> Bool 
runInstrumentedTest x (inp, n) 
                | evaluate (instrumentation x) inp == n = True 
                | otherwise = False

instrumentedTestSuite :: PicoC -> [(Inputs,Int)] -> Bool 
instrumentedTestSuite x [] = True
instrumentedTestSuite x (h:t)
                | runInstrumentedTest x h == True = instrumentedTestSuite x t
                | otherwise = False




                


{-
main :: IO ()
main = do
    let programa = PicoC [
            Atrib "x" (Const 5),
            PRINT "Valor de x atribuido",
            While (Minor (Var "x") (Const 10)) [
                PRINT "Entrando no loop",
                Atrib "x" (Add (Var "x") (Const 1)),
                PRINT "Incrementando x"
            ],
            PRINT "Saindo do loop",
            PRINT "Fim do programa"
          ]
    PRINT $ evaluate programa []
-}  