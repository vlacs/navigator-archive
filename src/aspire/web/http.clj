(ns aspire.web.http
  (:require [ring.util.response :refer [response]]))

(def default-ports
  {:http 80
   :https 443})

(defn make-response
  ([code]
   (make-response code "" []))
  ([code body & opts]
   {:status code
    :headers (if opts (hash-map opts) {})
    :body body}))

(defn access-denied
  [body]
  (make-response 403 body))

(defn server-error
  [body]
  (make-response 500 body))

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
 
