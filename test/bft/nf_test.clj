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


(def pnf-equivalent
  '(⊕ (Λ x y) z y))


(defn match-truth-table
  "Execute the form for each line of the truth table"
  [form table]
  (let [[litterals lines] table
        lambda (eval (cons 'fn (list litterals form)))]
    (->> lines
         (map (fn [[args result]]
                (= (apply lambda args) (boolean result))))
         (apply land)
         )))


(match-truth-table pnf-equivalent table-pnf)



(facts "About truth table"
  (fact "convertion to CNF should be correct"
    (table->nf table :cnf :fancy) => cnf-equivalent)
  (fact "convertion to DNF should be correct"
    (table->nf table :dnf :fancy) => dnf-equivalent)
  (fact "convertion to PNF should be correct"
    (table->nf table-pnf :pnf :fancy) => pnf-equivalent))


(facts "Execution"
  (fact "of CNF form should match the original truth table"
    (match-truth-table cnf-equivalent table) => true)
  (fact "of DNF form should match the original truth table"
    (match-truth-table dnf-equivalent table) => true)
  (fact "of PNF form should match the original truth table"
    (match-truth-table pnf-equivalent table-pnf) => true))

(facts "Convertion should work"
  (fact "from CNF to BNF"
    (convert '[x y z] cnf-equivalent :dnf :fancy) => dnf-equivalent)
  (fact "from CNF to PNF"
    (convert '[x y z] cnf-equivalent
             :pnf :fancy) => pnf-equivalent-same-table)
  (fact "from DNF to CNF"
    (convert '[x y z] dnf-equivalent :cnf :fancy) => cnf-equivalent)
  (fact "from DNF to PNF"
    (let [bnf (table->nf table-pnf :pnf :fancy)]
      (convert '[x y z] bnf :pnf :fancy)) => pnf-equivalent)



  ;; TODO 
  (comment (fact "from PNF to CNF"
             (let [pnf (table->nf table :pnf :fancy)]
               (convert '[x y z] pnf :cnf :fancy)) => cnf-equivalent))
  (comment (fact "from PNF to DNF"
             (let [pnf (table->nf table :pnf :fancy)]
               (convert '[x y z] pnf :dnf :fancy)) => dnf-equivalent)))


