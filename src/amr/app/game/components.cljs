(ns amr.app.game.components
  (:require [amr.app.game.events :as event]
            [amr.app.events :as app]
            [amr.app.game.subs :as sub]
            [amr.util :as util]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [reitit.frontend.easy :refer [href]]
            [clojure.string :as str]))

;;; UI ;;;

(defn status-bars [entities]
  (letfn [(status-bar [title id val]
            [:div.status-bar.col {}
             [:label {:for id} title]
             [:progress {:id id :max 10 :value val}
              (str val "%")]])]
    [:div.status-bars.card.padded
     (for [[id val] entities]
       ^{:key id} [status-bar (name id) id val])]))

;;; CARDS ;;;

;;; GENERAL

(defn banner [title]
  [:div.col.banner
   [:section.padded.col
    [:h1 title]]])

(defn text [{:keys [title texts]}]
  [:div.col.card.text
   [:section.padded.col
    [:h1 title]
    (for [text texts]
      ^{:key text} [:p text])]])

(defn ul [label items]
  [:<>  
   [:h2.list-heading label]
   [:ul {:class label}
    (for [item items]
      ^{:key item} [:li.item item])]])

;;; SPECIFIC

(defn timeline [years]
  (let [state (r/atom "2020")]
    (fn []
      [:div.card.col.timeline
       [:section.padded
        [:input.timeline
         {:type "range" :list "years"
          :value @state :min 2020 :max 2024 :step 1
          :on-change #(reset! state (.. % -target -value))}]
        [:datalist#years
         [:option {:label "2020"} 2020]
         [:option {:label "2021"} 2021]
         [:option {:label "2022"} 2022]
         [:option {:label "2023"} 2023]
         [:option {:label "2024"} 2024]]
        [:h1.year @state]
        [:p (years @state)]]])))

(defn entity [{:keys [key represents relation]} {:keys [clickable?]}]
  (let [session #:session{:id (random-uuid) :date (js/Date.) :entity key}]
    [:div.card-border.entity {:class (str (name key) " " (when clickable? "clickable"))
                              :id (name key)
                              :on-click #(when clickable?
                                           (rf/dispatch [::event/create-session session])
                                           (rf/dispatch [::event/submit-session session])
                                           (rf/dispatch [::event/request-stack-for (name key)])
                                           (rf/dispatch [::app/scroll-to-top])
                                           (rf/dispatch [::event/screen :screen/reflection]))}
     [:div.card.col 
      [:div.card-header
       [:label "Entity"]]
      [:section.padded.row 
       [:img.entity-portrait {:src (str "/img/entities/" (name key) ".png")}]
       [:div.col
        ;; [:h1 (name key)]
        [ul "represents" represents]
        [ul "relation" relation]]]]]))

(defn current-entity [entites]
  (let [current-entity (:session/entity @(rf/subscribe [::sub/from-session :session]))]
    [entity (first (filter #(= current-entity (:key %)) entites)) {:clickable? false}]))

(defn projection
  ([]
   (let [p @(rf/subscribe [::sub/from-session :projection])] 
     (projection p {:clickable? false})))
  ([{:projection/keys [id text name] :as projection} {:keys [clickable? screen]}]
   [:div.card-border.projection.bacteria
    {:id id
     :class (when clickable? "clickable")
     :on-click #(when clickable?
                  (rf/dispatch [::event/select-projection projection])
                  (rf/dispatch [::event/screen screen]))}
    [:div.card.col 
     [:div.card-header
      [:label "Projection"]]
     [:section.padded
      [:h1 name]
      [:p text]]]]))

(defn policy []
  (let [{:policy/keys [id name text tags]} @(rf/subscribe [::sub/from-session :policy])]  
    [:div.policy.card-border.flora {:id id}
     [:div.card.col.policy-body
      [:div.card-header
       [:label "Policy by Fauna"]
       [:div.tags.row
        (for [tag tags]
          ^{:key tag} [:span.tag (str "#" (clojure.core/name tag))])]]
      [:section.padded
       [:h1 name]
       [:p text]]]]))

;;; FORMS ;;;

(defn reflection
  "Component for creating submitting effects. Some might call it badly named."
  []
  (let [state (r/atom #:effect{:text "" :impact nil :hover? false})]

    (letfn [(btn-impact [label k] ^{:key k}
              (let [active? (when (= k (:effect/impact @state)) "button-active")]
                [:input.btn.btn-impact
                 {:type "button"
                  :value label
                  :on-click #(swap! state assoc :effect/impact k)
                  :id (when active? "button-active")}]))

            (valid-input? [{:effect/keys [text impact]}]
              (and impact (> (count text) 30)))]
      
      (fn [] 
        (let [policy  @(rf/subscribe [::sub/from-session :policy])
              session @(rf/subscribe [::sub/from-session :session])
              effect  #:effect{:id (random-uuid) :policy (:policy/id policy) :session (:session/id session)}]

          [:div.card.effect {:class (when (:effect/hover? @state) "tear")}
           [:div.card-header.header-entity
            [:label "Effect submission from"]]
           [:section.padded
            [:h1 (str "How does this impact " (name (:session/entity session))) " ?"]
            [:p "Elaborate on how this affects you."]
            [:form.col
             [:div.impact.row
              [btn-impact "Positively" :impact/positive]
              [btn-impact "Negatively" :impact/negative]] 
             [:textarea {:rows 10
                         :value (:effect/text @state)
                         :on-change #(swap! state assoc :effect/text (.. % -target -value))}]
             [:input#submit
              {:type "button"
               :value "submit"
               :on-mouse-over (fn [] (swap! state assoc :effect/hover? true))
               :on-mouse-out  (fn [] (swap! state assoc :effect/hover? false))
               :disabled (if (valid-input? @state) false true)
               :on-click #(do (rf/dispatch [::event/submit-effect (merge @state effect)])
                              (rf/dispatch [::app/scroll-to-top])
                              (case (:effect/impact @state)
                                :impact/negative
                                (do (rf/dispatch [::event/screen :screen/derive-policy]))

                                :impact/positive
                                (do (rf/dispatch [::event/screen :screen/select-projection])
                                    (rf/dispatch [::event/request-all :projection]))))}]]]])))))

(defn select-projection []
  (let [current-projection @(rf/subscribe [::sub/from-session :projection])
        projections (->> @(rf/subscribe [::sub/from-temp :projection])
                         (remove #(= % current-projection)))]
    [:<> 
     (for [p projections]
       ^{:key (:projection/id p)}
       [projection p {:clickable? true :screen :screen/new-policy}])]))

;; get the derived key from somewhere idk
(defn write-policy [{:keys [derived]}]
  (let [state (r/atom #:policy{:text "" :name "" :tags ""})
        {session-id :session/id} @(rf/subscribe [::sub/from-session :session])
        {projection-id :projection/id} @(rf/subscribe [::sub/from-session :projection])
        policy #:policy{:projection projection-id :session session-id :id (random-uuid) :derived derived}]
    (fn [] 
      [:div.card.write-policy
       [:section.padded
        [:h1 "New policy"]
        [:p "Write a new policy to addresses the bad thing that just happened."]]
       [:form.col.padded
        [:div.row 
         [:div.col {:style {:margin-right "5rem"}}
          [:label "Name"]
          [:textarea {:rows 1
                      :value (:name @state)
                      :on-change #(swap! state assoc :name (-> % .-target .-value))}]]
         [:div.col 
          [:label "Tags"] ;; TODO should be an [:select [:option]]
          [:textarea {:rows 1
                      :value (:tags @state)
                      :on-change #(swap! state assoc :tags (-> % .-target .-value))}]]]
        [:label "Contents"]
        [:textarea {:rows 10
                    :value (:text @state)
                    :on-change #(swap! state assoc :text (-> % .-target .-value))}]
        [:input.btn {:type "button"
                     :value "Submit"
                     :on-click #(rf/dispatch [::event/submit-policy (merge @state policy)])}]]])))
