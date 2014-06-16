(ns navigator.test-config
  (:require [datomic.api :as d]
            [datomic-schematode :as dst]
            [navigator.schema :as schema]
            [navigator.validation :refer [validations]]
            [navigator]
            [helmsman]
            [schema.core :as s]
            [timber.core :as timber]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.params :refer [wrap-params]]))

(def system {:datomic-uri "datomic:mem://navigator-test"})
(def datomic-uri (:datomic-uri system))

(defn routes [system]
  (helmsman/compile-routes (into [] (concat timber/helmsman-assets
                                            (navigator/helmsman-def system)
                                            [[wrap-params]]))))

(defn start-datomic! [system]
  (d/create-database datomic-uri)
  (assoc system :db-conn
         (d/connect datomic-uri)))

(defn load-schema! [system]
  [(dst/init-schematode-constraints! (:db-conn system))
   (dst/load-schema! (:db-conn system) schema/schema)])

(defn stop-datomic! [system]
  (dissoc system :db-conn)
  (d/delete-database datomic-uri)
  system)

(defn start-jetty! [system]
  (assoc system :jetty (jetty/run-jetty (routes system) {:port 8081 :join? false})))

(defn stop-jetty! [system]
  (if-let [jetty-server (:jetty system)]
    (.stop jetty-server)
    (dissoc system :jetty)))

(defn start!
  "Starts the current development system."
  [& options]
  (alter-var-root #'system start-datomic!)
  (load-schema! system)
  (if (some #(= :jetty %) options)
    (alter-var-root #'system start-jetty!)))

(defn stop!
  "Shuts down and destroys the current development system."
  []
  (alter-var-root #'system
                  (fn [s] (when s (-> s
                                      (stop-datomic!)
                                      (stop-jetty!))))))

(defn validator
  [entity-type data]
  (let [validation (entity-type validations)]
    (if validation
      (try
        (s/validate
         validation
         data)
        (catch Exception e (.getMessage e)))
      data)))

(defn testing-fixture [f]
  (start!)
  (f)
  (stop!))
