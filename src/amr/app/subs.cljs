(ns amr.app.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(reg-sub
 ::active-page
 (fn [db _]
   (get-in db [:app :active-page])))

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

(reg-sub
 :game/ui?
 (fn [db _]
   (get-in db [:game :ui?])))

;; TODO optimize
(reg-sub
 :game/state
 (fn [db _]
   (get-in db [:game])))
