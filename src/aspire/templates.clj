(ns aspire.templates
  (:require [net.cgrand.enlive-html :as en]
            [hiccup.core :as hc]
            [aspire.util :as a-util]))

;; Selectors
;; -----------------------
(def selectors {:common {:header [:nav#top-bar]
                         :side-nav [:nav#side-nav]}
                :onboarding {:intro [:main#content :div.intro]
                             :steps [:main#content :ol.onboarding-steps]
                             :step [:main#content :ol.onboarding-steps :li.step-1]}})

;; Utility fns
;; -----------------------
(defn render [template]
  (reduce str template))

(defn resnippet [template]
  (en/html-snippet (render template)))

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
(en/defsnippet common-header "public/index.html" (get-in selectors [:common :header])
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
(en/deftemplate onboarding "public/index.html"
  [alert-count profile-url user-disp-name welcome-head welcome-msg steps]
  (get-in selectors [:common :header]) (en/substitute (common-header alert-count profile-url user-disp-name))
  (get-in selectors [:onboarding :intro]) (en/substitute (onboarding-intro welcome-head welcome-msg))
  (get-in selectors [:onboarding :steps]) (en/content (map-indexed (fn dostep [n desc] (onboarding-step (inc n) desc)) steps))
  ;(search-active)
  [:form#search] (en/set-attr :data-active true)
  [:body#onboarding] (en/set-attr :data-search-active true)
  )

(en/deftemplate onboarding "public/index.html"
  [alert-count profile-url user-disp-name welcome-head welcome-msg steps]
  (get-in selectors [:common :header]) (en/substitute (common-header alert-count profile-url user-disp-name))
  (get-in selectors [:onboarding :intro]) (en/substitute (onboarding-intro welcome-head welcome-msg))
  (get-in selectors [:onboarding :steps]) (en/content (map-indexed (fn dostep [n desc] (onboarding-step (inc n) desc)) steps))
  ;(search-active)
  [:form#search] (en/set-attr :data-active true)
  [:body#onboarding] (en/set-attr :data-search-active true)
  )

(defmacro template-mauler [name & forms]
  `(en/deftemplate ~name ~@forms))

(defn search-on []
  [[:form#search] (en/set-attr :data-active true)
   [:body#onboarding] (en/set-attr :data-search-active true)])

(template-mauler yerp "public/admin/index.html"
                 [alert-count profile-url user-disp-name greeting steps]
                 (get-in selectors [:common :header]) (en/substitute (common-header alert-count profile-url user-disp-name))
                 [:body#admin] (en/set-attr :data-search-active false)
                 [:div.config-onboarding :h2] (en/content "Configure Page: Onboarding")
                 [:main#content :h1] (en/content "Aspire Administration")
                 [:form#config-onboarding] (en/set-attr :action "/config/page/onboarding")
                 [:form#config-onboarding :input#greeting] (en/set-attr :value greeting)
                 [:form#config-onboarding :textarea#steps] (en/content steps)
                 (:+mauling search-on)
                 ;;[:form#search] (en/set-attr :data-active true)
                 ;;[:body#onboarding] (en/set-attr :data-search-active true)
                 )


