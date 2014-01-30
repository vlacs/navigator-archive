(ns aspire.web-test-config
  (:require [aspire.web :as a-web]
            [clojure.test :refer :all]
            [ring.mock.request :as rmr]))

(defn set-up-test []
  ;; this is where we would also initialize and populate test db

  ;; the following vars containing the various responses are available to the
  ;; tests defined in web_test.clj
  (def onboarding-response (a-web/onboarding (rmr/request :get "/"))))
  
(defn teardown-test []
  (comment
    ;; this is where we would dismantle the test db
    (println "tearing down test ...")))

(defn web-test-config
  [web-tests]
  (set-up-test)
  (web-tests)
  (try
    (teardown-test)
    (finally (println "web test complete"))))


