(ns navigator.web.resources
  (:require [liberator.core :refer [defresource]]
            [navigator.handlers :as a-hdl]))

(defresource onboarding!
  :available-media-types ["text/html"]
  :handle-ok a-hdl/onboarding!)

(defresource admin!
  :allowed-methods [:get]
  :available-media-types ["text/html"]
  :handle-ok a-hdl/admin!)

(defresource config-key [key]
  :allowed-methods [:put]
  :available-media-types ["text/html"]
  :handle-ok (fn [_]
               (hiccup.page/html5
                [:head
                 ;; alt: page.css
                 [:link {:rel "stylesheet" :href "css/global.css"}]]
                [:body
                 [:div#main
                  [:div [:p#loading "This isn't implemented yet."]]]])))

(defresource config-page!
  :allowed-methods [:post]
  :available-media-types ["text/html"]
  :post! a-hdl/config-page!
  ;; TODO: Display a user-friendly "Your changes were saved" message.
  :post-redirect? (fn [_] {:location "/admin"}))

