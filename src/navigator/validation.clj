(ns navigator.validation
  (:require [schema.core :as s]))

(def validations
  {:user2comp      {:sis-user-id                        s/Int
                    :comp                               s/Str
                    :start-date                         s/Str
                    (s/optional-key :iscomplete)        s/Bool
                    (s/optional-key :isoverridden)      s/Bool
                    (s/optional-key :completion-date)   s/Str
                    :proposed-completion-date           s/Str
                    (s/optional-key :current-score)     s/Str
                    (s/optional-key :final-score)       s/Str
                    (s/optional-key :score-denominator) s/Int
                    (s/optional-key :score-type)        s/Keyword}})

(comment

  (defn comp-tag-in
    [comp-tag]
    (s/validate
     {:comp-tag/name s/Str
      (s/optional-key :comp-tag/description) s/Str
      :comp-tag/version s/Str
      :comp-tag/type s/Str
      (s/optional-key :comp-tag/isrequestable) s/Bool
      (s/optional-key :comp-tag/icon) s/Str
      :comp-tag/status s/Keyword
      (s/optional-key :comp-tag/isfinal) s/Bool
      ;; :comp-tag/disp-ctxs
      ;; :comp-tag/child-tags ref
      ;; :comp-tag/child-perf-asmts ref
      ;; :comp-tag/child-comps ref
      }
     comp-tag))

  )
