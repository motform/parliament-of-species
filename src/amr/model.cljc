(ns amr.model
  (:require [amr.utils :as utils]
            [clojure.string :as str]
            [malli.core :as m]
            [malli.error :as me]
            [malli.generator :as mg]
            [malli.provider :as mp]
            [malli.util :as mu]))

;; TODO introduce references

;;; PREDS

(defn- UUID? [s]
  ;; TODO move into malli format
  (uuid? (utils/->UUID s)))

(def entity
  [:enum :entity/flora :entity/fauna :entity/homo-sapiens :entit/aqua])

(def domain
  [:enum :effect :policy :projection])

(def impact
  [:enum :impact/positive :impact/negative])

;;; DOMAIN ENTITIES 

(def session
  [:map
   [:session/id UUID?]
   [:session/entity entity]
   [:session/start-date inst?]
   [:session/end-date inst?]
   [:session/author UUID?]])

(def projection
  [:map
   [:projection/id UUID?]
   [:projection/source [:vector string?]]
   [:projection/name string?]
   [:projection/text string?]])

(def policy
  [:map 
   [:policy/id UUID?] 
   [:policy/projection UUID?] 
   [:policy/derived UUID?] 
   [:policy/session UUID?] 
   [:policy/name string?] 
   [:policy/text string?]])

(def effect
  [:map
   [:effect/id UUID?] 
   [:effect/policy UUID?] 
   [:effect/session UUID?] 
   [:effect/impact UUID?] 
   [:effect/text string?] 
   [:effect/tag [:vector qualified-keyword?]]])

(def schema
  [:schema
   {:registery
    {::session session}}
   "model"])

(comment
  
  
  (m/validate effect (first (mg/sample effect)))

  )
