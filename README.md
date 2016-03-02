## Boolean Functions Translator

> “Fairies have to be one thing or the other,  
> because being so small they unfortunately have room for one feeling only at a time.   
> They are, however, allowed to change, only it must be a complete change.”   
>  ― J.M. Barrie, Peter Pan  

So let's talk the fairy way!  
Made with the almighty [Clojure](http://clojure.org/), the red pill that takes you down the rabbit hole.  


###  Abstract

A Clojure library designed to translate boolean functions back and forth different normal forms. **The generated code is valid and dynamically executable Clojure code.**

At the moment, can transform:
- From truth table
  - to CNF (**C**onjunctive **N**ormal **F**orm)
  - to DNF (**D**isjunctive **NF**)
  - to PNF (**P**olynomial **NF** or Algebraic **NF**)


**Comming soon**:
- From truth table
  - to MNF (**M**edian **NF**)
- From one form to another one
- From a form to a boolean lattice, displayed as a [force-directed graph](http://bl.ocks.org/mbostock/1062288) using [d3.layout.force3D](https://github.com/ggeoffrey/d3.layout.force3D)
  

## Usage

Keep in mind this is a work in progress ;)

```clojure
;; First, declare our namespace and import everything we need

(ns your-namespace
   (:require [bft.nf :refer [table->nf]]
             [bft.utils :refer [land lor lnot lxor, Λ V ¬ ⊕]])


;;  Define a truth table
(def my-table [ '[x y z]
               [[[0 0 0] 0]
                [[0 0 1] 1]
                [[0 1 0] 1]
                [[0 1 1] 0]
                [[1 0 0] 1]
                [[1 0 1] 0]
                [[1 1 0] 1]
                [[1 1 1] 0]]])

;; Then transform it !

(let [[litterals rows] my-table]
  (table->nf litterals rows :cnf))  ;; to CNF
  
;; => (land (lor x y z) (lor x (lnot y) (lnot z)) (lor (lnot x) y (lnot z)) (lor (lnot x) (lnot y) (lnot z)))

(let [[litterals rows] my-table]
  (table->nf litterals rows :dnf))  ;; to DNF

;; => (lor (land (lnot x) (lnot y) z) (land (lnot x) y (lnot z)) (land x (lnot y) (lnot z)) (land x y (lnot z)))
  
```

If you want “real“ maths, you can use `:fancy` as trailing parameter and it will produce output with `Λ`, `V`, `¬` and `⊕`. These symbols behave exactly like `land`, `lor`, `lnot` and `lxor` -they are aliases-. If you can type them directly with your keybord -Dvorak, Bépo- do not hesitate, it's really easier to read. Remember `Λ`, `V`, `¬` and `⊕` are valid functions: `(¬ false) => true`. 

```clojure

(let [[litterals rows] my-table]
  (table->nf litterals rows :dnf :fancy))  ;; to fancy DNF

;; => (V (Λ (¬ x) (¬ y) z) (Λ (¬ x) y (¬ z)) (Λ x (¬ y) (¬ z)) (Λ x y (¬ z)))
  
```
**Run it!**
```clojure
(def my-lambda   ;; it's just a copy-past of the generated DNF form.
  (λ [x y z]
    (V 
     (Λ (¬ x) (¬ y) z)
     (Λ (¬ x) y (¬ z))
     (Λ x (¬ y) (¬ z))
     (Λ x y (¬ z)))))

;; call it
(my-lambda 1 1 0) ;; => true
  
```

#### Q & A
**Q**: Why using `land`, `lor` and `lnot` rather than the classicals `and`, `or` and `not`?  
**A**: Because in Clojure numbers are java.lang.Long instances. So 0 is not falsey -it's an object-. Demo: 
`(boolean 0) => true`, `(boolean false) => false`. So I introduced `land`, `lor` and `lnot` that works the same way on `true`, `false`, `0` and `1`. In case of unexpected input, it will produce `nil`, allowing you to find where the problem is.
  
**Q**: What are `Λ`, `V`, `¬` and `⊕`? How can I type them on my keyboard?  
**A**: They are respectively the same as `land`, `lor`, `lnot` and `lxor`. They are true aliases. I added them because they are way more readable -their shape has a direct meaning, they are not words, they are symbols-. They allow production of a “more mathematical“ form. I can type most of them directly on my Bépo keyboard -french dvorak layout-. They are accessible with:

| Symbol | Shortcut     | AKA         |
|--------|--------------|-------------|
| Λ      | Alt-gr+g L   | Uppercase λ |
| V      | Maj+v        | Uppercase v |
| ¬      | Alt-gr+Maj+7 |             |
| ⊕      | ∅            |             |

I have no idea for other layouts. But don't waste time on this, just use `land`, `lor`, `lnot` and `lxor`.


#### Why Clojure? 

Because it's a functionnal, [homoiconic](https://en.wikipedia.org/wiki/Homoiconicity) programmable programming language. Allowing a program to create programs, and a function to create and manipulate functions' code. As we are transforming boolean functions, is there a better suited language than a Lisp? Are you able to write, parse, and manipulate the AST at runtime with a classic imperative language? Think about it.

#### License

Copyright © 2016 - Will eventually change -

Distributed under the Eclipse Public License either version 1.0 or any later version.


#### Bibliography
> [1] Miguel Couceiro, Stephan Foldes, and Erkko Lehtonen. Composition of
post classes and normal forms of boolean functions. Discrete mathematics, 306(24) :3223–3243, 2006.

> [2] Miguel Couceiro, Erkko Lehtonen, Jean-Luc Marichal, and Tamás Waldhauser. An algorithm for producing median formulas for boolean functions. In Reed Muller 2011 Workshop, pages 49–54, 2011.

> [3] Yves Crama and Peter L Hammer. Boolean functions : Theory, algorithms, and applications. Cambridge University Press, 2011.

> [4] Martin LENSCHAT, Pierre MERCURIALI and Stéphane TIV : Formes normales en logique propositionnelle. 2015. Université de Lorraine & [Loria](http://www.loria.fr/loria-news?set_language=en). [PDF](http://mathinfo.univ-lorraine.fr/sites/mathinfo.univ-lorraine.fr/files/users/documents/SCA/projtut/2014-2015/rapports/m1sca_rapportprojettut_lentschat_mercuriali_tiv.pdf) (French).

