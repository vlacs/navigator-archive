(ns aspire.core
  (:require [aspire.conf :as a-conf]
            [aspire.cli :as a-cli]
            [aspire.util :as a-util]
            [aspire.sqldb-ddl :as a-sqldb-ddl]
            [aspire.sqldb :as a-sqldb]
            [aspire.web :as a-web]))

(defn system
  "See http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded"
  [conf]
  {:conf conf})

(defn start!
  "Performs side effects to initialize the system, acquire resources,
  and start it running. Returns an updated instance of the system."
  [system]
  (assoc system
    :sql-db-pool
    (a-sqldb/default-connection! (get-in system [:conf :conf-sql-db]))
    :jetty-instance
    (a-web/run! (get-in system [:conf :conf-web]))))

(defn stop!
  "Performs side effects to shut down the system and release its
  resources. Returns an updated instance of the system."
  [system]
  (.close (:datasource @(:sql-db-pool system)))
  (.stop (:jetty-instance system))
  (dissoc system :sql-db-pool :jetty-instance))

#_(defn aspire [{:keys [conf-sql-db] :as conf}]
  (println :aspire "Do it. Do it now." (dissoc conf-sql-db :password))
  (println (a-sqldb/select! {:select [:*] :from [:comp] :limit 2}))
  (a-web/run!))

(defn get-conf! [args]
  (let [opts (a-cli/get-opts args)
        conf (a-conf/getconf a-conf/configs opts)]
    [opts conf]))

(defn -main [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))

  (let [[opts conf] (get-conf! args)
        db (:conf-sql-db conf)
        system (system conf)]
    (a-util/output! (:verbose opts) :opts opts :system system :conf conf :db db)

    (cond
     (:init-sql opts) (a-sqldb-ddl/init! db) 
     (:zero-out-sql-db opts) (a-sqldb-ddl/print-drop-sql!) 
     :else (start! system))))

(comment

  (def home (System/getProperty "user.home"))
  (def mydir (format "%s/.aspire/" home))
  (def args ["--verbose"
             "--conf-sql-db" (format "%s/conf-sql-db.edn" mydir)
             "--conf-web" (format "%s/conf-web.edn" mydir)])
  (def system (apply -main args))

  )

