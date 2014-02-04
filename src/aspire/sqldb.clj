; TODO: see http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded
(ns ^{:author "Matt Oquist <moquist@vlacs.org>"
      :doc "A work-in-progress/experimental SQL abstraction library for Aspire

Our long-term plan for Aspire is to use Datomic for storage. For now,
this file just has some examples of how to use HoneySQL.

We're using Korma's connection management and query fns."}
  aspire.sqldb
  (:require [clojure.edn :as edn]
            [honeysql.core :as hsql]
            [honeysql.helpers :as hsql-h]
            [korma.db :as kdb]
            [clojure.string :refer [split]]))

(defn default-connection! [db]
  (let [pool (kdb/delay-pool db)]
    (kdb/default-connection pool)
    pool))

(def query-results-types
  {"SELECT" :results
   "UPDATE" :keys
   "INSERT" :keys})

(defn update!
  "Thin wrapper on kdb/do-query for update/insert.

   update! should be used for INSERT and UPDATE calls, since HoneySQL doesn't handle those.
   Note that we don't have HoneySQL's named parameters here.
   Examples:
      (update! \"UPDATE fribbet SET pectin = ? WHERE pectin = ? AND jam = ?\" [7 6 \"cherry\"])
      (update! \"INSERT INTO fribbet (jam, pan) VALUES (?, ?)\" [\"snozzberry\" \"non-stick\"])"
  [sql params]
  (if (= "DELETE" (-> sql (split #"\s") first))
    (throw (Exception. "DELETE is forbidden")))
  (kdb/do-query {:results :keys
                 :sql-str sql
                 :params params}))

(defn select!
  "HoneySQL-friendly SELECT wrapper on kdb/do-query.

   Note that we DO have HoneySQL's named parameters here.
   Example:
      (select!
       {:select [:id :name :description :version]
        :from [:comp]
        :where [:= :id (hsql/param :id)]}
       {:id 7})"
  [sqlmap & {:as paramsmap}]
  (let [[sql & params] (hsql/format sqlmap :params paramsmap)]
    (kdb/do-query {:results :results
                   :sql-str sql
                   :params params})))

(defn select-one!
  "*** See the select! fn. ***
  Queries the database for a single row.
  It alters the query to return only 1 row, then takes the first and only result.
  If there is no result, nil is returned."
  [& all-params]
  (if-let [result (apply select! all-params)]
    (first result)
    nil))

;; Just an example. Basic parts of common SQL queries can be written
;; in HoneySQL-compatible structures, and then composed together.
(def sel-comp-by-id
  {:select [:id :name :description :version] ; better to specify fields we want -- prep for Datomic
   :from [:comp]
   ;; Should we use named params, or ordered bindable params?
   :where [:= :id (hsql/param :id)]})

;; Another rough example.
(def sel-comp-by-name-description
  {:select [:id :name :description :version]
   :from [:comp]
   :where [:and
           [:like :name (hsql/param :name)]
           [:like :description (hsql/param :description)]]})

(comment
  (def schema (edn/read-string (slurp "schema.edn")))
  (def db (edn/read-string (slurp (format "%s/.aspire/conf-sql-db.edn" (System/getProperty "user.home")))))
  (default-connection! db)

  (select! sel-comp-by-id :id 1)
  (select! sel-comp-by-name-description :name "%disjunctivist%" :description "%whee%")

  (def comp
    ; insert a new comp, keep track of its ID
    (update! "INSERT INTO comp (name, version) VALUES (?, ?)" ["messiah" "v1"]))
  (def comp_tag
    ; insert a new comp_tag, keep track of its ID
    (update! "INSERT INTO comp_tag (name, version, type, disp_level_num) VALUES (?, ?, ?, ?)"
             ["Handel" "v1" "oratorio" 1]))
  ; insert a new comp2comp_tag relation, pulling the requisite IDs out
  ; of the results of earlier inserts.
  (update! "INSERT INTO comp2comp_tag (comp_id, comp_tag_id) VALUES (?, ?)"
           [(:id comp) (:id comp_tag)])

  )

