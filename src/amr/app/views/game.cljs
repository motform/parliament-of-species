(ns amr.app.views.game
  "The screens that form the game.

  TODO: move this to edn? How to read that with cljs? Ajax call most likely."
  (:require [re-frame.core :as rf]))

;; Helpers

(defn change-screen [title screen]
  [:button {:on-click #(rf/dispatch [:game/screen screen])}
   title])

(defn nav [next]
  (let [previous @(rf/subscribe [:game/previous-screen])]
    [:nav
     (when previous [change-screen "Back" previous])
     [change-screen "Next" next]]))

(defn status-bar [title id val]
  [:div.status-bar
   [:label {:for id} title]
   [:progress {:id id :max 10 :value val}
    (str val "%")]])

;; Screens

(defn intro []
  [:<>
   [:h1 "Introduction"]
   [:p "In a world of AMR, all the bees are dead and all the rice is gone."]
   [nav :entities]])

(defn overview []
  (let [{:keys [aqua flora fauna homo-sapiens]} @(rf/subscribe [:game/entities])]
    [:<>
     [:h1 "The world"]
     [:p "This is the world we live in."]
     [status-bar "Flora" "status-flora" flora]
     [status-bar "Fauna" "status-fauna" fauna]
     [status-bar "Aqua"  "status-aqua" aqua]
     [status-bar "Homo Sapiens" "status-homo-sapiens" homo-sapiens]
     [nav :select-entity]]))

(defn entities []
  (letfn [(entity [title descripiton]
            [:div.entity
             [:h2 title]
             [:p descripiton]])]
    [:<>
     [:h1 "Overview of entities"]
     [:div.entities
      [entity "Homo Sapiens" "evil evil men"]
      [entity "Aqua" "wow water nice"]
      [entity "Flora" "Monstera mastermind"]
      [entity "Fauna" "dogs and cats living together"]]
     [nav :overview]]))

(defn select-entity []
  (letfn [(entity [title key color]
            [:div.entity
             {:id (name key)
              :style {:background-color color}
              :on-click #(do (rf/dispatch [:game/select-entity key])
                             (rf/dispatch [:game/screen :event]))} 
             title])]
    [:<>
     [:h1 "Select your entity"]
     [:div.select-entity
      [entity "Homo Sapiens" :homo-spaiens "brown"]
      [entity "Aqua"         :aqua         "blue"]
      [entity "Flora"        :flora        "green"]
      [entity "Fauna"        :fauna        "red"]]]))

(defn event []
  [:<>
   [:h1 "Event!"]])

(defn game []
  (let [screen @(rf/subscribe [:game/screen])]
    [:main 
     (case screen
       :intro [intro]
       :overview [overview]
       :entities [entities]
       :select-entity [select-entity]
       :event [event]
       [:div.error "No screen found."])]))
