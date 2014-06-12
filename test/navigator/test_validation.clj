(ns navigator.test-validation
  (:require [clojure.test :refer :all]
            [navigator.validation :refer :all]))

(deftest task-validation-test
  (testing "task validation"
    (is (= (validator :task {}) "Value does not match schema: {:description missing-required-key, :version missing-required-key, :name missing-required-key, :id-sk-origin missing-required-key, :id-sk missing-required-key}"))))

(deftest perf-asmt-validation-test
  (testing "perf-asmt validation"
    (is (= (validator :perf-asmt {}) "Value does not match schema: {:version missing-required-key, :comps missing-required-key, :credit-value-numerator missing-required-key, :id-sk missing-required-key, :type missing-required-key, :duration-rating-days missing-required-key, :credit-value-denominator missing-required-key, :id-sk-origin missing-required-key}"))))

(deftest user2comp-validation-test
  (testing "user2comp validation"
    (is (= (validator :user2comp {}) "Value does not match schema: {:sis-user-id missing-required-key, :comp missing-required-key, :start-date missing-required-key, :proposed-completion-date missing-required-key}"))))

(deftest user2perf-asmt-validation-test
  (testing "user2perf-asmt validation"
    (is (= (validator :user2perf-asmt {}) "Value does not match schema: {:grade missing-required-key, :task missing-required-key, :user missing-required-key}"))))
