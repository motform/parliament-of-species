(ns amr.utils
  (:require #?(:cljs [re-frame.core :as rf])))

(defn ?assoc
  "Associates the `k` into the `m` if the `v` is truthy, otherwise returns `m`.
  NOTE: this version of ?assoc only does a single kv pair."
  [m k v]
  (if v (assoc m k v) m))


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
