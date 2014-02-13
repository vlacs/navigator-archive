(ns navigator.data.user
  ^{:author "Jon Doane <jdoane@vlacs.org"
    :doc "This library knows how to get user data."}
  (:require [navigator.sqldb :refer [select! select-one! update!]]
            [honeysql.core :refer [param]]))

(def roles {::admin "ADMIN"
            ::adult-ed "ADULTED"
            ::deprecated "DEPRECATED"
            ::duplicate-student "DUPLICATESTUDENT"
            ::email-only "EMAILONLY"
            ::former-admin "FORMERADMIN"
            ::former-teacher "FORMERTEACHER"
            ::guardian "GUARDIAN"
            ::inactive "INACTIVE"
            ::office-general "OFFICEGENERAL"
            ::partner-school "PartnerSchool"
            ::student "STUDENT"
            ::teacher "TEACHER"})

(def user-table :mdl_sis_user)
(def sis-user-fields [:id
                      :sis_user_idstr
                      :sis_user_id
                      :username
                      ;;; Anything that needs the user's password should get
                      ;;; checked on the spot. Let's not let hashed passwords
                      ;;; float around the system. -jdoane 20140111
                      ;;; :password
                      :privilege
                      :lastname
                      :firstname
                      :email
                      :timemodified
                      :timecreated
                      :istest])

(def select-user-sql
  {:select sis-user-fields
   :from [user-table]})

(defn field-equals
  [field]
  [:= field (param field)])

(def user-by-sis-user-idstr-sql
  (assoc select-user-sql :where [(field-equals :sis_user_idstr)]))

(def valid-user-sql
  (assoc select-user-sql :where [:and
                                 (field-equals :username)
                                 (field-equals :password)]))

(defn get-user-by-sis-user-id
  [sis-user-idstr]
  (select! user-by-sis-user-idstr-sql :sis_user_idstr sis-user-idstr))

(defn get-valid-user
  "Get user when username and password match."
  [username password]
  (select-one! valid-user-sql :username username :password password))

