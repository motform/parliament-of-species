(ns amr.app.about.core
  (:require [amr.app.game.components :as game]))

(defn entity [{:keys [key represents relation]}]
  [:div.entity.bg.col.centered
   {:style {:background-image (str "url(/svg/bg/policy/" (name key) ".svg)")
            :background-color (str "var(--" (name key) "-bg)")
            :padding "5rem 0"}}
   [:div.narrow.row {:style {:margin "0 auto"}}
    [:img {:src (str "/svg/entity/" (name key) ".svg")}]
    [:div.col.narrow {:style {:justify-content "center"}}
     [:p.text represents]
     [:p.text relation]]]])

(defn about []
  [:<> 
   [:main.about.padded.col.centered
    [:h1 "The Entities"]
    [:p "The Parliament of Species is established in 2030 to tackle antibiotic-resistance and its repercussions at a global level. The entities of Aqua, Fauna, Flora and Homo Sapiens have to create policies that positively impact their well-being by managing the threat of antibiotic-resistance."]]
   [:section.about-entities  
    (for [{:keys [key] :as e} game/entites]
      ^{:key key}
      [entity e])]])
