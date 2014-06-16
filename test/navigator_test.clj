(ns navigator-test
  (:require [clojure.test :refer :all]
            [datomic.api :as d]
            [navigator.test-config :as nt-config]
            [navigator.testslib :as n-tl]
            [datomic-schematode :as dst]))

(use-fixtures :once nt-config/testing-fixture)

(deftest create-comps
  (testing "raw assertion"
    (is (n-tl/ensure-tx (dst/tx (:db-conn nt-config/system)
                                       :enforce
                                       [{:db/id (d/tempid :db.part/user)
                                         :comp/id-sk "i am a shared key"
                                         :comp/name "COMP there it is"}]))))
  (let [comp {:comp/name "I will keep typing"
              :comp/version "v1"
              :comp/status :comp.status/active
              :comp/id-sk "yeep"}]
    (testing "create competency"
      (is (n-tl/ensure-tx (navigator/tx-entity! (:db-conn nt-config/system) :comp comp))))
    (testing "get transacted comp"
      (is (= (sort comp)
             (sort (into {} (navigator/get-competency (:db-conn nt-config/system) "yeep"))))))
    (testing "create duplicate competency"
      (is (n-tl/should-throw @(navigator/tx-entity! (:db-conn nt-config/system)
                                                    :comp
                                                    (assoc comp :comp/id-sk "jeep")))))))

(deftest update-comps
  (let [comp {:comp/name "can I update?"
              :comp/version "v1"
              :comp/status :comp.status/active
              :comp/id-sk "yeep"}
        comp-archived (assoc comp :comp/status :comp.status/archived)]
    (testing "update competency"
      (is (n-tl/ensure-tx (navigator/tx-entity! (:db-conn nt-config/system) :comp comp)))
      (is (n-tl/ensure-tx (navigator/tx-entity! (:db-conn nt-config/system) :comp comp-archived)))
      (is (= (sort comp-archived)
             (sort (into {} (navigator/get-competency (:db-conn nt-config/system) "yeep"))))))))


(comment
  (map #(keys (deref %))
       (dst/load-schema! (d/connect config/db-url) [[:u {:attrs [[:a :string]]}]]))
  '((:db-before :db-after :tx-data :tempids) (:db-before :db-after :tx-data :tempids)))

(comment
  (def test-schemas
    [[:user {:attrs [[:username :string :db.unique/identity]
                     [:pwd :string "Hashed password string"]
                     [:email :string :indexed]
                     [:dob :string :indexed]
                     [:lastname :string :indexed]
                     [:status :enum [:pending :active :inactive :cancelled]]
                     [:group :ref :many]]
             :part :app
             :dbfns [(ds-constraints/unique :user :lastname :dob)]}]
     [:group {:attrs [[:name :string]
                      [:permission :string :many]]
              ;; testing without :part
              }]])

  (deftest expand-fields-test
    (testing "expand-fields"
      (is (= (ds/expand-fields
              (get-in (apply hash-map (flatten test-schemas))
                      [:user :attrs]))
             {"group" [:ref #{:many}], "status" [:enum #{[:pending :active :inactive :cancelled]}], "lastname" [:string #{:indexed}], "dob" [:string #{:indexed}], "email" [:string #{:indexed}], "pwd" [:string #{"Hashed password string"}], "username" [:string #{:db.unique/identity}]}))))

  (deftest expand-schemas-test
    (testing "expand-schemas"
      (is (= (ds/expand-schemas test-schemas)
             [{:part :db.part/app, :namespace "user", :name "user", :basetype :user, :fields {"group" [:ref #{:many}], "status" [:enum #{[:pending :active :inactive :cancelled]}], "lastname" [:string #{:indexed}], "dob" [:string #{:indexed}], "email" [:string #{:indexed}], "pwd" [:string #{"Hashed password string"}], "username" [:string #{:db.unique/identity}]}} {:part :db.part/user, :namespace "group", :name "group", :basetype :group, :fields {"permission" [:string #{:many}], "name" [:string #{}]}}]))))

  (deftest schematize-test
    (testing "schematize"
      (is (= (ds/schematize test-schemas (constantly -1))
             '([{:db/id -1, :db/ident :db.part/app, :db.install/_partition :db.part/db}] ({:db/noHistory false, :db/cardinality :db.cardinality/many, :db.install/_attribute :db.part/db, :db/index false, :db/fulltext false, :db/doc "", :db/isComponent false, :db/valueType :db.type/ref, :db/ident :user/group, :db/id -1} {:db/noHistory false, :db/cardinality :db.cardinality/one, :db.install/_attribute :db.part/db, :db/index false, :db/fulltext false, :db/doc "", :db/isComponent false, :db/valueType :db.type/ref, :db/ident :user/status, :db/id -1} [:db/add -1 :db/ident :user.status/pending] [:db/add -1 :db/ident :user.status/active] [:db/add -1 :db/ident :user.status/inactive] [:db/add -1 :db/ident :user.status/cancelled] {:db/noHistory false, :db/cardinality :db.cardinality/one, :db.install/_attribute :db.part/db, :db/index true, :db/fulltext false, :db/doc "", :db/isComponent false, :db/valueType :db.type/string, :db/ident :user/lastname, :db/id -1} {:db/noHistory false, :db/cardinality :db.cardinality/one, :db.install/_attribute :db.part/db, :db/index true, :db/fulltext false, :db/doc "", :db/isComponent false, :db/valueType :db.type/string, :db/ident :user/dob, :db/id -1} {:db/noHistory false, :db/cardinality :db.cardinality/one, :db.install/_attribute :db.part/db, :db/index true, :db/fulltext false, :db/doc "", :db/isComponent false, :db/valueType :db.type/string, :db/ident :user/email, :db/id -1} {:db/noHistory false, :db/cardinality :db.cardinality/one, :db.install/_attribute :db.part/db, :db/index false, :db/fulltext false, :db/doc "Hashed password string", :db/isComponent false, :db/valueType :db.type/string, :db/ident :user/pwd, :db/id -1} {:db/noHistory false, :db/cardinality :db.cardinality/one, :db.install/_attribute :db.part/db, :db/index false, :db/unique :db.unique/identity, :db/fulltext false, :db/doc "", :db/isComponent false, :db/valueType :db.type/string, :db/ident :user/username, :db/id -1}) ({:db/noHistory false, :db/cardinality :db.cardinality/many, :db.install/_attribute :db.part/db, :db/index false, :db/fulltext false, :db/doc "", :db/isComponent false, :db/valueType :db.type/string, :db/ident :group/permission, :db/id -1} {:db/noHistory false, :db/cardinality :db.cardinality/one, :db.install/_attribute :db.part/db, :db/index false, :db/fulltext false, :db/doc "", :db/isComponent false, :db/valueType :db.type/string, :db/ident :group/name, :db/id -1}))))))

  (deftest load-schema!-test
    (testing "load-schema!"
      (is (= (map #(keys (deref %))
                  (ds/load-schema! (d/connect config/db-url) [[:u {:attrs [[:a :string]]}]]))
             '((:db-before :db-after :tx-data :tempids) (:db-before :db-after :tx-data :tempids))))))




  )
