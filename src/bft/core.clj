(ns bft.core
  (:require [clojure.core.match :refer [match]]))


(def med
  "The med(x,y,z) funtion, returning the most frequent arg."
  (fn [x y z]
    (match [x y z]
           [1 1 _] 1
           [_ 1 1] 1
           [1 _ 1] 1
           :else   0)))

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


(def check-line-format
  "Check if a line is well formated."
  (fn [line]
    (let [[names values result] line]
      (cond
       (empty? names) nil  ;; no litterals ? -> nil
       (empty? values) nil  ;; no corresponding values ? -> nil
       (not (= (count names) (count values))) nil ;; not the same amount of litterals and value ? -> nil
       (not (or (= 1 result) (= 0 result))) nil ;; result is neighter 0 nor 1
       :else line))))



(def transform-to
  "Transform a line to a conjuction or disjunction.
   i.e.: (transform-to <line> :disjunction)"
  (fn [line type]
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
                          nil)))))))))

  
(def line->conjuntion
  "Give the conjunction of a boolean proposition"
  (fn [line]
    (let [line (check-line-format line)]
      (when (not (nil?))
        (transform-to line :conjuction)))))

;;(line->conjuntion ['[x y z] [0 0 0] 1])

(def line->disjunction
  "Give the disjunction of a boolean proposition"
  (fn [line]
    (let [line (check-line-format line)]
      (when (not (nil? line))
        (transform-to line :disjunction)))))

;;(line->disjunction ['[x y z] [0 0 0] 1])



(def table->dnf
  "Transform a truth table to DNF.
  ex:  (table->dnf '[x y z] [[[0 0 1] 1]
                             [[1 0 0] 0]
                             [[0 1 0] 1]])"
  (fn [names table]
    (->> table  ;; take the table
         (filter (fn [line] (= 1 (last line)))) ;; keep only the true lines
         (map (fn [line] (cons names line)))
         (map line->conjuntion) ;; convert each true lines to CNF
         (cons 'or ) ;; factorize by V
         )))

(comment (table->dnf '[x y z] [[[0 0 0] 1]
                               [[0 0 1] 0]
                               [[0 1 0] 0]
                               [[0 1 1] 1]
                               [[1 0 0] 0]
                               [[1 0 1] 1]
                               [[1 1 0] 0]
                               [[1 1 1] 1]]))

(def table->cnf
  "see table->dnf for example and comments"
  (fn [names table]
    (->> table
         (filter (fn [line] (= 0 (last line))))
         (map (fn [line] (cons names line)))
         (map line->disjunction)
         (cons 'and))))

(comment (table->cnf '[x y z]  [[[0 0 0] 0]
                                [[0 0 1] 1]
                                [[0 1 0] 1]
                                [[0 1 1] 0]
                                [[1 0 0] 1]
                                [[1 0 1] 0]
                                [[1 1 0] 1]
                                [[1 1 1] 0]]))


