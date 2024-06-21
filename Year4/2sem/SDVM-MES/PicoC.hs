module PicoC where

import ParserPicoC
import Gramatica
import Geradores
import Otimizacoes
import Exemplos
import Programas
import TestSuite
import Mutacoes
import Instrumentacao

import Data.Generics.Zipper
import Data.Maybe

import Test.QuickCheck


import Library.StrategicData (StrategicData)
import Library.Ztrategic
--import MES_Project.Library.Ztrategic
    ( adhocTP, applyTP, failTP, full_tdTP, idTP, innermost )
import Library.StrategicData (StrategicData)




-------------------------------------------------------------
---------------------       prop       ----------------------
-------------------------------------------------------------

-- propriedades pedidas nas primeiras aulas 
prop :: PicoC -> Bool
prop ast = ast == parse (unparse ast)

prop2 :: String -> Bool 
prop2 ast = ast == unparse (parse ast)


-- Propriedade pedida na pascoa
propp :: PicoC -> IO Bool
propp ast = do
    parsed <- return (parse (unparse ast))
    return (ast == parsed)





--testee2 :: IO()
--testee2 = do
--    -- Generate the first example
--    firstExample <- generate (vectorOf 1 (genPicoC 2))
--    let picoC = case firstExample of
--                    [x] -> x
--                    _   -> error "Unexpected number of PicoC values generated"    
--    print $ megaEstrat picoC
--    print "\n\n"
--    result <- propp $ megaEstrat picoC
--    print result
--
--testee3 :: IO()
--testee3 = do
--    -- Generate the first example   
--    examples <- generate (vectorOf 1 (genPicoC 2))
--    print examples
--    case examples of
--        [x] -> do
--            print x
--            result <- propp $ megaEstrat x
--            print result
--        _ -> putStrLn "Unexpected number of PicoC values generated"
--