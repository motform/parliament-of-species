(ns amr.app.about.core
  (:require [amr.app.game.components :as game]
            [reitit.frontend.easy :refer [href]]
            [re-frame.core :as rf]))

(defn entity [{:keys [key represents relation]}]
  [:div.entity.bg.col.centered
   {:style {:background-image (str "url(/svg/bg/policy/" (name key) ".svg)")
            :background-color (str "var(--" (name key) "-bg)")
            :padding "5rem 0"}}
   [:div.narrow.row
    [:img {:src (str "/svg/entity/" (name key) ".svg")}]
    [:div.col
     [:p.text represents]
     [:p.text relation]]]])

(defn about []
  [:<> 
   [:main.about.padded.col.centered
    [:h1 "the Parliament of Species"]
    [:p "The Parliament of Species is established in 2030 to tackle AMR and its repercussions at a global level. The entities of Aqua, Fauna, Flora and Homo Sapiens have to create policies that positively impact their wellbeing by managing the threat of AMR."]]
   [:section.about-entities  
    (for [{:keys [key] :as e} game/entites]
      ^{:key key}
      [entity e])]
   [game/timeline {:static? true}]
   [:section.col.centered  
    [:a.entry
     {:href (href :route/policymaking)}
     "Write a policy"]]])
