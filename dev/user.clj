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
            [datomic-schematode.core :as schematode]
            [clojure.edn :as edn]
            ))

;; N.B.: (ns cljs.user (:use [clojure.zip :only [insert-child]])) (see http://stackoverflow.com/questions/12879027/cannot-use-in-clojurescript-repl)

(defn go
  "Initializes the current development system and starts it running."
  []
  (nt-config/start!))

(defn reset []
  (nt-config/stop!)
  (refresh :after 'user/go))

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

