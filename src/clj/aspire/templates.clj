(ns aspire.templates
  (:require [net.cgrand.enlive-html :as en]
            [hiccup.core :as hc]))

(defn render [template]
  (reduce str template))

(en/deftemplate index "public/index.html"
  [user-display-name]
  [:script] nil
  [:body] (en/append
           {:tag "script"
            :attrs {:type "text/javascript"
                    :src "js/aspire.js"}}))

