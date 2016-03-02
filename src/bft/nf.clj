(ns bft.nf
  "Everything to generate Normal forms.DNF & CNF."
  (:require [bft.utils :refer [Λ V ¬]]
            [bft.nf.bnf-cnf :as bnf-cnf]
            [bft.nf.pnf :as pnf]))


(def translators
  "This map define translators functions with 
  their symbols and parameters."
  {:classic {:dnf '[:conjunction lor lnot 1]
             :cnf '[:disjunction land lnot 0]}
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

