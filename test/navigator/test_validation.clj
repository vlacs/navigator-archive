(ns navigator.test-validation
  (:require [clojure.test :refer :all]
            [navigator.validation :refer :all]))

(deftest vtask-in-test
  (testing "task-in"
    (is (= (task-in {}) '[{:task/description ("description must be present"), :task/version ("version must be present"), :task/name ("name must be present"), :task/id-sk-origin ("id-sk-origin must be present"), :task/id-sk ("id-sk must be present")} {:bouncer.core/errors {:task/description ("description must be present"), :task/version ("version must be present"), :task/name ("name must be present"), :task/id-sk-origin ("id-sk-origin must be present"), :task/id-sk ("id-sk must be present")}}]))))

(deftest comp-in-test
  (testing "comp-in"
    (is (= (comp-in {}) '[{:comp/tags ("tags must be present"), :comp/status ("status must be present"), :comp/version ("version must be present"), :comp/description ("description must be present"), :comp/name ("name must be present"), :comp/id-sk ("id-sk must be present")} {:bouncer.core/errors {:comp/tags ("tags must be present"), :comp/status ("status must be present"), :comp/version ("version must be present"), :comp/description ("description must be present"), :comp/name ("name must be present"), :comp/id-sk ("id-sk must be present")}}]))))

(deftest comp-tag-in-test
  (testing "comp-tag-in"
    (is (= (comp-tag-in {}) '[{:comp-tag/child-of ("child-of must be present"), :comp-tag/disp-ctxs ("disp-ctxs must be present"), :comp-tag/status ("status must be present"), :comp-tag/icon ("icon must be present"), :comp-tag/type ("type must be present"), :comp-tag/version ("version must be present"), :comp-tag/description ("description must be present"), :comp-tag/name ("name must be present")} {:bouncer.core/errors {:comp-tag/child-of ("child-of must be present"), :comp-tag/disp-ctxs ("disp-ctxs must be present"), :comp-tag/status ("status must be present"), :comp-tag/icon ("icon must be present"), :comp-tag/type ("type must be present"), :comp-tag/version ("version must be present"), :comp-tag/description ("description must be present"), :comp-tag/name ("name must be present")}}]))))

(deftest perf-asmt-in-test
  (testing "perf-asmt-in"
    (is (= (perf-asmt-in {}) '[{:perf-asmt/comps ("comps must be present"), :perf-asmt/type ("type must be present"), :perf-asmt/version ("version must be present"), :perf-asmt/name ("name must be present"), :perf-asmt/id-sk-origin ("id-sk-origin must be present"), :perf-asmt/id-sk ("id-sk must be present")} {:bouncer.core/errors {:perf-asmt/comps ("comps must be present"), :perf-asmt/type ("type must be present"), :perf-asmt/version ("version must be present"), :perf-asmt/name ("name must be present"), :perf-asmt/id-sk-origin ("id-sk-origin must be present"), :perf-asmt/id-sk ("id-sk must be present")}}]))))

(deftest user2comp-in-test
  (testing "user2comp-in"
    (is (= (user2comp-in {}) '[{:user2comp/score-type ("score-type must be present"), :user2comp/final-score ("final-score must be present"), :user2comp/current-score ("current-score must be present"), :user2comp/proposed-completion-date ("proposed-completion-date must be present"), :user2comp/start-date ("start-date must be present"), :user2comp/comp ("comp must be present"), :user2comp/sis-user-id ("sis-user-id must be present")} {:bouncer.core/errors {:user2comp/score-type ("score-type must be present"), :user2comp/final-score ("final-score must be present"), :user2comp/current-score ("current-score must be present"), :user2comp/proposed-completion-date ("proposed-completion-date must be present"), :user2comp/start-date ("start-date must be present"), :user2comp/comp ("comp must be present"), :user2comp/sis-user-id ("sis-user-id must be present")}}]))))

(deftest user2perf-asmt-in-test
  (testing "user2perf-asmt-in"
    (is (= (user2perf-asmt-in {}) '[{:user2perf-asmt/perf-asmt ("perf-asmt must be present"), :user2perf-asmt/user ("user must be present")} {:bouncer.core/errors {:user2perf-asmt/perf-asmt ("perf-asmt must be present"), :user2perf-asmt/user ("user must be present")}}]))))
