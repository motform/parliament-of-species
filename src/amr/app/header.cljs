(ns amr.app.header
  (:require [amr.app.subs :as sub]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [reitit.core :as reitit]
            [reitit.frontend.easy :refer [href]]))

(defn header [router current-route]
  [:header.row
   [:a.identity.row {:href (href :route/home)}
    [:img.logo {:src "/svg/logo.svg"}]
    [:p.nameplate "Parliament" [:br] "of Species"]]
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
   (balance nil {:labels? true}))
  ([opts]
   (balance nil opts))
  ([entites {:keys [class labels?]}]
   (let [hover? (r/atom false)]

     (letfn [(entity [[entity level]]
               ^{:key entity} [:div.balance-entity.col.centered
                               {:class (name entity)
                                :style {:grid-column (str "span " level)}}
                               (when labels?
                                 [:label
                                  {:class (when @hover? "hovering")}
                                  (name entity)])])]
       
       (fn []
         (let [entites (or entites @(rf/subscribe [::sub/entities]))]
           [:div
            {:on-mouse-over (fn [] (reset! hover? true))
             :on-mouse-out  (fn [] (reset! hover? false))}
            [:div.balance {:class class}
             (doall (map entity entites))]]))))))
