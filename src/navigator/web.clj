(ns navigator.web
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.params :as ring-params]
            [ring.middleware.resource :as ring-resource]
            [ring.middleware.file-info :as file-info]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :as response]
            [liberator.dev :refer [wrap-trace]]
            [compojure.core :refer [defroutes context ANY GET POST PUT]]
            [hiccup.page]
            [navigator.templates :as a-tpl]
            [navigator.sqldb :as a-sqldb]
            [navigator.security :as a-sec]
            [navigator.web.resources :as a-res]
            [navigator.web.http :refer [wrap-host-urls]])
  (:gen-class))

(defroutes admin-routes
  (GET "/" [] a-res/admin!)
  (ANY "/debug" req (prn-str req)))

(defroutes config-routes
  (PUT "/key/:key" [] a-res/config-key)
  (POST "/page/:page" [] a-res/config-page!))

(defroutes app-routes
  ;; just for now, send everybody to /welcome
  (ANY "/" [] (response/redirect "/welcome"))
  (GET "/welcome" [] a-res/onboarding!)
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

