(defproject aspire "0.1.0-SNAPSHOT"
  :description "Aspire supports VLACS' vision for competency-based virtual education."
  :url "https://github.com/vlacs/aspire"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.cli "0.2.4"]
                 [jdbc-pg-init "0.1.2"]
                 [clj-http "0.7.7" :exclusions [org.clojure/tools.reader]]
                 [enlive "1.1.5"]
                 [hiccup "1.0.4"]
                 [ring "1.2.1" :exclusions [org.clojure/tools.reader]]
                 [compojure "1.1.5" :exclusions [org.clojure/core.incubator]]
                 [liberator "0.10.0"]
                 [prismatic/dommy "0.1.1"]
                 [hickory "0.5.2" :exclusions [org.clojure/data.json]]
                 [honeysql "0.4.2"]
                 [korma "0.3.0-RC5" :exclusions [org.clojure/java.jdbc]]
                 [com.cemerick/friend "0.2.0"]
                 [digest "1.4.3"]
                 [saml20-clj "0.1.3"]
                 [com.taoensso/timbre "3.0.0" :exclusions [org.clojure/tools.macro]]
                 ]
  :pedantic? :warn ; :abort
  :plugins [[lein-ring "0.8.8" :exclusions [org.clojure/clojure]]
            [test2junit "1.0.1"]]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.4"]
                                  [ring-mock "0.1.5"]]}}

  :main ^{:skip-aot true} aspire.core
  :ring {:handler aspire.web/app})
