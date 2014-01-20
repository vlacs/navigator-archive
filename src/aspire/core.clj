(ns aspire.core
  (:require [aspire.conf :as a-conf]
            [aspire.cli :as a-cli]
            [aspire.util :as a-util]
            [aspire.sqldb-ddl :as a-sqldb-ddl]
            [aspire.sqldb :as a-sqldb]
            [aspire.web :as a-web]))

(def jetty-args
  [:port :join?])

(defn system
  "See http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded"
  [conf]
  {:conf conf})

(defn start-database!
  [system]
  (assoc system :sql-db-pool
         (a-sqldb/default-connection! (get-in system [:conf :conf-sql-db]))))

(defn stop-database!
  [system]
  (.close (:datasource @(:sql-db-pool system)))
  (dissoc system :sql-db-pool))

(defn start-http!
  [system]
  (let [args (select-keys (get-in system [:conf :conf-web]) jetty-args)]
    (assoc system :jetty-instance (a-web/run! args))))

(defn stop-http!
  [system]
  (.stop (:jetty-instance system))
  (dissoc system :jetty-instance))

;; This defines how the system should be started and stopped.
(def all-sub-systems
  {:jetty-instance {:startup start-http!
                    :shutdown stop-http!}
   :sql-db-pool {:startup start-database!
                 :shutdown stop-database!}})

;; This defines the order in which the system is started up.
(def startup-order
  [:sql-db-pool :jetty-instance])

;; This defines the order in which the system is shut down.
(def shutdown-order (into [] (reverse startup-order)))

(defn process-single-interaction!
  "Causes side-effects, such as stopping or starting the HTTP server
  or connecting or disconnecting from a database. Whatever task gets
  called has full access to the global state of the system (third arg
  passed becomes the first and only arg passed to the task fn.)"
  [sub-sys task system]
  (a-util/output! true (str "Interactions with [" sub-sys "] executing [" task "]"))
  ((get-in all-sub-systems [sub-sys task]) system))

(defn process-ordered-interaction!
  "Does the same task to several sub systems in the order that they're
  provided. Uses sub-system task map for calling task fns."
  [ordered-sub-systems task system]
  (loop [remaining-sub-systems ordered-sub-systems
         system-state system]
    (if (empty? remaining-sub-systems)
      system-state
      (let [sub-sys (first remaining-sub-systems)]
        (recur (rest remaining-sub-systems)
               (process-single-interaction! sub-sys task system-state))))))

(defn start!
  "Performs side effects to initialize the system, acquire resources,
  and start it running. Returns an updated instance of the system."
  ([system] 
   (process-ordered-interaction! startup-order :startup system))
  ([system sub-sys]
   (process-single-interaction! sub-sys :startup system)))

(defn stop!
  "Performs side effects to shut down the system and release its
  resources. Returns an updated instance of the system."
  ([system]
   (process-ordered-interaction! shutdown-order :shutdown system))
  ([system sub-sys]
   (process-single-interaction! sub-sys :shutdown system)))

(defn opts-and-conf-from-args
  [args]
  (let [opts (a-cli/get-opts args)]
    [opts
     (a-conf/load-configs (:config-path opts))]))

(defn -main [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))

  (let [opts (a-cli/get-opts args)
        conf (a-conf/load-configs (:config-path opts))
        db (:conf-sql-db conf)
        system (system conf)]
    (a-util/output! (:verbose opts) :opts opts :system system :conf conf :db db)

    (cond
      (:init-sql opts) (a-sqldb-ddl/init! db) 
      (:zero-out-sql-db opts) (a-sqldb-ddl/print-drop-sql!) 
      :else (start! system))))

