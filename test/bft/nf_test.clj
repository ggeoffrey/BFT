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

(def pnf-equivalent-same-table
  '(⊕ (Λ x y) z y x))

(facts "About truth table"
  (fact "convertion to CNF should be correct"
    (table->nf table :cnf :fancy) => cnf-equivalent)
  (fact "convertion to DNF should be correct"
    (table->nf table :dnf :fancy) => dnf-equivalent)
  (fact "convertion to PNF should be correct"
    (table->nf table-pnf :pnf :fancy) => pnf-equivalent))

(facts "Convertion should work"
  (fact "from CNF to BNF"
    (convert '[x y z] cnf-equivalent :dnf :fancy) => dnf-equivalent)
  (fact "from CNF to PNF"
    (convert '[x y z] cnf-equivalent
             :pnf :fancy) => pnf-equivalent-same-table)
  (fact "from DNF to CNF"
    (convert '[x y z] dnf-equivalent :cnf :fancy) => cnf-equivalent)
  (fact "from BNF to PNF"
    (convert '[x y z] dnf-equivalent
             :pnf :fancy) => pnf-equivalent-same-table)
  (fact "from PNF to CNF"
    (convert '[x y z] pnf-equivalent-same-table
             :cnf :fancy) => cnf-equivalent)
  (fact "from PNF to DNF"
    (convert '[x y z] pnf-equivalent-same-table
             :dnf :fancy) => dnf-equivalent))


