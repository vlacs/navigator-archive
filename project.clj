(defproject org.vlacs/navigator "0.1.0-SNAPSHOT"
  :description "Navigator supports VLACS' vision for competency-based virtual education."
  :url "https://github.com/vlacs/navigator"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.cli "0.2.4"]
                 [com.datomic/datomic-free "0.9.4707"]
                 [datomic-schematode "0.1.0-RC1"]
                 [org.vlacs/hatch "0.1.2"]]
  :pedantic? :warn ; :abort
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.4"]]}}
  :repl-options {:init-ns user
                 :welcome (println "Anchors aweigh, and push the boat out. Please no panic stations.")})
