(ns amr.app.game.core
  (:require [amr.app.game.components :as c]
            [amr.app.game.events :as event]
            [amr.app.game.subs :as sub]
            [re-frame.core :as rf]))

;;; SMAPLE DATA ;;;

(def lorem-ipsum
  "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")

;;; CARDS

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
                     [::event/add-cards [:card/projection :card/policy :card/reflection]]]
            :data [{:key :entity/aqua         :text lorem-ipsum}
                   {:key :entity/flora        :text lorem-ipsum}
                   {:key :entity/fauna        :text lorem-ipsum}
                   {:key :entity/homo-sapiens :text lorem-ipsum}]}}

   :card/projection
   {:cardinality ::one
    :view c/projection}

   :card/policy
   {:cardinality ::one
    :view c/policy}

   :card/reflection
   {:cardinality ::one
    :view c/reflection
    :events {::event/remove-cards [:card/policy :card/reflection]
             ::event/add-cards    [:card/effect :card/write-policy]}}

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

;;; MACHINERY

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
  (let [active-cards @(rf/subscribe [::sub/cards])]
    [:main.game
     (for [[k data] (select-keys cards active-cards)]
       ^{:key k} [render k data])]))
