(ns bft.core-test
  (:use midje.sweet)
  (:require [bft.nf :refer :all]
            [bft.utils :refer :all]))

(def table [ '[x y z]
            [[[0 0 0] 0]
             [[0 0 1] 1]
             [[0 1 0] 1]
             [[0 1 1] 0]
             [[1 0 0] 1]
             [[1 0 1] 0]
             [[1 1 0] 1]
             [[1 1 1] 0]]])

(def cnf-equivalent
  '(Λ (V x y z)
      (V x (¬ y) (¬ z))
      (V (¬ x) y (¬ z))
      (V (¬ x) (¬ y) (¬ z))))

(def dnf-equivalent
  '(V (Λ (¬ x) (¬ y) z)
      (Λ (¬ x) y (¬ z))
      (Λ x (¬ y) (¬ z))
      (Λ x y (¬ z))))



(facts "About truth table"
  (let [[names rows] table]
    (fact "convertion to CNF should be correct"
      (table->nf names rows :cnf) => cnf-equivalent)
    (fact "convertion to DNF should be correct"
      (table->nf names rows :dnf) => dnf-equivalent)))
