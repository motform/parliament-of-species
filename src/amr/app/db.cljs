(ns amr.app.db
  (:require [cljs.reader :as reader]
            [cljs.spec.alpha :as s]
            [re-frame.core :as rf]))

(s/def ::db (s/keys :req-un [::state]))
(s/def ::state (s/keys :req-un [::active-page]))
(s/def ::active-page #{:home :game})

(def default-db
  {:state {:active-page :home}
   :game  {:screen :intro
           :previous-screen nil ;; TODO should be a queue
           :entity nil
           :entities {:aqua 8
                      :flora 2
                      :fauna 7
                      :homo-sapiens 5}}})

;;; local-storage

(def ls-key "amr.dsi")

(defn collections->local-storage [db]
  (.setItem js/localStorage ls-key (str (:stories db))))

(rf/reg-cofx ; source: re-frame docs
 :local-store-collections
 (fn [cofx _]
   (assoc cofx :local-store-collections
          (some->> (.getItem js/localStorage ls-key) (reader/read-string)))))
