(ns navigator.security
  (:require [digest :refer [md5]]
            [clojure.string :as str]
            [navigator.data.user :refer [get-valid-user]]
            [navigator.util :refer [keywords->ns]]
            [cemerick.friend :as friend]
            [cemerick.friend.workflows :as workflows]
            [saml20-clj.sp :as saml20-sp]))

(def http-static-results
  {:unknown-user {:status 500
                  :body "The SAML response contained an unknown user."}
   :invalid-saml {:status 500
                  :body "The SAML response could not be verified."}})

(def active-roles
  (set (keywords->ns 'navigator.data.user
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

(defn saml20-workflow
  "Produces a workflow fn that takes in a ring request and handles SAML appropriately."
  [saml-request-factory-fn! credential-fn idp-url]
  (fn saml20-workflow-instance
    [request]
    ;;; We need SAMLResponse and RelayState. They will be POSTed.
    (let [params (:params request)
          http-method (:request-method request)]
      (if (and (:SAMLResponse params) (:RelayState params) (= http-method :post))
        (let [parsed-response (saml20-sp/parse-saml-response (:SAMLResponse params))]
          ;; Was it successful? If it was, log them in!  
          (if (:success? parsed-response)
            (if-let [user-record (credential-fn (:user-identifier parsed-response))]
              (workflows/make-auth user-record {::friend/workflow :saml20
                                                ;;; We have a relay state, we
                                                ;;; should redirect if we can.
                                                ::friend/redirect-on-auth? false})
              (:unknown-user http-static-results))
            (:invalid-saml http-static-results)))
        (saml20-sp/get-idp-redirect idp-url (saml-request-factory-fn!) (:current-url request))))))
 
(def friend-options
  {:allow-anon? false
   :unauthenticated-handler #(workflows/http-basic-deny "Authenticate with your VLACS user account" %)
   :workflows [(workflows/http-basic
                 :credential-fn md5-credential-fn
                 :realm "Navigator")]})

(defn require-login
  [ring-routes]
  (friend/authenticate ring-routes friend-options))

(defn logout-route
  [ring-route]
  nil
  )

(defn authorized?
  "low-granularity, page-level authorization checker
   Returns a fn that takes in a ring request and checks that the
   currently authenticated user has at least one of the specified
   roles."
  [& roles]
  (fn authorized?* [request]
    (let [ca (friend/current-authentication request)
          privilege (-> ca :privilege str/lower-case keyword)]
      (some #{privilege} roles))))

;; This menu stuff is a total hack, and probably doesn't even belong
;; in this file. (Putting it in util.clj causes circular deps...)
;; TODO: Somehow combine this with our actual routes.
;; TODO: How to combine this page-level access with more granular
;; access based on parameters in the route (e.g., you can update
;; *your* profile, but not just any profile)?
(def menu-items
  {:admin {:route "/admin" :name "Admin" :weight 100 :desc "Perform administrative functions" :authorized? (authorized? :admin)}
   :home {:route "/" :name "Home" :weight 0 :desc "Main Navigator page" :authorized? (constantly true)}
   :onboarding {:route "/welcome"
                :name "Competency Map"
                :weight 10
                :desc "Add learning to your playlist"
                :authorized? (constantly true)}})

(defn menu [request]
  (let [ca (friend/current-authentication request)]
    (->> menu-items
         (filter (fn menu-f* [[k v]]
                   (let [f (:authorized? v)]
                     (f request))))
         (sort (fn menu-s* [& _] true)))))

