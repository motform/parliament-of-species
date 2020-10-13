(ns amr.app.game.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(reg-sub
 ::entities
 (fn [db _]
   (get-in db [:game :entities])))

(reg-sub
 ::state
 (fn [db _]
   (get-in db [:game])))

(reg-sub
 ::projection
 (fn [db _]
   (get-in db [:game :projection])))
