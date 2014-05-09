(ns navigator.test-data
  (:require [navigator]
            [datomic.api :as d]
            [hatch]))

(declare test-data)

(defn preclean [[entity-type entities]]
  (map #(hatch/clean-entity navigator/partitions navigator/valid-attrs entity-type %1) entities))

(defn load-test-data!
  ([db-conn] (load-test-data! db-conn test-data))
  ([db-conn data]
     (->> (partition 2 data)
          (map preclean)
          (reduce concat)
          (hatch/tx! db-conn))))

(def test-data
  [:comp-tag [{:db/id (d/tempid (:comp-tag navigator/partitions) -1)
               :comp-tag/name "VLACS HS Diploma"
               :comp-tag/version "v1"
               :comp-tag/type "program-of-study"
               :comp-tag/status :comp-tag.status/active}
              {:db/id (d/tempid (:comp-tag navigator/partitions) -2)
               :comp-tag/name "Mathematics"
               :comp-tag/version "v1"
               :comp-tag/type "area-of-study"
               :comp-tag/status :comp-tag.status/active}
              {:db/id (d/tempid (:comp-tag navigator/partitions) -3)
               :comp-tag/name "English"
               :comp-tag/version "v1"
               :comp-tag/type "area-of-study"
               :comp-tag/status :comp-tag.status/active}
              {:db/id (d/tempid (:comp-tag navigator/partitions) -4)
               :comp-tag/name "Foreign Language"
               :comp-tag/version "v1"
               :comp-tag/type "area-of-study"
               :comp-tag/status :comp-tag.status/active}
              {:db/id (d/tempid (:comp-tag navigator/partitions) -7)
               :comp-tag/name "Spanish 1"
               :comp-tag/version "v1"
               :comp-tag/type "course"
               :comp-tag/status :comp-tag.status/active
               :comp-tag/child-of [(d/tempid (:comp-tag navigator/partitions) -4)]}
              {:db/id (d/tempid (:comp-tag navigator/partitions) -9)
               :comp-tag/name "Quantitative Skills"
               :comp-tag/version "v1"
               :comp-tag/type "cluster"
               :comp-tag/status :comp-tag.status/active}
              {:db/id (d/tempid (:comp-tag navigator/partitions) -5)
               :comp-tag/name "Geometry"
               :comp-tag/version "v1"
               :comp-tag/type "course"
               :comp-tag/status :comp-tag.status/active
               :comp-tag/child-of [(d/tempid (:comp-tag navigator/partitions) -9)
                                   (d/tempid (:comp-tag navigator/partitions) -2)
                                   (d/tempid (:comp-tag navigator/partitions) -1)]}
              {:db/id (d/tempid (:comp-tag navigator/partitions) -10)
               :comp-tag/name "Critical and Creative Thinking"
               :comp-tag/version "v1"
               :comp-tag/type "cluster"
               :comp-tag/status :comp-tag.status/active}
              {:db/id (d/tempid (:comp-tag navigator/partitions) -6)
               :comp-tag/name "English 1"
               :comp-tag/version "v1"
               :comp-tag/type "course"
               :comp-tag/status :comp-tag.status/active
               :comp-tag/child-of [(d/tempid (:comp-tag navigator/partitions) -10)
                                   (d/tempid (:comp-tag navigator/partitions) -3)
                                   (d/tempid (:comp-tag navigator/partitions) -1)]}
              {:db/id (d/tempid (:comp-tag navigator/partitions) -11)
               :comp-tag/name "Musical Intelligence"
               :comp-tag/version "v1"
               :comp-tag/type "cluster"
               :comp-tag/status :comp-tag.status/active}
              {:db/id (d/tempid (:comp-tag navigator/partitions) -8)
               :comp-tag/name "Music Theory"
               :comp-tag/version "v1"
               :comp-tag/type "course"
               :comp-tag/status :comp-tag.status/active
               :comp-tag/child-of [(d/tempid (:comp-tag navigator/partitions) -10)
                                   (d/tempid (:comp-tag navigator/partitions) -11)]}]
   :comp [{:comp/id-sk "1"
           :comp/name "Can add vectors"
           :comp/version "v1"
           :comp/status :comp.status/active
           :comp/tags [(d/tempid (:comp-tag navigator/partitions) -1)
                       (d/tempid (:comp-tag navigator/partitions) -2)
                       (d/tempid (:comp-tag navigator/partitions) -9)
                       (d/tempid (:comp-tag navigator/partitions) -5)]}
          {:comp/id-sk "2"
           :comp/name "Can draw circles"
           :comp/verion "v1"
           :comp/status :comp.status/active
           :comp/tags [(d/tempid (:comp-tag navigator/partitions) -1)
                       (d/tempid (:comp-tag navigator/partitions) -2)
                       (d/tempid (:comp-tag navigator/partitions) -9)
                       (d/tempid (:comp-tag navigator/partitions) -5)]}
          {:comp/id-sk "3"
           :comp/name "Can say \"hegemon\" in Spanish"
           :comp/version "v1"
           :comp/status :comp.status/active
           :comp/tags [(d/tempid (:comp-tag navigator/partitions) -1)
                       (d/tempid (:comp-tag navigator/partitions) -4)
                       (d/tempid (:comp-tag navigator/partitions) -7)]}
          {:comp/id-sk "4"
           :comp/name "Can say \"disjunctivist\" in English"
           :comp/version "v1"
           :comp/status :comp.status/archived
           :comp/tags [(d/tempid (:comp-tag navigator/partitions) -1)
                       (d/tempid (:comp-tag navigator/partitions) -3)
                       (d/tempid (:comp-tag navigator/partitions) -6)]}
          {:comp/id-sk "5"
           :comp/name "Can write restaurant criticism"
           :comp/version "v1"
           :comp/status :comp.status/active
           :comp/tags [(d/tempid (:comp-tag navigator/partitions) -10)
                       (d/tempid (:comp-tag navigator/partitions) -6)]}
          {:comp/id-sk "6"
           :comp/name "Can create balloon animals"
           :comp/version "v1"
           :comp/status :comp.status/active
           :comp/tags [(d/tempid (:comp-tag navigator/partitions) -1)
                       (d/tempid (:comp-tag navigator/partitions) -11)]}
          {:comp/id-sk "7"
           :comp/name "Can say \"disjunctivist\" in English"
           :comp/version "v2"
           :comp/status :comp.status/active
           :comp/tags [(d/tempid (:comp-tag navigator/partitions) -1)
                       (d/tempid (:comp-tag navigator/partitions) -3)
                       (d/tempid (:comp-tag navigator/partitions) -6)]}
          {:comp/id-sk "8"
           :comp/name "Can compose trios for washboard"
           :comp/version "v1"
           :comp/status :comp.status/preactive
           :comp/tags [(d/tempid (:comp-tag navigator/partitions) -11)]}]])
