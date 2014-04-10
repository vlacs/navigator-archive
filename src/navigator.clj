(ns navigator
  ^{:author "David Zaharee <dzaharee@vlacs.org>"
    :doc "This library knows how to work with competency data."}
  (:require [clojure.edn :as edn]
            [datomic.api :as d]
            [datomic-schematode.core :as schematode]
            [navigator.schema :as schema]))

(def updatable-fields
  {:comp [:comp/description :comp/status :comp/tags]})

(def valid-fields
  {:comp [:comp/id-sk :comp/name :comp/description :comp/version :comp/status :comp/tags]})

;; TODO: use https://github.com/rkneufeld/conformity ... in schematode. But noting it here. :-)
(defn init! [system]
  [(schematode/init-schematode-constraints! (:db-conn system))
   (schematode/load-schema! (:db-conn system) schema/schema)])

;; Get functions

(defn get-entity
  "Gets an entity using query and bindings. The db conn is assumed to
  be the first binding of the query and doesn't need to be included in
  the bindings part of the arguments."
  ([conn query & bindings]
     (d/entity (d/db conn) (ffirst (apply d/q query (d/db conn) bindings)))))

(defn get-competency
  "Get competency by shared key"
  [conn id-sk]
  (get-entity conn '[:find ?e
                     :in $ ?id-sk
                     :where [?e :comp/id-sk ?id-sk]]
              id-sk))

(defn get-competency-by-name-version
  "Get competency by name+verion"
  [conn name version]
  (get-entity conn '[:find ?e
                     :in $ ?name ?version
                     :where [?e :comp/name ?name]
                            [?e :comp/version ?version]]
              name version))
;; TODO: we probably want other ways to get competency(s)

(defn get-perf-asmt
  "Get perf-asmt by shared key"
  [conn id-sk]
  (get-entity conn '[:find ?e
                     :in $ ?id-sk
                     :where [?e :perf-asmt/id-sk ?id-sk]]
              id-sk))

(defn get-user2comp
  "Get user2comp by sis-user-id and comp"
  [conn sis-user-id comp-eid]
  (get-entity conn '[:find ?e
                     :in $ ?sis-user-id ?comp-eid
                     :where [?e :user2comp/sis-user-id ?sis-user-id]
                            [?e :user2comp/comp ?comp-eid]]))

;; Creation/update functions

;; TODO: schematode may have a thing that does this?
(defn tx
  "transact with schematode constraints"
  [conn txs]
  (schematode/tx conn :enforce txs))

#_(defn- transact-entity-fn
  [valid-fields]
  (fn [conn eid & {:as fields}]
    (tx conn (merge {:db/id eid} (select-keys fields valid-fields)))))

(defn- update-entity-fn
  [updatable-fields transact-fn]
  (fn [conn & {:keys [eid] :as entity}]
    (apply transact-fn conn eid (select-keys entity updatable-fields))))

(defn- transact-competency
  [conn eid & {:as entity}]
  (tx conn [(merge {:db/id eid} (select-keys entity (:comp valid-fields)))]))

(defn update-competency
  "Consider using :comp/id-sk instead of the entity ID here, IF every :comp has a :comp/id-sk."
  [conn & {:keys [eid] :as entity}]
  (apply transact-competency conn eid (select-keys entity (:comp updatable-fields))))

(defn create-competency
  "Create a new competency"
  ;; TODO: where does id-sk come from?
  [conn name version status & {:as optional-fields}]
  (apply transact-competency conn (d/tempid :db.part/user)
         :comp/name name
         :comp/version version
         :comp/status status
         (flatten (seq optional-fields))))

(def transact-comp-tag
  1
  #_
  (transact-entity-fn [:comp-tag/name
                       :comp-tag/description
                       :comp-tag/version
                       :comp-tag/type
                       :comp-tag/isrequestable
                       :comp-tag/icon
                       :comp-tag/status
                       :comp-tag/isfinal
                       :comp-tag/disp-ctxs
                       :comp-tag/child-of]))

(def update-comp-tag
  ;; TODO: again, can we update name-version?
  (update-entity-fn [:comp-tag/description
                     :comp-tag/type
                     :comp-tag/isrequestable
                     :comp-tag/icon
                     :comp-tag/status
                     :comp-tag/isfinal] transact-comp-tag))

(defn create-comp-tag
  "Create a new competency tag"
  [conn name version type status & {:as optional-fields}]
  (apply transact-comp-tag conn (d/tempid :db.part/user)
         :comp-tag/name name
         :comp-tag/version version
         :comp-tag/type type
         :comp-tag/status status
         (flatten (seq optional-fields))))

(def update-comp-tag-disp-ctx
  #_
  (transact-entity-fn [:comp-tag-disp-ctx/name]))

(defn create-comp-tag-disp-ctx
  "Create a new competency tag display context"
  [conn name]
  (apply update-comp-tag-disp-ctx (d/tempid :db.part/user) :comp-tag-disp-ctx/name name))

(def transact-perf-asmt
  #_
  (transact-entity-fn [:perf-asmt/id-sk
                       :perf-asmt/name
                       :perf-asmt/version
                       :perf-asmt/type
                       :perf-asmt/duration-rating-days
                       :perf-asmt/comp]))

(def update-perf-asmt
  ;; TODO: can we update name version type?
  (update-entity-fn [:perf-asmt/duration-rating-days] transact-perf-asmt))

(defn create-perf-asmt
  "Create a new perf assessment"
  ;; TODO: where does id-sk come from?
  ;; TODO: should name be required?
  [conn version type & {:as optional-fields}]
  (apply transact-perf-asmt (d/tempid :db.part/user)
         :perf-asmt/version version
         :perf-asm/type type
         (flatten (seq optional-fields))))

(def transact-user2comp
  1
  #_
  (transact-entity-fn [:user2comp/sis-user-id
                       :user2comp/comp
                       :user2comp/start-date
                       :user2comp/iscomplete
                       :user2comp/isoverridden
                       :user2comp/completion-date
                       :user2comp/proposed-completion-date
                       :user2comp/current-score
                       :user2comp/final-score
                       :user2comp/score-denominator
                       :user2comp/score-type
                       :user2comp/qualitative-notes]))

(def update-user2comp
  (update-entity-fn [:user2comp/start-date
                     :user2comp/iscomplete
                     :user2comp/isoverridden
                     :user2comp/completion-date
                     :user2comp/proposed-completion-date
                     :user2comp/current-score
                     :user2comp/final-score
                     :user2comp/score-denominator
                     :user2comp/score-type
                     :user2comp/qualitative-notes]
                    transact-user2comp))

(defn create-user2comp
  "Create a new user2comp"
  [conn sis-user-id comp & {:as optional-fields}]
  (apply transact-user2comp (d/tempid :db.part/user)
         :user2comp/sis-user-id sis-user-id
         :user2comp/comp comp
         (flatten (seq optional-fields))))
