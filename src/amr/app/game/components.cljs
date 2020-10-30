(ns amr.app.game.components
  (:require [amr.app.events :as app]
            [amr.app.game.events :as event]
            [amr.app.game.subs :as sub]
            [amr.app.header :refer [balance]]
            [amr.util :as util]
            [clojure.string :as str]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [reitit.frontend.easy :refer [href]]))

;;; INTRO

(defn intro []
  [:section.intro 
   [:h1 "Introduction"]
   [:div.text 
    [:p "Antimicrobial resistance (AMR) is caused by bacteria changing over time and no longer responding to medicines. As a result, the medicines become ineffective and infections persist in the body, increasing the risk of disease spread, severe illness and death."]
    [:p "Antimicrobials — including antibiotics — are medicines used to prevent and treat infections in homo sapiens, animals and plants. Bacteria that develop antimicrobial resistance are referred to as Superbugs."]]])

(def years
  [["2020" "Bacteria are becoming more and more immune to antibiotics. Despite there being numerous studies and initiatives to find alternatives, researchers do not seem very hopeful."]
   ["2024" "AMR is a globally widespread problem. This is due to the overuse of antibiotics in food production and for treating non-fatal diseases. This has not only a direct impact on Homo Sapiens, but also on other entities, namely Aqua, Fauna and Flora."]
   ["2026" "Societies around the globe have adapted to the situation, where every-day matters such as food and health have drastically changed. However, another important aspects of society that had to readapt to this situation are policies and economics."]
   ["2030" "The Parliament of Species is established to tackle AMR and its repercussions at a global level: the entities of Aqua, Fauna, Flora and Homo Sapiens have to create policies that positively impact their wellbeing by managing the threat of AMR."]])

(defn timeline [opts]
  [:section.timeline.col.centered
   [:h1 "the History of the Future"]
   (for [[year text] years]
     ^{:key year}
     [:div.year.col.centered {:id (str "year-" year)}
      [:h3 year]
      [:p.text text]])
   (when-not (:static? opts)
     [:div.bg-hl.col.centered>a.landing-play ;; TODO rework into a app-wide btn
      {:href (href :route.policymaking/select-entity)
       :on-click #(do (rf/dispatch [::app/scroll-to-top]))}
      "Select entity"])])

(def entites
  [{:key :entity/aqua
    :represents "Aqua represents all oceans, rivers and lakes."
    :relation "Aqua can be affected by the antibiotics given to aquatic animals and antibiotic pollution in the rivers and sewage. "}

   {:key :entity/flora
    :represents "Flora represents all plant kind and agriculture."
    :relation "Flora can be affected by the antibiotics used in crops and by the contamination from animal waste that contains antibiotics."}

   {:key :entity/fauna
    :represents "Fauna represents all animal kind and husbandry."
    :relation "Fauna can be affected by the antibiotics given to farm animals in the food industry and by the superbugs that infect animals."}

   {:key :entity/homo-sapiens
    :represents "Homo-sapiens represents all humankind past and present and society."
    :relation "Homo-sapiens can be affected as antibiotics become less effective in healthcare due to the overuse, underuse and misuse of medicines. The lack of public knowledge and unified global approach to AMR can affect Homo-sapiens both locally and globally."}])

;;; SESSION MANAGEMENT

(defn no-session []
  [:section.resume.col.centered {:style {:min-height "100rem"}}
   [:p "You don't have an active session, do you want to start one?"]
   [:a.landing-play
    {:href (href :route.policymaking/intro)
     :on-click #(do (rf/dispatch [::app/scroll-to-top])
                    (rf/dispatch [::event/reset-session]))}
    "Write a new policy!"]])

(defn resume-session [{{:session/keys [id]} :session}]
  [:section.resume.col.centered
   [:p "Do you want to resume your active session?"]
   [:div.landing-play 
    {:on-click #(rf/dispatch [::event/resume id])
     :style {:margin-top "10rem"}}
    "Resume session"]])

(defn empty-library []
  [:section.col.centered.empty-library
   [:h1 "Create global policies with other entities"]
   [:div {:style {:display "grid" :grid-template-columns "1fr 1fr" :grid-gap "20rem"}}
    [:div 
     [:p "Your role as a representative of The Parliament of Species is to maintain the balance between the four entities while managing the antimicrobial resistance at a low level. In order to maintain the balance, representatives collaborate to create new global policies in response to the repercussions of antimicrobial resistance that positively impact all of the entities."]
     [balance {:class "small" :labels? false}]]
    [:div 
     [:p "The wellbeing of the entities effects the level of antimicrobial resistance. When a policy positively affects an entity the resistance decreases, however when if it negatively affects an entity the resistance increases. If the level of resistance increases past the global threshold, The Parliament of Species is reset."]
     [balance #:entity{:aqua 2 :flora 2 :fauna 2 :homo-sapiens 2 :resistance 22} {:class "small" :labels? false}]]]
   [:a.entry
    {:href (href :route.policymaking/intro)
     :on-click #(do (rf/dispatch [::app/scroll-to-top])
                    (rf/dispatch [::event/reset-session]))}
    "Policymake!"]])

(defn session [{:keys [session written-policy written-effect] :as s}]
  [:div.session-libray.padded.col
   [:p.error s]
   [:p (str (:session/date session))]
   [:p (:session/entity session)]])

(defn session-library []
  (let [sessions @(rf/subscribe [::sub/sessions])
        current-session @(rf/subscribe [::sub/current-session])]
    [:<>  
     (when current-session
       [resume-session current-session])
     [empty-library]
     #_(when-not (empty? sessions)
         [:div.col.centered.sessions
          [:h1 "Previous sessions"]
          [:div.grid
           (for [[id s] sessions] ;; TODO sort-by date
             ^{:key id} 
             [session s])]
          [:div.entry
           {:on-click #(rf/dispatch [::event/delete-sessions])}
           "Delete sessions"]])]))

(defn entity [{:keys [key represents relation]} state]
  (let [session #:session{:id (random-uuid) :date (js/Date.) :entity key}]
    [:div.entity.bg.col.centered.padded.clickable
     {:style (when (#{key :no-hover} @state)
               {:background-image (str "url(/svg/bg/policy/" (name key) ".svg)")
                :background-color (str "var(--" (name key) "-bg)")})
      :on-mouse-over #(reset! state key)
      :on-mouse-out  #(reset! state :no-hover)
      :on-click #(do (rf/dispatch [::event/create-session session])
                     (rf/dispatch [::event/submit-session session])
                     (rf/dispatch [::event/new-pair session key])
                     (rf/dispatch [::app/scroll-to-top])
                     (rf/dispatch [::event/save-route :route.policymaking/write-effect])
                     (rf/dispatch [::app/navigate :route.policymaking/write-effect]))}
     [:div.narrow.row {:style {:margin "0 auto"}}
      [:img {:src (str "/svg/entity/" (name key) ".svg")}]
      [:div.col.narrow {:style {:justify-content "center"}}
       [:p.text represents]
       [:p.text relation]]]]))

(defn select-entity [session]
  (let [state (r/atom :no-hover)]
    (fn []
      (if (:effect session)
        [resume-session session]
        [:section.col.centered.wide
         [:h1 "Select your entity"]
         [:p {:style {:margin-bottom "20rem"}}
          "Choose an entity to represent in the Parliament of Species."]
         [:div.col.centered.wide
          (for [{:keys [key] :as e} entites]
            ^{:key key}
            [entity e state])]]))))

;; EFFECT

(defn intro-effect []
  [:section.intro 
   [:h1 "React to a Policy"]
   [:div.text 
    [:p "How do you think this policy would affect your entity?"]]])

(defn intro-policy []
  [:section.intro 
   [:h1 "Write a new Policy"]
   [:div.text 
    [:p "Make a new policy in response to the projection that would positively impact both your entity and the others. You can improve the previous policy or create a new one."]]])

(defn projection []
  (let [{:projection/keys [id text name]} @(rf/subscribe [::sub/current-projection])] 
    [:section.projection.col.centered
     [:div.label "Projection — " name]
     [:div.padded-high.grid.narrow
      [:img {:src (str "/svg/glyphs/projection/" id ".svg")}]
      [:div {:style {:grid-column "span 2"}}
       (for [t (str/split-lines text)]
         ^{:key (first t)}
         [:p.projection-text t])]]]))

(defn policy [{:keys [tearable?]}]
  (let [{:policy/keys [id name text session]} @(rf/subscribe [::sub/current-policy])
        entity (:session/entity session)
        entity-name (when entity (clojure.core/name entity))] 
    [:section.policy
     [:div.bg.col.centered
      {:style {:background-image (str "url(/svg/bg/policy/" entity-name ".svg)")
               :background-color (str "var(--" entity-name "-bg)")}}
      [:div.label "Policy by " (util/prn-entity entity)]
      [:div.narrow.padded-high
       [:h2 name]
       [:p.text text]]]]))

(defn review-effect []
  (let [impact @(rf/subscribe [::sub/effect-impact])]
    [:section.review-effect.col.centered.wide
     [:div.label "Effect of policy"]
     [:div.col.centered.narrow.padded
      [:h2 "This policy has impact the entities in these ways."]
      [:div.row.impacts.padded-high.wide
       (for [[entity {:impact/keys [positive negative]}] impact]
         ^{:key entity}
         [:h5 {:class (str (name entity) "-fg")} ;; TODO add 0/0 for yet to react entites
          "▲ " (if-let [p positive] p 0) " "
          "▼ " (if-let [n negative] n 0)])]]]))

(defn thank-you []
  [:section.intro.col.centered
   [:h1 "Thank you for your contribution!"]
   [:div.text>p "Your policy will be reviewed by other members of the parliament of species."]
   [:a.entry {:href (href :route.policymaking/select-entity)}
    "Make another policy"]])

;;; FORMS

(defn write-effect []
  (let [state (r/atom #:effect{:text "" :impact nil :hover? false})
        policy  @(rf/subscribe [::sub/from-session :policy])
        session @(rf/subscribe [::sub/from-session :session])
        already-written? @(rf/subscribe [::sub/from-session :effect])
        effect  #:effect{:id (random-uuid) :policy policy :session (:session/id session)}]

    (letfn [(btn-impact [label k] ^{:key k}
              (let [active? (when (= k (:effect/impact @state)) "button-active")]
                [:input.btn.btn-impact
                 {:type "button"
                  :value label
                  :on-click #(swap! state assoc :effect/impact k)
                  :id (when active? "button-active")}]))

            (valid-len? [s len]
              (> (count s) len))

            (valid-input? [{:effect/keys [text impact]}]
              (and impact (valid-len? text 130)))]
      
      (fn [] 
        [:section.write.col.centered 
         [:div.label "Effect submission form"]
         [:div.padded.col.centered.narrow.wide
          (if already-written?
            [:div.padded.col.centered.wide
             [:p "You have already reacted to this policy."]
             [:a.entry
              {:href (href :route.policymaking/write-policy)}
              "Proceed to the next step"]]
            [:<> 
             [:h2 "How does this impact " (util/prn-entity (:session/entity session)) "?"]
             [:p "How do you think this policy would affect your entity? Write the possible effects below."]
             [:form.col.wide
              [:div.impact.row
               [btn-impact "Positively" :impact/positive]
               [btn-impact "Negatively" :impact/negative]] 
              [:textarea {:rows 10
                          :value (:effect/text @state)
                          :on-change #(swap! state assoc :effect/text (.. % -target -value))}]
              [:label {:style {:color (if (valid-len? (:effect/text @state) 130) "var(--ok)" "var(--hl)")}}
               "Your reaction to the policy, at least 130 characters long."]
              [:input#submit
               {:type "button"
                :value "submit"
                :on-mouse-over (fn [] (swap! state assoc :effect/hover? true))
                :on-mouse-out  (fn [] (swap! state assoc :effect/hover? false))
                :disabled (not (valid-input? @state))
                :on-click #(do (rf/dispatch [::event/submit-effect (-> @state (dissoc :effect/hover?) (merge effect))])
                               (rf/dispatch [::app/navigate :route.policymaking/write-policy])
                               (rf/dispatch [::event/save-route :route.policymaking/write-policy]))}]]])]]))))

;; TODO get the derived key from somewhere idk
(defn write-policy [{:keys [derived]}]
  (let [state (r/atom #:policy{:text "" :name "" :hover? false})
        {session-id :session/id} @(rf/subscribe [::sub/from-session :session])
        projection-id @(rf/subscribe [::sub/from-session :projection])
        policy #:policy{:projection projection-id :session session-id :id (random-uuid) :derived derived}]

    (letfn [(valid-len? [s len]
              (> (count s) len))

            (valid-input? [{:policy/keys [text name]}]
              (and (valid-len? name 10)
                   (valid-len? text 130)))] 

      (fn [] 
        [:section.write.col.centered
         [:div.label "Policy submission form"]
         [:div.col.centered.text.wide.padded-high
          [:h2 "Write a new policy"]
          [:p.text "Make a new policy in response to the projection that would positively impact both your entity and the others. You can improve the previous policy or create a new one."]
          [:form.col.wide
           [:textarea.name {:rows 1
                            :value (:policy/name @state)
                            :on-change #(swap! state assoc :policy/name (.. % -target -value))}]
           [:label {:style {:color (if (valid-len? (:policy/name @state) 10) "var(--ok)" "var(--hl)")}}
            "The name of the policy, at least 10 characters long."]
           [:textarea {:rows 10
                       :value (:policy/text @state)
                       :on-change #(swap! state assoc :policy/text (.. % -target -value))}]
           [:label {:style {:color (if (valid-len? (:policy/text @state) 130) "var(--ok)" "var(--hl)")}}
            "The contents of the policy, at least 130 characters long."]
           [:input#submit
            {:type "button"
             :value "Submit"
             :on-mouse-over (fn [] (swap! state assoc :policy/hover? true))
             :on-mouse-out  (fn [] (swap! state assoc :policy/hover? false))
             :disabled (not (valid-input? @state))
             :on-click #(do (rf/dispatch [::event/submit-policy (-> @state (dissoc :policy/hover?) (merge policy))])
                            (rf/dispatch [::app/navigate :route.policymaking/end])
                            (rf/dispatch [::app/scroll-to-top])
                            (rf/dispatch [::event/save-route nil])
                            (rf/dispatch [::event/reset-session]))}]]]]))))
