(ns navigator.validation
  (:require [valip.core :refer [validate]]
            [valip.predicates :refer [digits? present?]]))


(defn valid?
  [validator input]
  (let [res (validator input)]
    (if (nil? res)
      true
      false)))

(defn validate-task-in
  [task]
  (validate task
            [:task/id-sk        present? "must be present"]
            [:task/id-sk        digits?  "must consist of digits"]
            [:task/id-sk-origin present? "must be present"]
            [:task/name         present? "must be present"]
            [:task/version      present? "must be present"]
            [:task/description  present? "must be present"]))

(defn validate-comp-in
  [comp]
  (validate comp
            [:comp/id-sk       present? "must be present"]
            [:comp/id-sk       digits?  "must consist of digits"]
            [:comp/name        present? "must be present"]
            [:comp/description present? "must be present"]
            [:comp/version     present? "must be present"]
            [:comp/status      present? "must be present"]
            [:comp/tags        present? "must be present"]))

(defn validate-comp-tag-in
  [comp-tag]
  (validate comp-tag
            [:comp-tag/name          present? "must be present"]
            [:comp-tag/description   present? "must be present"]
            [:comp-tag/version       present? "must be present"]
            [:comp-tag/type          present? "must be present"]
            [:comp-tag/isrequestable present? "must be present"]
            [:comp-tag/icon          present? "must be present"]
            [:comp-tag/status        present? "must be present"]
            [:comp-tag/isfinal       present? "must be present"]
            [:comp-tag/disp-ctxs     present? "must be present"]
            [:comp-tag/child-of      present? "must be present"]))

(defn validate-perf-asmt-in
  [perf-asmt]
  (validate perf-asmt
            [:perf-asmt/id-sk                    present? "must be present"]
            [:perf-asmt/id-sk                    digits?  "must consist of digits"]
            [:perf-asmt/id-sk-origin             present? "must be present"]
            [:perf-asmt/name                     present? "must be present"]
            [:perf-asmt/version                  present? "must be present"]
            [:perf-asmt/type                     present? "must be present"]
            [:perf-asmt/duration-rating-days     present? "must be present"]
            [:perf-asmt/comps                    present? "must be present"]
            [:perf-asmt/credit-value-numerator   present? "must be present"]
            [:perf-asmt/credit-value-denominator present? "must be present"]))

(defn validate-user2comp-in
  [user2comp]
  (validate user2comp
            [:user2comp/sis-user-id              present? "must be present"]
            [:user2comp/comp                     present? "must be present"]
            [:user2comp/start-date               present? "must be present"]
            [:user2comp/proposed-completion-date present? "must be present"]
            [:user2comp/current-score            present? "must be present"]
            [:user2comp/final-score              present? "must be present"]
            [:user2comp/score-denominator        present? "must be present"]
            [:user2comp/score-type               present? "must be present"]))

(defn validate-user2perf-asmt-in
  [user2perf-asmt]
  (validate user2perf-asmt
            [:user2perf-asmt/user      present? "must be present"]
            [:user2perf-asmt/perf-asmt present? "must be present"]
            [:user2perf-asmt/grade     present? "must be present"]))
