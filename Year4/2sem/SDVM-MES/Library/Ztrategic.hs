{-# LANGUAGE Rank2Types, DeriveDataTypeable, ScopedTypeVariables, TypeApplications, FlexibleContexts #-}
{-# OPTIONS_GHC -Wno-unrecognised-pragmas #-}
{-# HLINT ignore "Use camelCase" #-}
module Library.Ztrategic where

import Library.ZipperAG
import Data.Generics.Zipper hiding (left, right, up, down')
import Data.Generics.Aliases
import Data.Maybe
import Data.Data
import Control.Monad -- (join, mplus, MonadPlus)

-- monad transformers, to extract the monad from adhocTP
import Control.Monad.Trans.Class
--import Control.Monad.Trans.Class hiding(lift)
import Control.Monad.Trans.Maybe

import Control.Monad.State.Lazy
import System.IO.Unsafe (unsafePerformIO)
import System.Random (randomRIO, initStdGen, setStdGen)


import Debug.Trace

import Library.StrategicData


gatherChildren (trav, untrav) z = maybe [] ((navigation:) . gatherChildrenR navigation) $ down' z 
 where navigation = (fromJust . down' . trav, untrav . fromJust . up)


gatherChildrenR (toNthChild, fromNthChild) z = maybe [] ((navigation:) . gatherChildrenR navigation) $ right z 
 where navigation = (fromJust . right . toNthChild, fromNthChild)



breadthFirst_tdTP f = MkTP $ \z -> bf' f z [(id, id)]
 where bf' f z [] = return z 
       bf' f z ((trav, untrav):travs) = 
           do 
             let thisNode = trav z 
             thisNodeTransformed <- applyTP f thisNode
             let newTravs = gatherChildren (trav, untrav) thisNodeTransformed 
                 baseTransformed = untrav thisNodeTransformed 
             bf' f baseTransformed (travs ++ newTravs)

----------
----
--- TP
----
----------

newtype TP m  = MkTP (forall a. (Typeable a, StrategicData a) => Zipper a -> m (Zipper a))

unTP (MkTP f) = f

applyTP :: (Typeable a, StrategicData a) => TP m -> Zipper a -> m (Zipper a)
applyTP = unTP 

full_tdTP :: (Monad m) => TP m  -> TP m 
full_tdTP f = f `seqTP` allTPdown (full_tdTP f) `seqTP` allTPright (full_tdTP f)

full_buTP :: (Monad m) => TP m -> TP m
full_buTP f = allTPright (full_buTP f) `seqTP` allTPdown (full_buTP f) `seqTP` f

once_tdTP :: (MonadPlus m) => TP m -> TP m
once_tdTP f = f `choiceTP` oneTPdown (once_tdTP f) `choiceTP` oneTPright (once_tdTP f)

once_buTP :: (MonadPlus m) => TP m -> TP m  
once_buTP f = oneTPright (once_buTP f) `choiceTP` oneTPdown (once_buTP f) `choiceTP` f

--Experimental
stop_tdTP :: (MonadPlus m) => TP m -> TP m
stop_tdTP f = f `choiceTP` (allTPdown (stop_tdTP f) `seqTP` allTPright (stop_tdTP f)) 

--Experimental
stop_buTP :: (MonadPlus m) => TP m -> TP m
stop_buTP f = (allTPdown (stop_tdTP f) `seqTP` allTPright (stop_tdTP f)) `choiceTP` f

{-
New stuff
-}

atRoot :: Monad m => TP m -> TP m 
atRoot tp =
  MkTP (\z -> moveM up (\v -> Just $ v.$ arity z) (unTP tp z) (unTP (atRoot tp)) z)

-- towardsRoot :: Monad m => TP m -> TP m
-- towardsRoot tp = allTPup tp `seqTP` tp
--   MkTP (\z -> moveM up (\v -> Just $ v.$arity z) (return z) (unTP (towardsRoot tp)) z) `seqTP` tp


{- I suggest to have these operators instead of towardsRoot (and if possible change seqTP and choiceTP to work left-to-right) -}
full_uptdTP :: (Monad m) => TP m  -> TP m 
full_uptdTP f = allTPup (full_uptdTP f) `seqTP` f

full_upbuTP :: (Monad m) => TP m  -> TP m 
full_upbuTP f = f `seqTP` allTPup (full_upbuTP f)

once_uptdTP :: (MonadPlus m) => TP m -> TP m
once_uptdTP f =  oneTPup (once_upbuTP f) `choiceTP` f

once_upbuTP :: (MonadPlus m) => TP m -> TP m  
once_upbuTP f = f `choiceTP` oneTPup (once_uptdTP f)


full_tdTPupwards :: forall m a. (Typeable a, Monad m) => Proxy a -> TP m -> TP m
full_tdTPupwards _ tp = MkTP $ \z -> 
        let (Just v) = undefined -- (getHole @(Maybe a) z)
            z' = trans forbid z
            traversed = applyTP (atRoot (full_tdTP tp)) z' 
        in undefined -- fmap (setHole v) traversed



-- counts a node if mutable 
counter_func :: (Typeable a, MonadPlus m) => (a -> m a) -> a -> StateT Int m a
counter_func tr e = do 
  t <- lift $ tr e
  modify succ 
  return e

-- counts the mutable nodes
counting :: (StrategicData a, Typeable n, MonadPlus m) => 
        Zipper a -> (n -> m n) -> m Int
counting r tr = execStateT (applyTP (full_tdTP step) r) 0
 where step = idTP `adhocTPSeq` counter_func tr

-- applies mutation if node is transformable and we are supposed to mutate now
mutation_func :: (MonadPlus m) => (a -> m a) -> a -> StateT Int m a
mutation_func tr e = do 
  --run the transformation to check for mzeros (which will end computations)
  t <- lift $ tr e
  modify pred
  x <- get 
  if x==0
    then return t 
    else return e

-- applies mutation_func with a random counter deciding when to actually mutate
mutating :: (StrategicData a, Typeable b, MonadPlus m) =>
     Zipper a -> Int -> (b -> m b) -> m (Zipper a)
mutating r index tr = evalStateT (applyTP (full_tdTP step) r) index
 where step = idTP `adhocTPSeq` mutation_func tr

once_RandomTP :: (Typeable n, StrategicData a) => 
            Zipper a -> (n -> Maybe n) -> IO (Zipper a) 
once_RandomTP r tr = do
  let Just n = counting r tr
  s <- initStdGen
  setStdGen s
  index <- randomRIO(1, n)
  let Just v = mutating r index tr
  return v

-- mutations :: (Data a, Typeable n, StrategicData a) => 
--             a -> (n -> Maybe n) -> [a]
mutations z tr = applyTU (full_tdTU step) $ toZipper z 
    where step = failTU `adhocTUZ` select tr
--           select :: (Typeable n, StrategicData a) =>
--                     (n -> Maybe n) -> n -> Zipper a -> [a]
          select tr node zipper = case tr node of 
            Nothing -> [] 
            Just newNode -> let newZipper = setHole newNode zipper 
                            in [fromZipper newZipper]


{-
/New stuff
-}

adhocTP :: (Monad m, Typeable b) => TP m -> (b -> m b) -> TP m
adhocTP f g = MkTP $ \z -> do 
          let tr = transM (MaybeT . maybe (return Nothing) (fmap cast . g) . cast) z
          val <- runMaybeT tr 
          maybe (applyTP f z) return val

adhocTPSeq :: (MonadPlus m, Typeable b) => TP m -> (b -> m b) -> TP m
adhocTPSeq f g = (MkTP $ \z -> do 
             let tr = transM (MaybeT . maybe (return Nothing) (fmap cast . g) . cast) z
             val <- runMaybeT tr 
             maybe (applyTP f z) (applyTP (tryTP f)) val) 
                `choiceTP` f

adhocTPZ :: (Monad m, Typeable a, Typeable b) => TP m -> (b -> Zipper a -> m b) -> TP m
adhocTPZ f g = MkTP $ \z -> do
          let tr = transM (MaybeT . maybe (return Nothing) (\b -> maybe (return Nothing) (fmap cast . g b) (cast z)) . cast) z
          val <- runMaybeT tr 
          maybe (applyTP f z) return val

adhocTPZSeq :: (MonadPlus m, Typeable a, Typeable b) => TP m -> (b -> Zipper a -> m b) -> TP m
adhocTPZSeq f g = (MkTP $ \z -> do
             let tr = transM (MaybeT . maybe (return Nothing) (\b -> maybe (return Nothing) (fmap cast . g b) (cast z)) . cast) z
             val <- runMaybeT tr 
             maybe (applyTP f z) (applyTP (tryTP f)) val) 
                `choiceTP` f

--Identity function
idTP :: Monad m => TP m
idTP = MkTP (return . id)

--Failing function
failTP :: MonadPlus m => TP m 
failTP = MkTP (const mzero)

allTPright :: (Monad m) => TP m  -> TP m 
allTPright f = MkTP $ \z -> moveM right left (return z) (applyTP f) z

allTPdown :: (Monad m) => TP m  -> TP m 
allTPdown f = MkTP $ \z -> moveM down' up (return z) (applyTP f) z

oneTPright :: (MonadPlus m) => TP m -> TP m
oneTPright f = MkTP $ moveM right left mzero (applyTP f)

oneTPdown :: (MonadPlus m) => TP m -> TP m
oneTPdown f = MkTP $ moveM down' up mzero (applyTP f) 

--EXPERIMENTAL:
allTPleft :: (Monad m) => TP m  -> TP m 
allTPleft f = MkTP $ \z -> moveM left right (return z) (applyTP f) z

allTPup :: (Monad m) => TP m  -> TP m 
allTPup f = MkTP $ \z -> moveM up (\v -> Just $ v.$(arity z)) (return z) (applyTP f) z
-- (Just . flip (.$) (arity z))

oneTPleft :: (MonadPlus m) => TP m -> TP m
oneTPleft f = MkTP $ moveM left right mzero (applyTP f)

oneTPup :: (MonadPlus m) => TP m -> TP m
oneTPup f = MkTP $ \z -> moveM up (\v -> Just $ v.$arity z) mzero (applyTP f) z

--Sequential composition, ignores failure
seqTP :: Monad m => TP m -> TP m -> TP m
seqTP f g = MkTP ((unTP f) `mseq` (unTP g))
f `mseq` g =  \x -> f x >>= g 

--Sequential composition, chooses leftmost only if possible
choiceTP :: MonadPlus m => TP m -> TP m -> TP m
choiceTP f g = MkTP ((unTP f) `mchoice` (unTP g))
f `mchoice` g   =  \x -> (f x) `mplus` (g x)

--Apply a function, fail the composition if it fails
monoTP :: (MonadPlus m, Typeable b) => (b -> m b) -> TP m
monoTP = adhocTP failTP

--Apply a function with access to the zipper, fail the composition if it fails
monoTPZ :: (MonadPlus m, Typeable a, Typeable b) => (b -> Zipper a -> m b) -> TP m
monoTPZ = adhocTPZ failTP

--Try to apply a zipper function, and apply identity if it fails
tryTP :: MonadPlus m => TP m -> TP m
tryTP s = s `choiceTP` idTP

repeatTP :: MonadPlus m => TP m -> TP m
repeatTP s =  tryTP (s `seqTP` (repeatTP s))


-- TODO Make sure that this innermost works for all cases?!
-- note that this is significantly faster than innermost'
innermost :: (MonadPlus m) => TP m -> TP m
innermost s = allTPright (innermost s) 
      `seqTP` allTPdown (innermost s) 
      `seqTP` (tryTP (s `seqTP` (innermost s)))

innermost' :: (MonadPlus m) => TP m -> TP m
innermost' s  = repeatTP (once_buTP s)

outermost  :: (MonadPlus m) => TP m -> TP m
outermost s = repeatTP (once_tdTP s)

----------
----
--- TU
----
----------

type TU m d = (forall a. Zipper a -> m d) 

applyTU :: (Zipper a -> m d) -> Zipper a -> m d 
applyTU f z = f z 

foldr1TU :: (Monoid (m d), StrategicData a, Foldable m) => (Zipper a -> m d) -> Zipper a -> (d -> d -> d) -> d
foldr1TU f z red = foldr1 red $ (full_tdTU f z) 

foldl1TU :: (Monoid (m d), StrategicData a, Foldable m) => (Zipper a -> m d) -> Zipper a -> (d -> d -> d) -> d
foldl1TU f z red = foldl1 red $ (full_tdTU f z) 

foldrTU :: (Monoid (m d), StrategicData a, Foldable m) => (Zipper a -> m d) -> Zipper a -> (d -> c -> c) -> c -> c
foldrTU f z red i = foldr red i $ (full_tdTU f z) 

foldlTU :: (Monoid (m d), StrategicData a, Foldable m) => (Zipper a -> m d) -> Zipper a -> (c -> d -> c) -> c -> c
foldlTU f z red i = foldl red i $ (full_tdTU f z) 

full_tdTU :: (Monoid (m d), StrategicData a) => (Zipper a -> m d) -> (Zipper a -> m d)   
full_tdTU f = f `seqTU` allTUdown (full_tdTU f) `seqTU` allTUright (full_tdTU f)

full_buTU :: (Monoid (m d), StrategicData a) => (Zipper a -> m d) -> (Zipper a -> m d)
full_buTU f = allTUright (full_buTU f) `seqTU` allTUdown (full_buTU f) `seqTU` f

once_tdTU :: (MonadPlus m, Monoid (m d), StrategicData a) => (Zipper a -> m d) -> (Zipper a -> m d) 
once_tdTU f = f `choiceTU` allTUdown (once_tdTU f) `choiceTU` allTUright (once_tdTU f)

once_buTU :: (MonadPlus m, Monoid (m d), StrategicData a) => (Zipper a -> m d) -> (Zipper a -> m d)
once_buTU f = allTUright (once_buTU f) `choiceTU` allTUdown (once_buTU f) `choiceTU` f

--Experimental
stop_tdTU :: (MonadPlus m, Monoid (m d), StrategicData a) => (Zipper a -> m d) -> (Zipper a -> m d)
stop_tdTU f = f `choiceTU` (allTUdown (stop_tdTU f) `seqTU` allTUright (stop_tdTU f)) 

--Experimental
stop_buTU :: (MonadPlus m, Monoid (m d), StrategicData a) => (Zipper a -> m d) -> (Zipper a -> m d)
stop_buTU f = (allTUright (stop_buTU f) `seqTU` allTUdown (stop_buTU f)) `choiceTU` f


{-
New!
-}

full_uptdTU :: (MonadPlus m, Monoid (m d), StrategicData a) => (Zipper a -> m d)  -> (Zipper a -> m d) 
full_uptdTU f = allTUup (full_uptdTU f) `seqTU` f

full_upbuTU :: (MonadPlus m, Monoid (m d), StrategicData a) => (Zipper a -> m d)  -> (Zipper a -> m d) 
full_upbuTU f = f `seqTU` allTUup (full_upbuTU f)

once_uptdTU :: (MonadPlus m, Monoid (m d), StrategicData a) => (Zipper a -> m d)  -> (Zipper a -> m d)
once_uptdTU f =  allTUup (once_uptdTU f) `choiceTU` f

once_upbuTU :: (MonadPlus m, Monoid (m d), StrategicData a) => (Zipper a -> m d)  -> (Zipper a -> m d)  
once_upbuTU f = f `choiceTU` allTUup (once_upbuTU f)

allTUup :: (Monoid (m d), StrategicData a) => (Zipper a -> m d) -> (Zipper a -> m d)
allTUup f z = case up z of 
            Nothing -> mempty
            Just r  -> f r

{-
/New!
-}

allTUdown :: (Monoid (m d), StrategicData a) => (Zipper a -> m d) -> (Zipper a -> m d)
allTUdown f z = case down' z of 
            Nothing -> mempty
            Just d  -> f d

-- allTUright f z = rightQ mempty f z
allTUright :: (Monoid (m d), StrategicData a) => (Zipper a -> m d) -> (Zipper a -> m d)
allTUright f z = case right z of 
            Nothing -> mempty
            Just r  -> f r



adhocTU :: (Monad m, Typeable a) => (Zipper c -> m d) -> (a -> m d) -> (Zipper c -> m d)
adhocTU f g z = maybe (f z) id (zTryReduceM g z)

adhocTUZ :: (Monad m, Typeable a) => (Zipper c -> m d) -> (a -> Zipper c -> m d) -> (Zipper c -> m d)
adhocTUZ f g z = maybe (f z) id (zTryReduceMZ g z)

--TODO fix this type
seqTU :: (Monoid (m d)) => (Zipper a -> m d)-> (Zipper a -> m d) -> (Zipper a -> m d)
seqTU x y z = (x z) `mappend` (y z)

--Sequential composition 
choiceTU :: (MonadPlus m) => (Zipper a -> m d) -> (Zipper a -> m d) -> (Zipper a -> m d)
choiceTU x y z = x z `mplus` y z 

failTU :: (MonadPlus m) => (Zipper a -> m d)
failTU = const mzero

constTU :: Monad m => d -> (Zipper a -> m d)
constTU = const . return

--Apply a function, fail the composition if it fails
monoTU :: (MonadPlus m, Typeable a) => (a -> m d) -> (Zipper e -> m d)
monoTU = adhocTU failTU

--TODO: fix type signature
--Apply a function with access to the zipper, fail the composition if it fails
monoTUZ :: (MonadPlus m, Typeable a) => (a -> Zipper e -> m d) -> (Zipper e -> m d)
monoTUZ = adhocTUZ failTU


--TODO: fix type signature
--TODO: improve using Data.Generics.Aliases, fitting mkT or mkM inside of transM instead of chaining casts 
--elevate a reduction, which can access the zipper, to the zipper level. If the type does not match, returns Nothing 
zTryReduceMZ :: (Typeable a) => (a -> Zipper c -> b) -> (Zipper c -> Maybe b)
zTryReduceMZ f z = getHole z >>= return . flip f z

--elevate a reduction to the zipper level. If the type does not match, returns Nothing 
zTryReduceM :: (Typeable a) => (a -> b) -> TU Maybe b
zTryReduceM f z = getHole z >>= return . f

