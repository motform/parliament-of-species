(ns amr.app.views.app
  (:require [amr.app.views.home :refer [home]]
            [amr.app.views.game :refer [game]]
            [amr.app.subs :as subs]
            [re-frame.core :as rf]))

(defn active-page [page]
  (case page
    :home [home]
    :game [game]
    [:div.error "Error, no page found!"]))

(defn app []
  (let [page @(rf/subscribe [::subs/active-page])]
    [:<>
     ;; [header]
     [active-page page]]))
