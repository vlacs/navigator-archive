(ns aspire.web
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.params :as ring-params]
            [ring.middleware.resource :as ring-resource]
            [ring.middleware.file-info :as file-info]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :as response]
            [liberator.core :refer [resource defresource]]
            [liberator.dev :refer [wrap-trace]]
            [compojure.core :refer [defroutes ANY GET POST PUT]]
            [hiccup.page]
            [aspire.templates :as a-tpl]
            [aspire.sqldb :as a-sqldb]
            [aspire.security :as a-sec]
            [aspire.handlers :as a-hdl])
  (:gen-class))

(defresource onboarding
  :handle-ok (a-tpl/render (a-tpl/onboarding (rand-int 100) "http://google.com" "Bo Jackson"
                                             "Welcome to VLACS! (make me configurable)" "Check out our learning options below. You'll find projects, courses, and opportunities to learn by pursuing your interests. Join us in your learning adventure! Actually, this text needs to be configured by the admin in some reasonable kind of interface."
                                             ["Actually, these steps should be configured by the admin in some reasonable kind of interface." "Maybe there will be a step here." "Possibly another step?"])))

(defresource admin
  :allowed-methods [:get]
  :available-media-types ["text/html"]
  :handle-ok a-hdl/admin
  )

(defresource config-key [key]
  :allowed-methods [:put]
  :available-media-types ["text/html"]
<<<<<<< HEAD
  :handle-ok (fn [_]
               (hiccup.page/html5
                [:head
                 ;; alt: page.css
                 [:link {:rel "stylesheet" :href "css/global.css"}]]
                [:body
                 [:div#main
                  [:div [:p#loading "Loading..."]]
                 [:script {:src "js/aspire.js"}]]])))

;;; Simple definition of default ports for http(s). We'll use this to
;;; keep URLs looking pretty.
(def default-ports
  {:http 80
   :https 443})

(defn cons-url
  ([protocol hostname]
   (cons-url protocol hostname nil ""))
  ([protocol hostname port]
   (cons-url protocol hostname port ""))
  ([protocol hostname port uri]
   (str
     (name protocol) "://" hostname
     (if (and
           (not= (get default-ports protocol) port)
           (not= port nil))
       (str ":" port) "")
     uri)))

(defn wrap-host-urls
  "This adds string representations of the path to the root of the
  web server and another that represents the current URL."
  [handler & [opts]]
  (fn wrap-host-urls-middleware
    [req]
    (let [url-fn (partial cons-url
                          (get req :scheme)
                          (get req :server-name)
                          (get req :server-port))]
      (-> req
          (assoc :base-url (url-fn ""))
          (assoc :current-url (url-fn (:uri req)))
          (handler)))))

(defresource config-page
  :allowed-methods [:post]
  :available-media-types ["text/html"]
  :post! a-hdl/config-page!
  ;; TODO: Display a user-friendly "Your changes were saved" message.
  :post-redirect? (fn [_] {:location "/admin"}))

(defroutes app-routes
  ;; just for now, send everybody to /welcome
  (ANY "/" [] (response/redirect "/welcome"))
  (ANY "/debug" req (prn-str req))
  (GET "/welcome" [] onboarding)
  (GET "/admin" [] admin)
  (PUT "/config/key/:key" [] config-key)
  (POST "/config/page/:page" [] config-page)
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

