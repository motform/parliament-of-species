(ns amr.app.routes
  (:require [amr.app.archive.core :refer [archive]]
            [amr.app.game.core :refer [game]]
            [amr.app.home.core :refer [home]]
            [amr.app.events :as event]
            [amr.app.subs :as sub]
            [reitit.core :as reitit]
            [reitit.frontend :as reitit.frontend]
            [reitit.frontend.easy :as reitit.easy]
            [re-frame.core :as rf]))

;;; ROUTER ;;;

(def titles
  {:home ""
   :archive "Archive of Futures"
   :game "Game"})

;; TODO add route coercion as per https://github.com/metosin/reitit/blob/master/examples/frontend-re-frame/src/cljs/frontend_re_frame/core.cljs
;; TODO stop the href from linking to '/#/uri' 
(def routes
  ["/"
   [""
    {:name :route/home
     :view home
     :link-text "Home"}]
   ["game"
    {:name :route/game
     :view game
     :title "Collaborative Policy Making"
     :link-text "Game"}]
   ["archive"
    {:name :route/archive
     :view archive
     :title "Archive of Futures"
     :link-text "Archive"}]])

(def router
  (reitit.frontend/router routes))

;;; FNS ;;;

(defn on-navigate [new-match]
  (when new-match
    (rf/dispatch [::event/navigated new-match])))

(defn init-routes! []
  (reitit.easy/start!
   router
   on-navigate
   {:use-fragment true}))

;;; COMPONENTS ;;;

;; (defn nav [router current-route]
;;   [:ul
;;    (for [route-name (reitit/route-names router)
;;          :let [route (reitit/match-by-name router route-name)
;;                text (-> route :data :link-text)]]
;;      [:li {:key route-name}
;;       [:a {:href (href route-name)} text]])])

(defn router-component [router]
  (let [current-route @(rf/subscribe [::sub/route])]
    [:<>
     ;; [nav router current-route]
     (when current-route
       [(-> current-route :data :view)])]))
