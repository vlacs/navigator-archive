(ns navigator.core-test
  (:require [clojure.test :refer :all]
            [navigator.core :refer :all]))

(deftest pass-test
  (testing "I pass"
    (is (= 1 1))))
