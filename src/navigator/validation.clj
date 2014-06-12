(ns navigator.validation
  (:require [schema.core :as s]))

(def validations
  {:task           {:id-sk        s/Str
                    :id-sk-origin s/Keyword
                    :name         s/Str
                    :version      s/Str
                    :description  s/Str}
   :perf-asmt      {:id-sk                    s/Str
                    :id-sk-origin             s/Keyword
                    (s/optional-key :name)    s/Str
                    :version                  s/Str
                    :type                     s/Keyword
                    :duration-rating-days     s/Int
                    :comps                    [s/Str]
                    :credit-value-numerator   s/Int
                    :credit-value-denominator s/Int}
   :user2comp      {:sis-user-id                        s/Int
                    :comp                               s/Str
                    :start-date                         s/Str
                    (s/optional-key :iscomplete)        s/Bool
                    (s/optional-key :isoverridden)      s/Bool
                    (s/optional-key :completion-date)   s/Str
                    :proposed-completion-date           s/Str
                    (s/optional-key :current-score)     s/Str
                    (s/optional-key :final-score)       s/Str
                    (s/optional-key :score-denominator) s/Int
                    (s/optional-key :score-type)        s/Keyword}
   :user2perf-asmt {:user  s/Str
                    :task  s/Str
                    :grade s/Bool}})

(defn validator
  [entity-type data]
  (let [validation (entity-type validations)]
    (try
      (s/validate
       validation
       data)
      (catch Exception e (.getMessage e)))))

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
