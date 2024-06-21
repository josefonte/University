module Library.StrategicData 
   (StrategicData(..), right, left, up, down', forbid, isJust) 
  where 

import Data.Generics
import Data.Generics.Zipper hiding (left, right, up, down')
import qualified Data.Generics.Zipper as Z (left, right, up, down')

import Prelude hiding (null)
import Data.Typeable
import Control.Exception (Exception, throw, catch, evaluate)
import System.IO.Unsafe (unsafePerformIO)

class Typeable t => StrategicData t where
  isTerminal :: Zipper t -> Bool
  -- default isTerminal :: Zipper t -> Bool
  isTerminal _ = False

right :: StrategicData a => Zipper a -> Maybe (Zipper a)
right z = case Z.right z of 
            Just r -> if isTerminal r || isNull r then right r else Just r 
            Nothing -> Nothing 

left :: StrategicData a => Zipper a -> Maybe (Zipper a)
left z = case Z.left z of 
           Just r -> if isTerminal r || isNull r then left r else Just r 
           Nothing -> Nothing  

up :: StrategicData a => Zipper a -> Maybe (Zipper a)
up z = case Z.up z of 
         Just r -> if isTerminal r || isNull r then right r else Just r
         Nothing -> Nothing 
     
down' :: StrategicData a => Zipper a -> Maybe (Zipper a)
down' z = case Z.down' z of 
            Just r -> if isTerminal r || isNull r then right r else Just r
            Nothing -> Nothing 

isJust (Just _) = True
isJust Nothing  = False


{-
instance Typeable m => StrategicData (Let m) where
  isTerminal z =  isJust (getHole z :: Maybe m) || isJust (getHole z :: Maybe Name) || isJust (getHole z :: Maybe Int)
(for this you need to enable the ScopedTypeVariables extension)
-}



----------
----
--- The Null Pointer!!
----
----------

isNull z = query isNull' z
forbid = const null

-- | The null value.
-- When forced, a NullPointerException will be thrown.
null :: a
null = throw NullPointerException
{-# NOINLINE null #-}



isNull' x = unsafePerformIO $
    (evaluate x *> pure False) 
        `catch` 
            \NullPointerException -> pure True

-- | Thrown on attempt to use 'null'.
data NullPointerException = NullPointerException deriving (Eq, Show, Typeable)

instance Exception NullPointerException



--- Null

{-
data Null    = Null

setNull :: Typeable b => Proxy b -> Zipper a -> Zipper a
setNull p z = maybe  z (const (setHole Null z)) (nullableHole p)
 where nullableHole :: Typeable b => Proxy b -> Maybe b
       nullableHole _ = getHole z

isNull  = query (mkQ False (\Null -> True))
-}

