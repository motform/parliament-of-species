(ns amr.app.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(reg-sub
 ::route
 (fn [db _]
   (get-in db [:app :route])))
