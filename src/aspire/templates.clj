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

;; Utilities
;; -----------------------
(defmacro liven
  "Trades the caching done by en/snippet and en/template for the
   flexibility and composability of:
   (1) taking in nodes at runtime and
   (2) returning nodes instead of strings."
  [nodes & forms]
  `(en/flatmap (en/transformation ~@forms) (en/html-resource ~nodes)))

(defmacro maybe-substitute
  "Shamelessly borrowed from https://github.com/swannodette/enlive-tutorial/blob/master/src/tutorial/utils.clj"
  ([expr] `(if-let [x# ~expr] (html/substitute x#) identity))
  ([expr & exprs] `(maybe-substitute (or ~expr ~@exprs))))

(defmacro maybe-content
  "Shamelessly borrowed from https://github.com/swannodette/enlive-tutorial/blob/master/src/tutorial/utils.clj"
  ([expr] `(if-let [x# ~expr] (html/content x#) identity))
    ([expr & exprs] `(maybe-content (or ~expr ~@exprs))))

(defn render
  "Shamelessly borrowed from https://github.com/swannodette/enlive-tutorial/blob/master/src/tutorial/utils.clj"
  [template]
  (apply str template))

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

;; Transformers
;; -----------------------
(defn search-toggle [nodes tf]
  (let [tf (if tf true false)]
    (liven nodes
           [:form#search] (en/set-attr :data-active tf)
           [:body] (en/set-attr :data-search-active tf))))

;; Templates
;; -----------------------
(defn onboarding [alert-count profile-url user-disp-name welcome-head welcome-msg steps]
  (-> (liven "public/index.html"
             (get-in selectors [:common :header])
             (en/substitute (common-header alert-count profile-url user-disp-name))
             (get-in selectors [:onboarding :intro])
             (en/substitute (onboarding-intro welcome-head welcome-msg))
             (get-in selectors [:onboarding :steps])
             (en/content (map-indexed (fn dostep [n desc] (onboarding-step (inc n) desc)) steps)))
      (search-toggle true)
      (en/emit*)
      (render)))

(defn search-toggle2 [tf]
  (let [tf (if tf true false)]
    #(assoc % :attrs (apply assoc (:attrs % {}) [:data-active tf :data-search-active tf]))
    ))

(en/deftemplate admin2 "public/admin/index.html"
  [header greeting steps]
  (get-in selectors [:common :header]) (en/substitute header)
  [:body#admin] (en/set-attr :data-search-active false)
  [:div.config-onboarding :h2] (en/content "Configure Page: Onboarding")
  [:main#content :h1] (en/content "Aspire Administration")
  [:form#config-onboarding] (en/set-attr :action "/config/page/onboarding")
  [:form#config-onboarding :input#greeting] (en/set-attr :value greeting)
  [:form#config-onboarding :textarea#steps] (en/content steps)
  [#{[:form#search (en/attr? :data-active)] [:body (en/attr? :data-search-active)]}] (search-toggle2 false)
  )

(defn admin [header greeting steps]
  (-> (liven "public/admin/index.html"
             (get-in selectors [:common :header]) (en/substitute header)
             [:body#admin] (en/set-attr :data-search-active false)
             [:div.config-onboarding :h2] (en/content "Configure Page: Onboarding")
             [:main#content :h1] (en/content "Aspire Administration")
             [:form#config-onboarding] (en/set-attr :action "/config/page/onboarding")
             [:form#config-onboarding :input#greeting] (en/set-attr :value greeting)
             [:form#config-onboarding :textarea#steps] (en/content steps))
      (search-toggle false)
      (en/emit*)
      (render)))
