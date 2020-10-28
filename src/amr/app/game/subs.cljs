(ns amr.app.game.subs
  (:require [amr.util :as util]
            [re-frame.core :refer [reg-sub subscribe]]))

(reg-sub
 ::screen
 (fn [db _]
   (get-in db [:game :screen])))

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
 ::current-session
 (fn [db _]
   (get-in db [:game :current-session])))

(reg-sub
 ::current-projection
 (fn [db _]
   (let [session (get-in db [:game :current-session])
         projection (get-in db [:sessions session :projection])]
     (-> (get-in db [:archive :storage projection])
         (select-keys [:projection/id :projection/name :projection/text])))))

(reg-sub
 ::current-policy
 (fn [db _]
   (let [session (get-in db [:game :current-session])
         {:keys [projection policy]} (get-in db [:sessions session])]
     (-> (get-in db [:archive :storage projection :projection/policies policy])
         (select-keys [:policy/id :policy/name :policy/text :policy/session])))))

(reg-sub
 ::effect-impact
 (fn [db _]
   (let [session (get-in db [:game :current-session])
         {:keys [projection policy]} (get-in db [:sessions session])]
     (util/calculate-impact (get-in db [:archive :storage projection :projection/policies policy :policy/effects])))))

(reg-sub
 ::sessions
 (fn [db _]
   (:sessions db)))
