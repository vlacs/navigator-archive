(ns navigator.test-data
  (:require [navigator]))

(declare test-data)

(defn load-multiple!
  [db-conn entity-type entities]
  (doall (map #(navigator/tx-entity! db-conn entity-type %) entities)))

(defn load-test-data!
  ([db-conn] (load-test-data! db-conn test-data))
  ([db-conn data]
     (doall (map (fn [[entity-type entities]] (load-multiple! db-conn entity-type entities))
                 (partition 2 data)))))

(def test-data
  [:comp-tag [{:comp-tag/name "VLACS HS Diploma"
               :comp-tag/version "v1"
               :comp-tag/type "program-of-study"
               :comp-tag/status :comp-tag.status/active}
              {:comp-tag/name "Mathematics"
               :comp-tag/version "v1"
               :comp-tag/type "area-of-study"
               :comp-tag/status :comp-tag.status/active}
              {:comp-tag/name "English"
               :comp-tag/version "v1"
               :comp-tag/type "area-of-study"
               :comp-tag/status :comp-tag.status/active}
              {:comp-tag/name "Foreign Language"
               :comp-tag/version "v1"
               :comp-tag/type "area-of-study"
               :comp-tag/status :comp-tag.status/active}
              {:comp-tag/name "Geometry"
               :comp-tag/version "v1"
               :comp-tag/type "course"
               :comp-tag/status :comp-tag.status/active}
              {:comp-tag/name "English 1"
               :comp-tag/version "v1"
               :comp-tag/type "course"
               :comp-tag/status :comp-tag.status/active}
              {:comp-tag/name "Spanish 1"
               :comp-tag/version "v1"
               :comp-tag/type "course"
               :comp-tag/status :comp-tag.status/active}
              {:comp-tag/name "Music Theory"
               :comp-tag/version "v1"
               :comp-tag/type "course"
               :comp-tag/status :comp-tag.status/active}
              {:comp-tag/name "Quantitative Skills"
               :comp-tag/version "v1"
               :comp-tag/type "cluster"
               :comp-tag/status :comp-tag.status/active}
              {:comp-tag/name "Critical and Creative Thinking"
               :comp-tag/version "v1"
               :comp-tag/type "cluster"
               :comp-tag/status :comp-tag.status/active}
              {:comp-tag/name "Musical Intelligence"
               :comp-tag/version "v1"
               :comp-tag/type "cluster"
               :comp-tag/status :comp-tag.status/active}]
   :comp [{:comp/id-sk "1"
           :comp/name "Can add vectors"
           :comp/version "v1"
           :comp/status :comp.status/active
           :comp/tags [[:comp-tag/name "VLACS HS DIPLOMA" :comp-tag/version "v1"]
                       [:comp-tag/name "Mathematics" :comp-tag/version "v1"]]}
          {:comp/id-sk "2"
           :comp/name "Can draw circles"
           :comp/verion "v1"
           :comp/status :comp.status/active
           :comp/tags [[:comp-tag/name "VLACS HS DIPLOMA" :comp-tag/version "v1"]]}
          {:comp/id-sk "3"
           :comp/name "Can say \"hegemon\" in Spanish"
           :comp/version "v1"
           :comp/status :comp.status/active
           :comp/tags [[:comp-tag/name "VLACS HS DIPLOMA" :comp-tag/version "v1"]]}
          {:comp/id-sk "4"
           :comp/name "Can say \"disjunctivist\" in English"
           :comp/version "v1"
           :comp/status :comp.status/archived
           :comp/tags [[:comp-tag/name "VLACS HS DIPLOMA" :comp-tag/version "v1"]]}
          {:comp/id-sk "5"
           :comp/name "Can write resaurant criticism"
           :comp/version "v1"
           :comp/status :comp.status/active
           :comp/tags [[:comp-tag/name "VLACS HS DIPLOMA" :comp-tag/version "v1"]]}
          {:comp/id-sk "6"
           :comp/name "Can create balloon animals"
           :comp/version "v1"
           :comp/status :comp.status/active
           :comp/tags [[:comp-tag/name "VLACS HS DIPLOMA" :comp-tag/version "v1"]]}
          {:comp/id-sk "7"
           :comp/name "Can say \"disjunctivist\" in English"
           :comp/version "v2"
           :comp/status :comp.status/active
           :comp/tags [[:comp-tag/name "VLACS HS DIPLOMA" :comp-tag/version "v1"]]}
          {:comp/id-sk "8"
           :comp/name "Can compose trios for washboard"
           :comp/version "v1"
           :comp/status :comp.status/preactive
           :comp/tags [[:comp-tag/name "VLACS HS DIPLOMA" :comp-tag/version "v1"]]}]])
