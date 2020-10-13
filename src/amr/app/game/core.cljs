(ns amr.app.game.core
  (:require [amr.app.game.components :as c]
            [amr.app.game.events :as event]
            [amr.app.game.subs :as sub]
            [amr.utils :as utils]
            [re-frame.core :as rf]))

;;; ----------------------------------------------------------------------------
;;; SMAPLE DATA ;;;
;;; ----------------------------------------------------------------------------

(def lorem-ipsum
  "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")

(def sample-projection
  #:projection
  {:id (random-uuid)
   :author (random-uuid)
   :name "All the rice has died"
   :text lorem-ipsum
   :entities #{:entity/aqua :entity/flora}})

(def sample-projection2
  #:projection
  {:id (random-uuid)
   :author (random-uuid)
   :name "Wow still no rice"
   :text lorem-ipsum
   :entities #{:entity/aqua :entity/flora}})

(def sample-policy
  #:policy
  {:id (random-uuid)
   :author (random-uuid)
   :name "Resurrect all the rice"
   :text lorem-ipsum
   :tags #{:economic :farming}})

(def sample-reflection
  #:policy
  {:id (random-uuid)
   :author (random-uuid)
   :entity :entity/aqua
   :name "Resurrect all the rice"
   :impact :pos
   :text lorem-ipsum})

;;; ----------------------------------------------------------------------------
;;; CARDS
;;; ----------------------------------------------------------------------------

(def cards
  {:card/intro
   {:cardinality ::one
    :view c/text
    :data {:title "Introduction to AMR"
           :text  lorem-ipsum}}

   :card/select-entity
   {:cardinality ::many
    :tip {:text "Select your entity"}
    :cards {:view c/entity
            :events [[::event/remove-cards [:card/intro :card/select-entity]]
                     [::event/request-projection]
                     [::event/add-cards [:card/projection :card/policy :card/reflection]]]
            :data [{:key :entity/aqua         :text lorem-ipsum}
                   {:key :entity/flora        :text lorem-ipsum}
                   {:key :entity/fauna        :text lorem-ipsum}
                   {:key :entity/homo-sapiens :text lorem-ipsum}]}}

   :card/projection
   {:cardinality ::one
    :view c/projection
    :data sample-projection}

   :card/policy
   {:cardinality ::one
    :view c/policy
    :data sample-policy}

   :card/reflection
   {:cardinality ::one
    :view c/reflection
    :events {:remove-cards [:card/policy :card/reflection]
             :add-cards [:card/effect :card/write-policy]}}

   :card/effect
   {:cardinality ::one
    :view c/effect} 

   :card/write-policy
   {:cardinality ::one
    :events {:remove-cards [:card/projection :card/effect :card/write-policy]
             :add-cards [:card/final]}
    :view c/write-policy}

   :card/final
   {:cardinality ::one
    :view c/text
    :data {:title "You win"
           :text lorem-ipsum}}})

;;; ----------------------------------------------------------------------------
;;; FNS
;;; ----------------------------------------------------------------------------

(defn- render-tip [{:keys [text route]}]
  [c/banner text route])

(defn- render-card [{:keys [view data events]}]
  [view data events])

(defn- render-cards [{:keys [view data events]}]
  (for [card data]
    ^{:key card} [view card events]))

(defn- render [id {:keys [cardinality cards tip] :as data}]
  [:<>
   (when tip [render-tip tip])
   (case cardinality
     ::one  [render-card data]
     ::many (render-cards cards) ;; WARN triggers a warning
     [:p.error "Card " id " has an invalid cardinality " cardinality])])

(defn game []
  (let [state @(rf/subscribe [::sub/state])]
    [:main.game
     (for [[k data] (select-keys cards (:cards state))]
       ^{:key k} [render k data])]))
