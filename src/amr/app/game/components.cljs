(ns amr.app.game.components
  (:require [amr.app.game.events :as event]
            [re-frame.core :as rf]
            [reagent.core :as r]))

;;; UI ;;;

(defn change-screen [title screen]
  [:input.btn {:type "button"
               :value title
               :on-click #(rf/dispatch [::event/screen screen])}])

(defn nav [next]
  (let [previous @(rf/subscribe [::event/previous-screen])]
    [:nav
     (when previous [change-screen "Back" previous])
     [change-screen "Next" next]]))

(defn status-bars [entities]
  (letfn [(status-bar [title id val]
            [:div.status-bar.col {}
             [:label {:for id} title]
             [:progress {:id id :max 10 :value val}
              (str val "%")]])]
    [:div.status-bars.card.padded
     (for [[id val] entities]
       ^{:key id} [status-bar (name id) id val])]))

(defn game-ui [{:keys [entity entities ui?]}]
  [:header.ui.col 
   [:h1 "Parliament of Species"]
   [:h3 "A social policy making game"]
   (when entity [:h1.card.padded entity])
   (when ui? [status-bars entities])])

;;; CARDS ;;;

(defn projection [{:projection/keys [id name text]}]
  [:div.card.col {:id id}
   [:section.padded.projection
    [:h1 name]
    [:p text]]
   [:footer.row
    [:label "Projection"]]])

(defn policy [{:policy/keys [id name text tags]}]
  [:div.card.col {:id id}
   [:section.padded.policy
    [:h1 name]
    [:p text]]
   [:footer.row
    [:label "Policy"]
    [:div.tags.row
     (for [tag tags]
       ^{:key tag} [:span.tag (str "#" (clojure.core/name tag))])]]])

(defn effect []
  [:div.card.col.effect
   [:section.padded.row {:style {:justify-content "space-between"}}
    [:p ":("]
    [:p "Aqua -1 Flora +1 Fauna - Homo-sapiens -2 Bacteria +3"]]
   [:section.padded
    [:p "oh no it all went bad"]]
   [:footer.padded.col  
    [:label "effects of the policy"]]])

;; TODO add validation
(defn reflection [policy-id]
  (let [state (r/atom {:text "" :reaction nil})]
    (letfn [(b'reaction [label k state] ^{:key k}
              [:input.btn.btn-reaction
               {:type "button"
                :value label
                :on-click #(swap! state assoc :reaction k)
                :class (when (= k (:reaction @state)) "btn-reaction-active")}])]
      (fn [] 
        [:div.card.reflection
         [:section.padded
          [:h1 "How does this impact you?"]
          [:p "Elaborate on how this affects you."]
          [:form.col
           [:div.btns
            [b'reaction "Good" :pos state]
            [b'reaction "Bad"  :neg state]] 
           [:textarea {:rows 10
                       :value (:text @state)
                       :on-change #(swap! state assoc :text (-> % .-target .-value))}]
           [:input.btn {:type "button"
                        :value "Submit"
                        :on-click #(do (rf/dispatch [::event/submit-reflection @state policy-id])
                                       (rf/dispatch [::event/screen :write-policy]))}]]]]))))

(defn archive []
  [:div.card.col.padded 
   [:p "Having a hard time figuring out what to write?"
    [:br] "Look at previous entities polices in the Archive for inspiration!"]
   [:a {:href "/archive"} "Go to the archive!"]])

(defn new-policy []
  (let [state (r/atom {:text "" :name "" :tags ""})]
    (fn [] 
      [:div.card.policy
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
                     :on-click #(do (rf/dispatch [::event/submit-reflection @state])
                                    (rf/dispatch [::event/screen :write-policy]))}]]])))
