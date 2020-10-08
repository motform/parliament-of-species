(ns amr.app.views.app
  (:require [amr.app.views.home :refer [home]]
            [amr.app.subs :as subs]
            [re-frame.core :as rf]))

(defn active-page [page]
  (case page
    :home [:h1 "foobar!"]
    [:h1 "Error, no page found!"]))

(defn app []
  (let [page @(rf/subscribe [:active-page])]
    [:<>
     ;; [header]
     [active-page page]]))
