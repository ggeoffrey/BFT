(ns bft.nf
  "DNF & CNF.")


(def *translators*
  "This map define translators functions with 
  their symbols and parameters."
  (atom {}))

(def classic-translators
  {:dnf '[:conjunction or not 1]
   :cnf '[:disjunction and not 0]})


(def fancy-translators
  {:dnf '[:conjunction  V ¬ 1]
   :cnf '[:disjunction Λ ¬ 0]})



(defn use-symbols!
   "Allow output to be written with math symbols
  like Λ, V, ¬ or with classic ones like and, or and not."
   [type]
   (case type
     :fancy (reset! *translators* fancy-translators)
     (reset! *translators* classic-translators))
   nil)


;; DEFAULT

(use-symbols! :fancy)



;; ---------------------------------------------


(defn- transform-to
  "Transform a line to a conjunction or disjunction.
   i.e.: (transform-to <line> :disjunction)"
  [line type]
  (let [[names values] line
        [_ operator negation] (get @*translators* (case type
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


;; ------------------------------------------------



(defn table->nf
  "Transform a truth table to a normal form
   ex:  (table->dnf '[x y z] 
                   [[[0 0 1] 1]
                    [[1 0 0] 0]
                    [[0 1 0] 1]]
                    :dnf"
  [names table form]
  (let [[translator factor _ result-to-focus-on] (get @*translators* form)]
    (->> table   ;; take the table
         (filter (fn [line] (= (last line) result-to-focus-on ))) ;; keep only desired lines
         (map (fn [line] (cons names line)))  ;; add names to it
         (map (fn [line] (transform-to line translator))) ;; convert each line to NF
         (cons factor)
         )))  ;; factorize by Λ or V
