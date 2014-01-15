(ns aspire.web-test-config
  (:use aspire.web
        clojure.test
        ring.mock.request))

(defn setup-test []

  ;; this is where we would also initialize and populate test db

  ;; the following vars containing the various responses are available to the
  ;; tests defined in web_test.clj
  (def api-response (resource-api (request :get "/api")))

  (def app-response (resource-app (request :get "/")))

  (def comps-response (resource-app (request :get "/comps"))))
  
(defn teardown-test []
  (comment
    ;; this is where we would dismantle the test db
    (println "tearing down test ...")))

(defn web-test-config
  [web-tests]
  (setup-test)
  (web-tests)
  (try
    (teardown-test)
    (finally (println "web test complete"))))


