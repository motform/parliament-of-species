(ns amr.app.views.home
  (:require [re-frame.core :as rf]))

(defn home [] 
  [:main.home
   [:h1 "AMR Game Prototype #1"]
   [:p "To do it, do it, don't let it go."]
   [:button {:on-click #(rf/dispatch [:active-page :game])}
    "Do it"]])
