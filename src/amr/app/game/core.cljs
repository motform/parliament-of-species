(ns amr.app.game.core
  (:require [amr.app.game.components :as c]
            [amr.app.game.subs :as sub]
            [re-frame.core :as rf]))

(defn session-library []
  [:main.game.col.centered
   [c/session-library]])

(defn intro []
  [:main.game.col.centered
   [c/intro]
   [c/timeline]])

(defn select-entity []
  (let [session @(rf/subscribe [::sub/current-session])]
    [:main.game.col.centered 
     [c/select-entity session]]))

(defn write-effect []
  (let [session @(rf/subscribe [::sub/current-session])]
    (println session)
    (if-not (:session session)
      [c/no-session]
      [:main.game.col.centered
       [c/current-entity]
       [:div.narrow.col.centered
        [c/intro-effect]
        [c/projection]
        [c/policy {:tearable? true}]
        [c/write-effect]]])))

(defn write-policy []
  (let [session @(rf/subscribe [::sub/current-session])]
    (if-not (:session session) 
      [c/no-session]
      [:main.game.col.centered
       [c/current-entity]
       [:div.narrow.col.centered
        [c/intro-policy]
        [c/projection]
        [c/policy]
        [c/review-effect]
        [c/write-policy]]])))

(defn end []
  [:main.game.col.centered
   [c/thank-you]])
