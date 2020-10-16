(ns amr.app.game.events
  (:require [ajax.core :as ajax]
            [re-frame.core :as rf :refer [reg-event-db reg-event-fx reg-fx inject-cofx]]))

(defn manipulate-cards [cards push pop]
  (->> (apply conj cards push)
       (remove (into #{} pop))
       (into [])))

(reg-event-db
 ::select-entity
 (fn [db [_ entity]]
   (assoc-in db [:game :entity] entity)))

(reg-event-db
 ::add-cards
 (fn [db [_ cards]]
   (update-in db [:game :cards] #(apply conj % cards))))

(reg-event-db
 ::remove-cards
 (fn [db [_]]
   db))

(reg-event-db
 ::submit-reflection
 (fn [db [_ {:keys [reflection add-cards remove-cards]}]]
   (-> db
       (assoc-in [:game :reflection] reflection)
       (update-in [:game :cards] manipulate-cards add-cards remove-cards))))

(reg-event-db
 ::submit-policy
 (fn [db [_ {:keys [policy add-cards remove-cards]}]]
   (-> db
       (assoc-in [:game :policy] policy)
       (update-in [:game :cards] manipulate-cards add-cards remove-cards))))

(reg-event-db
 ::handle-projection
 (fn [db [_ response]]
   (-> db
       (assoc-in [:app :pending-request?] false)
       (assoc-in [:game :projection] response))))

(reg-event-fx
 ::request-projection
 (fn [{:keys [db]} _]
   {:db (assoc-in db [:state :pending-request] true)
    :http-xhrio {:method :get
                 :uri "http://localhost:3131/api/projection/random"
                 :timeout 800000
                 :format (ajax/transit-request-format)
                 :response-format (ajax/transit-response-format {:keywords? true})
                 :on-success [::handle-projection]
                 :on-failure [:http/failure]}}))
