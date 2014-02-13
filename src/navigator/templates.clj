(ns aspire.templates
  (:require [net.cgrand.enlive-html :as en]
            [aspire.util :as a-util]))

;; Selectors
;; -----------------------
(def selectors {:common {:header [:nav#top-bar]
                         :title [:head :title]
                         :menu [:nav#side-nav]
                         :main [:main#content]}
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

(en/defsnippet onboarding-step "public/index.html" (get-in selectors [:onboarding :step])
  [n desc]
  [:strong] (en/content (str "Step " n))
  [:span] (en/content desc))

(en/defsnippet onboarding-main "public/index.html" (get-in selectors [:common :main])
  [greeting greeting-msg steps]
  (get-in selectors [:onboarding :intro]) (en/substitute (onboarding-intro greeting greeting-msg))
  (get-in selectors [:onboarding :steps]) (en/content (map-indexed (fn dostep* [n desc] (onboarding-step (inc n) desc)) steps)))

(en/defsnippet admin-main "public/admin/index.html" (get-in selectors [:common :main])
  [greeting greeting-msg steps-str]
  [:h1] (en/content "Aspire Administration")
  [:div.config-onboarding :h2] (en/content "Configure Page: Onboarding")
  [:form#config-onboarding] (en/set-attr :action "/config/page/onboarding")
  [:form#config-onboarding :input#greeting] (en/set-attr :value greeting)
  [:form#config-onboarding :input#greeting-msg] (en/set-attr :value greeting-msg)
  [:form#config-onboarding :textarea#steps] (en/content steps-str))

(en/defsnippet menu-item "public/index.html" [:nav#side-nav :ul :> en/first-child]
  [{:keys [name href]}]
  [:a] (en/do->
        (en/content name)
        (en/set-attr :href href)))

(en/defsnippet common-menu "public/index.html" [:nav#side-nav]
  [menu-items]
  [:ul] (en/content (map menu-item menu-items)))

;; Transformers
;; -----------------------
(defn search-toggle
  "Enables/disables the search form state."
  [nodes tf]
  (let [tf (if tf true false)]
    (transformer nodes
           [[:form#search (en/attr? :data-active)]] (en/set-attr :data-active tf)
           [[:body (en/attr? :data-search-active)]] (en/set-attr :data-search-active tf))))

(en/defsnippet common-head "public/index.html" [:head]
  [title desc]
  [:title] (en/content title)
  [[:meta (en/attr-has :name "description")]] (en/set-attr :content desc))

;; Template
;; -----------------------
(en/deftemplate base "public/index.html"
  ;; Do things in this order. main [probably] pulls in head, header,
  ;; and menu, so those transforms must follow main.
  [{:keys [head header menu]} main search-toggle]
  (get-in selectors [:common :main]) (en/substitute main)
  (get-in selectors [:common :header]) (en/substitute header)
  (get-in selectors [:common :menu]) (en/substitute menu)
  [:head] (en/substitute head)
  [[:form#search (en/attr? :data-active)]] (en/set-attr :data-active search-toggle)
  [[:body (en/attr? :data-search-active)]] (en/set-attr :data-search-active search-toggle))

;; Templaters
;; -----------------------
(defn common-snippets [ctx]
  {:head (common-head "TODO: Get the title out of the request." "TODO: Also get a description out of the request.")
   :header (common-header (rand-int 100) "http://google.com" "Bo Jackson")
   :menu (common-menu [{:name "Admin" :href "/admin"}
                       {:name "Home" :href "/"}
                       {:name "Add to your playlist" :href "/welcome"}])})

(defn onboarding [common-snippets & args]
  (let [main (apply onboarding-main args)]
    (render (base common-snippets main true))))

(defn admin [common-snippets & args]
  (let [main (apply admin-main args)]
    (render (base common-snippets main false))))
