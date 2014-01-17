(ns aspire.conf
  (:require [aspire.util :as a-util]
            [clojure.edn :as edn])
  (:import [java.io File]))

(def default-config-path "/etc/aspire")

(def config-associations {:jetty-instance [:conf-web]
                          :sql-db-pool [:conf-sql-db]})

(def default-configs {:conf-web "conf-web.edn"
                      :conf-sql-db "conf-sql-db.edn"})

(defn determine-path
  "Converts an option to a usable path. In other words, if the
  path is nil, it will return the default path. If the path
  is not nil, use it."
  [path]
  (if path
    path
    default-config-path))

(defn throw-file-missing
  "Gets called if a file is missing when it shouldn't be."
  [path]
  (throw (ex-info
           (format "File missing at (%s)" path)
           {:cause :file-missing :file-path path})))

(defn file-exists?
  "Does the a file at this path exist?"
  [path]
  (if (.isFile (File. path))
    true
    false))

(defn load-edn
  "Loads data from an .edn file. Throws an exception if it fails.
  Takes one argument; a path."
  [path]
    (if (file-exists? path)
      (edn/read-string (slurp path))
      (throw-file-missing path)))

(defn find-required-configs
  [sub-system-list]
  (flatten (vals (select-keys config-associations sub-system-list))))

(defn make-keyed-conf
  "Takes in a base config path and the config file name being loaded up, k.
  Returns a vector of k followed by the contents of the respective .edn file."
  [path k]
  (let [file-name (get default-configs k)]
    [k (load-edn (str path "/" file-name))]))

(defn load-configs
  ([path] (load-configs path (keys config-associations)))
  ([path sub-system-list]
   (let [required-configs (find-required-configs sub-system-list)
         handler-fn (partial make-keyed-conf (determine-path path))]
    (prn required-configs)
    (apply hash-map (flatten (map handler-fn required-configs))))))

