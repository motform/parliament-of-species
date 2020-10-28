(ns amr.app.landing
  (:require [reagent.core :as r]
            [reitit.frontend.easy :refer [href]]
            [amr.app.header :refer [balance]]))


(defn entity [name deg selected-entity]
  [:span {:class name
          :on-mouse-over #(reset! selected-entity name)
          :on-mouse-out  #(reset! selected-entity nil)
          :style {:transform (str "rotate(" deg "deg)")}}
   name])

(defn hero []
  (let [selected-entity (r/atom nil)]
    (fn []
      [:section.col.landing-hero {:class @selected-entity}
       [:div.content 
        [:h1 "In 2030, " [:em "Superbugs"] " are rapidly spreading across the globe threatening the ability to treat common infections."]
        [:p [:em "Superbugs:"] " drug-resistant pathogens that have acquired new resistance mechanisms that cause infections that are not treatable with antibiotics (WHO)."]
        [:p "The " [:em "Parliament of Species"] " has been created to tackle antibiotic resistance and its repercussions at a global level. The entities of" [entity "aqua" 1 selected-entity] "," [entity "flora" -2 selected-entity] "," [entity "fauna" -1 selected-entity] "&" [entity "homo-sapiens" 2 selected-entity] " have united to mange the threat of antimicrobial resistance by creating global policies that positively impact their wellbeing."]
        [:p "In order to coexist, the entities must find a balance between their wellbeing and while managing the level of antimicrobial resistance."]]])))

(defn about []
  [:section.col.landing-about
   [:h1 "Create global policies with other entities"]
   [:div.grid.narrow
    [:div
     [:div.icons.row.spaced
      [:img {:src "/svg/glyphs/landing/aqua.svg"}] 
      [:img {:src "/svg/glyphs/landing/fauna.svg"}]] 
     [:div.icons.row.spaced
      [:img {:src "/svg/glyphs/landing/flora.svg"}]
      [:img {:src "/svg/glyphs/landing/homo-sapiens.svg"}]]]
    [:div
     [:p "Your role as a representative of The Parliament of Speices is to maintain the balance between the four entities while managing the antimicrobial resistance at a low level."]
     [:p "In order to maintain the balance, representatives collaborate to create new global policies in response to the repercussions of antimicrobial resistance that positively impact all of the entities. The balance of the wellbeing of the entities is displayed in the coloured bar at the top of the screen."]
     [balance {:class "small" :labels? false}]]
    [:div
     [:p "The wellbeing of the entities effects the level of antimicrobial resistance. When a new policy has been created, a different entity receives the policy to assess whether this has a positive or negative impact on their wellbeing. When an entity responds positively, the level of antimicrobial resistance decreases, when it responds negatively it increases."]
     [:p "If the level of resistance increases past the global threshold, The Parliament of Species is reset and the polices that caused the largest negative impact on all of the entities are abolished."]
     [balance #:entity{:aqua 2 :flora 2 :fauna 2 :homo-sapiens 2 :resistance 22} {:class "small" :labels? false}]]]
   [:div.landing-play {:href (href :route/policymaking)}
    "Create a new policy"]])

(defn archive []
  [:section.col.landing-archive
   [:h1 "Explore the Archive of Species"]
   [:p.text "The Parliament of Species archive contains the policies created by the representatives and the effects that they had on the entities."]
   [:div.landing-visit {:href (href :route/archive)}
    "View the Archive"]])

(defn landing []
  [:main.landing.col 
   [hero]
   [about]
   [archive]])
