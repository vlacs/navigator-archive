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
 
(def friend-options
  {:allow-anon? false
   :unauthenticated-handler #(workflows/http-basic-deny "Login with your VLACS user account" %)
   :workflows [(workflows/http-basic
                 :credential-fn md5-credential-fn
                 :realm "Aspire")]})

(defn require-login
  [ring-routes]
  (friend/authenticate ring-routes friend-options))

(defn has-role?
  [role]
  )

(defn require-roles
  "Requires roles based on the output of a predicate."
  [pred]
  )

(defn logout-route
  [ring-route]
  nil
  )
