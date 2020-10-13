(ns amr.server.routes
  (:require [amr.server.db :as db])
  (:import [java.util UUID]))

(def game
  [["/projection"
    ["/id/{id}"
     {:name :api.projection/id
      :get (fn [{{:keys [id]} :path-params}]
             {:status 200
              :body (db/id->e (UUID/fromString id) :projection)})}]
    ["/random"
     {:name :api.projection/random
      :get (fn [_]
             {:status 200
              :body (db/random :projection)})}]]

   ["/policy"
    ["/id/{id}"
     {:name :api.policy/id
      :get (fn [{{:keys [id]} :path-params}]
             {:status 200
              :body (db/id->e (UUID/fromString id) :policy)})}
     ["/projection/{projection-id}"
      {:name :api.policy/projection
       :doc "Returns a random policy connected to the projection"
       :get (fn [{{:keys [projection-id]} :path-params}]
              {:status 200
               :body (db/projection->policy (UUID/fromString projection-id) :policy)})}]]

    ["/effect"
     ["/id/{id}"
      {:name :api.policy/id
       :get (fn [{{:keys [id]} :path-params}]
              {:status 200
               :body (db/id->e (UUID/fromString id) :effect)})}]]

    ]])
