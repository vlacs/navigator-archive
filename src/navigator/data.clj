(ns navigator.data
  (:require [monocular]
            [datomic.api :as d]))

(def comp-monocular-map
  {:keywords {:comp filter-comp
              :competency filter-comp
              :tag filter-tag}
   :default filter-all})

(def comp-searcher (monocular/searcher comp-monocular-map))

(defn combine-search [searcher previous new-raw]
  (concat previous (monocular/parse searcher new-raw)))

(defn get-comp-map [db-conn ctx]
  (merge ctx
         (if-let [competencies (get-competencies db-conn)]
           (let [previous (get-in ctx [:request :params :search])
                 new-raw (get-in ctx [:request :params :search-raw])
                 combined-search (combine-search comp-searcher previous new-raw)]
             (if (empty? combined-search) {:competencies competencies}
                 {:competencies ((monocular/transform comp-searcher combined-search) competencies)
                  :monocular combined-search})))))
