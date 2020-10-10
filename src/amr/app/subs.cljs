(ns amr.app.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(reg-sub
 ::active-page
 (fn [db _]
   (get-in db [:app :active-page])))
