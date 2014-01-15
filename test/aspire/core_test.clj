(ns aspire.core-test
  (:require [clojure.test :refer :all]
            [aspire.core :refer :all]))

(deftest pass-test
  (testing "I pass"
    (is (= 1 1))))
