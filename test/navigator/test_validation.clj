(ns navigator.test-validation
  (:require [clojure.test :refer :all]
            [navigator.test-config :refer [validator]]))

(deftest user2comp-validation-test
  (testing "user2comp validation"
    (is (= (validator :user2comp {}) "Value does not match schema: {:sis-user-id missing-required-key, :comp missing-required-key, :start-date missing-required-key, :proposed-completion-date missing-required-key}"))))
