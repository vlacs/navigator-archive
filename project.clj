(defproject aspire "0.1.0-SNAPSHOT"
  :description "Aspire supports VLACS' vision for competency-based virtual education."
  :url "https://github.com/vlacs/aspire"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]

                 ;; see https://github.com/vlacs/jdbc-pg-init
                 [jdbc-pg-init "0.1.1-SNAPSHOT"]
                 ;;[org.clojure/java.jdbc "0.3.0-beta1"]
                 [org.clojure/tools.namespace "0.2.4"]
                 [postgresql/postgresql "8.4-702.jdbc4"]])
