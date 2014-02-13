(ns navigator.web-test
  (:require [navigator.web :as a-web]
            [clojure.test :refer :all]
            [ring.mock.request :as rmr]
            [navigator.web-test-config :refer :all]))

;; web-test-config makes the mock ring requests to the resources
;; defined in web.clj. The resulting vars are available for testing here.
(use-fixtures :once web-test-config) 

#_(deftest resource-api-test
  (def keyed-headers
    (into {}
      (for [[k v] (:headers api-response)]
        [(keyword k) v])))
  (is (= (:status api-response) 200))
  (is (= (:Content-Type keyed-headers) "application/edn;charset=UTF-8" )))
 
;; Disabling this temporarily; see my comments in web_test_config. --moquist
#_(deftest resource-app-test
  (is (= (:status onboarding-response) 200)))

