(ns aspire.templates
  (:require [net.cgrand.enlive-html :as en]
            [hiccup.core :as hc]))

(def header-sel [:nav#top-bar])

(defn render [template]
  (reduce str template))

(en/defsnippet header "public/index.html" header-sel
  [alert-count profile-url user-disp-name]
  [:li.alerts :a] (en/set-attr :href "/alerts")
  [:li.alerts :a :span.alert-count] (en/content (str alert-count))
  [:li.profile :a] (en/do->
                    (en/set-attr :href profile-url)
                    (en/content user-disp-name))
  [:li.logout :a] (en/set-attr :href "/logout"))

(en/defsnippet intro "public/index.html" [:main#content :div.intro]
  [title desc]
  [:h2] (en/content title)
  [:h2 :> :p] (en/content desc))

#_(en/defsnippet onboarding-steps "public/index.html"
  [:main#content {[:ol.onboarding-steps] [[:li.step-1 (en/nth-of-type 1)]]}]
  [steps]
  )

(en/deftemplate base "public/index.html"
  [alert-count profile-url user-disp-name]
  [header-sel] (en/content (header alert-count profile-url user-disp-name)))

