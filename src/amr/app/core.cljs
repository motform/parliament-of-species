(ns amr.app.core
  (:require [amr.app.archive.core :refer [archive]]
            [amr.app.db :as db]
            [amr.app.events :as event]
            [amr.app.game.core :refer [game]]
            [amr.app.home.core :refer [home]]
            [amr.app.routes :as routes]
            [amr.app.subs :as subs]
            [re-frame.core :as rf]
            [reagent.dom :as r]))

(def debug? ^boolean goog.DEBUG)

(defn dev-setup []
  (when debug?
    (enable-console-print!)))

;; (defn ^:dev/after-load clear-cache-and-render! []
;;   (rf/clear-subscription-cache!)
;;   (render))

(defn ^:dev/after-load mount []
  (rf/clear-subscription-cache!)
  (routes/init-routes!)
  (r/render [routes/router-component routes/router]
            (.getElementById js/document "app")))


(defn ^:export init []
  (rf/dispatch-sync [::event/initialize-db db/default-db])
  (dev-setup)
  (mount))
