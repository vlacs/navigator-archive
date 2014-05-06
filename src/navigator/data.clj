(ns navigator.data
  (:require [datomic.api :as d]))

;; TODO: make this POC real
(defn get-comp-map [db-conn ctx]
  (apply str (interpose " " (map #(d/touch
                                   (d/entity
                                    (d/db db-conn)
                                    (first %)))
                                 (d/q '[:find ?e :where [?e :db/ident]] (d/db db-conn))))))
