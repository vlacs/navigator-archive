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
  [conn query & bindings]
  (d/entity conn (ffirst (apply d/q query conn bindings))))

(defn get-competency
  "Gets a competency by shared key"
  [conn id-sk]
  (get-entity conn '[:find ?e
                     :in $ ?id-sk
                     :where [:e? :comp/id-sk ?id-sk]]
              id-sk))


;; Creation/update functions

(defn with-constraints
  "transact with schematode constraints"
  [conn tx]
  (d/transact conn [[:schematode-tx [tx]]]))

(defn- transact-entity-fn
  [fields ns]
  (fn [conn eid & {:as updates}]
    (with-constraints conn (merge {:db/id eid}
                                  (prefix-keys (select-keys updates fields) prefix)))))

(defn- update-entity-fn
  [updatable-fields transact-fn]
  (fn [conn eid & {:as updates}]
    (apply transact-fn conn eid (select-keys updates updatable-fields))))

(def transact-competency
  ^{:private true}
  (transact-entity-fn [:id-sk :name :description :version :status :tags :duration-rating-days] "comp"))

(def update-competency
  ;; TODO: can we update name-version?
  (update-entity-fn [:description :status :duration-rating-days] transact-competency))

(defn create-competency
  "Create a new competency"
  ;; TODO: where does id-sk come from?
  [conn name version status & {:as optional-fields}]
  (apply transact-competency conn (d/tempid :db.part/user)
         :name name
         :version version
         (flatten (seq optional-fields))))

(def transact-comp-tag
  ^{:private true}
  (transact-entity-fn [:name :description :version :type :isrequestable :icon :status :isfinal :disp-ctxs :child-of] "comp-tag"))

(def update-comp-tag
  ;; TODO: again, can we update name-version?
  (update-entity-fn [:description :type :isrequestable :icon :status :isfinal] transact-comp-tag))

(defn create-comp-tag
  "Create a new competency tag"
  [conn name version type isrequirestable status isfinal & {:as optional-fields}]
  (apply transact-comp-tag conn (d/tempid :db.part/user)
         :name name
         :version version
         :type type
         :isrequestable isrequestable
         :status status
         :isfinal isfinal
         (flatten (seq optional-fields))))

(def update-comp-tag-disp-ctx
  (transact-entity-fn [:name] "comp-tag-disp-ctx"))

(defn create-comp-tag-disp-ctx
  "Create a new competency tag display context"
  [conn name]
  (apply transact-comp-tag-disp-ctx (d/tempid :db.part/user) :name name))

(def transact-perf-asmt
  (transact-entity-fn [:id-sk :name :version :type :duration-rating-days :comp] "perf-asmt"))

(def update-perf-asmt
  ;; TODO: can we update name version type?
  (update-entity-fn [:duration-rating-days]))

(defn create-perf-asmt
  "Create a new perf assessment"
  ;; TODO: where does id-sk come from?
  ;; TODO: should name be required?
  [conn version type & {:as optional-fields}]
  (apply transact-perf-asmt (d/tempid :db.part/user)
         :version version
         :type type
         (flatten (seq optional-fields))))

(def transact-user2comp
  ^{:private true}
  (transact-entity-fn [:sis-user-id
                       :comp
                       :start-date
                       :iscomplete
                       :isoverridden
                       :completion-date
                       :proposed-completion-date
                       :current-score
                       :final-score
                       :score-denominator
                       :score-type
                       :qualitative-notes]
                      "user2comp"))

(def update-user2comp
  (update-entity-fn [:start-date
                     :iscomplete
                     :isoverridden
                     :completion-date
                     :proposed-completion-date
                     :current-score
                     :final-score
                     :score-denominator
                     :score-type
                     :qualitative-notes]
                    transact-user2comp))

(defn create-user2comp
  "Create a new user2comp"
  [conn sis-user-id comp & {:as optional-fields}]
  (apply transact-user2comp (d/tempid :db.part/user) :sis-user-id sis-user-id :comp comp
         (flatten (seq optional-fields))))
