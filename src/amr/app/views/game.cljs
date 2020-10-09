(ns amr.app.views.game
  "The screens that form the game.

  TODO: move this to edn? How to read that with cljs? Ajax call most likely."
  (:require [re-frame.core :as rf]
            [reagent.core :as r]))

;; smaple data

(def lorem-ipsum "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")

(def sample-projection
  #:projection{:id (random-uuid)
               :author (random-uuid)
               :name "All the rice has died"
               :text lorem-ipsum
               :entities #{:entity/aqua :entity/flora}})

(def sample-projection2
  #:projection{:id (random-uuid)
               :author (random-uuid)
               :name "Wow still no rice"
               :text lorem-ipsum
               :entities #{:entity/aqua :entity/flora}})

(def sample-policy
  #:policy{:id (random-uuid)
           :author (random-uuid)
           :name "Resurrect all the rice"
           :text lorem-ipsum
           :tags #{:economic :farming}})

(def sample-reflection
  #:policy{:id (random-uuid)
           :author (random-uuid)
           :entity :entity/aqua
           :name "Resurrect all the rice"
           :impact :pos
           :text lorem-ipsum})

;; Navigation and UI

(defn change-screen [title screen]
  [:input.btn {:type "button"
               :value title
               :on-click #(rf/dispatch [:game/screen screen])}])

(defn nav [next]
  (let [previous @(rf/subscribe [:game/previous-screen])]
    [:nav
     (when previous [change-screen "Back" previous])
     [change-screen "Next" next]]))

(defn c'status-bars [entities]
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
   (when ui? [c'status-bars entities])])

(defn c'projection [{:projection/keys [id name text]}]
  [:div.card.col {:id id}
   [:section.padded.projection
    [:h1 name]
    [:p text]]
   [:footer.row
    [:label "Projection"]]])

(defn c'policy [{:policy/keys [id name text tags]}]
  [:div.card.col {:id id}
   [:section.padded.policy
    [:h1 name]
    [:p text]]
   [:footer.row
    [:label "Policy"]
    [:div.tags.row
     (for [tag tags]
       ^{:key tag} [:span.tag (str "#" (clojure.core/name tag))])]]])

(defn c'effect []
  [:div.card.col.effect
   [:section.padded.row {:style {:justify-content "space-between"}}
    [:p ":("]
    [:p "Aqua -1 Flora +1 Fauna - Homo-sapiens -2 Bacteria +3"]]
   [:section.padded
    [:p "oh no it all went bad"]]
   [:footer.padded.col  
    [:label "effects of the policy"]]])

;; TODO add validation
(defn c'reflection [policy-id]
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
                        :on-click #(do (rf/dispatch [:game/submit-reflection @state policy-id])
                                       (rf/dispatch [:game/screen :write-policy]))}]]]]))))

(defn c'archive []
  [:div.card.col.padded 
   [:p "Having a hard time figuring out what to write?"
    [:br] "Look at previous entities polices in the Archive for inspiration!"]
   [:a {:href "/archive"} "Go to the archive!"]])

(defn c'new-policy []
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
                     :on-click #(do (rf/dispatch [:game/submit-reflection @state])
                                    (rf/dispatch [:game/screen :write-policy]))}]]])))

;; Screens

(defn s'intro []
  [:div.card.padded 
   [:h1 "Introduction"]
   [:p "In a world of AMR, all the bees are dead and all the rice is gone."]
   [nav :entities]])

(defn s'entities []
  (rf/dispatch [:game/ui? true])
  (letfn [(entity [title descripiton]
            [:div.entity
             [:h2 title]
             [:p descripiton]])]
    [:div.card.padded
     [:h1 "Overview of entities"]
     [:div.entities
      [entity "Homo Sapiens" "evil evil men"]
      [entity "Aqua" "wow water nice"]
      [entity "Flora" "Monstera mastermind"]
      [entity "Fauna" "dogs and cats living together"]]
     [nav :select-entity]]))

(defn s'select-entity []
  (letfn [(entity [title key color]
            [:div.entity
             {:id (name key)
              :style {:background-color color}
              :on-click #(do (rf/dispatch [:game/select-entity key])
                             (rf/dispatch [:game/screen :projection]))} 
             title])]
    [:div.card.padded
     [:h1 "Select your entity"]
     [:div.select-entity
      [entity "Homo Sapiens" :homo-spaiens "brown"]
      [entity "Aqua"         :aqua         "blue"]
      [entity "Flora"        :flora        "green"]
      [entity "Fauna"        :fauna        "red"]]]))

(defn s'projection [projection policy]
  [:<>
   [c'projection projection]
   [c'policy policy]
   [c'reflection (:policy/id policy)]])

(defn s'write-policy [projection]
  [:<> 
   [c'effect]
   [c'projection projection]
   [c'archive]
   [c'new-policy]])

(defn game []
  (let [state @(rf/subscribe [:game/state])]
    [:main
     [game-ui state]
     (case (:screen state)
       :intro         [s'intro]
       :entities      [s'entities]
       :select-entity [s'select-entity]
       :projection    [s'projection sample-projection sample-policy]
       :write-policy  [s'write-policy sample-projection]
       [:div.error "No screen found."])]))
