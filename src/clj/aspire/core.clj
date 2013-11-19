(ns aspire.core
  (:require [aspire.conf :as a-conf]
            [aspire.cli :as a-cli]
            [aspire.util :as a-util]
            [aspire.sqldb-ddl :as a-sqldb-ddl]
            [aspire.sqldb :as a-sqldb]
            [aspire.web :as a-web]))

(defn aspire [{:keys [conf-sql-db] :as conf}]
  (println :aspire "Do it. Do it now." (dissoc conf-sql-db :password))
  (a-sqldb/default-connection! conf-sql-db)
  (println (a-sqldb/select! {:select [:*] :from [:comp] :limit 2}))
  (a-web/run))

(defn -main [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (let [opts (a-cli/get-opts args)
        conf (a-conf/getconf a-conf/configs opts)
        db (:conf-sql-db conf)]
    (a-util/output! :opts opts :conf conf :db db)

    (cond
     (:init-sql opts) (a-sqldb-ddl/init! db) 
     (:zero-out-sql-db opts) (a-sqldb-ddl/print-drop-sql!) 
     :else (aspire conf))))

(comment

  (-main "--conf-sql-db" (format "%s/.aspire/conf-sql-db.edn" (System/getProperty "user.home")))

  )

