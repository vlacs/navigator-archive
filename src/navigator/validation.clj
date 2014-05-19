(ns navigator.validation
  (:require [bouncer
             [core :as b]
             [validators :as v]]))

(defn task-in
  [task]
  (b/validate task
              :task/id-sk        v/required
              :task/id-sk-origin v/required
              :task/name         v/required
              :task/version      v/required
              :task/description  v/required))

(defn comp-in
  [comp]
  (b/validate comp
              :comp/id-sk       v/required
              :comp/name        v/required
              :comp/description v/required
              :comp/version     v/required
              :comp/status      v/required
              :comp/tags        v/required))

(defn comp-tag-in
  [comp-tag]
  (b/validate comp-tag
              :comp-tag/name        v/required
              :comp-tag/description v/required
              :comp-tag/version     v/required
              :comp-tag/type        v/required
              :comp-tag/icon        v/required
              :comp-tag/status      v/required
              :comp-tag/disp-ctxs   v/required
              :comp-tag/child-of    v/required))

(defn perf-asmt-in
  [perf-asmt]
  (b/validate perf-asmt
              :perf-asmt/id-sk        v/required
              :perf-asmt/id-sk-origin v/required
              :perf-asmt/name         v/required
              :perf-asmt/version      v/required
              :perf-asmt/type         v/required
              :perf-asmt/comps        v/required))

(defn user2comp-in
  [user2comp]
  (b/validate user2comp
              :user2comp/sis-user-id              v/required
              :user2comp/comp                     v/required
              :user2comp/start-date               v/required
              :user2comp/proposed-completion-date v/required
              :user2comp/current-score            v/required
              :user2comp/final-score              v/required
              :user2comp/score-type               v/required))

(defn user2perf-asmt-in
  [user2perf-asmt]
  (b/validate user2perf-asmt
              :user2perf-asmt/user      v/required
              :user2perf-asmt/perf-asmt v/required))
