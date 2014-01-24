(ns aspire.templates
  (:require [net.cgrand.enlive-html :as en]
            [hiccup.core :as hc]))

;; Selectors
;; -----------------------
(def selectors {:onboarding {:header [:nav#top-bar]
                             :intro [:main#content :div.intro]
                             :steps [:main#content :ol.onboarding-steps]
                             :step [:main#content :ol.onboarding-steps :li.step-1]}})

;; Utility fns
;; -----------------------
(defn render [template]
  (reduce str template))

(defmacro maybe-substitute
  "Shamelessly borrowed from https://github.com/swannodette/enlive-tutorial/blob/master/src/tutorial/utils.clj"
  ([expr] `(if-let [x# ~expr] (html/substitute x#) identity))
  ([expr & exprs] `(maybe-substitute (or ~expr ~@exprs))))

(defmacro maybe-content
  "Shamelessly borrowed from https://github.com/swannodette/enlive-tutorial/blob/master/src/tutorial/utils.clj"
  ([expr] `(if-let [x# ~expr] (html/content x#) identity))
    ([expr & exprs] `(maybe-content (or ~expr ~@exprs))))

;; Template Snippets
;; -----------------------
(en/defsnippet onboarding-header "public/index.html" (get-in selectors [:onboarding :header])
  [alert-count profile-url user-disp-name]
  [:li.alerts :a] (en/set-attr :href "/alerts")
  [:li.alerts :a :span.alert-count] (en/content (str alert-count))
  [:li.profile :a] (en/do->
                    (en/set-attr :href profile-url)
                    (en/content user-disp-name))
  [:li.logout :a] (en/set-attr :href "/logout"))

(en/defsnippet onboarding-intro "public/index.html" (get-in selectors [:onboarding :intro])
  [title desc]
  [:h2] (en/content title)
  [:p] (en/content desc))

(en/defsnippet onboarding-step "public/index.html"
  (get-in selectors [:onboarding :step])
  [n desc]
  [:strong] (en/content (str "Step " n))
  [:span] (en/content desc))

#_(en/defsnippet onboarding-steps "public/index.html"
    (get-in selectors [:onboarding :steps])
  [steps]
  [:strong]
  )

;; Templates
;; -----------------------
(en/deftemplate base "public/index.html"
  [alert-count profile-url user-disp-name welcome-head welcome-msg steps]
  (get-in selectors [:onboarding :header]) (en/substitute (onboarding-header alert-count profile-url user-disp-name))
  (get-in selectors [:onboarding :intro]) (en/substitute (onboarding-intro welcome-head welcome-msg))
  (get-in selectors [:onboarding :steps]) (en/content (map-indexed (fn dostep [n desc] (onboarding-step (inc n) desc)) steps)))


