(ns amr.app.game.core
  (:require [amr.app.game.components :as c]
            [amr.app.game.events :as event]
            [amr.app.game.subs :as sub]
            [amr.app.subs :as app]
            [re-frame.core :as rf]))

;;; DATA

(def lorem-ipsum
  "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")

(def intro-text
  ["AMR: Antimicrobial resistance happens when microorganisms, such as bacteria [...], change when they are exposed to antimicrobial drugs, such as antibiotics[...]. Microorganisms that develop antimicrobial resistance are sometimes referred to as “superbugs”."

   "As a result, the medicines become ineffective and infections persist in the body, increasing the risk of spread to others. - WHO "])

(def years
  {"2020" (str "2020 " lorem-ipsum)
   "2021" (str "2021 " lorem-ipsum)
   "2022" (str "2022 " lorem-ipsum)
   "2023" (str "2023 " lorem-ipsum)
   "2024" (str "2024 " lorem-ipsum)})

(def entites
  [{:key :entity/aqua
    :represents ["Oceans, rivers, lakes" "Rain"]
    :relation ["Antibiotics used on many aquatic animals" "Sewedge" "Antibiotic pollution in rivers"]}

   {:key :entity/flora
    :represents ["Plantkind" "Agriculture"]
    :relation ["Antibiotics used in veggies" "Since animals are given antibiotics, their wastes are also affecting the ecosystem"]}

   {:key :entity/fauna
    :represents ["Animalkind" "Animal Husbandry"]
    :relation ["Farm animals are given a lot of antibiotics"
               "Superbugs can contaminate animals and kill them"
               "Animals can contaminate each others with superbugs"
               "Animals become resistant to antibiotics by eating plants and drinking water"]}

   {:key :entity/homo-sapiens
    :represents ["Humankind, past and present" "Society"]
    :relation ["Antibiotics becoming less effective in healthcare"
               "Super bugs"
               "Overuse, underuse and misuse of antibiotics"
               "Different global approaches to AMR"
               "Needs good sanitary conditions, clean drinking water and proper sewage systems to prevent diseases spreading"
               "Lack of public knowledge about antibiotics"]}])

;;; MACHINERY

(defn game []
  (let [screen @(rf/subscribe [::sub/screen])]
    [:main.game
     [#:screen

      {:intro
       (fn []
         [:<>
          [c/text {:title "Antimicrobial Resistance" :texts intro-text}]
          [c/timeline years]
          (for [entity entites]
            ^{:key (:key entity)} [c/entity entity {:clickable? true}])])

       :write-effect
       (fn []
         [:<>
          [c/current-entity entites]
          [c/projection]
          [c/policy {:tearable? true}]
          [c/write-effect]])

       :review-effect
       (fn []
         [:<>
          [c/review-effect]])

       ;; NOTE deprecated
       :select-projection
       (fn []
         [:<>
          [c/current-entity entites]
          [c/banner "Select a new projection"]
          [c/select-projection]])

       :write-policy
       (fn []
         [:<>
          [c/current-entity entites]
          [c/projection {:tearable? true}]
          [c/write-policy]])

       :end 
       (fn []
         [:<>
          [c/text {:title "You win!"}]])} ;; TODO make a nicer end screen
      screen]]))

