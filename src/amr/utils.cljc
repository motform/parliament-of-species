(ns amr.utils
  (:require [clojure.string :as str]
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

(defn remove-nil
  "Returns `m` with all nil keys removed."
  [m]
  (into {} (remove #(nil? (val %)) m)))

(defn remove-empty [m]
  (into {} (remove #(str/blank? (val %)) m)))

(defn ->entity [entity]
  (keyword "entity" entity))

;; TODO add cljs version
#?(:clj
   (defn ->UUID
     ([s] 
      (when s (UUID/fromString s)))
     ([m ks]
      (update-vals m ks ->UUID))))

#?(:cljs
   (defn ->uri [route]
     (let [host (.. js/window -location -host)]
       (str "http://" host "/" route))))

#?(:cljs
   (defn emit-n
     "Emits a multi-event
      SOURCE: https://github.com/Day8/re-frame/issues/51"
     [event-vs]
     (doall (map rf/dispatch (remove nil? event-vs)))
     nil))
