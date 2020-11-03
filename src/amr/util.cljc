(ns amr.util
  (:require [clojure.string :as str]
            #?(:cljs [cognitect.transit :as transit])
            #?(:cljs [re-frame.core :as rf]))
  #?(:clj (:import [java.util UUID])))

(defn ?assoc
  "Associates the `k` into the `m` if the `v` is truthy, otherwise returns `m`.
  NOTE: this version of ?assoc only does a single kv pair."
  [m k v]
  (if v (assoc m k v) m))

(defn assoc-in-when
  "Associates the `k` into the `m` if `pred` is truthy, otherwise returns `m`."
  [m ks v pred]
  (if pred (assoc-in m ks v) m))

(defn ?update
  ([m k f] 
   (if (m k) (update m k f) m))
  ([m k f x]
   (if (m k) (update m k f x) m)))

(defn update-vals [m vals f]
  (reduce #(update-in % [%2] f) m vals))

(defn remove-vals
  "Remove kvs from `m` where (`pred` v) is truthy."
  [m pred]
  (into {} (remove #(pred (val %)) m)))

(defn unqualify
  "Unqualifies all the keys in `m`."
  [m]
  (into {} (map (fn [[k v]] [(-> k name keyword) v]) m)))

(defn index-by
  "Index a seq of `m` by common `k`, for reducing into `ms`."
  ([k] 
   (fn [ms m]
     (assoc ms (k m) m)))
  ([k ms]
   (reduce (index-by k) {} ms)))

(defn map-vals
  "Maps a `f` to all the v in `m`"
  [m f]
  (into {} (for [[k v] m] [k (f v)])))

(defn remove-keys [pred m]
  (apply dissoc m (filter pred (keys m))))

(defn prn-entity [entity]
  (when entity
    (-> entity name (str/replace #"-" " ") str/capitalize)))

(defn ->entity [entity]
  (keyword "entity" entity))

(defn ->lookup-ref [v ref-k]
  [ref-k v])

(defn ->uuid
  ([s] #?(:clj (when s (UUID/fromString s))
          :cljs (when s (uuid s))))
  ([m ks] #?(:clj (update-vals m ks ->uuid))))

(defn calculate-impact [effects]
  (merge (zipmap [:entity/aqua :entity/flora :entity/fauna :entity/homo-sapiens]
                 (repeat #:impact{:positive 0, :negative 0}))
         (-> (group-by #(get-in % [:effect/session :session/entity]) effects)
             (map-vals #(map :effect/impact %))
             (map-vals frequencies))))

#?(:clj
   (defn uuid []
     (UUID/randomUUID)))

#?(:cljs
   (defn ->url [route]
     (if goog.DEBUG
       (str "http://localhost:3000" route)
       route)))

#?(:cljs
   (defn do-events
     "Sets off the events in `event-vs`.
      SOURCE: https://github.com/Day8/re-frame/issues/51"
     [event-vs]
     (doall (map rf/dispatch (remove nil? event-vs)))
     nil))
