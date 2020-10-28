(ns amr.app.routes
  (:require [amr.app.about.core :refer [about]]
            [amr.app.archive.core :refer [archive]]
            [amr.app.archive.events :as archive]
            [amr.app.events :as event]
            [amr.app.game.core :refer [game]]
            [amr.app.game.events :as game]
            [amr.app.landing :refer [landing]]
            [re-frame.core :as rf]
            [reitit.coercion.spec :as reitit.spec]
            [reitit.frontend :as reitit.frontend]
            [reitit.frontend.easy :as reitit.easy]
            [reitit.core :as reitit]))

;;; ROUTER ;;;

(def routes
  ["/"
   [""
    {:name :route/home
     :view landing
     :link-text "Home"}]

   ["policymaking"
    {:name :route/policymaking
     :view game
     :title "Policymaking"
     :link-text "Policymaking"
     :controllers [{:start #(rf/dispatch [::game/screen :screen/sessions])}]}]

   ["archive"
    {:name :route/archive
     :view archive
     :title "Archive of Futures"
     :link-text "Archive"
     :controllers [{:start #(rf/dispatch [::archive/request-archive])}]}]

   ["about"
    {:name :route/about
     :view about
     :title "About"
     :link-text "About"}]])

(def router
  (reitit.frontend/router
   routes
   {:data {:coersion reitit.spec/coercion}}))

;;; FNS ;;;

(defn on-navigate [new-match]
  (if new-match
    (rf/dispatch [::event/navigated new-match])
    (rf/dispatch [::event/navigate :route/home])))

(defn init-routes! []
  (reitit.easy/start!
   router
   on-navigate
   {:use-fragment false})) ;; TODO is it preferable to us fragments?
