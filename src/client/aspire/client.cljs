(ns aspire.client
  (:require [clojure.browser.repl :as repl]
            [clojure.browser.net :as cb-net]
            [clojure.browser.event :as cb-event]
            [hickory.core :as hickory]
            [goog.string :as gstring]
            [goog.string.format :as gformat]
            #_[aspire.model :as a-mdl]
            [aspire.callbacks :as a-cb]
            ))

(repl/connect "http://localhost:9010/repl")
(def api-uri "http://localhost:4001/api")

;; xhr
;; --------------------------------------------------
;; Create and keep one xhr object that knows about our two XHR callbacks.
(def xhr (cb-net/xhr-connection.)) ; additional state!
(cb-event/listen xhr :error a-cb/err)
(cb-event/listen xhr :success a-cb/success)

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

