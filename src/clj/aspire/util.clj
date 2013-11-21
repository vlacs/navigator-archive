(ns aspire.util)

(defn output!
  "Call output! instead of using prn. Respects --verbose. Returns nil."
  [verbose? & msgs] (when verbose? (apply prn msgs)))

