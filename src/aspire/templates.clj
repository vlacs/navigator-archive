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
(defmacro transformer
  "Intended for preprocessing before handing results to en/snippet and
   en/template for caching.
   Takes in and returns nodes."
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

#_(en/defsnippet menu "public/index.html" {})

;; Transformers
;; -----------------------
(defn search-toggle
  "Enables/disables the search form state."
  [nodes tf]
  (let [tf (if tf true false)]
    (transformer nodes
           [[:form#search (en/attr? :data-active)]] (en/set-attr :data-active tf)
           [[:body (en/attr? :data-search-active)]] (en/set-attr :data-search-active tf))))

;; Templates
;; -----------------------
(en/deftemplate onboarding (search-toggle "public/index.html" true)
  [header greeting greeting-msg steps]
  (get-in selectors [:common :header]) (en/substitute header)
  (get-in selectors [:onboarding :intro]) (en/substitute (onboarding-intro greeting greeting-msg))
  (get-in selectors [:onboarding :steps]) (en/content (map-indexed (fn dostep [n desc] (onboarding-step (inc n) desc)) steps)))

(en/deftemplate admin (search-toggle "public/admin/index.html" false)
  [header greeting greeting-msg steps-str]
  (get-in selectors [:common :header]) (en/substitute header)
  [:body#admin] (en/set-attr :data-search-active false)
  [:div.config-onboarding :h2] (en/content "Configure Page: Onboarding")
  [:main#content :h1] (en/content "Aspire Administration")
  [:form#config-onboarding] (en/set-attr :action "/config/page/onboarding")
  [:form#config-onboarding :input#greeting] (en/set-attr :value greeting)
  [:form#config-onboarding :input#greeting-msg] (en/set-attr :value greeting-msg)
  [:form#config-onboarding :textarea#steps] (en/content steps-str))

