(ns aspire.web
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.params :as ring-params]
            [ring.middleware.resource :as ring-resource]
            [ring.middleware.file-info :as file-info]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :as response]
            [liberator.core :refer [resource defresource]]
            [liberator.dev :refer [wrap-trace]]
            [compojure.core :refer [defroutes context ANY GET POST PUT]]
            [hiccup.page]
            [aspire.templates :as a-tpl]
            [aspire.sqldb :as a-sqldb]
            [aspire.security :as a-sec]
            [aspire.handlers :as a-hdl]
            [aspire.web.http :refer [wrap-host-urls]]
            )
  (:gen-class))


(defresource onboarding!
  :available-media-types ["text/html"]
  :handle-ok a-hdl/onboarding!)

(defresource admin
  :allowed-methods [:get]
  :available-media-types ["text/html"]
  :handle-ok a-hdl/admin!)

(defresource config-key [key]
  :allowed-methods [:put]
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

(defresource config-page
  :allowed-methods [:post]
  :available-media-types ["text/html"]
  :post! a-hdl/config-page!
  ;; TODO: Display a user-friendly "Your changes were saved" message.
  :post-redirect? (fn [_] {:location "/admin"}))

(defroutes admin-routes
  (GET "/" [] admin)
  (ANY "/debug" req (prn-str req)))

(defroutes config-routes
  (PUT "/key/:key" [] config-key)
  (POST "/page/:page" [] config-page))

(defroutes app-routes
  ;; just for now, send everybody to /welcome
  (ANY "/" [] (response/redirect "/welcome"))
  (GET "/welcome" [] onboarding!)
  (context "/admin" [] admin-routes)
  (context "/config" [] config-routes)
  (ANY "/logout" [] "Nothing here yet but us chickens."))

(def app
  (-> app-routes
      (wrap-trace :header :ui)
      (ring-params/wrap-params)
      (ring-resource/wrap-resource "public")
      (file-info/wrap-file-info)  
      (wrap-params)
      (wrap-host-urls)
      (a-sec/require-login)
      ))

(defn run!
  [& args]
  (apply jetty/run-jetty #'app args))

