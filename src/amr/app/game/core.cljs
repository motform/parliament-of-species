(ns amr.app.game.core
  "The screens that form the game.

  TODO: Make the screens cars in a fluid, data-driven queue."
  (:require [amr.app.game.components :as c]
            [amr.app.game.events :as event]
            [amr.app.game.subs :as sub]
            [re-frame.core :as rf]
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

;; Screens

(defn s'intro []
  [:div.card.padded 
   [:h1 "Introduction"]
   [:p "In a world of AMR, all the bees are dead and all the rice is gone."]
   [c/nav :entities]])

(defn s'entities []
  (rf/dispatch [::event/ui? true]) ;; TODO Refactor this into nil pun on :game :entities map
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
     [c/nav :select-entity]]))

(defn s'select-entity []
  (letfn [(entity [title key color]
            [:div.entity
             {:id (name key)
              :style {:background-color color}
              :on-click #(do (rf/dispatch [::event/select-entity key])
                             (rf/dispatch [::event/screen :projection]))} 
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
   [c/projection projection]
   [c/policy policy]
   [c/reflection (:policy/id policy)]])

(defn s'write-policy [projection]
  [:<> 
   [c/effect]
   [c/projection projection]
   [c/archive]
   [c/new-policy]])

(defn game []
  (let [state @(rf/subscribe [::sub/state])]
    [:main
     [c/game-ui state]
     ;; TODO refactor into cards/router?
     (case (:screen state)
       :intro         [s'intro]
       :entities      [s'entities]
       :select-entity [s'select-entity]
       :projection    [s'projection sample-projection sample-policy]
       :write-policy  [s'write-policy sample-projection]
       [:div.error "No screen found."])]))
