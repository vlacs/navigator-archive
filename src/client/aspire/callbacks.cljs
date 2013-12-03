(ns aspire.callbacks
  (:require [cljs.reader :as edn]
            [dommy.core :as dommy]
            [aspire.templates :as a-tpl])
  (:use-macros
   [dommy.macros :only [sel1]]))

(defn err [ev]
  (.log js/console "Error: " ev))

(defn success [ev]
  (let [response-text (.getResponseText (.-target ev))
        {:keys [name version description cnt-completed cnt-remaining]} (:comp_tag (edn/read-string response-text))]
    #_(.log js/console (js/goog.debug.expose ev))
    #_(.log js/console :name name)
    (dommy/replace-contents! (sel1 :#loading)
                             (a-tpl/comp-tag name version description cnt-completed cnt-remaining)
                             )))

