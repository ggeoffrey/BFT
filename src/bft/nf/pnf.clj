(ns bft.nf.pnf
  "Some special & advanced functions to work with PNF"
  (:require [clojure.math.combinatorics :as combo]
            [clojure.set :as s]
            [bft.utils :refer [boolean hamming parity land Λ]]))



(def symbols
  {:classic '[lxor land]
   :fancy   '[⊕ Λ]})



(defn function-args-subsets
  "Give arguments' subsets for a given function's arguments"
  [args]
  (->>
   (combo/partitions '[x y z])  ;; generate all combinations
   (filter (fn [item] (not (empty? item))))  ;; drop empty ones
   (mapcat identity)  ;; flatten one level
   (sort-by count >)
   (distinct)))


(defn args-opposite
  "Give you all the other arguments. [z]-> [x y]. [x z] -> z."
  [items args]
  (s/difference (set args) (set items)))


(defn arg-position
  "Give the zero-based index of an argument"
  [item args]
  (.indexOf args item))


(defn select-lines
  "Select only lines where the specified indexes are 0"
  [indexes litterals lines]
  (->> lines
       (filter (fn [line]  ;; keep only lines
                 (apply Λ ;; that fullfil for all
                        (map (fn [arg]   ;; that the given args 
                               (let [pos (arg-position arg litterals)
                                     values (first line)]
                                 ;; are false
                                 (false? (boolean (get values pos)))
                                 ))
                             indexes))))))




(defn- raw-pnf
  "Take litterals and lines and create PNF in vector form."
  [litterals lines]
  ;; generate pairs of litterals [x] [y] [z] [x y] [x z] [y z] [x y z].
  (let [combinations (function-args-subsets litterals)]
    ;; take all the combinations
    (->> combinations
         ;; get their opposite to focus on colums to check
         (map (fn [combi] (args-opposite combi litterals)))
         ;; select lines where those opposites are 0
         (map (fn [select-criteria-i] (select-lines select-criteria-i
                                                   litterals
                                                   lines)))
         ;; keep only those having an even parity for their Hamming vector
         (map (fn [line-i]
                (boolean
                 (->> line-i
                      (map last) ;; focus on result
                      (hamming)
                      (parity)))))
         ;; Couple combinations with their respective parity
         (map vector combinations)
         ;; keep only the true ones, parity is at last position
         (filter last)
         ;; keep only the litteral combination, drop the parity
         (map first))))



(defn table->pnf
  "Transform a truth table to PNF."
  ([table]
   (table->pnf table :classic))

  ([table mode]
   (let [[litterals lines] table]
     (table->pnf litterals lines mode)))
  
  ([litterals lines mode]
   (let [[sym-xor sym-and] (get symbols mode)]
     ;; compute the cnf
     (->> (raw-pnf litterals lines)
          ;; for each part, factorize by Λ
          (map (fn [part]
                 (cond
                   (= 1 (count part)) (first part)
                   :else (->> part
                              (apply list)
                              (cons sym-and)))))
          (cons sym-xor)))))


