(ns amr.server.db
  (:require [amr.config :refer [config]]
            [amr.server.data :as data]
            [clojure.edn :as edn]
            [datomic.client.api :as d]
            [amr.utils :as utils]
            [amr.server.db :as db]))

;; TODO figure out how to include the entity-enums

;;; SETUP ;; NOTE maybe move this to a component library?

(def schema (-> "resources/edn/schema.edn" slurp edn/read-string))
(def client (d/client (get-in config [:datomic :cfg])))
(def conn (d/connect client {:db-name "amr"}))
(def db (d/db conn))

;;; HELPERS

(defn- q-rand [arg-map]
  (first (rand-nth (d/qseq arg-map))))

;;; QUERIES

(defn id->e
  "Return the entity with `id` in domain `k`."
  [id k]
  (let [domain (keyword (name k) "id")]
    (d/q {:query '[:find (pull ?e [*])
                   :where [?e ?ns ?id]
                   :in $ ?id ?ns]
          :args [db id domain]})))

(defn random
  "Return random entity from domain `k`."
  [k]
  (let [ns (keyword (name k) "id")]
    (q-rand {:query '[:find (pull ?e [*])
                      :where [?e ?ns _]
                      :in $ ?ns]
             :args [db ns]})))

(defn policy-for-entity
  "Returns a random policy for `projection` _not_ written by the `entity`"
  [projection entity]
  (q-rand {:query '[:find (pull ?policy [*])
                    :where
                    [?policy :policy/session ?session]
                    (not [?session :session/entity ?entity])
                    [?e :policy/projection ?projection]
                    [?projection :projection/id ?projection-id]
                    :in $ ?projection-id ?entity]
           :args [db projection (utils/->entity entity)]}))


(comment
  ;;; DEV LOCAL
  (d/transact conn {:tx-data schema})
  (d/transact conn {:tx-data (data/parse-csv "resources/csv/projections.csv" :projection)})

  ;;; Testing in-memory with peer library
  (require '[datomic.api :as dm])

  (def muri "datomic:mem://amr.server/repl")

  (do (dm/delete-database muri)
      (dm/create-database muri)
      (def mconn (dm/connect muri)))
  
  (defn transact-singularly [data]
    (doall (map #(dm/transact mconn [%]) data)))

  (do (dm/transact mconn schema)
      (dm/transact mconn (data/parse-csv "resources/csv/projections.csv" :projection))
      (dm/transact mconn (data/parse-csv "resources/csv/sessions.csv" :session))

      ;; First transact non-derived polices, then the derived ones
      (transact-singularly (remove data/derived? (data/parse-csv "resources/csv/policies.csv" :policy)))
      (transact-singularly (filter data/derived? (data/parse-csv "resources/csv/policies.csv" :policy)))
      (transact-singularly (data/parse-csv "resources/csv/effects.csv" :effect))
      (def mdb (dm/db mconn)))

  ;;; QUERIES 

  ;; count the impact
  (dm/q '[:find ?i
          :with ?impact
          :where
          [?impact :effect/impact ?i]]
        mdb)

  (dm/q '[:find ?n
          :where
          [?pol :policy/id  #uuid "189d2c8f-71f8-41bd-bbb1-d3980eecdd96"]
          [?pol :policy/session ?pro]
          [?pro :session/entity ?n]]
        mdb)

  (dm/q '[:find (pull ?e [*])
          :where
          [?e :policy/session ?s]
          (not [?s :session/entity ?entity])
          [?e :policy/projection ?p]
          [?p :projection/id ?projection-id]
          :in $ ?projection-id ?entity]
        mdb #uuid "a975be9f-6ab6-4df1-8036-57a5be9ecb13" (utils/->entity "fauna"))

  ;; GETTING THE IDENT OF AN ENUM
  (dm/q '[:find ?type-ident
          :where
          [?e :policy/session ?s]
          [?s :session/entity ?entity]
          [?entity :db/ident ?type-ident]
          [?e :policy/projection ?p]
          [?p :projection/id ?projection-id]
          :in $ ?projection-id]
        mdb #uuid "a975be9f-6ab6-4df1-8036-57a5be9ecb13")

  )
