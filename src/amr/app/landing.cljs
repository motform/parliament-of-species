(ns amr.app.landing
  (:require [amr.app.header :refer [balance]]
            [amr.util :as util]
            [reagent.core :as r]
            [reitit.frontend.easy :refer [href]]))


(defn entity [name deg selected-entity]
  [:span {:class name
          :on-mouse-over #(reset! selected-entity name)
          :on-mouse-out  #(reset! selected-entity nil)
          :style {:transform (str "rotate(" deg "deg)")}}
   (util/prn-entity name)])

(defn hero []
  (let [selected-entity (r/atom nil)]
    (fn []
      [:section.col.centered.landing-hero {:class @selected-entity}
       [:h1.narrow "Welcome to the Parliament of Species, a platform where we collectively imagine the future of antibiotic-resistant bacteria. "]
       [:p.text "The year is 2030 — " [:em "antibiotic-resistant bacteria"] " have started causing infections that are not treatable with antibiotics. These bacteria are rapidly spreading across the globe threatening the ability to treat common infections. "]
       [:p.text "The " [:em "Parliament of Species"] ", consisting of the Entities " [entity "aqua" 1 selected-entity] "," [entity "flora" -2 selected-entity] "," [entity "fauna" -1 selected-entity] "&" [entity "homo-sapiens" 2 selected-entity] " was created to tackle antibiotic-resistant bacteria and its impact at a global level."]
       [:p.text "In order to coexist, the entities must find a balance between their wellbeing, while managing the level of antibiotic-resistant bacteria. Your role as an entity of The Parliament of Species is to maintain this balance through collaborative policy making."]
       [:p.text "The balance of the wellbeing of the entities is displayed in the coloured bar at the top of the screen, as well as the level of antibiotic-resistant bacteria. If the level of resistance increases past the global threshold, " [:em "The Parliament of Species"] " is reset and the Policy Proposals that caused the largest negative impact on all of the entities are discrded."]
       [:a.landing-visit {:href (href :route/policymaking)}
        "Participate in the Parliament"]])))

;; NOTE not in use
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
     [balance #:entity{:aqua 2 :flora 2 :fauna 2 :homo-sapiens 2 :bacteria 22} {:class "small" :labels? false}]]]
   [:a.landing-play {:href (href :route/policymaking)
                     :style {:margin-top "-30rem"}}
    "Create a new policy"]])

;; NOTE not in use
(defn archive []
  [:section.col.landing-archive
   [:h1 "Explore the Archive of Species"]
   [:p.text "The Parliament of Species archive contains the policies created by the representatives and the effects that they had on the entities."]
   [:a.landing-visit {:href (href :route/archive)}
    "View the Archive"]])

(defn landing []
  [:main.landing.col 
   [hero]])
