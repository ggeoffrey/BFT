(ns bft.core-test
  (:use midje.sweet)
  (:require [bft.core :refer :all]))


(def table [ '[x y z]
            [[[0 0 0] 0]
             [[0 0 1] 1]
             [[0 1 0] 1]
             [[0 1 1] 0]
             [[1 0 0] 1]
             [[1 0 1] 0]
             [[1 1 0] 1]
             [[1 1 1] 0]]])

(def bad-table [ '[x y z]     
                [[[0 0 0] 0]  
                 [[0 0 ]  1]  
                 [[0 1 0] 1]  
                 [[0 1 1] 11]  
                 [[1 0 0] 1]  
                 [[1 0 1]  ]  
                 [[1 1 0] 1]  
                 [[1 1 1] 0]]])


(fact "About `table format verification`"
  (fact "a GOOD table should pass verification successfully"
    (apply check-table-format table) => nil)
  (fact "for a BAD table"
    (fact "verifiction function should return bad lines"
      (apply check-table-format bad-table) => seq?)
    (fact "returned lines must be the bad ones"
      (let [[_ table ] bad-table
            bad-lines (list (get table 1)
                            (get table 3)
                            (get table 5))]
        (apply check-table-format bad-table) => bad-lines))))





