(ns amr.app.game.events
  (:require [re-frame.core :as rf :refer [reg-event-db reg-event-fx reg-fx inject-cofx path after debug]]))

(reg-event-db
 ::ui?
 (fn [db [_ bool]]
   (assoc-in db [:game :ui?] bool)))

(reg-event-db
 ::screen
 (fn [db [_ screen]]
   (let [current-screen (get-in db [:game :screen])]
     (-> db
         (assoc-in [:game :previous-screen] current-screen)
         (assoc-in [:game :screen] screen)))))

(reg-event-db
 ::select-entity
 (fn [db [_ entity]]
   (assoc-in db [:game :entity] entity)))

;; TODO make into ajax-fx
(reg-event-db
 ::submit-reflection
 (fn [db [_ reflection id]]
   (assoc-in db [:temp id] reflection)))
