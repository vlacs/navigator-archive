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
               (a-tpl/render (a-tpl/index "hi"))))

(defroutes app-routes
  (ANY "/" [] resource-base))

(def app
  (-> app-routes
      (ring-resource/wrap-resource "public")
      (file-info/wrap-file-info)))

(defn run!
  [& args]
  (apply jetty/run-jetty #'app args))

