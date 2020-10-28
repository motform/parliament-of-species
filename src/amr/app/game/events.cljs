(ns amr.app.game.events
  (:require [ajax.core :as ajax]
            [amr.util :as util]
            [re-frame.core :refer [reg-event-db reg-event-fx]]))

(reg-event-db
 ::screen
 (fn [db [_ screen]]
   (assoc-in db [:game :screen] screen)))

(reg-event-db
 ::save-screen
 (fn [db [_ screen]]
   (let [session (get-in db [:game :current-session])]
     (assoc-in db [:sessions session :screen] screen))))

(reg-event-db
 ::create-session
 (fn [db [_ {:session/keys [id] :as session}]]
   (let [author (get-in db [:app :author])]
     (-> db
         (assoc-in [:sessions id :session] (assoc session :session/author author))
         (assoc-in [:game :current-session] id)))))

(reg-event-db
 ::reset-session
 (fn [db _]
   (assoc-in db [:game :current-session] nil)))

(reg-event-db
 ::select-entity
 (fn [db [_ entity]]
   (let [session (get-in db [:game :current-session])]
     (assoc-in db [:sessions session :session :session/entity] entity))))

(defn- random-projection [db]
  (->> (get-in db [:archive :storage]) keys rand-nth))

(defn- random-policy [db projection entity]
  (->> (get-in db [:archive :storage projection :projection/policies])
       keys
       rand-nth))

(reg-event-db
 ::new-pair
 (fn [db [_ {session :session/id} entity]]
   (let [projection (random-projection db)
         policy (random-policy db projection entity)]
     (println policy)
     (println projection)
     (-> db 
         (assoc-in [:sessions session :projection] projection)
         (assoc-in [:sessions session :policy] policy)))))

(reg-event-db
 ::delete-sessions
 (fn [db _]
   (assoc-in db [:sessions] {})))

(reg-event-db
 ::resume
 (fn [db [_ session]]
   (let [screen (get-in db [:sessions session :screen])]
     (-> db 
         (assoc-in [:game :current-session] session)
         (assoc-in [:game :screen] screen)))))

;;; POST

(reg-event-fx
 ::submit-session
 (fn [_ [_ session]]
   {:http-xhrio {:method :post
                 :uri (util/->url "/api/submit/session")
                 :params session
                 :format (ajax/transit-request-format)
                 :response-format (ajax/transit-response-format {:keywords? true})
                 :on-success [:http/success]
                 :on-failure [:http/failure]}}))

(reg-event-fx
 ::submit-effect
 (fn [{:keys [db]} [_ effect]]
   (let [session (get-in db [:game :current-session])
         {:keys [projection policy session]} (get-in db [:sessions session])
         afx (assoc effect :effect/session session)]
     {:db (-> db ;; TODO make sure this works (it should)
              (assoc-in [:sessions session :written-effect] afx)
              (update-in [:archive :storage projection :projection/policies policy :policy/effects] conj afx))
      :http-xhrio {:method :post
                   :uri (util/->url "/api/submit/effect")
                   :params effect
                   :format (ajax/transit-request-format)
                   :response-format (ajax/transit-response-format {:keywords? true})
                   :on-success [:http/success]
                   :on-failure [:http/failure]}})))

(reg-event-fx
 ::submit-policy
 (fn [{:keys [db]} [_ policy]]
   (let [current-session (get-in db [:game :current-session])]
     {:db (assoc-in db [:sessions current-session :written-policy] policy)
      :http-xhrio {:method :post
                   :uri (util/->url "/api/submit/policy")
                   :params policy
                   :format (ajax/transit-request-format)
                   :response-format (ajax/transit-response-format {:keywords? true})
                   :on-success [:http/success]
                   :on-failure [:http/failure]}})))
