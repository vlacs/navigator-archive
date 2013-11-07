(defproject aspire "0.1.0-SNAPSHOT"
  :description "Create, read, and update a PostgreSQL DB for Aspire.
  PostgreSQL will be replaced by Datomic in a future version of
  Aspire."
  :url "https://github.com/vlacs/aspire"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [jdbc-pg-init "0.1.1-SNAPSHOT"]
                 ;[org.clojure/java.jdbc "0.3.0-beta1"]
                 [org.clojure/tools.namespace "0.2.4"]
                 [postgresql/postgresql "8.4-702.jdbc4"]])
