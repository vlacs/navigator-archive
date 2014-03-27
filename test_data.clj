(ns navigator.test-data
  (:require [navigator.data.competency :as c]))

(defn load-test-data! [conn]
  (create-competency conn "Can add vectors" "v1" :comp.status/active)
  (create-competency conn "Can draw circles" "v1" :comp.status/active)
  (create-competency conn "Can say \"hegemon\" in Spanish" "v1" :comp.status/active)
  (create-competency conn "Can say \"disjunctivist\" in English" "v1" :comp.status/active)
  (create-competency conn "Can write resaurant criticism" "v1" :comp.status/active)
  (create-competency conn "Can create balloon animals" "v1" :comp.status/active)
  (create-competency conn "Can compose trios for washboard" "v1" :comp.status/active)

  (create-comp-tag conn "VLACS HS Diploma", "v1", "program-of-study", :comp-tag.status/active)
  (create-comp-tag conn "Mathematics", "v1", "area-of-study", :comp-tag.status/active)
  (create-comp-tag conn "English", "v1", "area-of-study", :comp-tag.status/active)
  (create-comp-tag conn "Foreign Language", "v1", "area-of-study", :comp-tag.status/active)
  (create-comp-tag conn "Geometry", "v1", "course", :comp-tag.status/active)
  (create-comp-tag conn "English 1", "v1", "course", :comp-tag.status/active)
  (create-comp-tag conn "Spanish 1", "v1", "course", :comp-tag.status/active)
  (create-comp-tag conn "Music Theory", "v1", "course", :comp-tag.status/active)
  (create-comp-tag conn "Quantitative Skills", "v1", "cluster", :comp-tag.status/active)
  (create-comp-tag conn "Critical and Creative Thinking", "v1", "cluster", :comp-tag.status/active)
  (create-comp-tag conn "Musical Intelligence", "v1", "cluster", :comp-tag.status/active)





  )
