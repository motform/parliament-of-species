(ns amr.app.core
  (:require [amr.app.db :as db]
            [amr.app.header :refer [header balance]]
            [amr.app.events :as event]
            [amr.app.routes :as routes]
            [amr.app.subs :as sub]
            [day8.re-frame.http-fx]
            [re-frame.core :as rf]
            [reagent.dom :refer [render]]))

(def debug? ^boolean goog.DEBUG)

(defn footer []
  [:footer.padded "Counterfactual speculations by Malmö University."])

(defn app [router]
  (let [current-route @(rf/subscribe [::sub/route])]
    [:<>
     [header router current-route]
     [balance]
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
  (dev-setup)
  (mount))
