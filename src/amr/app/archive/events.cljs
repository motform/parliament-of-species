(ns amr.app.archive.events
  (:require [ajax.core :as ajax]
            [amr.util :as util]
            [re-frame.core :refer [reg-event-db reg-event-fx]]))

(reg-event-db
 ::select-projection
 (fn [db [_ projection]]
   (assoc-in db [:archive :selected-projection] projection)))

(reg-event-db
 ::select
 (fn [db [_ k id]]
   (assoc-in db [:archive k] id)))

;;; AJAX

;; TODO move to datascript
(reg-event-fx
 ::request-archive
 (fn [{:keys [db]} _]
   {:db (assoc-in db [:app :pending-request] true)
    :http-xhrio {:method :get
                 :uri (util/->url "/api/archive")
                 :response-format (ajax/transit-response-format {:keywords? true})
                 :on-success [::handle-archive]
                 :on-failure [:http/failure]}}))

(reg-event-db
 ::handle-archive
 (fn [db [_ archive]]
   (-> db
       (assoc-in [:archive :storage] archive)
       (assoc-in [:app :pending-request?] false))))
