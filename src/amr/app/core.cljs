(ns amr.app.core
  (:require [amr.app.db :as db]
            [amr.app.events :as event]
            [amr.app.subs :as subs]
            [amr.app.archive.core :refer [archive]]
            [amr.app.home.core :refer [home]]
            [amr.app.game.core :refer [game]]
            [goog.dom :as gdom]
            [re-frame.core :as rf]
            [reagent.dom :as r]))

(enable-console-print!) 

(defn active-page [page]
  (case page
    :home [home]
    :game [game]
    :archive [archive]
    [:div.error "Error, no page found!"]))

(defn app []
  (let [page @(rf/subscribe [::subs/active-page])]
    [active-page page]))

(defn render []
  (r/render [app]
            (gdom/getElement "mount")))

(defn ^:dev/after-load clear-cache-and-render! []
  (rf/clear-subscription-cache!)
  (render))

(defn ^:export mount []
  (rf/dispatch-sync [::event/initialize-db db/default-db])
  (render))
