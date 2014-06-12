(ns user
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]
            [clojure.test :as test]
            [datomic.api :as d]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [navigator]
            [navigator.test-config :as nt-config]
            [clojure.edn :as edn]))

;; N.B.: (ns cljs.user (:use [clojure.zip :only [insert-child]])) (see http://stackoverflow.com/questions/12879027/cannot-use-in-clojurescript-repl)

(defn go
  "Start with Datomic."
  []
  (nt-config/start!))

(defn go-jetty
  "Start with Datomic and Jetty."
  []
  (nt-config/start! :jetty))

(defn reset
  ([] (reset 'user/go))
  ([go-fn-symbol]
     (nt-config/stop!)
     (refresh :after go-fn-symbol)))

(defn reset-jetty
  "Convenience fn if you want to reset and have Jetty."
  []
  (reset 'user/go-jetty))

(defn touch-that
  "Execute the specified query on the current DB and return the
   results of touching each entity.

   The first binding must be to the entity.
   All other bindings are ignored."
  [query & data-sources]
  (map #(d/touch
         (d/entity
          (d/db (:db-conn nt-config/system))
          (first %)))
       (apply d/q query (d/db (:db-conn nt-config/system)) data-sources)))

(defn ptouch-that
  "Example: (ptouch-that '[:find ?e :where [?e :user/username]])"
  [query & data-sources]
  (pprint (apply touch-that query data-sources)))

(comment

  ;; TODO: put working examples here
  (navigator/create-competency (:db-conn nt-config/system) "I will keep typing" "v1" :comp.status/active :comp/id-sk "jeep")
  (ptouch-that '[:find ?e :where [?e :comp/name]])


  ;; Utility fns
  ;; -----------------------
  (defn html->hiccup [html]
    (-> html
        hickory/parse-fragment
        (->> (map hickory/as-hiccup))))

  (defn renode
    "Take a rendered enlive template (html) and turn it back into a seq of enlive nodes"
    [template]
    (en/html-snippet (n-tpl/render template))))
