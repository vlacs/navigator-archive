(ns navigator.util
  (:require [clojure.string :as str]
            [navigator.sqldb :as n-sql]))

(defn output!
  "Call output! instead of using prn. Respects --verbose. Returns nil."
  [verbose? & msgs] (when verbose? (apply prn msgs)))

(defn keyword-in-ns
  [target-ns keyword-in]
  (keyword (str target-ns "/" (name keyword-in))))

(defn keywords->ns
  [target-ns & keywords]
  (let [transform-fn (partial keyword-in-ns target-ns)]
    (map transform-fn (flatten keywords))))

(defn get-config!
  "Get the specified config key from the DB."
  [key]
  (:value
   (first
    (n-sql/select! {:select [:value]
                    :from [:config]
                    :where [:= :key key]}))))

(defn set-config!
  "Set the value of the specified config.key in the DB, inserting a new row or updating as appropriate."
  [key value]
  (if-let [_ (get-config! key)]
    (n-sql/update! "UPDATE config SET value = ? WHERE key = ?" [value key])
    (n-sql/update! "INSERT INTO config (value, key) VALUES (?, ?)" [value key])))

(defn split-paragraphs
  "Split the given text into a vector of paragraphs."
  [text]
  (filter #(not (str/blank? %))
          (str/split text #"[\n\r]")))

