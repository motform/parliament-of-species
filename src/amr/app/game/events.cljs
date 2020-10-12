(ns amr.app.game.events
  (:require [re-frame.core :as rf :refer [reg-event-db reg-event-fx reg-fx inject-cofx path after debug]]))

(defn manipulate-cards [cards push pop]
  (->> (apply conj cards push)
       (remove (into #{} pop))
       (into [])))

(reg-event-db
 ::select-entity
 (fn [db [_ {:keys [entity add-cards remove-cards]}]]
   (-> db
       (assoc-in [:game :entity] entity)
       (update-in [:game :cards] manipulate-cards add-cards remove-cards))))

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
