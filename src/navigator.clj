(ns navigator
  ^{:author "David Zaharee <dzaharee@vlacs.org>"
    :doc "This library knows how to work with competency data."}
  (:require [clojure.edn :as edn]
            [datomic.api :as d]
            [navigator.schema :as schema]
            [hatch]))

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

(def partitions (hatch/schematode->partitions schema/schema))

(def valid-attrs (hatch/schematode->attrs schema/schema))

(def tx-entity! (partial hatch/tx-clean-entity! partitions valid-attrs))

;; queue functions

(defn task-in [db-conn message]
  (tx-entity! db-conn :task (merge {:task/id-sk (str (get-in message [:header :entity-id :task-id]))}
                                   (hatch/slam-all (get-in message [:payload :entity]) :task))))


(comment

  (navigator/task-in (:db-conn nt-config/system)
                     {:payload
                      {:entity
                       {:competency-parents [1 2 3],
                        :name "tie shoes (together)",
                        :version "v3"}},
                      :header
                      {:entity-type "task", :operation "assert", :entity-id {:task-id 17}}})

)
