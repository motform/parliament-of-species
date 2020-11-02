(ns amr.app.routes
  (:require [amr.app.about.core :refer [about]]
            [amr.app.archive.core :refer [archive]]
            [amr.app.archive.events :as archive]
            [amr.app.events :as event]
            [amr.app.game.core :as game]
            [amr.app.landing :refer [landing]]
            [re-frame.core :as rf]
            [reitit.coercion.spec :as reitit.spec]
            [reitit.core :as reitit]
            [reitit.frontend :as reitit.frontend]
            [reitit.frontend.easy :as reitit.easy]))

;;; ROUTER ;;;

(def routes
  ["/" {:controllers [{:start #(rf/dispatch [::event/request-balance])}]}

   [""
    {:name :route/home
     :view landing
     :link-text "Home"}]

   ["policymaking"
    {:name :route/policymaking
     :view game/session-library
     :title "Participate"
     :link-text "Participate"
     :in-header? true}]

   ;; TODO nest these under /policymaking
   ["intro"
    {:name :route.policymaking/intro
     :view game/intro
     :title "Introduction to Parliament"
     :link-text "Intro"}]

   ["select-entity"
    {:name :route.policymaking/select-entity
     :view game/select-entity
     :title "Select your entity"
     :link-text "Select entity"}]

   ["write-effect"
    {:name :route.policymaking/write-effect
     :view game/write-effect
     :title "React to a policy"
     :link-text "Write effect"}]

   ["write-policy"
    {:name :route.policymaking/write-policy
     :view game/write-policy
     :title "Write a policy"
     :link-text "Write policy"}]

   ["end"
    {:name :route.policymaking/end
     :view game/end
     :title "Thank you!"
     :link-text "End"}]

   ["archive"
    {:name :route/archive
     :view archive
     :title "Archive of Futures"
     :link-text "Archive"
     :in-header? true
     :controllers [{:start #(rf/dispatch [::archive/request-archive])}]}]

   ["entities"
    {:name :route/entities
     :view about
     :title "Entities"
     :link-text "Entities"
     :in-header? true}]])

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
