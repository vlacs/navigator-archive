(ns aspire.core
  (:require [aspire.conf :as a-conf]
            [aspire.cli :as a-cli]
            [aspire.util :as a-util]
            [aspire.sql-db :as a-sql-db])
  (:gen-class))

(defn aspire [conf]
  (println :aspire "Do it. Do it now."))

(defn -main [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (let [opts (a-cli/get-opts args)
        conf (a-conf/getconf a-conf/configs opts)
        db (:conf-sql-db conf)]
    (a-util/output! :opts opts :conf conf :db db)

    (cond
     (:init-sql opts) (a-sql-db/init! db) 
     (:zero-out opts) (a-sql-db/print-drop-sql!) 
     :else (aspire conf))))



