(ns aspire.templates
  (:use-macros
   [dommy.macros :only [deftemplate]]))

;; Utility Fns
;; -------------------
(defn ratio->pct
  "Convert a ratio to a percentage-ratio"
  [ratio]
  (* 100 ratio))

(defn ratio->width [ratio]
  (str "width:" (int (ratio->pct ratio)) "%"))

;; Templates
;; -------------------
(deftemplate comp-tag [name version description cnt-completed cnt-remaining]
  [:div {:class "four-columns competency-pod"}
   [:img {:alt "", :src "http://i1.ytimg.com/i/fjFOToJzoI_7JEO9IfECMQ/mq1.jpg?v=4fa19736"}]
   [:h3 {:class "comp-tag-name"} name]
   [:h2 {:class "comp-tag-version"} version]
   [:p {:class "comp-tag-description"} description]
   [:footer {}
    [:a {:class "button", :href "#"} "Resume"]
    [:div {:class "status-bar"}
     [:span {:class "percent-complete",
             :style (-> (/ cnt-completed cnt-remaining) ratio->width)}] " "
     [:span {:class "status-context"} (str cnt-completed "/" cnt-remaining)]]]])


