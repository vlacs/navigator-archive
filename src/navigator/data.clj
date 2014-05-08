(ns navigator.data
  (:require [monocular.core :as monocular]
            [datomic.api :as d]
            [clojure.set :refer [union]]
            [clojure.edn :as edn]))

(defn- get-comp [db eid]
  (let [comp (into {} (d/entity db eid))]
    (assoc comp
           :comp/tags
           (map #(d/entity db (:db/id %))
                (:comp/tags comp)))))

(defn get-active-competencies [db-conn]
  (let [db (d/db db-conn)]
    (map #(get-comp db (first %))
         (d/q '[:find ?e :where [?e :comp/status :comp.status/active]] db))))

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
  (union (set (filter-comp s comps))
         (set (filter-tag s comps))))

(def comp-monocular-map
  {:keywords {:comp filter-comp
              :competency filter-comp
              :tag filter-tag}
   :default filter-all})

(def comp-searcher (monocular/searcher comp-monocular-map))

(defn combine-search [searcher previous search]
  (into [] (concat previous
                   (if (not-empty search)
                     (monocular/parse searcher search)
                     []))))

(defn parse-search [ctx]
  (merge ctx
         {:monocular (or (combine-search comp-searcher
                                         (edn/read-string (get-in ctx [:request :params "previous"]))
                                         (get-in ctx [:request :params "search"]))
                         [])}))

(defn get-comp-map [db-conn ctx]
  (merge ctx {:competencies (let [competencies (get-active-competencies db-conn)
                                  search (:monocular ctx)]
                              (if (empty? search)
                                competencies
                                ((monocular/transform comp-searcher search) competencies)))}))
