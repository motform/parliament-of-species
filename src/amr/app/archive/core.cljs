(ns amr.app.archive.core
  (:require [amr.app.archive.events :as event]
            [amr.app.archive.subs :as sub]
            [amr.util :as util]
            [re-frame.core :as rf]))

(defn selection-bar [xs]
  [:div.row
   (for [x (util/unqualify (into {} xs))]
     [:p (prn-str x)])])

(defn projection [{:projection/keys [name text]}]
  [:div.selected-projection.row
   [:img {:src "/svg/glyphs/fauna.svg"}]
   [:div.body
    [:h1 name]
    [:p text]]])

(defn projections []
  (let [projections @(rf/subscribe [::sub/from-archive :projection])
        selected-projection @(rf/subscribe [::sub/selected-projection])]
    [:section.projections.col
     [:h1 "Projections"]
     [selection-bar (vals projections)]
     [projection (projections selected-projection)]]))

(defn policies []
  (let [policies @(rf/subscribe [::sub/from-archive :policy])]
    [:section.policies.col
     [:h1 "Polices for Fish"]
     (for [[id {:policy/keys [name text]}] policies]
       ^{:key id}
       [:div
        [:h2 text]
        [:p name]])]))

(defn effects []
  (let [effects @(rf/subscribe [::sub/from-archive :effect])]
    [:section.effects.col
     [:h1 "Effects of the policy"]
     ]))

(defn archive []
  [:<> 
   [:main.archive.padded
    [projections]
    [policies]
    [effects]]
   [:section.discarded
    [:h1 "Discarded polices"]
    [:p "Will probably not be in the first version."]]])
