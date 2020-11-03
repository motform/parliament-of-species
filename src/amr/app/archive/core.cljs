(ns amr.app.archive.core
  (:require [amr.app.archive.events :as event]
            [amr.app.archive.subs :as sub]
            [amr.util :as util]
            [re-frame.core :as rf]
            [clojure.string :as str]
            [reagent.core :as r]))

(defn archive-hero []
  [:div.archive-hero 
   [:h1 "Archive of Futures"]
   [:p "The Archive contains the Policy Proposals created by the representatives categorized by scientific Projections. As the Proposals are Assessed by the other entities, their reviews are added to the archive."]])

(defn selection-bar [selected ns xs]
  (let [ks (map #(keyword ns %) [:id :name])
        xf (comp util/unqualify #(select-keys % ks))
        ms (map xf (vals xs))]
    [:ul.row.narrow
     (for [{:keys [id name]} (sort-by :name ms)]
       ^{:key id} [:li {:class (when (= id selected) "selected")
                        :on-click #(rf/dispatch [::event/select ns id])}
                   name])]))

(defn projection [{:projection/keys [text id]}]
  [:div.selected-projection.wide.col.centered>div.grid.narrow
   [:img {:src (str "/svg/glyphs/projection/" id ".svg")}]
   [:div {:style {:grid-column "span 2"}}
    (for [t (str/split-lines text)]
      ^{:key (first t)}
      [:p.projection-text t])]])

(defn projections [selected-projection]
  (let [projections @(rf/subscribe [::sub/projections])]
    [:section.col.centered.wide
     [selection-bar selected-projection :projection projections]
     [projection (projections selected-projection)]]))

(defn impact [effects]
  [:div.impact.row.spaced.wide
   (if-not effects 
     [:span.wide.col.centered "No effects"]
     (for [[entity {:impact/keys [negative positive]}] (util/calculate-impact effects)]
       ^{:key entity}
       [:h5 {:class (str (name entity) "-fg")}
        "▲ " (if-let [p positive] p 0) " "
        "▼ " (if-let [n negative] n 0)]))])

(defn effect [{:effect/keys [session impact text]}]
  (let [entity (:session/entity session)]
    [:div.col
     [:p.signature {:class (str (name entity) "-fg")}
      [:span.arrow (#:impact{:positive "▲ ":negative "▼ "} impact)] (util/prn-entity entity)]
     [:p text]]))

(defn effects [projection policy]
  (let [effects @(rf/subscribe [::sub/effects-for projection policy])]
    (when-not (empty? (:policy/effects effects))
      [:section.effects.col.centered.wide
       [:div.label "Entity assessments of this Policy Proposal"]
       [impact (:policy/effects effects)]
       [:div.grid-auto.padded
        (for [e (:policy/effects effects)]
          ^{:key e}
          [effect e])]])))

(defn policy-card [{{entity :session/entity} :policy/session
                    :policy/keys [name id text effects session]} selected state]
  (let [entity-name (clojure.core/name entity)
        {:keys [hover select]} @state]
    [:div.policy-card
     {:on-mouse-over #(swap! state assoc :hover id)
      :on-mouse-out  #(swap! state assoc :hover nil)
      :on-mouse-down #(swap! state assoc :select id)}
     [:div.col.bg-small.padded
      {:style (if (or (= hover id) (= select id) (not (or hover select)))
                {:background-image (str "url(/svg/bg/policy/" entity-name ".svg)")    :background-color (str "var(--" entity-name "-bg)")}
                {:background-image (str "url(/svg/bg/policy/bg-" entity-name ".svg)") :background-color "var(--bg-card)"})
       :on-click #(rf/dispatch [::event/select :policy id])}
      [:h2 name]
      [:h4 (util/prn-entity entity)]
      [:p text]]
     [impact effects]]))

(defn policy [{{entity :session/entity} :policy/session
               :policy/keys [id text name session derived]}
              selected-projection selected-policy state] ; TODO have less props in this
  (let [entity-name (clojure.core/name entity)]
    [:div.archive-policy
     [:div.grid.bg.archive-policy-header
      {:style
       {:background-image (str "url(/svg/bg/policy/" entity-name ".svg)")
        :background-color (str "var(--" entity-name "-bg)")}}
      [:h2 {:style {:grid-column "span 3"}} name]
      [:p  {:style {:grid-column "span 2"}} text]]
     #_[:div (if derived ;; TODO change derived, bad name
               [:p "This policy is derived from "
                [:span {:on-click #(do (swap! state assoc :select derived)
                                       (rf/dispatch [::event/select :policy derived]))}
                 "another policy."
                 #_derived]] ;; TODO get the policy name
               [:p "This policy is not derived."])]
     [effects selected-projection selected-policy]]))

(defn policies []
  (let [state (r/atom {:hover nil :select nil})]
    (fn [selected-projection selected-policy]
      (let [{:projection/keys [policies]} @(rf/subscribe [::sub/policies-for selected-projection])]
        [:section.policies.col.centered.wide
         [:div.grid {:style {:grid-gap "5rem"}}
          [:div.col {:style {:grid-row "span 2"}}
           [:div.label.wide (count policies) " Policy Proposals"]
           [:div.policy-cards 
            (for [p (->> policies vals (sort-by #(count (:policy/effects %))) reverse)]
              ^{:key (:policy/id p)}
              [policy-card p selected-policy state])]]
          (if (get policies selected-policy) ; MUST be an explicit call to `get` as policies might be nil
            [policy (policies selected-policy) selected-projection selected-policy state]
            [:div.label.wide.archive-policy
             "Select a Policy Proposal in the menu to the left to learn more it."])]]))))

(defn discarded []
  [:section.discarded.col
   [:h1 "Discarded polices"]
   [:p "Will probably not be in the first version."]])

(defn archive []
  (let [selected-projection @(rf/subscribe [::sub/selected :projection])
        selected-policy @(rf/subscribe [::sub/selected :policy])]
    [:main.archive.col.centered.wide
     [archive-hero]
     [projections selected-projection]
     [policies selected-projection selected-policy]
     #_[effects  selected-projection selected-policy]]))
