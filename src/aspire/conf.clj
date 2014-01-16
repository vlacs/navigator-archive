(ns aspire.conf
  (:require [aspire.util :as a-util]
            [clojure.edn :as edn])
  (:import [java.io File]))

(def confdir "/etc/aspire")

;; The --long-opt for each conf file must match the :conf-key.
(def configs {:conf-sql-db
              {:file "conf-sql-db.edn"
               :opts ["-s" "--conf-sql-db"]}
              :conf-web
              {:file "conf-web.edn"
               :opts ["-w" "--conf-web"]}})

(defn confpath-default [configs conf-key]
  (str confdir "/" (:file (configs conf-key))))

(defn conf-opts
  "Pull out the clojure.tools.cli friendly opts for CLI parsing."
  [configs]
  (for [conf-key (keys configs)]
    (concat
     (:opts (configs conf-key))
     ["Specify an alternate config file (.edn format)"
      :default (confpath-default configs conf-key)])))

(defn ensureconf
  "Take in a config-key and an optional location override from the command line.
   Throws an exception if the conf file does not exist.
   Return the path to the conf file."
  [configs conf-key override-path]
  (let [default (confpath-default configs conf-key)
        path (or override-path default)]
    (if (.isFile (File. path))
      path
      (throw (ex-info
              (format "conf file missing (%s, %s)" conf-key path)
              {:cause :conf-missing :conf-key conf-key})))))

(defn getconfs
  "Load the specified configuration files as found under /etc, or with
   paths overridden by the CLI opts."
  [configs opts]
  (for [conf-key (keys configs)
        :let [confpath (ensureconf configs conf-key (opts conf-key))]]
    (do
      (a-util/output! :getconfs conf-key confpath)
      [conf-key (edn/read-string (slurp confpath))])))

(defn getconf
  "Return unified map of conf data including the specified
   configuration files, with optional overrides from CLI opts."
  [configs opts]
  (apply hash-map (flatten (getconfs configs opts))))
