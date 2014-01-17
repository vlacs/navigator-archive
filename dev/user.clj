(ns user
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [hickory.core :as hickory]
            [aspire.core :as aspire.core]
            [aspire.conf :as aspire.conf]
            ))

;; N.B.: (ns cljs.user (:use [clojure.zip :only [insert-child]])) (see http://stackoverflow.com/questions/12879027/cannot-use-in-clojurescript-repl)

(def system nil)

(defn init! []
  (let [home (System/getProperty "user.home")
        mydir (format "%s/.aspire" home)
        args ["--verbose"
              "--config-path" mydir]
        [opts conf] (aspire.core/opts-and-conf-from-args args)]
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

;; Utility fns
;; -----------------------
(defn html->hiccup [html]
  (-> html
      hickory/parse-fragment
      (->> (map hickory/as-hiccup))))

