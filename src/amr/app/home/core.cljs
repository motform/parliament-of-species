(ns amr.app.home.core
  (:require [amr.app.events :as event]
            [re-frame.core :as rf]
            [reitit.frontend.easy :refer [href]]))

(defn home [] 
  [:main.home.col
   [:h1 "Wow man save the world or something"]
   [:a {:href (href :route/archive)} "The archive"]
   [:a {:href (href :route/game)}    "The game"]])
