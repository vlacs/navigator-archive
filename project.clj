(defproject org.vlacs/navigator "0.1.0-SNAPSHOT"
  :description "Navigator supports VLACS' vision for competency-based virtual education."
  :url "https://github.com/vlacs/navigator"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.cli "0.2.4"]
                 [com.datomic/datomic-free "0.9.4707"]
                 [liberator "0.10.0"]
                 [datomic-schematode "0.1.0-RC1"]
                 ^{:voom {:repo "https://github.com/vlacs/timber"}}
                 [org.vlacs/timber "0.1.2-20140429_021539-g5842210"]
                 [org.vlacs/hatch "0.1.2"]
                 [org.vlacs/helmsman "0.2.5"]]
  :resource-paths ["resources"]
  :pedantic? :warn ; :abort
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.4"]
                                  [ring "1.2.2"]]}}
  :repl-options {:init-ns user
                 :welcome (println "Anchors aweigh, and push the boat out. Please no panic stations.")})
