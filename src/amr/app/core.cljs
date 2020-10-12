(ns amr.app.core
  (:require [amr.app.archive.core :refer [archive]]
            [amr.app.db :as db]
            [amr.app.events :as event]
            [amr.app.game.core :refer [game]]
            [amr.app.home.core :refer [home]]
            [amr.app.routes :as routes]
            [amr.app.subs :as sub]
            [re-frame.core :as rf]
            [reagent.dom :as r]
            [reitit.core :as reitit]
            [reitit.frontend.easy :refer [href]]))

(def debug? ^boolean goog.DEBUG)

(defn header [router current-route]
  [:header.padded.col
   [:h1 "Parliment of Species"]
   [:ul
    (for [route-name (reitit/route-names router)
          :let [route (reitit/match-by-name router route-name)
                text (get-in route [:data :link-text])]]
      [:li {:key route-name}
       (when (= route-name (get-in current-route [:data :name])) "> ")
       [:a {:href (href route-name)} text]])]])

(defn footer []
  [:footer.padded "Counterfactual speculations by Malmö University."])

(defn app [router]
  (let [current-route @(rf/subscribe [::sub/route])]
    [:<>
     [header router current-route]
     (when current-route [(get-in current-route [:data :view])])
     [footer]]))

(defn dev-setup []
  (when debug?
    (enable-console-print!)))

(defn ^:dev/after-load mount []
  (rf/clear-subscription-cache!)
  (routes/init-routes!)
  (r/render [app routes/router]
            (.getElementById js/document "app")))

(defn ^:export init []
  (rf/dispatch-sync [::event/initialize-db db/default-db])
  (dev-setup)
  (mount))
