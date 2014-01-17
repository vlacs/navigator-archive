(ns aspire.util)

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

