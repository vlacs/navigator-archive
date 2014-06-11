(ns navigator.schema
  (:require [datomic-schematode.constraints :as constraints]))

(def schema
  [{:namespace :comp
    :attrs [[:id-sk :string "Shared key for 3rd parties" :db.unique/identity]
            [:name :string :indexed] ; not null
            [:description :string :indexed]
            [:version :string]               ; not null
            [:status :enum [:active :archived :preactive]] ; not null
            [:tags :ref :many]]
    :dbfns [(constraints/unique :comp :name :version)]}
   {:namespace :comp-tag
    :attrs [[:name :string :indexed] ; not null
            [:description :string]
            [:version :string]     ; not null
            [:type :string :indexed] ; not null
            [:isrequestable :boolean] ; default f
            [:icon :uri]
            [:status :enum [:active :archived]] ; not null
            [:isfinal :boolean] ; default f
            [:disp-ctxs :ref :many]
            [:child-tags :ref :many]
            [:child-perf-asmts :ref :many]
            [:child-comps :ref :many]]
    :dbfns [(constraints/unique :comp-tag :name :version)]}
   {:namespace :comp-tag-disp-ctx
    :attr [[:name :string]]}
   {:namespace :perf-asmt
    :attrs [[:id-sk :string "Shared key from 3rd parties"]
            [:id-sk-origin :keyword] ; e.g., :moodle, :show-evidence, etc.
            [:name :string]
            [:version :string] ; not null

            ;; Example types: :project :course-competency ... TODO: enum?
            [:type :string] ; not null
            [:duration-rating-days :bigint]
            [:comps :ref :many]
            [:credit-value-numerator :bigint]
            [:credit-value-denominator :bigint]]
    :dbfns [(constraints/unique :perf-asmt :name :version :type)
            (constraints/unique :perf-asmt :id-sk :id-sk-origin)]}
   {:namespace :task
    :attrs [[:id-sk :string]
            [:id-sk-origin :keyword] ; e.g., :moodle, :show-evidence, etc.
            [:name :string]
            [:version :string]
            [:description :string]

            ;; TODO: is this correct?
            [:comps :ref :many]]
    :dbfns [(constraints/unique :perf-asmt :id-sk :id-sk-origin)]}
   {:namespace :user2comp
    :attrs [[:sis-user-id :bigint :indexed]
            [:comp :ref :indexed]
            [:start-date :instant]
            [:iscomplete :boolean] ; default f
            [:isoverridden :boolean] ; default f
            [:completion-date :instant]
            [:proposed-completion-date :instant]
            [:current-score :string]
            [:final-score :string]
            [:score-denominator :bigint]
            [:score-type :enum [:numeric :letter :pass-fail :qualitative]] ; enum is what we want here, n'est-ce pas?
            [:qualitative-notes :string]]}

   {:namespace :user2perf-asmt
    :attrs [[:user :ref]
            [:perf-asmt :ref]
            ;; TODO: finish this
            [:grade :boolean]]}
   {:namespace :user2task
    :attrs [[:user :ref]
            [:task :ref]
            ;; TODO: finish this
            [:progress :boolean]]}

   {:namespace :config
    :attrs [[:key :string :indexed] ; not null
            [:value :string :indexed]]}])
