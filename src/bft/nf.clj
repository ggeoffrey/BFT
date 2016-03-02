(ns bft.nf
  "Everything to generate Normal forms.DNF & CNF."
  (:require [bft.utils :refer [Λ V ¬, hamming]]
            [bft.nf.bnf-cnf :as bnf-cnf]
            [bft.nf.pnf :as pnf]
            [clojure.math.combinatorics :as combo]))


(def translators
  "This map define translators functions with 
  their symbols and parameters."
  {:classic {:dnf '[:conjunction lor lnot 0]
             :cnf '[:disjunction land lnot 1]}
   :fancy {:dnf '[:conjunction V ¬ 1]
           :cnf '[:disjunction Λ ¬ 0]}})





(defn table->nf
  "Transform a truth table to a normal form,
   with a mode in #{:classic :fancy}.
   ex:  (table->dnf '[x y z] 
                   [[[0 0 1] 1]
                    [[1 0 0] 0]
                    [[0 1 0] 1]]
                    :dnf :fancy"
  ([table form]
   (table->nf table form :classic))
  
  ([table form mode]
   (let [[litterals lines] table]
     (table->nf litterals lines form mode)))
  
  ([litterals lines form mode]
   (cond
     (= :pnf form) (pnf/table->pnf litterals lines mode)
     :else (let [translators (get translators mode)
                 [translator factor _ result-to-focus-on] (get translators form)]
             (->> lines   ;; take the table
                  (filter (fn [line] (= (last line) result-to-focus-on ))) ;; keep only desired lines
                  (map (fn [line] (cons litterals line)))  ;; add names to it
                  (map (fn [line] (bnf-cnf/transform-to line translator translators))) ;; convert each line to NF
                  (cons factor) ;; factorize by Λ or V
                  )))))



;; ------------- CONVERTIONS -----------------






(defn args-values-tuples
  "Create n args values possibilites like ((0 0 1) (0 1 1) (1 1 1))"
  [args]
  (let [args-count (count args) ;; get the args number
        ;; generate tuples with one 1, two 1 ... n 1.
        ones (loop [args-count args-count
                    result '()]
               (cond
                 (= -1 args-count) result  ;; -1 to have [0 0 0]
                 :else (recur (dec args-count) (cons
                                                (take args-count
                                                      (repeatedly (fn [] 1)))
                                                result))))]
    ;; for each tuble of zeros and ones
    (map (fn [ones]
           (flatten
            (cond
              ;; if it is too small…
              (< (count ones) args-count) (cons
                                           ;; fill the rest with zeros
                                           (take (- args-count (count ones))
                                                 (repeatedly (fn [] 0)))
                                           ones)
              :else ones)))
         ones)))


(defn litterals-values
  "Generate truth table arguments' values"
  [args]
  (->> args  ;; take the args
       (args-values-tuples)  ;; get tuples with zeros and ones
       (mapcat combo/permutations)  ;; compute possible permutations
       ;; as a vector (truth table)
       (sort-by hamming <)
       (mapv identity)))



(defn nf->table
  "Extract a truth table from a normal form"
  [args form]
  (let [;; make a λ from a form, inject it.
        form-as-λ (eval (cons 'fn (list args form)))
        ;; generate all possible args
        args-values (litterals-values args)]
    ;; for each args
    (->> args-values
         ;; make it a tuple [[args] result]
         (map (fn [args]
                (vector args (case (apply form-as-λ args)
                               false 0
                                     1))))
         ;; put them all in a vector
         (into [])
         ;; wrap it again
         (vector)
         ;; add the args
         (cons args)
         ;; add finaly make it a vector
         (apply vector)
         ;; tada ! a truth table
         )))


(def possible-nf
  "List of available normal forms"
  #{:dnf :cnf :pnf :mnf})

 
;; I'm sorry Mr. De Morgan and Karnaugh. But this way is way easier.
;; At least for me…

;; Todo check time complexity to see if a rewrite is necessary
(defn convert
  "Convert a form into another by extracting the truth table from it
   and converting it back again. Ok it's dirty but hey,
   let's be honnest, it works pretty well!"
  ([litterals form target-name]
   (convert litterals form target-name :classic))
  
  ([litterals form target-name mode]
   (let [table (nf->table litterals form)]
     (table->nf table target-name mode))))


