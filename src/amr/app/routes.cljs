(ns amr.app.routes
  (:require [reitit.core :as reitit]
            [re-frame.core :as rf]
            [pushy.core :as pushy]))

(def titles
  {:home ""
   :archive "Archive of Futures"
   :game "Game"})

(def routes
  [])

(defn- parse-url [url]
  ,,,)

(defn dispatch-route [matched-route]
  ,,,)

(defn app-routes []
  (pushy/start! (pushy/pushy dispatch-route parse-url)))

(def url-for ,,,)
