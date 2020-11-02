(ns amr.app.core
  (:require [amr.app.db :as db]
            [amr.app.archive.events :as archive]
            [amr.app.header :refer [header balance]]
            [amr.app.footer :refer [footer]]
            [amr.app.events :as event]
            [amr.app.routes :as routes]
            [amr.app.subs :as sub]
            [day8.re-frame.http-fx]
            [re-frame.core :as rf]
            [reagent.dom :refer [render]]))

(def debug? ^boolean goog.DEBUG)

(defn app [router]
  (let [current-route @(rf/subscribe [::sub/route])]
    [:<>
     [:div.excuse
      [:p "The Parliament of Species requires a screen size of a tablet or larger to participate."]
      [:p "Come back again soon to partake with your mobile device!"]]
     [header router current-route]
     [balance {:labels? true :sticky? true}]
     (when current-route [(get-in current-route [:data :view])])
     [footer]]))

(defn dev-setup []
  (when debug?
    (enable-console-print!)))

(defn ^:dev/after-load mount []
  (rf/clear-subscription-cache!)
  (routes/init-routes!)
  (render [app routes/router]
          (.getElementById js/document "app")))

(defn ^:export init []
  (rf/dispatch-sync [::event/initialize-db db/default-db])
  (rf/dispatch-sync [::archive/request-archive])
  (rf/dispatch-sync [::event/request-balance])
  (dev-setup)
  (mount))
