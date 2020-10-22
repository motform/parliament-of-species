(ns amr.app.archive.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(reg-sub
 ::from-archive
 (fn [db [_ k]] 
   (get-in db [:archive :storage k])))

(reg-sub
 ::selected-projection
 (fn [db _]
   (get-in db [:archive :selected-projection])))
