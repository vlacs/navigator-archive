(ns aspire.core
  (:require [jdbc-pg-init.core :as jpi]
            [clojure.edn :as edn]))

(def schema (edn/read-string (slurp "schema.edn")))

(comment
  (use '[clojure.tools.namespace.repl :only (refresh)])
  (def db (edn/read-string (slurp "sample-config.edn")))
  (def db (assoc db :password "" :subname "//kirk.office.vlacs.org/moodletest2_vlacs_org"))
  (jpi/init! db schema)
  )
