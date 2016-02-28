(ns bft.core
  (:require [clojure.core.match :refer [match]]
            [clojure.math.combinatorics :as combo]))


;; to generate tuples for PNF
(comment (->> (combo/partitions '[x y z])         
              (filter (fn [item] (not (empty? item))))
              (mapcat identity)
              (sort-by count <)
              (distinct)))


(defn med
  "The med(x,y,z) funtion, returning the most frequent arg."
  [x y z]
  (match [x y z]
         [1 1 _] 1
         [_ 1 1] 1
         [1 _ 1] 1
         :else   0))

;; (def med-variadic
;;   "Mediane avec nombre variable d'arguments.
;;   Pas vraiment sûr que cela ai du sens… mais bon…"
;;   (fn [& args]
;;     (cond
;;      (empty? args) 0   ;; f() -> 0
;;      (nil? (first (rest args)))  (first args)  ;; f(x) -> x
;;      :else (->> args              ;; prendre les arguments
;;                 (frequencies)     ;; déterminer la fréquence
;;                 (sort-by val >)   ;; trier par fréquence
;;                 (first)           ;; ne garder que le premier
;;                 (first)           ;; ne garder que l'argument
;;                 ))))


(defn check-line-format
  "Check if a line is well formated."
  [line]
  (let [[names values result] line]
    (cond
     (empty? names) nil  ;; no litterals ? -> nil
     (empty? values) nil  ;; no corresponding values ? -> nil
     (not (= (count names) (count values))) nil ;; not the same amount of litterals and value ? -> nil
     (not (or (= 1 result) (= 0 result))) nil ;; result is neighter 0 nor 1
     :else line)))



(defn transform-to
  "Transform a line to a conjuction or disjunction.
   i.e.: (transform-to <line> :disjunction)"
  [line type]
  (let [[names values] line
        operator (case type
                   :disjunction 'or
                   :conjuction  'and
                   nil)]
    (cons operator  ;; factorize by Λ or V
          (reverse  ;; the reversed list of ...
           ;; the litterals' list transformed to x or (not x), depending on the values
           (loop [names names    ;; litterals' local alias
                  values values  ;; values' local alias
                  result (list)  ;; aggregator for tail recurtion
                  ]
             (cond
              (empty? names) result ;; Have we done ? return the result
              :else (let [litteral (first names)      ;; take the current litteral
                          corresp-val (first values)] ;; and it's respective value
                      (case corresp-val
                        0 (recur (rest names) (rest values) (cons
                                                             (case type
                                                               :conjuction   (list 'not litteral)
                                                               :disjunction  litteral
                                                               nil)
                                                             result))
                        1 (recur (rest names) (rest values) (cons
                                                             (case type
                                                               :conjuction  litteral
                                                               :disjunction (list 'not litteral))
                                                             result))
                        nil))))))))

  
(defn line->conjuntion
  "Give the conjunction of a boolean proposition"
  [line]
  (let [line (check-line-format line)]
    (when (not (nil?))
      (transform-to line :conjuction))))

;;(line->conjuntion ['[x y z] [0 0 0] 1])

(defn line->disjunction
  "Give the disjunction of a boolean proposition"
  [line]
  (let [line (check-line-format line)]
    (when (not (nil? line))
      (transform-to line :disjunction))))

;;(line->disjunction ['[x y z] [0 0 0] 1])


(defn table->nf
  "Transform a truth table to a normal form
   ex:  (table->dnf '[x y z] 
                   [[[0 0 1] 1]
                    [[1 0 0] 0]
                    [[0 1 0] 1]]
                    :dnf"
  [names table form]
  (let [translator (case form
                     :dnf line->conjuntion
                     :cnf line->disjunction
                     nil)
        result-to-focus-on (case form
                             :dnf 1
                             :cnf 0
                             nil)
        factor (case form
                 :dnf 'or
                 :cnf 'and
                 nil)]
    (->> table   ;; take the table
         (filter (fn [line] (= (last line) result-to-focus-on ))) ;; keep only desired lines
         (map (fn [line] (cons names line)))  ;; add names to it
         (map translator)  ;; convert each line to NF
         (cons factor))))  ;; factorize by Λ or V


(comment (table->nf  '[x y z]
                    [[[0 0 0] 1]
                     [[0 0 1] 0]
                     [[0 1 0] 0]
                     [[0 1 1] 1]
                     [[1 0 0] 0]
                     [[1 0 1] 1]
                     [[1 1 0] 0]
                     [[1 1 1] 1]]
                    :dnf))


(comment (table->nf  '[x y z]
                    [[[0 0 0] 0]
                     [[0 0 1] 1]
                     [[0 1 0] 1]
                     [[0 1 1] 0]
                     [[1 0 0] 1]
                     [[1 0 1] 0]
                     [[1 1 0] 1]
                     [[1 1 1] 0]]
                    :cnf))


