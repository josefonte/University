-- |
-- Module      :  ZipperAG
-- Copyright   :  2013 Pedro Martins
-- License     :  BSD3
--
-- Maintainer  :  Pedro Martins <pedromartins4@gmail.com>
-- Stability   :  Experimental
-- Portability :  Portable
--
-- Zipper-based AG supporting functions
module Library.ZipperAG where

import Data.Generics.Zipper
import Data.Generics.Aliases (mkQ)
import Data.Maybe
import Data.Data (Data)

-- |Gives the n'th child
(.$) :: Zipper a -> Int -> Zipper a
z .$ 1 = fromJust (down' z)
z .$ n = fromJust (right ( z.$(n-1) ))

-- |parent
parent = fromJust.up

-- |Tests if z is the n'th sibling
(.|) :: Zipper a -> Int -> Bool
z .| 1 = case left z of
  Nothing -> False
  _ -> True
z .| n = case left z of
  Nothing -> False
  Just x ->  x .| (n-1)




(.$>) :: Zipper a -> Int -> Zipper a
zipper .$> n = let current = arity zipper
               in  (parent zipper).$(current+n)

(.$<) :: Zipper a -> Int -> Zipper a
zipper .$< n = let current = arity zipper
               in  (parent zipper).$(current-n)

arity :: Zipper a -> Int 
arity m = arity' m 1
 where arity' :: Zipper a -> Int -> Int
       arity' m n = case left m of
                     Nothing  -> n                     
                     Just m'  -> arity' m' (n+1)

mkAG :: Data x => x -> Zipper x
mkAG = toZipper


------------------------------------------------------
------------------------------------------------------
---------------------------
--------------------------- Experimental
---------------------------
------------------------------------------------------
------------------------------------------------------


-- compute attribute f of parent of zipper z 
(.^) :: (Zipper a -> b) -> Zipper a -> b 
(.^) f z = f $ parent z 

-- compute attribute f of root of zipper z 
(.^^) :: (Zipper a -> b) -> Zipper a -> b
(.^^) f z = moveQ up (f z) (f.^^) z

-- compute attribute f of node upwards in zipper z. We travel upwards in the zipper and query the first node that satisfies p
inherit :: Data n => (n -> Bool) -> (Zipper a -> b) -> Zipper a -> b 
inherit p f z = if query (mkQ False p) z then f z else (inherit p f).^ z