(ns bft.utils
  "Define some common utils and aliases for math symbols
  like Λ, V and ¬."
  (:refer-clojure :exclude [boolean]))

(defn boolean
  "Transform a value or a list of value to a boolean
  equivalent or a list of equivalent booleans.
  i.e. (1 0 (and 0 0)) -> (true false (and false true)"
  ([coll]
   (cond
       (not (seq? coll)) (case coll
                           0 false
                           1 true
                           coll)
       (empty? coll) nil
       :else (map boolean coll)))
  ([form & forms]
   (map boolean (cons form forms))))


(defn land
  "Logical and"
  ([x] x)
  ([x & xs] (and (boolean x) (apply land (boolean xs)))))

(defn lor
  "Logical or"
  ([x] x)
  ([x & xs] (or (boolean x) (apply lor (boolean xs)))))

(def lnot (comp not boolean))

(def Λ land)
(def V lor)
(def ¬ lnot)




