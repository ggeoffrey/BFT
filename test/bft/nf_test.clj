(ns bft.nf-test
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

(def table-pnf [ '[x y z]
                [[[0 0 0] 0]
                 [[0 0 1] 1]
                 [[0 1 0] 1]
                 [[0 1 1] 0]
                 [[1 0 0] 0]
                 [[1 0 1] 1]
                 [[1 1 0] 0]
                 [[1 1 1] 1]]])

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


(def pnf-equivalent
  '(⊕ (Λ x y) z y))

(facts "About truth table"
  (fact "convertion to CNF should be correct"
    (table->nf table :cnf :fancy) => cnf-equivalent)
  (fact "convertion to DNF should be correct"
    (table->nf table :dnf :fancy) => dnf-equivalent)
  (fact "convertion to PNF should be correct"
    (table->nf table-pnf :pnf :fancy) => pnf-equivalent))
