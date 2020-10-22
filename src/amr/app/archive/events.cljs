(ns amr.app.archive.events
  (:require [ajax.core :as ajax]
            [amr.util :as util]
            [re-frame.core :as rf :refer [reg-event-db reg-event-fx reg-fx inject-cofx path after debug]]))

(reg-event-db
 ::select-projection
 (fn [db [_ projection]]
   (assoc-in db [:archive :selected-projection] projection)))

;;; AJAX

;; TODO move to datascript
;; TODO narrow down the things you get back, could do a query per thing
(reg-event-fx
 ::request-archive
 (fn [{:keys [db]} _]
   (when (empty? (get-in db [:archive :storage]))
     {:db (assoc-in db [:state :pending-request] true)
      :http-xhrio {:method :get
                   :uri "http://localhost:3131/api/all"
                   :response-format (ajax/transit-response-format {:keywords? true})
                   :on-success [::handle-archive]
                   :on-failure [:http/failure]}})))

(reg-event-db
 ::handle-archive
 (fn [db [_ {:keys [projection policy effect]}]]
   (-> db
       (assoc-in [:archive :storage :projection] (util/index-by :projection/id projection))
       (assoc-in [:archive :storage :policy] (util/index-by :policy/id policy))
       (assoc-in [:archive :storage :effect] (util/index-by :effect/id effect))
       (assoc-in [:app :pending-request?] false))))
