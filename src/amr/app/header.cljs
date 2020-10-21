(ns amr.app.header
  (:require [amr.app.subs :as sub]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [reitit.core :as reitit]
            [reitit.frontend.easy :refer [href]]))

(defn header [router current-route]
  [:header.row
   [:a.nameplate {:href (href :route/home)} "Parliament" [:br] "of Species"]
   [:ul.links.row
    (for [route-name (reitit/route-names router)
          :let [route (reitit/match-by-name router route-name)
                text (get-in route [:data :link-text])]]
      (when-not (= route-name :route/home) 
        [:li {:key route-name}
         [:a {:href (href route-name)
              :class (when (= route-name (get-in current-route [:data :name])) "active")}
          text]]))]])

(defn balance
  ([]
   (balance nil {}))
  ([opts]
   (balance nil opts))
  ([entites {:keys [class]}]
   (let [hover? (r/atom false)]

     (letfn [(entity [[entity level]]
               ^{:key entity} [:div.balance-entity
                               {:class (name entity)
                                :style {:grid-column (str "span " level)}}
                               [:label.balance-label
                                {:class (when @hover? "show-labels")}
                                (name entity)]])]
       
       (fn []
         (let [entites (or entites @(rf/subscribe [::sub/entities]))]
           [:div
            {:on-mouse-over (fn [] (reset! hover? true))
             :on-mouse-out  (fn [] (reset! hover? false))}
            [:div.balance {:class class}
             (doall (map entity entites))]]))))))
