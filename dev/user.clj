(ns user
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [aspire.core :as aspire.core]))

(def system nil)

(defn init! []
  (let [home (System/getProperty "user.home")
        mydir (format "%s/.aspire/" home)
        args ["--verbose"
              "--conf-sql-db" (format "%s/conf-sql-db.edn" mydir)
              "--conf-web" (format "%s/conf-web.edn" mydir)]
        [opts conf] (aspire.core/get-conf! args)]
    (alter-var-root #'system (constantly (aspire.core/system conf)))
    system))

(defn start!
  "Starts the current development system."
  []
  (alter-var-root #'system aspire.core/start!))

(defn stop!
  "Shuts down and destroys the current development system."
  []
  (alter-var-root #'system
                  (fn [s] (when s (aspire.core/stop! s)))))

(defn go
  "Initializes the current development system and starts it running."
  []
  (init!)
  (start!))

(defn reset []
  (stop!)
  (refresh :after 'user/go))
