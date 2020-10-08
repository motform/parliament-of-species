(ns amr.app.events
  (:require [amr.app.db :as db]
            [amr.utils :as utils]
            [ajax.core :as ajax]
            [clojure.spec.alpha :as s]
            [re-frame.core :as rf :refer [reg-event-db reg-event-fx reg-fx inject-cofx path after debug]]))

;;; Helpers

(defn ->uri [route]
  (let [host (.. js/window -location -host)]
    (str "http://" host "/" route)))

;;; Interceptors

(defn- check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`.
  SOURCE: re-frame docs."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

(def check-spec-interceptor (after (partial check-and-throw :multiverse.db/db)))
(def spec-interceptor [check-spec-interceptor])

(def ->local-storage (after db/collections->local-storage))
(def local-storage-interceptor [->local-storage])

;;; State

;; (reg-event-fx
;;  :initialize-db
;;  [(inject-cofx :local-store-collections) spec-interceptor]
;;  (fn [{:keys [local-store-collections]} [_ default-db]]
;;    {:db (utils/?assoc default-db :stories local-store-collections)}))

(reg-event-fx
 :initialize-db
 (fn [_ [_ default-db]]
   {:db default-db}))

(reg-event-db
 :active-page
 (fn [db [_ page]]
   (assoc-in db [:state :active-page] page)))

(reg-event-db
 :game/screen
 (fn [db [_ screen]]
   (let [current-screen (get-in db [:game :screen])]
     (-> db
         (assoc-in [:game :previous-screen] current-screen)
         (assoc-in [:game :screen] screen)))))
