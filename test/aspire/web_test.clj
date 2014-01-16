(ns aspire.web-test
  (:use aspire.web
        clojure.test
        ring.mock.request
        aspire.web-test-config))

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
 
(deftest resource-app-test
  (is (= (:status app-response) 200)))

