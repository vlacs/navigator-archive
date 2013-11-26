(ns aspire.client
  (:require [clojure.browser.repl :as repl]
            [clojure.browser.net :as cb-net]
            [clojure.browser.event :as cb-event]
            [cljs.reader :as edn]
            [dommy.core :as dommy]
            [dommy.attrs :as d-attrs]
            [goog.string :as gstring]
            [goog.string.format :as gformat]
            [aspire.model :as a-mdl]
            )
  (:use-macros
   [dommy.macros :only [by-id sel1]]))

(repl/connect "http://localhost:9000/repl")
(def api-uri "http://localhost:4001/api")

;; Callbacks
;; --------------------------------------------------
;; If possible we'll get by with one err callback and one success
;; callback for XHR, and use client-side routing and core.async
;; instead of multiple XHR callbacks.
(defn srv-callback-err [ev]
  (.log js/console "Error: " ev))

(defn srv-callback-success [ev]
  (let [response-text (.getResponseText (.-target ev))]
    (.log js/console (:now (edn/read-string response-text)))
    (dommy/replace! (sel1 :#loading) [:p#loading (gstring/format "This is now: %s" (:now (edn/read-string response-text)))])))

;; xhr
;; --------------------------------------------------
;; Create and keep one xhr object that knows about our two XHR callbacks.
(def xhr (cb-net/xhr-connection.)) ; additional state!
(cb-event/listen xhr :error srv-callback-err)
(cb-event/listen xhr :success srv-callback-success)

(defn xhr-fetch!
  "Fetch the specified uri.
   This fn will change as we add client-side routes and core.async."
  [uri]
  (cb-net/transmit xhr uri))

(defn handle-click
  "Shallow demo of click-handling fn."
  []
  (js/alert "Hello!")
  (xhr-fetch! api-uri))

(defn poll
  "Request new data every 10 seconds, because we can." 
  []
  (let [timer (goog.Timer. 10000)]
    (do (xhr-fetch! api-uri)
        (. timer (start))
        (cb-event/listen timer goog.Timer/TICK #(xhr-fetch! api-uri)))))

(defn start-app
  "Start polling and listen for UI events."
  []
  (do (poll)
      ;; figure out dommy/listen! and how we want to use core.async
      ;(cb-event/listen (by-id :clickable) "click" handle-click)
      ))

(start-app)

(comment
  (js/goog.debug.expose xhr)
  )

