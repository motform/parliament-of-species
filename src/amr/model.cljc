(ns amr.model
  (:require [amr.util :as util]
            [clojure.string :as str]
            [malli.core :as m]
            [malli.error :as me]
            [malli.generator :as mg]
            [malli.provider :as mp]
            [malli.util :as mu]))

;; TODO introduce references

;;; PREDS

(defn UUID? [s]
  ;; TODO move into malli format
  (uuid? (util/->uuid s)))

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
   [:session/date inst?]
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
   [:effect/id uuid?] 
   [:effect/policy uuid?] 
   [:effect/session uuid?] 
   [:effect/impact impact] 
   [:effect/text string?] 
   #_[:effect/tag [:vector qualified-keyword?]]])

(def schema
  [:schema
   {:registery
    {::session session}}
   "model"])

(comment
  
  (m/validate effect (first (mg/sample effect)))
  (mg/sample effect {:size 1})

  )
