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

(defn ->entity [entity]
  (keyword "entity" entity))

(defn ->lookup-ref [v ref-k]
  [ref-k v])

;; TODO add cljs version
(defn ->uuid
  ([s] #?(:clj (when s (UUID/fromString s))
          :cljs (when s (uuid s))))
  ([m ks] #?(:clj (update-vals m ks ->uuid))))

#?(:clj
   (defn uuid []
     (UUID/randomUUID)))

#?(:cljs
   (defn ->uri [route]
     (let [host (.. js/window -location -host)]
       (str "http://" host "/" route))))

#?(:cljs
   (defn do-events
     "Sets off the events in `event-vs`.
      SOURCE: https://github.com/Day8/re-frame/issues/51"
     [event-vs]
     (doall (map rf/dispatch (remove nil? event-vs)))
     nil))
