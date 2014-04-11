(ns navigator.schema
  (:require [datomic-schematode.constraints :as constraints]))

(def schema
  [[:comp {:attrs [[:id-sk :string "Shared key for 3rd parties" :db.unique/identity]
                   [:name :string :indexed] ; not null
                   [:description :string :indexed]

                   ;; TODO: We need a fn here to ensure that #{:name :version} is unique
                   [:version :string]               ; not null
                   [:status :enum [:active :archived :preactive]] ; not null
                   [:tags :ref :many]]
           :dbfns [(constraints/unique :comp :name :version)]}]
   [:comp-tag {:attrs [[:name :string :indexed] ; not null
                       [:description :string]

                       ;; TODO: We need a fn here to ensure that #{:name :version} is unique
                       [:version :string]     ; not null
                       [:type :string :indexed] ; not null
                       [:isrequestable :boolean] ; default f
                       [:icon :uri]
                       [:status :enum [:active :archived]] ; not null
                       [:isfinal :boolean] ; default f
                       [:disp-ctxs :ref :many]
                       [:child-of :ref :many] ; replaces comp_tag2parent
                       ]}]
   [:comp-tag-disp-ctx {:attr [[:name :string]]}]
   [:perf-asmt {:attrs [[:id-sk :string "Shared key for 3rd parties" :db.unique/identity]
                        [:name :string]
                        [:version :string] ; not null

                        ;; Example types: :project :course-competency ... TODO: enum?
                        [:type :string] ; not null
                        ;; TODO: (:name :version :type) unique
                        [:duration-rating-days :bigint]
                        [:comps :ref :many]
                        [:credit-value-numerator :bigint]
                        [:credit-value-denominator :bigint]]}]
   [:task {:attrs [[:id-sk :string :db.unique/identity]
                   [:name :string]
                   [:version :string]
                   [:description :string]

                   ;; TODO: is this correct?
                   [:comps :ref :many]]}]
   [:user2comp {:attrs [[:sis-user-id :bigint :indexed]
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
                        [:qualitative-notes :string]]}]

   [:user2perf-asmt {:attrs [[:user :ref]
                             [:perf-asmt :ref]
                             ;; TODO: finish this
                             [:grade :boolean]]}]

   [:user2task {:attrs [[:user :ref]
                        [:task :ref]
                        ;; TODO: finish this
                        [:progress :boolean]]}]

   [:config {:attrs [[:key :string :indexed] ; not null
                     [:value :string :indexed]]}]
   ])

(def schema-map (into {} schema))
