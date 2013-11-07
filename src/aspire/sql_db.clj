(ns aspire.sql-db
  (:require [jdbc-pg-init.core :as jpi]
            [clojure.edn :as edn]))

(def schema (edn/read-string (slurp "schema.edn")))

(defn init! [db]
  (jpi/init! db schema))

(defn print-drop-sql! []
  (doall (for [table (keys (schema :tables))]
           (println (format "DROP TABLE %s CASCADE;"
                            (name table)))))
  nil)
