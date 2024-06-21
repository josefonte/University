module Instrumentacao where

import Gramatica

instrumentation :: PicoC -> PicoC

instrumentation (PicoC insts) = PicoC (concatMap instrumentInst insts)

instrumentInst :: Inst -> [Inst]
instrumentInst inst = case inst of
    --Atrib var exp -> [logBefore inst, inst, logAfter inst]
    --While exp bloco -> [logBefore inst, While exp (instrumentBloco bloco), logAfter inst]
    --ITE exp bloco1 bloco2 -> [logBefore inst, ITE exp (instrumentBloco bloco1) (instrumentBloco bloco2), logAfter inst]
    --COMS c -> [logBefore inst, inst, logAfter inst]
    --PRINT s -> [logBefore inst, inst, logAfter inst]
    Atrib var exp -> [ inst, logAfter inst]
    While exp bloco -> [ While exp (instrumentBloco bloco), logAfter inst]
    ITE exp bloco1 bloco2 -> [ ITE exp (instrumentBloco bloco1) (instrumentBloco bloco2), logAfter inst]
    COMS c -> [ inst, logAfter inst]
    PRINT s -> [ inst, logAfter inst]
    Eliminar -> [inst]

instrumentBloco :: BlocoC -> BlocoC
instrumentBloco bloco = concatMap instrumentInst bloco

--logBefore :: Inst -> Inst
--logBefore inst = PRINT (Str ("Before: " ++ show inst))

--logAfter :: Inst -> Inst
--logAfter inst = PRINT (Str ("After: " ++ show inst))

logAfter :: Inst -> Inst
logAfter (ITE exp _ _) = PRINT (Str ("After: " ++ show exp))
logAfter inst = PRINT (Str ("After: " ++ show inst))
