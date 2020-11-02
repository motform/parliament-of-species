(ns amr.app.header
  (:require [amr.app.subs :as sub]
            [amr.app.game.subs :as game.sub]
            [amr.util :as util]
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
      (when (get-in route [:data :in-header?]) 
        [:li {:key route-name}
         [:a {:href (href route-name)
              :class (when (= route-name (get-in current-route [:data :name])) "active")}
          text]]))]])

;; TODO make naming of entity/balance consistent
(defn balance
  ([]     (balance nil {:labels? true}))
  ([opts] (balance nil opts))
  ([entites {:keys [class labels? sticky?]}]
   (let [hover? (r/atom false)]

     (letfn [(entity [[entity level] current-entity]
               ^{:key entity}
               [:a.balance-entity.col.centered
                {:class (name entity)
                 ;; TODO enable when routing works properly
                 ;; :href (href :route/about)
                 ;; :target "_blank"
                 :style {:grid-column (str "span " level)}}
                (when labels?
                  (let [current-entity? (= entity current-entity)] 
                    [:label {:class (if (or @hover? current-entity?) "hovering" "hidden")}
                     (when current-entity? "You represent ")
                     (if (= :entity/bacteria entity)
                       "Resistant Bacteria"
                       (util/prn-entity entity))]))])]
       
       (fn []
         (let [entites (or entites @(rf/subscribe [::sub/balance]))
               current-entity (:session/entity @(rf/subscribe [::game.sub/from-session :session]))] 
           [:section.balance
            {:class (str class (when sticky? " sticky"))
             :on-mouse-over (fn [] (reset! hover? true))
             :on-mouse-out  (fn [] (reset! hover? false))}
            (doall (map #(entity % current-entity) entites))]))))))
