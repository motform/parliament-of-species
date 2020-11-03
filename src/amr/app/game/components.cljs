(ns amr.app.game.components
  (:require [amr.app.archive.core :as archive]
            [amr.app.events :as app]
            [amr.app.game.events :as event]
            [amr.app.game.subs :as sub]
            [amr.app.header :refer [balance]]
            [amr.util :as util]
            [clojure.string :as str]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [reitit.frontend.easy :refer [href]]))

(def years
  [["2020" 1 "Bacteria are becoming more and more immune to antibiotics. Despite numerous studies and initiatives to find alternatives, researchers do not seem very hopeful."]
   ["2024" 2 "Antibiotic-resistant bacteria is a globally widespread problem. This is due to the overuse of antibiotics in food production and for treating non-fatal diseases. This has directly affected all four Entities: Homo sapiens, Fauna, Flora and Aqua."]
   ["2026" 1 "The Entities tried to adapt to the situation, where every-day matters such as food and health have drastically changed. However, their individual efforts proved ineffective, and it became clear that working in a united manner was the only option to survive."]
   ["2030" 2 "The Parliament of Species is established to tackle antibiotic-resistance and its impact at a global level. The Entities of Aqua, Fauna, Flora and Homo Sapiens have to create policies that positively impact their wellbeing by managing the threat of antibiotic-resistant bacteria."]])

(defn timeline []
  [:<>
   
   [:section.timeline.col.centered
    [:h1 "The History of the Future"]
    [:p.text "In participate, you need to learn about this world. Specifically, how antibiotic-resistant bacteria got became a global issue and why the Parliament was formed."]
    (for [[year bg text] years]
      ^{:key year}
      [:div.year.col.centered
       {:id (str "year-" year)
        :class (str "year-" bg)
        :style {:background-image (str "url(/svg/glyphs/timeline/part-" bg ".svg)")}}
       [:h3 year]
       [:p.text text]])
    [:div.col.centered>a.timeline-play ;; TODO rework into a app-wide btn
     {:href (href :route.policymaking/how-to)
      :on-click #(do (rf/dispatch [::app/scroll-to-top]))}
     "Continue"]]])

(def entites
  [{:key :entity/aqua
    :represents "Aqua represents all oceans, rivers and lakes."
    :relation "Aqua is impacted by antibiotic resistance as antibiotics are given to aquatic animals. The high production of antibiotics also results in pollution of rivers, oceans and sewage."}
   {:key :entity/fauna
    :represents "Fauna represents all animal kind and husbandry."
    :relation "Fauna is impacted by antibiotic resistance as antibiotics are given to farm animals in the food industry. Animals are also infected with resistant bacteria that are not treatable with antibiotics."}
   {:key :entity/flora
    :represents "Flora represents all plant kind and agriculture."
    :relation "Flora is impacted by antibiotic resistance as antibiotics are used in crops. As farm animals are given antibiotics, their waste contaminates fields and crops."}
   {:key :entity/homo-sapiens
    :represents "Homo sapiens represents all humankind: past, present and societies."
    :relation "Homo sapiens is impacted as antibiotics become less effective in healthcare due to the overuse, underuse and misuse of medicines. The lack of public knowledge and unified global approach to antibiotic-resistant bacteria can affect Homo sapiens both locally and globally."}])

(defn no-session []
  [:section.resume.col.centered {:style {:min-height "100rem"}}
   [:p "You don't have an active participation, do you want to start one?"]
   [:a.landing-play
    {:href (href :route.policymaking/intro)
     :on-click #(do (rf/dispatch [::app/scroll-to-top])
                    (rf/dispatch [::event/reset-session]))}
    "Participate!"]])

(defn resume-session [{{:session/keys [entity id]} :session}]
  [:section.resume.col.centered
   [:p {:style {:text-align "center"}}
    "You are already in a Parliamentary Session as " (util/prn-entity entity) "." [:br]
    "Do you want to resume your participation or start a new session?"]
   [:div.row.spaced.wide.text
    [:div.landing-play
     {:on-click #(rf/dispatch [::event/resume id])
      :style {:margin-top "10rem"}}
     "Resume Participation"]
    [:div.landing-play
     {:on-click #(do (rf/dispatch [::event/reset-session])
                     (rf/dispatch [::app/navigate :route.policymaking/how-to]))
      :style {:margin-top "10rem"}}
     "Start a new session"]]])

(defn intro-timeline []
  (let [current-session @(rf/subscribe [::sub/current-session])]
    [:<>  
     (when current-session
       [resume-session current-session])
     [timeline]]))

(defn how-to []
  (let [current-session @(rf/subscribe [::sub/current-session])]
    [:<> 
     (when current-session
       [resume-session current-session])
     [:section.how-to.col.centered.wide
      [:h1.text "How to Participate in the Parliament of Species"]
      [:p.text {:style {:margin-bottom "15rem"}}
       "Antibiotic-resistant bacteria is a reality that affects the four entities in different ways. All four are all interconnected, and each of their actions and well-being affect one another. The entities rely on each other to thrive, and are working on co-creating global policies in order to maintain a balance in their well being."]
      [:img {:src "img/bar.gif"}]
      [:div.bg-hl.col.centered>div.steps.wide.narrow
       [:div.step
        [:h2 "1. Choose an Entity"] 
        [:p "To join the Parliament of Species, select one of the four Entities. You will imagine a future through this entity’s perspective."]
        [:div.row.spaced {:style {:margin-bottom "8rem"}}
         [:img {:src "/svg/entity/flora.svg"}]
         [:img {:src "/svg/entity/fauna.svg"}]]
        [:div.row.spaced
         [:img {:src "/svg/entity/aqua.svg"}]
         [:img {:src "/svg/entity/homo-sapiens.svg"}]]]
       [:div.step
        [:h2 "2. Policy Assessment"]
        [:p "First, you will see a Projection about the future related to antibiotic-resistant bacteria."]
        [:p "Then, you will see a Policy Proposal from a different Entity. At this stage you will assess this proposal on behalf of your Entity."]
        [:p "Your Policy Assessment will be added to the Archive and will have an effect on the Balance of Entities, which is shown on the bar at the top of the screen."]]
       [:div.step
        [:h2 "3. Policy Proposal"]
        [:p "Based on the same Projection, you will create a new Policy Proposal."]
        [:p "When creating this Proposal consider how it will impact both your entity as well as the others."]]
       [:div.step
        [:h2 "4. Archiving"]
        [:p "Once submitting your proposal it will be added to the Archive and given the other Entities to assess."]]]
      [:div.resume.col.centered>a.landing-play
       {:style {:margin-top "-15rem"}
        :href (href :route.policymaking/select-entity)}
       "Choose an Entity"]]]))

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
      [:<>
       (when (:effect session) [resume-session session])
       [:section.col.centered.wide
        [:h1 "Choose your Entity"]
        [:p {:style {:margin-bottom "20rem"}}
         "To join the Parliament of Species, select one of the four entities. You will imagine a future through this entity’s perspective."]
        [:div.col.centered.wide
         (for [{:keys [key] :as e} entites]
           ^{:key key}
           [entity e state])]]])))

(defn intro-effect []
  [:section.intro.col.centered
   [:h1 "Policy Assessment"]
   [:div.text 
    [:p "How do you think this Policy Proposal would affect your Entity?"]]])

(defn intro-policy []
  [:section.intro 
   [:h1 "Policy Proposal"]
   [:div.text 
    [:p "Based on the same projection, you will create a new Policy Proposal. When creating this Proposal consider how it will impact both your Entity as well as the others."]]])

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

(defn policy []
  (let [{:policy/keys [id name text session]} @(rf/subscribe [::sub/current-policy])
        entity (:session/entity session)
        entity-name (when entity (clojure.core/name entity))] 
    [:section.policy.bg.col.centered.wide
     {:style {:background-image (str "url(/svg/bg/policy/" entity-name ".svg)")
              :background-color (str "var(--" entity-name "-bg)")}}
     [:div.label "Policy Proposal by " (util/prn-entity entity)]
     [:div.narrow.padded-high.grid
      [:img {:src (str "/svg/entity/" entity-name ".svg")}]
      [:div {:style {:grid-column "span 2"}}
       [:h2 name]
       [:p.text text]]]]))

(defn review-effect []
  (let [impact @(rf/subscribe [::sub/effect-impact])
        effects @(rf/subscribe [::sub/effects])]
    [:section.review-effect.col.centered.wide
     [:div.label "Policy assessments"]
     [:div.col.centered.narrow.padded
      [:h2 "The Entities have assessed the Policy Proposal as follows:"]
      [:div.padded.grid-auto.wide 
       (for [e effects]
         ^{:key e}
         [archive/effect e])]
      [:div.row.spaced.wide.padded
       (for [[entity {:impact/keys [positive negative]}] impact]
         ^{:key entity}
         [:h5 {:class (str (name entity) "-fg")} ;; TODO add 0/0 for yet to react entites
          "▲ " (if-let [p positive] p 0) " "
          "▼ " (if-let [n negative] n 0)])]]]))

(defn thank-you []
  [:section.intro.col.centered
   [:h1 "Thank you for your contribution!"]
   [:div.text
    [:p "Your policy will be assessed by other members of the Parliament of Species. Check out the Archive to see how it is doing."]
    [:p "If you want to keep working towards a balanced future, feel free to participate again."]]
   [:a.entry {:href (href :route.policymaking/select-entity)}
    "Participate again"]])

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
              (and impact (valid-len? text 50)))]
      
      (fn [] 
        [:section.write.col.centered.wide
         [:div.label "Policy assessment"]
         [:div.padded.col.centered.narrow.wide
          (if already-written?
            [:div.padded.col.centered.wide
             [:p "You have already assessed to this policy proposal."]
             [:a.entry
              {:href (href :route.policymaking/write-policy)}
              "Proceed to the next step"]]
            [:<> 
             [:h2 "Assess this Policy Proposal"]
             [:p.text "How do you think this Policy Proposal would impact " (util/prn-entity (:session/entity session)) "? Write your assessments in the area below."]
             [:form.col.wide.text
              [:div.row.spaced
               [btn-impact "Positively" :impact/positive]
               [btn-impact "Negatively" :impact/negative]] 
              [:textarea {:rows 10
                          :value (:effect/text @state)
                          :on-change #(swap! state assoc :effect/text (.. % -target -value))}]
              [:label {:style {:color (if (valid-len? (:effect/text @state) 50) "var(--ok)" "var(--hl)")}}
               "The contents of the Assessment. You have written " (count (:effect/text @state)) " of a minimum 50 characters."]
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
                   (valid-len? text 100)))] 

      (fn [] 
        [:section.write.col.centered.wide
         [:div.label "Policy Proposal"]
         [:div.col.centered.text.wide.padded-high
          [:h2 "Write a new Policy Proposal"]
          [:p.text "Make a new Policy Proposal in response to the Projection that would positively impact both your entity and the others."]
          #_[:p.text "You can improve the previous Policy Proposal by deriving it or create a brand new one."]
          [:form.col.wide.text
           [:textarea.name {:rows 1
                            :value (:policy/name @state)
                            :on-change #(swap! state assoc :policy/name (.. % -target -value))}]
           [:label {:style {:color (if (valid-len? (:policy/name @state) 10) "var(--ok)" "var(--hl)")}}
            "The name of the Policy Proposal, at least 10 characters long."]
           [:textarea {:rows 10
                       :value (:policy/text @state)
                       :on-change #(swap! state assoc :policy/text (.. % -target -value))}]
           [:label {:style {:color (if (valid-len? (:policy/text @state) 100) "var(--ok)" "var(--hl)")}}
            "The contents of the Policy Proposal. You have written " (count (:policy/text @state)) " of a minimum 100 characters."]
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
