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
    :attrs [[:name :string]]}
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
            [:qualitative-notes :string]]}])
