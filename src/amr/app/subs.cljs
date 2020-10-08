(ns amr.app.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(reg-sub
 :active-page
 (fn [db _]
   (get-in db [:state :active-page])))

(reg-sub
 :game/screen
 (fn [db _]
   (get-in db [:game :screen])))

(reg-sub
 :game/previous-screen
 (fn [db _]
   (get-in db [:game :previous-screen])))

(reg-sub
 :game/entities
 (fn [db _]
   (get-in db [:game :entities])))
