(ns navigator.sqldb-ddl
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

;; We don't want execute!. Use korma.
;(defn execute!
;  "Execute the specified SQL with the interpolated args.
;
;   Example:
;   (execute! \"INSERT INTO tablename (field1, field2) VALUES ('%s', '%s')\" field1val field2val)
;   "
;  [db sql & args]
;  (let [sql (apply format sql args)]
;       (jdbc/db-do-commands db true sql)))

(comment
  (def db (edn/read-string (slurp (format "/home/%s/.navigator/conf-sql-db.edn" "username"))))
  )
