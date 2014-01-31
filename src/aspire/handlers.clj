(ns aspire.handlers
  (:require [aspire.templates :as a-tpl]
            [aspire.util :as a-util]))


(defn config-key! [ctx]
  (spit "/tmp/expectorate.txt" (str ctx))
  "config-key!")

(def config-page-keys
  ;; List all the config keys we expect to receive from a particular
  ;; config page.
  {:onboarding ["greeting" "steps"]})

(defn- whitelist-config-page-keys
  "Ensure we do not process browser parameters other than those we expect from a given config page."
  [page params]
  (filter
   (fn [[key val]]
     (when (some #{key} (page config-page-keys)) true))
   params))

(defn config-page!
  "Take in a page of configuration updates for the config table.
   Apply the updates to the DB and return the map of results from set-config!."
  [ctx]
  (let [page (get-in ctx [:request :route-params :page])
        params (get-in ctx [:request :form-params])]
    #_[:params params]
    (map (fn do-config-page! [[k v]]
           (a-util/set-config! (str page "/" k) v))
         (whitelist-config-page-keys (keyword page) params))))

(defn admin! [_]
  (let [greeting (a-util/get-config! "onboarding/greeting")
        steps (a-util/get-config! "onboarding/steps")]
    (a-tpl/render (a-tpl/admin (rand-int 100) "http://google.com" "Jo Backson" greeting steps))))
