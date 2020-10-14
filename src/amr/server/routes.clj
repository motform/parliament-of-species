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
  [["/random"
    {:name :api/random
     :doc "Returns a random entity from that `domain`."
     :parameters {:path [:map [:domain domain?]]}
     :get (fn [{{domain "domain"} :query-params}]
            {:status 200
             :body (db/random domain)})}]

   ["/in/{domain}/{id}"
    {:name :api.in.domain/id
     :doc "Returns the entity from `domain` that corresponds to that `id`."
     :parameters {:path [:map
                         [:id UUID?]
                         [:domain domain?]]}
     :get (fn [{{:keys [domain id]} :path-params}]
            {:status 200
             :body (db/id->e (utils/->UUID id) (keyword domain))})}]

   ;; TODO test
   ["/policy"
    ["/for-entity"
     {:name :api.policy/for-entity
      :doc "Returns a random policy for `projection` _not_ written by the `entity`."
      :parameters {:query [:map
                           [:entity entity?]
                           [:projection uuid?]]}
      :get (fn [{{entity "entity" projection "projection"} :query-params}]
             (db/policy-for-entity (utils/->entity entity) (utils/->UUID projection)))}]]

   ["/stack"
    {:name :api/stack-for
     :doc "Returns the initial stack of cards for the `entity`."
     :parameters {:query [:map [:entity entity?]]}
     :get (fn [{{entity "entity"} :query-params}]
            (db/stack-for-entity (utils/->entity entity)))}]

   ;; ["/effect"]
   ])
