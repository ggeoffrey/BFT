## Boolean Functions Translator

> “Fairies have to be one thing or the other,  
> because being so small they unfortunately have room for one feeling only at a time.   
> They are, however, allowed to change, only it must be a complete change.”   
>  ― J.M. Barrie, Peter Pan  

So let's talk the fairy way!  
Made with the almighty [Clojure](http://clojure.org/), the red pill that takes you down the rabbit hole.  


###  Abtract

A Clojure library designed to translate boolean functions back and forth different normal forms. **The generated code is valid and dynamically executable Clojure code.**

At the moment, can transform:
- From truth table
  - to CNF (**C**onjunctive **N**ormal **F**orm)
  - to DNF (**D**isjunctive NF)


**Comming soon**:
- From truth table
  - to PNF (**P**olynomial NF or Algebraic NF)
  - to MNF (**M**edian NF)
- From one form to another one
- From a form to a boolean lattice, displayed as a [force-directed graph](http://bl.ocks.org/mbostock/1062288) using [d3.layout.force3D](https://github.com/ggeoffrey/d3.layout.force3D)
  

## Usage

This is a work in progress, coming soon ;)

#### Why Clojure? 

Because it's a functionnal, [homoiconic](https://en.wikipedia.org/wiki/Homoiconicity) programmable programming language. Allowing a program to create programs, and a function to create and manipulate functions' code. As we are transforming boolean functions, is there a better suited language than a Lisp? Are you able to write, parse, and manipulate the AST at runtime with a classic imperative language? Think about it.

#### License

Copyright © 2016 - Will eventually change -

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.


#### Bibliography
> [1] Miguel Couceiro, Stephan Foldes, and Erkko Lehtonen. Composition of
post classes and normal forms of boolean functions. Discrete mathematics, 306(24) :3223–3243, 2006.

> [2] Miguel Couceiro, Erkko Lehtonen, Jean-Luc Marichal, and Tamás Waldhauser. An algorithm for producing median formulas for boolean functions. In Reed Muller 2011 Workshop, pages 49–54, 2011.

> [3] Yves Crama and Peter L Hammer. Boolean functions : Theory, algorithms, and applications. Cambridge University Press, 2011.

> [4] Martin LENSCHAT, Pierre MERCURIALI and Stéphane TIV : Formes normales en logique propositionnelle. 2015. Université de Lorraine & [Loria](http://www.loria.fr/loria-news?set_language=en). [PDF](http://mathinfo.univ-lorraine.fr/sites/mathinfo.univ-lorraine.fr/files/users/documents/SCA/projtut/2014-2015/rapports/m1sca_rapportprojettut_lentschat_mercuriali_tiv.pdf) (French).

