(ns aspire.web
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.resource :as ring-resource]
            [ring.middleware.file-info :as file-info]
            [ring.util.response :as response]
            [liberator.core :refer [resource defresource]]
            [liberator.dev :refer [wrap-trace]]
            [compojure.core :refer [defroutes ANY]]
            [hiccup.page]
            [aspire.templates :as a-tpl]
            [aspire.sqldb :as a-sqldb])
  (:gen-class))

(defresource resource-app
  ;; This is intended to load up the very first page, and kick
  ;; everything off.
  ;; All other templating should be handled client-side.
  :available-media-types ["text/html"]
  :handle-ok (fn [_]
               (hiccup.page/html5
                [:head
                 ;; alt: page.css
                 [:link {:rel "stylesheet" :href "css/global.css"}]]
                [:body
                 [:div#main
                  [:div [:p#loading "Loading..."]]
                 [:script {:src "js/aspire.js"}]]])))

(defresource resource-base
  :available-media-types ["text/html"]
  :handle-ok (fn [_]
               (a-tpl/render (a-tpl/base (rand-int 100) "http://google.com" "Bo Jackson"
                                         "Welcome to VLACS! (make me configurable)" "Check out our learning options below. You'll find projects, courses, and opportunities to learn by pursuing your interests. Join us in your learning adventure! Actually, this text needs to be configured by the admin in some reasonable kind of interface."
                                         ["Actually, these steps should be configured by the admin in some reasonable kind of interface." "Maybe there will be a step here." "Possibly another step?"]))))

(defroutes app-routes
  (ANY "/" [] resource-base)
  (ANY "/logout" [] "Nothing here yet but us chickens."))

(def app
  (-> app-routes
      (ring-resource/wrap-resource "public")
      (file-info/wrap-file-info)))

(defn run!
  [& args]
  (apply jetty/run-jetty #'app args))

