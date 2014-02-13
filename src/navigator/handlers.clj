(ns navigator.handlers
  (:require [navigator.templates :as n-tpl]
            [navigator.util :as n-util]))

(def config-page-keys
  ;; List all the config keys we expect to receive from a particular
  ;; config page.
  {:onboarding ["greeting" "greeting-msg" "steps"]})

(defn- whitelist-config-page-keys
  "Ensure we do not process browser parameters other than those we expect from a given config page."
  [page params]
  (filter
   (fn [[key val]]
     (when (some #{key} (page config-page-keys)) true))
   params))

(defn config-key!
  "This is intended to be where an AJAX-PUT single config key would be handled."
  [ctx]
  (spit "/tmp/expectorate.txt" (str ctx))
  "This is not implemented yet.")

(defn config-page!
  "Take in a page of configuration updates for the config table.
   Apply the updates to the DB and return the map of results from set-config!."
  [ctx]
  (let [page (get-in ctx [:request :route-params :page])
        params (get-in ctx [:request :form-params])]
    (map (fn do-config-page! [[k v]]
           (n-util/set-config! (str page "/" k) v))
         (whitelist-config-page-keys (keyword page) params))))

;; Page handlers
;; -----------------------
(defn onboarding! [ctx]
  (let [common-snippets (n-tpl/common-snippets ctx)
        greeting (n-util/get-config! "onboarding/greeting")
        greeting-msg (n-util/get-config! "onboarding/greeting-msg")
        steps (n-util/split-paragraphs (n-util/get-config! "onboarding/steps"))]
    (n-tpl/onboarding common-snippets greeting greeting-msg steps)))

(defn admin! [ctx]
  (let [common-snippets (n-tpl/common-snippets ctx)
        greeting (n-util/get-config! "onboarding/greeting")
        greeting-msg (n-util/get-config! "onboarding/greeting-msg")
        steps-str (n-util/get-config! "onboarding/steps")]
    (n-tpl/admin common-snippets greeting greeting-msg steps-str)))