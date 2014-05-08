(ns navigator.data
  (:require [monocular.core :as monocular]
            [datomic.api :as d]
            [clojure.set :refer [union]]))

(defn- get-comp [db eid]
  (let [comp (into {} (d/entity db eid))]
    (assoc comp
           :comp/tags
           (map #(d/entity db (:db/id %))
                (:comp/tags comp)))))

(defn get-competencies [db-conn]
  (let [db (d/db db-conn)]
    (map #(get-comp db (first %))
         (d/q '[:find ?e :where [?e :comp/name]] db))))

(defn re-find-nil [pattern string]
  (if string
    (re-find pattern string)))

(defn filter-comp [s comps]
  (let [pattern (re-pattern (str "(?i)" s))]
    (filter #(or (re-find-nil pattern (:comp/name %))
                 (re-find-nil pattern (:comp/description %)))
            comps)))

(defn filter-tag [s comps]
  (let [pattern (re-pattern (str "(?i)" s))]
    (remove (fn filter-tag- [c]
              (empty? (remove #(not (or (re-find-nil pattern (:comp-tag/name %))
                                        (re-find-nil pattern (:comp-tag/description %))))
                              (:comp/tags c))))
            comps)))

(defn filter-all [s comps]
  (union (filter-comp s comps)
         (filter-tag s comps)))

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
