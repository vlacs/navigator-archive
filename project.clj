(defproject aspire "0.1.0-SNAPSHOT"
  :description "Aspire supports VLACS' vision for competency-based virtual education."
  :url "https://github.com/vlacs/aspire"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.cli "0.2.4"]
                 [jdbc-pg-init "0.1.2"]
                 [clj-http "0.7.7" :exclusions [org.clojure/tools.reader]]
                 [org.clojure/clojurescript "0.0-2080"]
                 [ring "1.1.8"]
                 [compojure "1.1.5"]
                 [liberator "0.9.0"]
                 [prismatic/dommy "0.1.1"]
                 [hickory "0.5.2" :exclusions [org.clojure/data.json]]
                 [honeysql "0.4.2"]
                 [korma "0.3.0-RC5" :exclusions [org.clojure/java.jdbc]]
                 ]
  :pedantic? :warn ; :abort
  :plugins [[lein-cljsbuild "0.3.2"]
            [lein-ring "0.8.8" :exclusions [org.clojure/clojure]]
            [test2junit "1.0.1"]]
  :hooks [leiningen.cljsbuild]
  :source-paths ["src/clj"]
  :cljsbuild {:crossovers [aspire.model]
              :builds {:main {:source-paths ["src/client"]
                              :crossover-path "cljsbuild-crossovers"
                              :compiler {:pretty-print true
                                         ;; :source-map is broken for some inexplicable reason. :( --moquist
                                         ;;:source-map true
                                         :output-to "resources/public/js/aspire.js"
                                         :optimizations :simple}}}}
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.4"]
                                  [ring-mock "0.1.5"]]}}
  
  :main ^{:skip-aot true} aspire.core
  :ring {:handler stacker.server/app})
