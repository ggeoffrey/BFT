(ns bft.utils-test
  (:use midje.sweet)
  (:require [bft.utils :refer :all]))


(defmacro apply-to-each
  [f & args]
  `(map (fn [item#] (apply ~f item#))
        ~@args))


(def args-bool-matrix [[false false false]
                       [false false true]
                       [false true  false]
                       [false true true]
                       [true  false  false]
                       [true  false true]
                       [true true  false]
                       [true  true  true]])

(def args-int-matrix [[0 0 0]
                      [0 0 1]
                      [0 1 0]
                      [0 1 1]
                      [1 0 0]
                      [1 0 1]
                      [1 1 0]
                      [1 1 1]])


(def result-for-land
  '(false false false false false false false true))

(def result-for-lor
  '(false true true true true true true true))

(def result-for-lxor
  '(false true true false true false false false))

(fact "Boolean functions"
  (fact "should behave according to boolean algebra"
    (fact "in classic form"
      (fact "for true booleans"
        (fact "with `land`"
          (apply-to-each land args-bool-matrix) => result-for-land)
        (fact "with `lor`"
          (apply-to-each lor args-bool-matrix) => result-for-lor)
        (fact "with `lxor`"
          (apply-to-each lxor args-bool-matrix) => result-for-lxor))
      (fact "for integers"
        (fact "with `land`"
          (apply-to-each land args-int-matrix) => result-for-land)
        (fact "with `lor`"
          (apply-to-each lor args-int-matrix) => result-for-lor)
        (fact "with `lxor`"
          (apply-to-each lxor args-int-matrix) => result-for-lxor)))
    
    (fact "in fancy form"
      (fact "for true booleans"
        (fact "with `Λ`"
          (apply-to-each Λ args-bool-matrix) => result-for-land)
        (fact "with `V`"
          (apply-to-each V args-bool-matrix) => result-for-lor)
        (fact "with `⊕`"
          (apply-to-each ⊕ args-bool-matrix) => result-for-lxor))
      (fact "for integers"
        (fact "with `Λ`"
          (apply-to-each Λ args-int-matrix) => result-for-land)
        (fact "with `V`"
          (apply-to-each V args-int-matrix) => result-for-lor)
        (fact "with `⊕`"
          (apply-to-each ⊕ args-int-matrix) => result-for-lxor)))))


(fact "Negation function"
  (fact "should behave according to boolean algebra"
    (fact "in classic form"
      (fact "for true booleans"
        (lnot true) => false
        (lnot false) => true)
      (fact "for integers"
        (lnot 1) => false
        (lnot 0) => true))
    (fact "in fancy form"
      (fact "for true booleans"
        (¬ true) => false
        (¬ false) => true)
      (fact "for integers"
        (¬ 1) => false
        (¬ 0) => true))))




(fact "The Hamming weight"
  (fact "should correspond to the amount of true values in a vector"
    (hamming [true true 0 1]) => 3))

(fact "The parity"
  (fact "should be the opposit of (mod _ 2)"
    (parity 16) => 0
    (parity -17) => 1))


