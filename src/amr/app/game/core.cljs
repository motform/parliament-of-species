(ns amr.app.game.core
  (:require [amr.app.game.components :as c]
            [amr.app.game.events :as event]
            [amr.app.game.subs :as sub]
            [amr.app.subs :as app]
            [re-frame.core :as rf]))

;; (def game-routes
;;   [["/policymaking"
;;     ["/"
;;      {:name :route.game/home
;;       :view (fn []
;;               (let [current-policy-route @(rf/subscribe [::sub/policy-route])
;;                     _ (rf/dispatch [::event/navigate current-policy-route])]
;;                 [:<>]))}]

;;     ["/select-entity"
;;      {:name :route.game/select-entity
;;       :doc ""
;;       :view (fn []
;;               [:<>
;;                [c/timeline years]
;;                (for [entity entites]
;;                  ^{:key (:key entity)} [c/entity entity {:clickable? true}])])}]

;;     ["/write-effect"
;;      {:name :route.game/write-effect
;;       :doc ""
;;       :view (fn []
;;               [:<>
;;                [c/current-entity entites]
;;                [c/projection]
;;                [c/policy {:tearable? true}]
;;                [c/write-effect]])}]

;;     ["/write-policy"
;;      {:name :route.game/write-policy
;;       :doc ""
;;       :view (fn []
;;               [:<>
;;                [c/current-entity entites]
;;                [c/review-effect]
;;                [c/projection {:tearable? true}]
;;                [c/write-policy]])}]]])

(defn game []
  (let [current-screen @(rf/subscribe [::sub/screen])]
    [:main.game.col.centered
     [#:screen

      {:sessions
       (fn []
         [:<>
          [c/session-library]])

       :intro
       (fn []
         [:<>
          [c/intro]
          [c/timeline]])

       :select-entity
       (fn []
         [:<>
          [c/select-entity]])

       :write-effect
       (fn []
         [:<>
          [c/current-entity]
          [:div.narrow.col.centered
           [c/intro-effect]
           [c/projection]
           [c/policy {:tearable? true}]
           [c/write-effect]]])

       :write-policy
       (fn []
         [:<>
          [c/current-entity]
          [:div.narrow.col.centered
           [c/intro-policy]
           [c/projection]
           [c/policy]
           [c/review-effect]
           [c/write-policy]]])

       :end 
       (fn []
         [:<>
          [c/thank-you]])} 
      current-screen]]))



