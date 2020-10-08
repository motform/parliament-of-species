(ns amr.app.db
  (:require [cljs.reader :as reader]
            [cljs.spec.alpha :as s]
            [re-frame.core :as rf]))

(def default-db
  {:state {:active-page :home}})

;;; local-storage

(def ls-key "amr.dsi")

(defn collections->local-storage [db]
  (.setItem js/localStorage ls-key (str (:stories db))))

(rf/reg-cofx ; source: re-frame docs
 :local-store-collections
 (fn [cofx _]
   (assoc cofx :local-store-collections
          (some->> (.getItem js/localStorage ls-key) (reader/read-string)))))
