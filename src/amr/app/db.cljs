(ns amr.app.db
  (:require [amr.model :as model]
            [cljs.reader :as reader]
            [malli.core :as m]
            [malli.generator :as mg]
            [re-frame.core :as rf]))

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

   [:temp [:map [:projections map?]]]])

(def default-db
  {:app  {:route nil
          :entities #:entity{:aqua 5 :flora 5 :fauna 5 :homo-sapiens 5 :bacteria 10}
          :pending-request? false
          :author (random-uuid)}
   :sessions {}
   :game {:screen :screen/intro
          :current-session nil
          }
   :temp {:projections {}}})

;;; LOCAL-STORAGE

(def ls-key "parliment.of.species")

(defn collections->local-storage [db]
  (.setItem js/localStorage ls-key (str db)))

(rf/reg-cofx ; source: re-frame docs
 :local-store-collections
 (fn [cofx _]
   (assoc cofx :local-store-collections
          (some->> (.getItem js/localStorage ls-key) (reader/read-string)))))
