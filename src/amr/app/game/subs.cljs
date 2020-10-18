(ns amr.app.game.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(reg-sub
 ::screen
 (fn [db _]
   (get-in db [:game :screen])))

(reg-sub
 ::entities
 (fn [db _]
   (get-in db [:game :entities])))

(reg-sub
 ::author
 (fn [db _]
   (get-in db [:app :author])))

(reg-sub
 ::from-session
 (fn [db [_ k]]
   (let [session (get-in db [:game :current-session])]
     (get-in db [:sessions session k]))))

(reg-sub
 ::from-temp
 (fn [db [_ k]] 
   (get-in db [:temp k])))
