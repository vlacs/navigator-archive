(ns navigator
  ^{:author "David Zaharee <dzaharee@vlacs.org>"
    :doc "This library knows how to work with competency data."}
  (:require [clojure.edn :as edn]
            [datomic.api :as d]
            [datomic-schematode.core :as schematode]
            [navigator.schema :as schema]))

;; TODO: maybe we split up this file?

(def updatable-attrs
  {:comp [:comp/name :comp/version :comp/description :comp/status :comp/tags]
   :comp-tag [:comp-tag/name :comp-tag/description :comp-tag/version
              :comp-tag/type :comp-tag/isrequestable :comp-tag/icon
              :comp-tag/status :comp-tag/isfinal :comp-tag/:disp-ctxs
              :comp-tag/child-of]
   :task [:task/name :task/version :task/description :task/comps]})

(def write-once-attrs
  {:comp [:comp/id-sk]
   :task [:task/id-sk]})

(def valid-attrs (merge-with concat write-once-attrs updatable-attrs))

;; TODO: use https://github.com/rkneufeld/conformity ... in schematode. But noting it here. :-)
(defn init! [system]
  [(schematode/init-schematode-constraints! (:db-conn system))
   (schematode/load-schema! (:db-conn system) schema/schema)])

;; utitlities

(defn slam
  "Slams two keywords together into one namespaced keyword"
  [ns n]
  (keyword (name ns) (name n)))

(defn prefix-keys
  "Prefix all keys in a map with prefix"
  [m prefix]
  (into {} (for [[k v] m] [(slam prefix k) v])))

(defn get-partition
  "Get the db partition an entity is in"
  [entity-type]
  (or (get-in schema/schema-map [entity-type :part])
      :db.part/user))

;; Get functions

(defn get-entity
  "Gets an entity using query and bindings. The db conn is assumed to
  be the first binding of the query and doesn't need to be included in
  the bindings part of the arguments."
  ([db-conn query & bindings]
     (d/entity (d/db db-conn) (ffirst (apply d/q query (d/db db-conn) bindings)))))

(defn get-competency
  "Get competency by shared key"
  [db-conn id-sk]
  (get-entity db-conn '[:find ?e
                     :in $ ?id-sk
                     :where [?e :comp/id-sk ?id-sk]]
              id-sk))

(defn get-competency-by-name-version
  "Get competency by name+verion"
  [db-conn name version]
  (get-entity db-conn '[:find ?e
                     :in $ ?name ?version
                     :where [?e :comp/name ?name]
                            [?e :comp/version ?version]]
              name version))
;; TODO: we probably want other ways to get competency(s)

(defn get-perf-asmt
  "Get perf-asmt by shared key"
  [db-conn id-sk]
  (get-entity db-conn '[:find ?e
                     :in $ ?id-sk
                     :where [?e :perf-asmt/id-sk ?id-sk]]
              id-sk))

(defn get-user2comp
  "Get user2comp by sis-user-id and comp"
  [db-conn sis-user-id comp-eid]
  (get-entity db-conn '[:find ?e
                     :in $ ?sis-user-id ?comp-eid
                     :where [?e :user2comp/sis-user-id ?sis-user-id]
                            [?e :user2comp/comp ?comp-eid]]))

;; Creation/update functions

(defn tx
  "transact with schematode constraints"
  [db-conn txs]
  (schematode/tx db-conn :enforce txs))

(defn filter-op-attrs*
  [op entity entity-type]
  (select-keys entity (conj (entity-type op) :db/id)))

(def filter-update-attrs
  (partial filter-op-attrs* updatable-attrs))

;; TODO: should we rename this?
(def filter-create-attrs
  (partial filter-op-attrs* valid-attrs))

(defn tx-update-entity
  [db-conn entity entity-type]
  (tx db-conn [(filter-update-attrs entity entity-type)]))

(defn tx-entity
  [db-conn entity entity-type]
  (tx db-conn [(filter-create-attrs entity entity-type)]))

;; queue functions

(defn task-in [db-conn message]
  (tx-entity db-conn (merge {:db/id (d/tempid (get-partition :task))
                          :task/id-sk (str (get-in message [:header :entity-id :task-id]))}
                         (prefix-keys (get-in message [:payload :entity]) :task))
             :task))
