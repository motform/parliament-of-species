(ns amr.app.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(reg-sub
 ::route
 (fn [db _]
   (get-in db [:app :route])))

(reg-sub
 ::pending-request?
 (fn [db _]
   (get-in db [:app :pending-request?])))

(reg-sub
 ::balance
 (fn [db _]
   (get-in db [:app :balance])))
