;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;  Welcome to Neverland visitor!          |
;;                                         |
;;  {\     /}                              |
;;   >`.()'<                               |
;;  {@ /|\/@}                              |
;;   `/'|`~'                               |
;;      \\       Let's inspect the fairy   |
;;      //       society together.         |
;;     ''                                  |
;;                                         |
;;                                         |
;; Take your brackets with you.            |           
;; We are going to use them alot!          |           
;;                                         |           
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(ns bft.core
  (:require [bft.nf :refer [table->nf use-symbols!]]
            [bft.utils :refer [Λ V ¬ land lor lnot]
            [clojure.core.match :refer [match]]
            [clojure.math.combinatorics :as combo]]))



(use-symbols! :fancy)
(use-symbols! :classic)


;; to generate tuples for PNF
(comment (->> (combo/partitions '[x y z])
              (filter (fn [item] (not (empty? item))))
              (mapcat identity)
              (sort-by count <)
              (distinct)))


(defn check-line-format
  "Check if a truth table's line is well formated."
  [line]
  (let [[names values result] line]
    (cond
     (empty? names) nil  ;; no litterals ? -> nil
     (empty? values) nil  ;; no corresponding values ? -> nil
     (not (= (count names) (count values))) nil ;; not the same amount of litterals and values ? -> nil
     (not (or (= 1 result) (= 0 result))) nil ;; result is neither 0 nor 1
     :else line)))


(defn- check-table-format
  "Check if a table is well formated.
  Return a list of incorrect lines or nil if everything ok."
  [names table]
  (let [bad-lines (filter (fn [line] (nil? (check-line-format
                                           (cons names line))))
                          table)]
    (cond
        (empty? bad-lines) nil
        :else bad-lines)))

