(ns aspire.web
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.resource :as ring-resource]
            [ring.middleware.file-info :as file-info]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :as response]
            [liberator.core :refer [resource defresource]]
            [liberator.dev :refer [wrap-trace]]
            [compojure.core :refer [defroutes ANY]]
            [hiccup.page]
            [aspire.templates :as a-tpl]
            [aspire.sqldb :as a-sqldb]
            [aspire.security :as a-sec])
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

(defresource resource-base
  :available-media-types ["text/html"]
  :handle-ok (fn [_]
               (a-tpl/render (a-tpl/base (rand-int 100) "http://google.com" "Bo Jackson"))))

(defroutes app-routes
  (ANY "/debug" req (prn-str req))
  (ANY "/" [] resource-base))

(def app
  (-> app-routes
      (ring-resource/wrap-resource "public")
      (file-info/wrap-file-info)  
      (wrap-params)
      (wrap-host-urls)
      (a-sec/require-login)
      ))

(defn run!
  [& args]
  (apply jetty/run-jetty #'app args))

