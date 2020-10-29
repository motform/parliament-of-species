(ns amr.app.archive.core
  (:require [amr.app.archive.events :as event]
            [amr.app.archive.subs :as sub]
            [amr.util :as util]
            [re-frame.core :as rf]
            [clojure.string :as str]))

(defn archive-hero []
  [:div.archive-hero 
   [:h1 "Archive of the Future"]
   [:p "The Archive contains the policies created by the representatives categorized by scientific projections. As the polices are assessed by the other entities their effects that they had are added to the archive."]])

(defn selection-bar [selected ns xs]
  (let [ks (map #(keyword ns %) [:id :name])
        xf (comp util/unqualify #(select-keys % ks))
        ms (map xf (vals xs))]
    [:ul.row
     (for [{:keys [id name]} (sort-by :name ms)]
       ^{:key id} [:li {:class (when (= id selected) "selected")
                        :on-click #(rf/dispatch [::event/select ns id])}
                   name])]))

(defn projection [{:projection/keys [text id]}]
  [:div.selected-projection.grid
   [:img {:src (str "/svg/glyphs/projection/" id ".svg")}]
   [:div {:style {:grid-column "span 2"}}
    (for [t (str/split-lines text)]
      ^{:key (first t)}
      [:p.projection-text t])]])

(defn projections [selected-projection]
  (let [projections @(rf/subscribe [::sub/projections])]
    [:section.col.narrow
     [selection-bar selected-projection :projection projections]
     [projection (projections selected-projection)]]))

(defn impact [effects]
  [:div.impact.row
   (for [[entity {:impact/keys [negative positive]}] (util/calculate-impact effects)]
     ^{:key entity}
     [:h5 {:class (str (name entity) "-fg")}
      "▲ " (if-let [p positive] p 0) " "
      "▼ " (if-let [n negative] n 0)])])

(defn policy [{:policy/keys [name id text effects session]} selected]
  (let [entity (:session/entity session)]
    [:div.archive-policy.col {:class (when (= id selected) "selected-policy")
                              :on-click #(rf/dispatch [::event/select :policy id])}
     [:h3 name]
     [:h4 [:em {:class (str (clojure.core/name entity) "-fg")} entity]]
     [:p text]
     [:h5 (if effects [impact effects] "No effects.")]]))

(defn policies [selected-projection selected-policy]
  (let [{:projection/keys [name policies]} @(rf/subscribe [::sub/policies-for selected-projection])]
    [:<> 
     [:div.separator.narrow (count policies) " polices for " name]
     [:section.policies.col.narrow>div.grid
      (for [p (vals policies)]
        ^{:key (:policy/id p)}
        [policy p selected-policy])]]))

(defn effect [{:effect/keys [session impact text]}]
  (let [entity (:session/entity session)]
    [:div.col
     [:label (name impact)]
     [:p.signature {:class (str (name entity) "-fg")} (name entity)]
     [:p text]]))

(defn effects [projection policy]
  (let [effects @(rf/subscribe [::sub/effects-for projection policy])]
    (when-not (empty? (:policy/effects effects))
      [:<>  
       [:div.separator.narrow {:style {:border-top "0"}}
        "Effects of " (:policy/name effects) ]
       [:section.effects.grid.narrow
        (for [e (:policy/effects effects)]
          [effect e])]])))

(defn discarded []
  [:section.discarded.col
   [:h1 "Discarded polices"]
   [:p "Will probably not be in the first version."]])

(defn archive []
  (let [selected-projection @(rf/subscribe [::sub/selected :projection])
        selected-policy @(rf/subscribe [::sub/selected :policy])]
    [:<> 
     [:main.archive.col.centered
      [archive-hero]
      [projections selected-projection]
      [policies selected-projection selected-policy]
      [effects selected-projection selected-policy]]]))
