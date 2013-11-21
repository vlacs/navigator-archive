(defproject aspire "0.1.0-SNAPSHOT"
  :description "Aspire supports VLACS' vision for competency-based virtual education."
  :url "https://github.com/vlacs/aspire"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.cli "0.2.4"]

                 ;; see https://github.com/vlacs/jdbc-pg-init
                 [jdbc-pg-init "0.1.2-SNAPSHOT"]

                 [org.clojure/clojurescript "0.0-1896"]
                 [ring "1.1.8"]
                 [compojure "1.1.5"]
                 [liberator "0.9.0"]

                 ;; Do we want both dommy and tinsel? If not, which
                 ;; one do we want?
                 [prismatic/dommy "0.1.1"]
                 [tinsel "0.4.0"]

                 [hiccup "1.0.4"]
                 [honeysql "0.4.2"]
                 [korma "0.3.0-RC5" :exclusions [org.clojure/java.jdbc]]
                 [org.clojure/core.async "0.1.242.0-44b1e3-alpha"]
                 ]
  :pedantic? :warn ; :abort
  :plugins [[lein-cljsbuild "0.3.2"]
            [lein-ring "0.8.8"]]
  :hooks [leiningen.cljsbuild]
  :source-paths ["src/clj"]
  :cljsbuild { 
    :builds {
      :main {
        :source-paths ["src/cljs"]
        :compiler {:output-to "resources/public/js/cljs.js"
                   :optimizations :simple
                   :pretty-print true}
        :jar true}}}

  :main ^:skip-aot aspire.core
  :ring {:handler stacker.server/app})
