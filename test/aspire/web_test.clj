(ns aspire.web-test
  (:use aspire.web
        clojure.test
        ring.mock.request))

(def api-response (resource-api (request :get "/api")))

(def app-response (resource-app (request :get "/")))

(def comps-response (resource-app (request :get "/comps")))

(deftest resource-api-test
  (def keyed-headers
    (into {}
      (for [[k v] (:headers api-response)]
        [(keyword k) v])))
  (is (= (:status api-response) 200))
  (is (= (:Content-Type keyed-headers) "application/edn;charset=UTF-8" )))
 
(deftest resource-app-test
  (is (= (:status app-response) 200)))

(deftest resource-comps-test
  (is (= (:status comps-response) 200)))
