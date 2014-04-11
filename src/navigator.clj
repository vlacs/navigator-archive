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




(defn update-competency2
  [conn comp]
  (tx conn [(select-keys entity (conj :db/id (:comp updatable-fields)))]))

(defn filter-attrs
  [entity entity-type]
  (select-keys entity (conj :db/id (entity-type updatable-fields))))

(defn update-competency3
  [conn comp]
  (tx conn [(filter-attrs comp :comp)]))

(defn update-entity
  [conn entity-type entity]
  (tx conn [(filter-attrs entity entity-type)]))

(defn create-competency2
  "Create a new competency"
  ;; TODO: where does id-sk come from?
  [conn name version status & {:as optional-fields}]
  (tx conn [(merge (select-keys optional-fields (:comp valid-fields))
                   {:db/id (d/tempid :db.part/user)
                    :comp/name name
                    :comp/version version
                    :comp/status status})]))


(defn- transact-competency
  [conn eid & {:as entity}]
  (tx conn [(merge {:db/id eid} (select-keys entity (:comp valid-fields)))]))

(defn update-competency
  ;; TODO: refactor me?
  "Consider using :comp/id-sk instead of the entity ID here, IF every :comp has a :comp/id-sk."
  [conn entity]
  ;;[(:db/id entity) (select-keys entity (:comp updatable-fields))]
  (apply transact-competency conn (:db/id entity) (flatten (seq (select-keys entity (:comp updatable-fields))))))

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
