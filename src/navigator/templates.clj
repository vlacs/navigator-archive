(ns navigator.templates
  (:require [net.cgrand.enlive-html :as html]
            [helmsman.uri :as h-uri]
            [helmsman.navigation :as h-nav]
            [timber.core :as timber]))

(defn base-page
  [{:keys [page-name brand request user-name main-menu user-menu page-content]}]
  (timber/base-page
   {:page-name page-name
    :brand brand
    :asset-uri-path (h-nav/id->uri-path request :timber/assets)
    :user-name user-name
    :main-menu main-menu
    :user-menu user-menu
    :page-content page-content}))

(def nav-side
  (timber/main-menu
   [{:menu-name "Competency Map"
     :menu-url  "/comp-map"}]))

(html/defsnippet ^{:doc "Load html for competency map page"}
  pg-comp-map "templates/pages/comp-map.html" [:div#content]
  [])

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
  [comp-map ctx]
  (layout-main {:title "VLACS Navigator - Competency Map"
                :content (str comp-map) ; (pg-comp-map)
                :ctx ctx}))
