(ns navigator.templates
  (:require [net.cgrand.enlive-html :as html]
            [helmsman.uri :as h-uri]
            [helmsman.navigation :as h-nav]
            [timber.core :as timber]
            [ring.util.codec :refer [url-encode]]))

(defn base-page
  [{:keys [page-name brand request user-name main-menu user-menu page-content]}]
  (timber/base-page
   {:page-name page-name
    :brand brand
    :asset-uri-path (h-nav/id->uri-path request :timber/assets)
    :user-name "David Zaharee"
    :main-menu main-menu
    :user-menu user-menu
    :page-content page-content}))

(def nav-side
  (timber/main-menu
   [{:menu-name "Competency Map"
     :menu-url  "/comp-map"}]))

(defn transform-tags [tags search]
  (html/clone-for [tag tags]
                  [:span#tag :a] (html/content (:comp-tag/name tag))
                  [:span#tag :a] (->> [:tag (:comp-tag/name tag)]
                                      (conj search)
                                      (str)
                                      (url-encode)
                                      (str "?previous=")
                                      (html/set-attr :href))))

(defn transform-comps [comps search]
  (html/clone-for [comp comps]
                  [:h2] (html/content (:comp/name comp))
                  [:p#tags :span#tag] (transform-tags (:comp/tags comp) search)))

(defn maybe-quote [s]
  (if s (str \" s \")))

(defn format-term [term]
  (let [a (first term)
        b (maybe-quote (second term))]
    (if b (if (= :default a) b
              (subs (str a ":" b) 1))
        (subs (str a) 1))))

(defn transform-search-terms [terms]
  (html/clone-for [term terms]
                  [:a] (html/content (format-term term))
                  [:a] (->> terms
                            (remove #(= term %))
                            (into [])
                            (str)
                            (url-encode)
                            (str "?previous=")
                            (html/set-attr :href))))

(html/defsnippet comp-map "templates/pages/comp-map.html"
  [:div#content]
  [ctx]
  [:div#competency] (transform-comps (:competencies ctx) (:monocular ctx))
  [:div#competencies] (fn [n] (if (not-empty (:competencies ctx)) n
                                  ((html/html-content "<h3>No Competencies Found</h3>") n)))
  [:input#previous] (html/set-attr :value (str (:monocular ctx)))
  [:a#search-term] (transform-search-terms (:monocular ctx))
  [:div#search-terms] (fn [n] (if (not-empty (:monocular ctx)) n))
  #_[:div#debugging] #_(html/append (str ctx)))

(defn layout-main
  "Main page layout type"
  [{:keys [title content ctx]}]
  (base-page {:page-name title
              :brand "VLACS Navigator"
              :request (:request ctx)
              :main-menu nav-side
              :page-content content}))

(defn view-comp-map
  "Users view"
  [ctx]
  (layout-main {:title "VLACS Navigator - Competency Map"
                :content (comp-map ctx) ;(apply str (:competencies ctx))
                :ctx ctx}))
