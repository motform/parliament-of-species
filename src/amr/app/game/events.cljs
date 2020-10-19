(ns amr.app.game.events
  (:require [ajax.core :as ajax]
            [re-frame.core :as rf :refer [reg-event-db reg-event-fx reg-fx inject-cofx]]))

(reg-event-db
 ::screen
 (fn [db [_ screen]]
   (assoc-in db [:game :screen] screen)))

(reg-event-db
 ::create-session
 (fn [db [_ {:session/keys [id] :as session}]]
   (let [author (get-in db [:app :author])]
     (-> db
         (assoc-in [:sessions id :session] (assoc session :session/author author))
         (assoc-in [:game :current-session] id)))))

(reg-event-db
 ::select-entity
 (fn [db [_ entity]]
   (let [session (get-in db [:game :current-session])]
     (assoc-in db [:sessions session :session :session/entity] entity))))

;; NOTE this currently overrides the original projection, which is slightly unnecessary
(reg-event-db
 ::select-projection
 (fn [db [_ projection]]
   (let [session (get-in db [:game :current-session])]
     (assoc-in db [:sessions session :projection] projection))))

;;; GET

(reg-event-fx
 ::request-stack-for
 (fn [{:keys [db]} [_ entity]]
   {:db (assoc-in db [:state :pending-request] true)
    :http-xhrio {:method :get
                 :uri "http://localhost:3131/api/stack"
                 :params {:entity entity}
                 :response-format (ajax/transit-response-format {:keywords? true})
                 :on-success [::handle-stack]
                 :on-failure [:http/failure]}}))

(reg-event-db
 ::handle-stack
 (fn [db [_ {:keys [projection policy]}]]
   (let [session (get-in db [:game :current-session])]
     (-> db
         (assoc-in [:sessions session :projection] projection)
         (assoc-in [:sessions session :policy] policy)
         (assoc-in [:app :pending-request?] false)))))

(reg-event-fx
 ::request-all
 (fn [{:keys [db]} [_ ns]]
   {:db (assoc-in db [:state :pending-request] true)
    :http-xhrio {:method :get
                 :uri (str "http://localhost:3131/api/all/" (name ns))
                 :response-format (ajax/transit-response-format {:keywords? true})
                 :on-success [::handle-temp ns]
                 :on-failure [:http/failure]}}))

(reg-event-db
 ::handle-temp
 (fn [db [_ ns data]]
   (assoc-in db [:temp ns] data)))

;;; POST

(reg-event-fx
 ::submit-session
 (fn [_ [_ session]]
   {:http-xhrio {:method :post
                 :uri "http://localhost:3131/api/submit/session"
                 :params session
                 :format (ajax/transit-request-format)
                 :response-format (ajax/transit-response-format {:keywords? true})
                 :on-failure [:http/failure]}}))


(reg-event-fx
 ::submit-effect
 (fn [{:keys [db]} [_ effect]]
   (let [session (get-in db [:game :current-session])]
     {:db (assoc-in db [:sessions session :effect] effect)
      :http-xhrio {:method :post
                   :uri "http://localhost:3131/api/submit/effect"
                   :params effect
                   :format (ajax/transit-request-format)
                   :response-format (ajax/transit-response-format {:keywords? true})
                   :on-failure [:http/failure]}})))

(reg-event-fx
 ::submit-policy
 (fn [_ [_ policy]]
   {:http-xhrio {:method :post
                 :uri "http://localhost:3131/api/submit/policy"
                 :params policy
                 :format (ajax/transit-request-format)
                 :response-format (ajax/transit-response-format {:keywords? true})
                 :on-failure [:http/failure]}}))
