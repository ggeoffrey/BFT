(ns bft.nf.bnf-cnf
  "Everything to transform to BNF and CNF")


(defn transform-to
  "Transform a line to a conjunction or disjunction.
   i.e.: (transform-to <line> :disjunction)"
  [line type translators]
  (let [[names values] line
        [_ operator negation] (translators (case type
                                             :disjunction :dnf
                                             :conjunction :cnf))]
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
                                                               :conjunction   (list negation litteral)
                                                               :disjunction  litteral
                                                               nil)
                                                             result))
                        1 (recur (rest names) (rest values) (cons
                                                             (case type
                                                               :conjunction  litteral
                                                               :disjunction (list negation litteral))
                                                             result))
                        nil))))))))
