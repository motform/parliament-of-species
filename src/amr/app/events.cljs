(ns amr.app.events
  (:require [ajax.core :as ajax]
            [amr.util :as util]
            [cljs.reader :as reader]
            [re-frame.core :as rf :refer [reg-event-db reg-event-fx reg-fx reg-cofx inject-cofx reg-global-interceptor ->interceptor]]
            [reitit.frontend.controllers :as reitit.contollers]
            [reitit.frontend.easy :as reitit.easy]))

;;; HELPERS

(defn set-title! [route]
  (let [route-title (get-in route [:data :title])]
    (set! (.-title js/document)
          (cond->> "Parliament of Species"
            route-title (str route-title " | ")))))

;;; INTERCEPTORS

(def ls-key "parliment.of.species")

(defn collections->local-storage [db]
  (.setItem js/localStorage ls-key (str (select-keys db [:sessions :game :meta]))))

(reg-cofx
 :local-store-collections
 (fn [cofx _]
   (assoc cofx :local-store-collections
          (some->> (.getItem js/localStorage ls-key) (reader/read-string)))))

(def save-to-localstorage
  (->interceptor
   :id ::save-to-localstorage
   :after (fn [{{:keys [db]} :coeffects :as context}]
            (collections->local-storage db)
            context)))

(reg-global-interceptor save-to-localstorage)

;;; EFFECTS

(reg-fx
 ::navigate!
 (fn [route]
   (set-title! route)
   (apply reitit.easy/push-state route)))

(reg-fx
 ::scroll-to-top!
 (fn []
   (. js/window scrollTo 0 0)))

;;; EVENTS

(reg-event-fx
 ::initialize-db
 [(inject-cofx :local-store-collections)]
 (fn [{:keys [local-store-collections]} [_ default-db]]
   {:db (merge default-db local-store-collections)}))

(reg-event-fx
 ::navigate
 (fn [_ [_ & route]]
   {::scroll-to-top! nil
    ::navigate! route}))

(reg-event-fx
 ::scroll-to-top
 (fn [_ _]
   {::scroll-to-top! nil}))

;; WARN This does two things (route and scroll), which I dislike
(reg-event-db
 ::navigated
 (fn [db [_ new-match]]
   (let [old-match (-> db :app :current-route)
         controllers (reitit.contollers/apply-controllers (:contollers old-match) new-match)]
     (set-title! new-match) 
     (when-not goog.DEBUG (. js/window scrollTo 0 0)) ; don't scroll on hot-reloads
     (assoc-in db [:app :route] (assoc new-match :contollers controllers)))))

(reg-event-fx
 ::request-balance
 (fn [{:keys [db]} _]
   {:db (assoc-in db [:app :pending-request] true)
    :http-xhrio {:method :get
                 :uri (util/->url "/api/balance")
                 :response-format (ajax/transit-response-format {:keywords? true})
                 :on-success [::handle-balance]
                 :on-failure [:http/failure]}}))

(reg-event-db
 ::handle-balance
 (fn [db [_ balance]]
   (assoc-in db [:app :balance] balance)))

;;; HTTP

(reg-event-db
 :http/failure
 (fn [db [_ result]]
   (assoc-in db [:app :http-failure] result)))

(reg-event-db
 :http/success
 (fn [db [_]]
   (assoc-in db [:app :pending-request?] false)))
