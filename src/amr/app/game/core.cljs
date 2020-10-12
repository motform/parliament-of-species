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
;;; MAIN
;;; ----------------------------------------------------------------------------

(def cards
  {:card/intro
   {:carnality ::one
    :view c/text
    :data {:title "The World in World World"
           :text  lorem-ipsum}}

   :card/select-entity
   {:carnality ::many
    :tip {:text "Select your entity"}
    :cards {:view c/entity
            :events {:remove-cards [:card/intro :card/select-entity]
                     :add-cards [:card/projection :card/policy :card/reflection]}
            :data [{:key :entity/aqua         :text lorem-ipsum}
                   {:key :entity/flora        :text lorem-ipsum}
                   {:key :entity/fauna        :text lorem-ipsum}
                   {:key :entity/homo-sapiens :text lorem-ipsum}]}}

   :card/projection
   {:carnality ::one
    :view c/projection
    :data sample-projection}

   :card/policy
   {:carnality ::one
    :view c/policy
    :data sample-policy}

   :card/reflection
   {:carnality ::one
    :view c/reflection
    :events {:remove-cards [:card/policy :card/reflection]
             :add-cards [:card/effect :card/write-policy]}}

   :card/effect
   {:carnality ::one
    :view c/effect} 

   :card/write-policy
   {:carnality ::one
    :events {:remove-cards [:card/projection :card/effect :card/write-policy]
             :add-cards [:card/final]}
    :view c/write-policy}

   :card/final
   {:carnality ::one
    :view c/text
    :data {:title "You win"
           :text lorem-ipsum}}})

(defn- render-tip [{:keys [text route]}]
  [c/banner text route])

(defn- render-card [{:keys [view data events]}]
  [view data events])

(defn- render-cards [{:keys [view data events]}]
  (for [card data]
    ^{:key card} [view card events]))

(defn- render [id {:keys [carnality cards tip] :as data}]
  [:<>
   (when tip [render-tip tip])
   (case carnality
     ::one  [render-card data]
     ::many (render-cards cards) ;; WARN triggers a warning
     [:p.error "Card " id " has an invalid carnality " carnality])])

(defn game []
  (let [state @(rf/subscribe [::sub/state])]
    [:main.game
     (for [[k data] (select-keys cards (:cards state))]
       ^{:key k} [render k data])]))
