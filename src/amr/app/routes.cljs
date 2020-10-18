(ns amr.app.routes
  (:require [amr.app.archive.core :refer [archive]]
            [amr.app.game.core :refer [game]]
            [amr.app.home.core :refer [home]]
            [amr.app.events :as event]
            [amr.app.subs :as sub]
            [reitit.core :as reitit]
            [reitit.coercion.spec :as reitit.spec]
            [reitit.frontend :as reitit.frontend]
            [reitit.frontend.easy :as reitit.easy]
            [re-frame.core :as rf]))

;;; ROUTER ;;;

(def routes
  ["/"
   [""
    {:name :route/home
     :view home
     :link-text "Home"}]

   ["game"
    {:name :route/game
     :view game
     :title "Collaborative Policy Making"
     :link-text "Policymaking"}]

   ["archive"
    {:name :route/archive
     :view archive
     :title "Archive of Futures"
     :link-text "Archive"}]

   ["about"
    {:name :route/about
     :view archive
     :title "About"
     :link-text "About"}]])

(def router
  (reitit.frontend/router
   routes
   {:data {:coersion reitit.spec/coercion}}))

;;; FNS ;;;

(defn on-navigate [new-match]
  (when new-match
    (rf/dispatch [::event/navigated new-match])))

(defn init-routes! []
  (reitit.easy/start!
   router
   on-navigate
   {:use-fragment false})) ;; TODO is it preferable to us fragments?
