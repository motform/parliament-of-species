(ns amr.app.home.core
  (:require [amr.app.events :as event]
            [reitit.frontend.easy :refer [href]]
            [re-frame.core :as rf]))

(defn home [] 
  [:main.home
   [:h1 "AMR Game Prototype #1"]
   [:a {:href (href :route/archive)} "The archive"]
   [:a {:href (href :route/game)}    "The game"]])
