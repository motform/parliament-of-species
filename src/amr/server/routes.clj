(ns amr.server.routes
  (:require [amr.model :as model]
            [amr.server.db :as db]
            [amr.util :as util]))

;;; ROUTES 

(def api
  [["/random"
    {:name :api/random
     :doc "Returns a random entity from that `domain`."
     :parameters {:path [:map [:domain model/domain]]}
     :get (fn [{{domain "domain"} :query-params}]
            {:status 200
             :body (db/random domain)})}]

   ["/balance"
    {:name :api/balance
     :doc "Returns the balance of the world"
     :get (fn [_]
            {:status 200
             :body (db/balance)})}]

   ["/in/{domain}/{id}"
    {:name :api.in.domain/id
     :doc "Returns the entity from `domain` that corresponds to that `id`."
     :parameters {:path [:map
                         [:id model/UUID?]
                         [:domain model/domain]]}
     :get (fn [{{:keys [domain id]} :path-params}]
            {:status 200
             :body (db/id->e (util/->uuid id) (keyword domain))})}]

   ["/all"
    ["/{ns}"
     {:name :api.all/ns
      :doc "Return all es in `ns.`"
      :parameters {:path [:map [:ns [:enum "projection" "policy" "effect"]]]}
      :get (fn [{{:keys [ns]} :path-params}]
             {:status 200
              :body (db/all (keyword ns "id"))})}]]

   ["/archive"
    {:name :api/archive
     :doc "Returns the archive as a vec of policy trees"
     :get (fn [_]
            {:status 200
             :body (db/archive)})}]

   ["/policy"
    ["/for-entity"
     {:name :api.policy/for-entity
      :doc "Returns a random policy for `projection` _not_ written by the `entity`."
      :parameters {:query [:map
                           [:entity model/entity]
                           [:projection model/UUID?]]}
      :get (fn [{{entity "entity" projection "projection"} :query-params}]
             {:status 200
              :body (db/policy-for-entity (util/->entity entity) (util/->uuid projection))})}]

    ["/impact"
     {:name :api.policy/impact
      :doc "Returns a the total impact of a `policy`."
      :parameters {:query [:map [:policy model/UUID?]]}
      :get (fn [{{policy "policy"} :query-params}]
             {:status 200
              :body (db/impact (util/->uuid policy))})}]]

   ["/stack"
    {:name :api/stack-for
     :doc "Returns the initial stack of cards for the `entity`."
     :parameters {:query [:map [:entity model/entity]]}
     :get (fn [{{entity "entity"} :query-params}]
            {:status 200
             :body (db/stack-for-entity (util/->entity entity))})}]

   ["/submit"
    ["/session"
     {:name :api.submit/session
      :doc "Submits the session into the `db`."
      :parameters {:body model/session}
      :post (fn [{body :body-params}]
              (db/submit-session body)
              {:status 201
               :body {:resource (str "/api/session/id/" (:session/id body))}})}]

    ["/effect"
     {:name :api.submit/effect
      :doc "Submits the effect into the `db`."
      :parameters {:body model/effect}
      :post (fn [{body :body-params}]
              (db/submit-effect body)
              {:status 200
               :body {:resource (str "/effect/id/" (str (:effect/id body)))}})}]

    ["/policy"
     {:name :api.submit/policy
      :doc "Submits the policy into the `db`."
      :parameters {:body model/policy}
      :post (fn [{body :body-params}]
              (db/submit-policy body) 
              {:status 201
               :body {:resource (str "/api/policy/id/" (:policy/id body))}})}]]])
