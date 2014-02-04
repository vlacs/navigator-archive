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

#_(en/defsnippet onboarding-steps "public/index.html"
    (get-in selectors [:onboarding :steps])
  [steps]
  [:strong]
  )

;; Transformers
;; -----------------------
(defn search-toggle
  "A proof-of-concept.
   Performs the requested transforms on the specified nodes and return the transformed nodes."
  [nodes tf]
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

;; admin is cool because it demonstrates threading with generic
;; transformation fns... but there's no caching of the template at any
;; step along the way. :(
;; Probably not the best long-term trade-off.
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

;; admin2 is cooler b/c it does the search-toggle transformations
;; (which can really be any desired combination of transforms in one
;; or more fns) on the template source, and the result is then cached
;; by deftemplate.
(en/deftemplate admin2 (search-toggle "public/admin/index.html" false)
  [header greeting steps]
  (get-in selectors [:common :header]) (en/substitute header)
  [:body#admin] (en/set-attr :data-search-active false)
  [:div.config-onboarding :h2] (en/content "Configure Page: Onboarding")
  [:main#content :h1] (en/content "Aspire Administration")
  [:form#config-onboarding] (en/set-attr :action "/config/page/onboarding")
  [:form#config-onboarding :input#greeting] (en/set-attr :value greeting)
  [:form#config-onboarding :textarea#steps] (en/content steps))

(comment
  ;; Trying to generically handle :body's :data-search-active and
  ;; :form's :data-active without 'liven was ugly at best, as far as I
  ;; could figure it out, and I gave up. Consider the selector/transform
  ;; pair in the deftemplate:
  [#{[:form#search (en/attr? :data-active)] [:body (en/attr? :data-search-active)]}] (search-toggle2 false)

  ;; ...and then search-toggle2 was going to involve something like
  ;; this (the implementation of set-attr in enlive), except that it
  ;; also had to check for the existence of the given attribute before
  ;; assoc'ing it in, and that was going to be so comparatively ugly I
  ;; just stopped:
  #(assoc % :attrs (apply assoc (:attrs % {}) kvs))

)
