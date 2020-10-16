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
     [:route string?]
     [:author-id uuid?]]]
   [:game
    [:session-id uuid?]
    [:entity? model/entity]
    [:cards [:vector qualified-keyword?]]
    [:policy [:map]]
    [:reflection [:map]]
    [:entites [:map]]]])

(def default-db
  {:app  {:route nil
          :author-id (random-uuid)}
   :game {:cards [:card/intro :card/select-entity]
          :session-id (random-uuid)
          :entity nil
          :content {:projection {}
                    :policy {}
                    :reflection {}}
          :entities #:entity{:aqua 8 :flora 2 :fauna 7 :homo-sapiens 5 :bacterica 2}}})

;;; LOCAL-STORAGE

(def ls-key "parliment.of.species")

(defn collections->local-storage [db]
  (.setItem js/localStorage ls-key (str db)))

(rf/reg-cofx ; source: re-frame docs
 :local-store-collections
 (fn [cofx _]
   (assoc cofx :local-store-collections
          (some->> (.getItem js/localStorage ls-key) (reader/read-string)))))
