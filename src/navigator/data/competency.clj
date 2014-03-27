(ns navigator.data.competency
  ^{:author "David Zaharee <dzaharee@vlacs.org>"
    :doc "This library knows how to work with competency data."}
  (:require [datomic.api :as d]))

;; utitlities

(defn prefix-keys
  "Prefix all keys in a map with prefix"
  [m prefix]
  (into {} (for [[k v] m] [(keyword prefix (name k)) v])))

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
  "Get competency by shared key"
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

(defn with-constraints
  "transact with schematode constraints"
  [conn tx]
  (d/transact conn [[:schematode-tx [tx]]]))

(defn- transact-entity-fn
  [fields]
  (fn [conn eid & {:as updates}]
    (with-constraints conn (merge {:db/id eid} (select-keys updates fields)))))

(defn- update-entity-fn
  [updatable-fields transact-fn]
  (fn [conn & {:keys {eid} :as entity}]
    (apply transact-fn conn eid (select-keys entity updatable-fields))))

(def transact-competency
  ^{:private true}
  (transact-entity-fn [:comp/id-sk :comp/name :comp/description :comp/version :comp/status :comp/tags :comp/duration-rating-days]))

(def update-competency
  ;; TODO: can we update name-version?
  (update-entity-fn [:comp/description :comp/status :comp/duration-rating-days] transact-competency))

(defn create-competency
  "Create a new competency"
  ;; TODO: where does id-sk come from?
  [conn name version status & {:as optional-fields}]
  (apply transact-competency conn (d/tempid :db.part/user)
         :comp/name name
         :comp/version version
         (flatten (seq optional-fields))))

(def transact-comp-tag
  ^{:private true}
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
  (transact-entity-fn [:comp-tag-disp-ctx/name]))

(defn create-comp-tag-disp-ctx
  "Create a new competency tag display context"
  [conn name]
  (apply update-comp-tag-disp-ctx (d/tempid :db.part/user) :comp-tag-disp-ctx/name name))

(def transact-perf-asmt
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
  ^{:private true}
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
