(ns amr.server.routes
  (:require [amr.server.db :as db]
            [reitit.coercion.malli :as malli]
            [amr.utils :as utils]))

;;; MALLI

(defn entity? [x]
  (#{:entity/flora :entity/fauna :entity/homo-sapiens :entit/aqua} x))

(defn UUID? [s]
  (uuid? (utils/->UUID s)))

(defn domain? [k]
  (#{:effect :policy :projection} k))

;;; ROUTES 

(def api
  [["/random/{domain}"
    {:name :api/random
     :doc "Returns a random entity from that `domain`."
     :coercion malli/coercion
     :parameters {:path [:map [:domain domain?]]}
     :get (fn [{{:keys [domain]} :path-params}]
            {:status 200
             :body (db/random domain)})}]

   ["/in/{domain}/{id}"
    {:name :api.in.domain/id
     :doc "Returns the entity from `domain` that corresponds to that `id`."
     :coersion malli/coercion
     :parameters {:path [:map
                         [:id UUID?]
                         [:domain domain?]]}

     :get (fn [{{:keys [domain id]} :path-params}]
            {:status 200
             :body (db/id->e (utils/->UUID id) (keyword domain))})}]

   ["/policy"
    ["/for-entity"
     {:name :api.policy/for-entity
      :doc "Returns a random policy for `projection` _not_ written by the `entity`."
      :coercion malli/coercion
      :parameters {:query [:map
                           [:entity entity?]
                           [:projection uuid?]]}
      :get (fn [{{:keys [entity projection]} :query-params}]
             (db/policy-for-entity entity projection))}]

    ;; ["/effect"]
    ]])
