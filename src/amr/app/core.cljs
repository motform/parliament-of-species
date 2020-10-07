(ns amr.app.core
  (:require [re-frame.core :as rf]
            [reagent.dom :as r]
            [goog.dom :as gdom]))

(enable-console-print!) 

(defn render []
  (r/render [:h1 "Parliament of Species!"]
            (gdom/getElement "mount")))

(defn ^:dev/after-load clear-cache-and-render! []
  (rf/clear-subscription-cache!)
  (render))

(defn ^:export mount []
  (render))
