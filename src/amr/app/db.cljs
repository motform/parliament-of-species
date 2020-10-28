(ns amr.app.db
  (:require [amr.model :as model]
            [malli.core :as m]
            [malli.generator :as mg]))

;;; INITIAL DB

;; TODO co-locate the schema and the initial-db?
(def default-db-schema
  [:map

   [:app
    [:map
     [:pending-request? boolean?]
     [:route string?]
     [:author uuid?]]]

   [:sessions
    [:map-of :uuid?
     [:map
      [:entity model/entity]
      [:policy model/policy]
      [:effect model/effect]]]]

   [:game
    [:session-id uuid?]
    [:entity? model/entity]
    [:cards [:vector qualified-keyword?]]
    [:policy [:map]]
    [:reflection [:map]]
    [:entites [:map]]]

   [:archive [:map
              [:effect map?]
              [:policy map?]
              [:projection map?]]]])

(def session
  [:map
   ])

(def default-db
  {:app  {:route nil
          :entities #:entity{:aqua 5 :flora 5 :fauna 5 :homo-sapiens 5 :resistance 10}
          :pending-request? false
          :author (random-uuid)} ;; TODO move
   :meta {:author (random-uuid)}
   :sessions {}
   :game {:screen :screen/sessions
          :current-session nil}
   :archive {:storage {}
             :projection #uuid "a975be9f-6ab6-4df1-8036-57a5be9ecb13"
             :policy nil
             :effect nil}})
