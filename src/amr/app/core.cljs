(ns amr.app.core
  (:require [amr.app.db :as db]
            [amr.app.events :as events]
            [amr.app.subs :as subs]
            [amr.app.views.app :refer [app]]
            [goog.dom :as gdom]
            [re-frame.core :as rf]
            [reagent.dom :as r]))

(enable-console-print!) 

(defn render []
  (r/render [app]
            (gdom/getElement "mount")))

(defn ^:dev/after-load clear-cache-and-render! []
  (rf/clear-subscription-cache!)
  (render))

(defn ^:export mount []
  (rf/dispatch-sync [:initialize-db db/default-db])
  (render))
