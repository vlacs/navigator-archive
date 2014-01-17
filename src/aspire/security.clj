(ns aspire.security
  (:require [digest :refer [md5]]
            [aspire.data.user :refer [get-valid-user]]
            [aspire.util :refer [keywords->ns]]
            [cemerick.friend :as friend]
            [cemerick.friend.workflows :as workflows]
            ))

(def active-roles
  (set (keywords->ns 'aspire.data.user
                     :admin
                     :adult-ed
                     :guardian
                     :student
                     :teacher)))

(defn md5-credential-fn
  "This credential fn checks the database for the
  user instead of a clojure entity"
  [user]
  (get-valid-user (:username user) (md5 (:password user))))

(defn saml20-credential-fn
  [saml-mutable-timeouts x509-cert saml-response]
  ;;; TODO: Write this using the saml20-clj library.
  nil
  ) 

(defn authorize-saml
  [ring-routes roles]
  nil
  )

(defn authorize-http-basic
  [ring-routes roles]
  (friend/authorize ring-routes roles
                    {:credential-fn md5-credential-fn
                     :workflows [(workflows/http-basic)]}))

(defn logout-route
  [ring-route]
  nil
  )
