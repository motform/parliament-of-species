(ns amr.app.archive.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(reg-sub
 ::archive
 (fn [db _] 
   (get-in db [:archive :storage])))

;; we could possibly do the key-selection here
(reg-sub
 ::projections
 (fn [db _]
   (get-in db [:archive :storage])))

(reg-sub
 ::policies-for
 (fn [db [_ projection]]
   (get-in db [:archive :storage projection])))

(reg-sub
 ::effects-for
 (fn [db [_ projection policy]]
   (get-in db [:archive :storage projection :projection/policies policy])))

(reg-sub
 ::selected
 (fn [db [_ k]]
   (get-in db [:archive k])))
