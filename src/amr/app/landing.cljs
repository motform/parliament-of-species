(ns amr.app.landing
  (:require [reagent.core :as r]
            [reitit.frontend.easy :refer [href]]
            [amr.app.header :refer [balance]]))

(defn landing []
  (let [state (r/atom nil)]

    (letfn [(entity [name deg]
              [:span {:class name
                      :on-mouse-over #(reset! state name)
                      :on-mouse-out  #(reset! state nil)
                      :style {:transform (str "rotate(" deg "deg)")}}
               name])]

      (fn [] 
        [:main.landing.col 

         [:section.col {:class @state}
          [:div.content 
           [:h1 "In 2030 " [:em "Superbugs"] ", bacteria that resist to all existing antibiotics, are spreading across the world."]
           [:p "The " [:em "Parliament of Species"] " is created to face antibiotic resistance and its repercussions at a global level: the entities of" [entity "aqua" 1] "," [entity "flora" -2] "," [entity "fauna" -1] "&" [entity "homo-sapiens" 2] " try to create policies that positively impact their wellbeing by managing the threat of antibiotic resistance."]
           [:p "Entities need to find a balance in the policies they create in order to coexist."]]]

         [:section.col.landing-about
          [:h1 "Create policies with other representatives"]
          [:div.grid
           [:div
            [:p "Get the opportunity to represent one of the four entities of the parliament of species — Aqua, Fauna, Flora and Homo Sapiens."]
            [:div.icons.row
             [:img {:src "/svg/glyphs/aqua.svg"}] 
             [:img {:src "/svg/glyphs/fauna.svg"}] 
             [:img {:src "/svg/glyphs/flora.svg"}]
             [:img {:src "/svg/glyphs/homo-sapiens.svg"}]]]

           [:div
            [:p "Your role as a representative is to maintain the balance between the four entities while keeping the antimicrobial resistance at a low level. To do this, representatives collaborate together to create policies that positively impact the wellbeing indicator? of all of the entities.
"]
            [balance {:class "small"}]]

           [:div
            [:p "The resistance level responds to the postive or negative impact of the policies on the entities. If the resistance reaches the limit the parliament is reset and the policies that had the most negative impact on all entities are deleted from the collaborative platform. "]
            [balance #:entity{:aqua 2 :flora 2 :fauna 2 :homo-sapiens 2 :bacteria 22} {:class "small"}]]]

          [:div.landing-play
           [:a.enter {:href (href :route/game)} "Create a new policy"]]]

         [:section.col.landing-archive
          [:h1 "Explore the Archive of Species"]
          ]
         ])))
  )
