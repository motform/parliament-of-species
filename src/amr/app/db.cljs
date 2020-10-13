(ns amr.app.db
  (:require [cljs.reader :as reader]
            [cljs.spec.alpha :as s]
            [malli.core :as m]
            [malli.provider :as mp]
            [re-frame.core :as rf]))

(s/def ::db (s/keys :req-un [::state]))
(s/def ::state (s/keys :req-un [::active-page]))
(s/def ::active-page #{:home :game})

(def default-db
  {:app  {:route nil}
   :game {:cards [:card/intro :card/select-entity]
          :session-id (random-uuid)
          :entity nil
          :policy {}
          :reflection {}
          :entities #:entity{:aqua 8
                             :flora 2
                             :fauna 7
                             :homo-sapiens 5
                             :bacterica 2}}})

;;; local-storage

(def ls-key "parliment.of.species")

(defn collections->local-storage [db]
  (.setItem js/localStorage ls-key (str db)))

(rf/reg-cofx ; source: re-frame docs
 :local-store-collections
 (fn [cofx _]
   (assoc cofx :local-store-collections
          (some->> (.getItem js/localStorage ls-key) (reader/read-string)))))
