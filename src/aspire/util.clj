(ns aspire.util)

(def ^:dynamic *isverbose* false)
(defn output!
  "Call output! instead of using prn. Respects --verbose. Returns nil."
  [& msgs] (when *isverbose* (apply prn msgs)))

