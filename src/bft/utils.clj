(ns bft.utils
  "Define some common utils and aliases for math symbols
  like Λ, V and ¬."
  (:refer-clojure :exclude [boolean]))

(defn boolean
  "Transform a value or a list of value to a boolean
  equivalent or a list of equivalent booleans.
  i.e. (1 0 (and 0 0)) -> (true false (and false true)"
  ([coll]
   (cond
       (not (seq? coll)) (case coll
                           0 false
                           1 true
                           coll)
       (empty? coll) nil
       :else (map boolean coll)))
  ([form & forms]
   (map boolean (cons form forms))))


(defn land
  "Logical and"
  ([] nil)
  ([x] x)
  ([x & xs] (and (boolean x) (apply land (boolean xs)))))

(defn lor
  "Logical or"
  ([x] x)
  ([x & xs] (or (boolean x) (apply lor (boolean xs)))))





(def lnot (comp not boolean))

(def Λ land)
(def V lor)
(def ¬ lnot)




;; ------------------------------------------------

(defn hamming
  "Compute the Hamming weight of a vector.
  The Hamming weight is the amount of 1 in this vector.
  Perfs: not ideal but at least O(n) as map & filter
         generate lazy sequences."
  [v]
  (->> (map boolean v)
       (filter true?)
       (count)))


(defn parity
  "Give the parity of the given integer.
  In verse of (mod _ 2)"
  [i]
  (cond 
    (even? i) 0
    :else     1))


;; -----------------

(defn lxor
  "Logical eXclusive or"
  ([] nil)
  ([& args]
   (= 1 (hamming args))))

(def ⊕ lxor)
(def ⊗ ⊕)





;; -----------------


(def temp1 '(land (lor (not x) (not y)) (not z)))
(def temp2 '(lor (land x (not y)) (lor z (lor (not w)))))


(defn unwrap
  "Replace all (x) by x in a form"
  [form]
  (cond
    :else (loop [form form]
            (cond
              (not (seq? form)) form
              (and (= 1 (count form))
                   (not (seq? (first form)))) (first form)
              (not (some seq? form)) form
              :else (recur (first form))))))

(defn unwrap-all
  "Unwrap all elements in a form"
  [form]
  (println form)
  (cond
    (not (seq? form)) form
    (not (some seq? form)) (unwrap form)
    (= 1 (count form)) (recur (first form))
    :else (map unwrap-all form)))


(defn negation?
  "State if a form is a negation"
  [form]
  (let [negation #{'not '¬}]
    (cond
      (not (seq? form)) false
      (contains? negation (first form)) true
      :else false)))

(defn double-negation?
  "State if a form is a double negation"
  [form]
  (and (negation? form)
       (negation? (first (rest form)))))


(defn simplify-not
  "Reduce double negation of a value to the value itself"
  [form]
  (cond
    (not (seq? form)) form
    ;; is this a double negation? recur 2 levels deeper
    (double-negation? form) (recur (rest (first (rest form))))
    ;; is this a list of list? flatten one level
    (seq? (first form)) (recur (first form))
    ;; return what we found
    :else form))


;; (->> '(lor (lor (lor (land x  (not y) y y (lor y y y y y y y)))))
;;      (simplify-logical)
;;      (simplify-not))

(defn simplify-logical
  "Symplify V(x,x) => x and equivalents"
  [form]
  (let [logical #{'lor 'land 'Λ 'V}]
    (cond
      (not (seq? form)) form  ;; not a form? just return it
      (contains? logical      ;; start with a logical operator?
                 (first form)) (cond
                                 ;; and has only one item in it. drop first
                                 (= 2 (count form)) (recur (rest form))
                                 ;; has only the same argument
                                 (= 1 (count
                                       (distinct
                                        (rest form)))) (first (rest form))
                                 ;; else keep the first and drop duplicates
                                 ;; amongs arguments
                                 :else
                                 (cons (first form) (->> (rest form)
                                                         (map simplify-logical)
                                                         (distinct))))
      ;; is it a list of list? then recur in it and flatten the result
      (seq? (first form)) (apply simplify-logical form)
      ;; is the arguments iterables?
      (and (seq? (rest form))
           ;; and not empty? /!\ Then recur on it keeping the first
           (not (empty? (rest form)))) (cons (first form)
                                             (simplify-logical (rest form)))
      ;; else don't touch it
      :else form)))


(defn simplify 
  "Take a form and symplifiy it according to De Morgan laws"
  [form]
  (let [simplified (->> form
                        (simplify-logical)
                        (simplify-not))]
    (cond
      (= 1 (count simplified)) (first simplified)
      :else simplified)))

(defn de-morgan 
  "Apply the De Morgan theorem to a given form"
  [form]
  (cond
    (seq? form) (cond
                  (negation? form) (rest form)
                  :else (map de-morgan form))
    :else (cond
            (= 'land form) 'lor
            (= 'lor form) 'land
            :else (list 'not  form))
    ))


(simplify
 (de-morgan '(lor (land (not y) z) x))
)
