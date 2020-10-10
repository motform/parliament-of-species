(ns amr.app.home.core
  (:require [amr.app.events :as event]
            [re-frame.core :as rf]))

(defn home [] 
  [:main.home
   [:h1 "AMR Game Prototype #1"]
   [:input.btn {:type "button"
                :value "Start the game"
                :on-click #(rf/dispatch [::event/active-page :game])}]
   [:input.btn {:type "button"
                :value "Go to the archive"
                :on-click #(rf/dispatch [::event/active-page :archive])}]])

