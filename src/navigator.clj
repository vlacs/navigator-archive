(ns navigator
  ^{:author "David Zaharee <dzaharee@vlacs.org>"
    :doc "This library knows how to work with competency data."}
  (:require [clojure.edn :as edn]
            [datomic.api :as d]
            [liberator.core :refer [resource]]
            [navigator.schema :as schema]
            [hatch]
            [navigator.templates :as templates]
            [navigator.data :as data]
            [navigator.validation :refer :all]))

;; front end

(defn helmsman-def [db-conn]
  [[:get "/comp-map/" (resource :allowed-methods [:get]
                                :available-media-types ["text/html"]
                                :handle-ok (fn [ctx] (apply str (templates/view-comp-map (data/get-comp-map db-conn ctx) ctx))))]])

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
  (let [task (merge {:task/id-sk (str (get-in message [:header :entity-id :task-id]))}
                                   (hatch/slam-all (get-in message [:payload :entity]) :task))]
    (if (valid? validate-task-in task)
      (tx-entity! db-conn :task task)
      false)))

(defn comp-in [db-conn message]
  (let [comp (merge {:comp/id-sk (str (get-in message [:header :entity-id :comp-id]))}
                                    (hatch/slam-all (get-in message [:payload :entity]) :comp))]
    (if (valid? validate-comp-in comp)
      (tx-entity! db-conn :comp comp)
      false)))

(defn comp-tag-in [db-conn message]
  (let [comp-tag (hatch/slam-all (get-in message [:payload :entity]) :comp-tag)]
    (if (valid? validate-comp-tag-in comp-tag)
      (tx-entity! db-conn :comp-tag comp-tag)
      false)))

(defn perf-asmt-in [db-conn message]
  (let [perf-asmt (merge {:perf-asmt/id-sk (str (get-in message [:header :entity-id :perf-asmt-id]))}
                                         (hatch/slam-all (get-in message [:paylod :entity]) :perf-asmt))]
    (if (valid? validate-perf-asmt-in perf-asmt)
      (tx-entity! db-conn :perf-asmt perf-asmt)
      false)))

(defn user2comp-in [db-conn message]
  (let [user2comp (hatch/slam-all (get-in message [:payload :entity]) :user2comp)]
    (if (valid? validate-user2comp-in user2comp)
      (tx-entity! db-conn :user2comp user2comp)
      false)))

(defn user2perf-asmt-in [db-conn message]
  (let [user2perf-asmt (hatch/slam-all (get-in message [:payload :entity]) :user2perf-asmt)]
    (if (valid? validate-user2perf-asmt-in user2perf-asmt)
      (tx-entity! db-conn :user2perf-asmt user2perf-asmt)
      false)))

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
