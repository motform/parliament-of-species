(ns amr.app.game.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(reg-sub
 ::screen
 (fn [db _]
   (get-in db [:game :screen])))

;; TODO REMOVE
(reg-sub
 ::previous-screen
 (fn [db _]
   (get-in db [:game :previous-screen])))

(reg-sub
 ::entities
 (fn [db _]
   (get-in db [:game :entities])))

;; TODO REMOVE
(reg-sub
 ::ui?
 (fn [db _]
   (get-in db [:game :ui?])))

(reg-sub
 ::state
 (fn [db _]
   (get-in db [:game])))
