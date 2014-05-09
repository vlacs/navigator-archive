(ns navigator.test-validation
  (:require [clojure.test :refer :all]
            [navigator.validation :refer :all]))

(deftest valid?-test
  (testing "valid?"
    (is (= (valid? (validate-task-in {}) false)))))

(deftest validate-task-in-test
  (testing "validate-task-in"
    (is (= (validate-task-in {}) '{:task/description ["must be present"], :task/version ["must be present"], :task/name ["must be present"], :task/id-sk-origin ["must be present"], :task/id-sk ["must be present"]}))))

(deftest validate-comp-in-test
  (testing "validate-comp-in"
    (is (= (validate-comp-in {}) '{:comp/tags ["must be present"], :comp/status ["must be present"], :comp/version ["must be present"], :comp/description ["must be present"], :comp/name ["must be present"], :comp/id-sk ["must be present"]}))))

(deftest validate-comp-tag-in-test
  (testing "validate-comp-tag-in"
    (is (= (validate-comp-tag-in {}) '{:comp-tag/child-of ["must be present"], :comp-tag/disp-ctxs ["must be present"], :comp-tag/status ["must be present"], :comp-tag/icon ["must be a url"], :comp-tag/type ["must be present"], :comp-tag/version ["must be present"], :comp-tag/description ["must be present"], :comp-tag/name ["must be present"]}))))

(deftest validate-perf-asmt-in-test
  (testing "validate-perf-asmt-in"
    (is (= (validate-perf-asmt-in {}) '{:perf-asmt/comps ["must be present"], :perf-asmt/type ["must be present"], :perf-asmt/version ["must be present"], :perf-asmt/name ["must be present"], :perf-asmt/id-sk-origin ["must be present"], :perf-asmt/id-sk ["must be present"]}))))

(deftest validate-user2comp-in-test
  (testing "validate-user2comp-in"
    (is (= (validate-user2comp-in {}) '{:user2comp/score-type ["must be present"], :user2comp/final-score ["must be present"], :user2comp/current-score ["must be present"], :user2comp/proposed-completion-date ["must be present"], :user2comp/start-date ["must be present"], :user2comp/comp ["must be present"], :user2comp/sis-user-id ["must be present"]}))))

(deftest validate-user2perf-asmt-in-test
  (testing "validate-user2perf-asmt-in"
    (is (= (validate-user2perf-asmt-in {}) '{:user2perf-asmt/perf-asmt ["must be present"], :user2perf-asmt/user ["must be present"]}))))
