(ns aspire.cli
  (:require [clojure.tools.cli :as cli]
            [aspire.util :as a-util]))

(def cli-options
  [["-p" "--config-path" "Specify a non-default config directory"]
   ["-h" "--help" "Print this help message" :flag true]
   ["-i" "--init-sql"
    "Initialize the SQL DB, and then exit"
    :flag true]
   ["-z" "--zero-out-sql-db"
    "Print out SQL to drop the Aspire tables from the DB, and then exit"
    :flag true]
   ["-v" "--verbose" "Print info to stdout while running" :flag true]])

(defn get-opts
  "Get and process all the CLI arguments, handle --help and --verbose. Return ONLY the options."
  [args]
  (let [[opts args usage] (apply cli/cli args cli-options)]
    (when (:help opts)
      (println usage)
      (System/exit 0)) 
    opts))
