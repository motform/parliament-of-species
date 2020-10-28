(ns amr.server.db
  (:require [amr.config :refer [config]]
            [amr.server.data :as data]
            [clojure.edn :as edn]
            [datomic.client.api :as d]
            [amr.util :as util]
            [amr.server.db :as db]))

;; TODO narrow the pulls
;;; SETUP

(def schema (-> "resources/edn/schema.edn" slurp edn/read-string))
(def client (d/client (get-in config [:datomic :cfg])))
(def conn (d/connect client {:db-name "amr"}))

;;; HELPERS

(defn- assoc-entity [[policy entity]]
  (assoc policy :policy/entity entity))

(defn seed-db! []
  ;; parse all the csvs -> 2dvec
  ;; filter for derived
  ;; map transmit-singularly
  )

;;; QUERIES

;; TODO rewrite to use pure pull API
(defn id->e
  "Return the entity with `id` in domain `k`."
  [id k]
  (let [domain (keyword (name k) "id")]
    (d/q {:query '[:find (pull ?e [*])
                   :where [?e ?ns ?id]
                   :in $ ?id ?ns]
          :args [(d/db conn) id domain]})))

(defn all
  "Return all es in `ns.`"
  [ns]
  (map first (d/q {:query '[:find (pull ?e [*])
                            :in $ ?ns
                            :where [?e ?ns]]
                   :args [(d/db conn) ns]})))

(defn random
  "Return random entity from domain `k`."
  [k]
  (let [ns (keyword (name k) "id")]
    (-> {:query '[:find (pull ?e [*])
                  :where [?e ?ns _]
                  :in $ ?ns]
         :args [(d/db conn) ns]}
        d/qseq
        rand-nth
        first)))

(defn policy-for-entity
  "Returns a random policy for `projection` _not_ written by the `entity`"
  [entity projection]
  (-> {:query '[:find (pull ?policy [*]) ?by-entity-ident
                :in $ ?projection-id ?entity
                :where
                [?policy :policy/session ?session]
                (not [?session :session/entity ?entity])
                [?session :session/entity ?by-entity]
                [?by-entity :db/ident ?by-entity-ident]
                [?policy :policy/projection ?projection]
                [?projection :projection/id ?projection-id]]
       :args [(d/db conn) projection entity]}
      d/qseq
      rand-nth
      assoc-entity))

(defn stack-for-entity
  "Returns the initial stack of cards for the `entity`."
  [entity]
  (let [{:projection/keys [id] :as projection} (random :projection)
        policy (policy-for-entity entity id)]
    {:projection projection
     :policy policy}))

(defn impact
  "Returns a the total impact of a `policy`."
  [policy]
  (let [base-effects (zipmap [:entity/aqua :entity/flora :entity/fauna :entity/homo-sapiens]
                             (repeat #:impact{:positive 0, :negative 0}))]
    (->> {:query '[:find ?entity-ident ?impact-ident
                   :in $ ?policy-id
                   :with ?effect
                   :where
                   [?policy :policy/id ?policy-id]
                   [?effect :effect/policy ?policy]

                   [?effect :effect/impact ?impact]
                   [?impact :db/ident ?impact-ident]

                   [?effect :effect/session ?session]
                   [?session :session/entity ?entity]
                   [?entity :db/ident ?entity-ident]]
          :args [(d/db conn) policy]}
         d/q
         frequencies
         (reduce-kv
          (fn [m [e i] n]
            (assoc-in m [e i] n))
          base-effects))))

(defn projection-tree
  "Pulls the entire tree for `projection`."
  [projection]
  (d/pull (d/db conn)
          [{[:effect/session] "..."}
           :effect/text :effect/id :effect/impact
           {[:effect/_policy :as :policy/effects] "..."}

           [:session/entity]
           {[:policy/session] "..."}

           :policy/id :policy/name :policy/text
           {[:policy/_projection :as :projection/policies] "..."}

           :projection/id :projection/text :projection/name]

          [:projection/id projection]))

(defn archive
  "Returns the entire archive as a vec of projection-trees,
  for use in the front-end app-db."
  []
  (->> (d/q {:query '[:find ?id :where [_ :projection/id ?id]]
             :args [(d/db conn)]})
       (mapv (comp projection-tree first))
       (mapv #(update-in % [:projection/policies] (partial util/index-by :policy/id)))
       (util/index-by :projection/id)))

;;; TRANSACTIONS 

(defn submit-effect
  "Adds an `effect` to the `db`."
  [effect]
  (let [tx (-> effect
               (update :effect/session util/->lookup-ref :session/id)
               (update :effect/policy util/->lookup-ref :policy/id))]
    (d/transact conn {:tx-data [tx]})))

(defn submit-session
  "Adds a `session` to the `db`."
  [session]
  (d/transact conn {:tx-data [session]}))

(defn submit-policy
  "Adds a `policy` to the `db`."
  [policy]
  (let [tx (-> policy
               (util/remove-vals nil?)
               (util/?update :policy/derived util/->lookup-ref :policy/id)
               (update :policy/session util/->lookup-ref :session/id)
               (update :policy/projection util/->lookup-ref :projection/id))]  
    (d/transact conn {:tx-data [tx]})))

(comment
  ;;; DEV LOCAL

  (defn transact-singularly [data]
    (doall (map #(d/transact conn {:tx-data [%]}) data)))

  (do
    (d/transact conn {:tx-data schema})
    (d/transact conn {:tx-data (data/parse-csv "resources/csv/projections.csv" :projection)})
    (d/transact conn {:tx-data (data/parse-csv "resources/csv/sessions.csv" :session)})

    ;; First transact non-derived polices, then the derived ones
    (transact-singularly (remove data/derived? (data/parse-csv "resources/csv/policies.csv" :policy)))
    (transact-singularly (filter data/derived? (data/parse-csv "resources/csv/policies.csv" :policy)))
    (transact-singularly (data/parse-csv "resources/csv/effects.csv" :effect))
    )

  ;;; Testing in-memory with peer library
  (require '[datomic.api :as dm])

  (def muri "datomic:mem://amr.server/repl")

  (do (dm/delete-database muri)
      (dm/create-database muri)
      (def mconn (dm/connect muri)))
  
  (defn transact-singularly-m [data]
    (doall (map #(dm/transact mconn [%]) data)))

  (do (dm/transact mconn schema)
      (dm/transact mconn (data/parse-csv "resources/csv/projections.csv" :projection))
      (dm/transact mconn (data/parse-csv "resources/csv/sessions.csv" :session))

      ;; First transact non-derived polices, then the derived ones
      (transact-singularly-m (remove data/derived? (data/parse-csv "resources/csv/policies.csv" :policy)))
      (transact-singularly-m (filter data/derived? (data/parse-csv "resources/csv/policies.csv" :policy)))
      (transact-singularly-m (data/parse-csv "resources/csv/effects.csv" :effect)))

  ;;; QUERIES 

  (def fx-q {:query '[:find (pull ?effect [*])
                      :in $ ?policy
                      :where
                      [?p :policy/id ?policy]
                      [?effect :effect/policy ?p]]
             :args [(d/db conn) #uuid "189d2c8f-71f8-41bd-bbb1-d3980eecdd96"]})

  (-> fx-q d/q)

  ;; Quantify the impact of a single effect
  (def impact-q {:query '[:find ?the-entity ?impact-value
                          :in $ ?id
                          :with ?effect
                          :where
                          [?policy :policy/id ?id]
                          [?effect :effect/policy ?policy]

                          [?effect :effect/impact ?impact]
                          [?impact :db/ident ?impact-value]

                          [?effect :effect/session ?session]
                          [?session :session/entity ?entity]
                          [?entity :db/ident ?the-entity]]
                 :args [(d/db conn) #uuid "189d2c8f-71f8-41bd-bbb1-d3980eecdd96"]})

  (->> impact-q
       d/q
       (map (fn [[entity impact]]
              [entity (if (= :impact/negative impact) [-1 1] [1 -1])]))
       (reduce
        (fn [m [entity [e b]]]
          (-> m (update entity + e) (update :entity/resistance + b)))
        #:entity{:fauna 0 :flora 0 :resistance 0 :aqua 0 :homo-sapiens 0}))

  ;; Get the combined impacts of the effects
  (->> impact-q
       d/q
       frequencies
       (reduce-kv
        (fn [m [e i] n]
          (assoc-in m [e i] n))
        {}))

  ;; to calculate the total effect of a policy we need:
  ;;  - have a policy id (in this case we use 17592186045437, as it has the most effects)
  ;;  - find all the effects that point to that policy
  ;;  - for all entities, make a tuple of [pos neg]
  ;;  - the bacterial effect is implicit in this data, I guess?

  (d/q {:query '[:find ?id ?n
                 :where
                 [?e :projection/name ?n]
                 [?e :projection/id ?id]
                 ]
        :args [(d/db conn)]})

  (-> {:query '[:find ?e
                :where [?e :projection/id]]
       :args [(d/db conn)]}
      d/q
      ffirst)


  (-> {:query '[:find ?policy
                :in $ ?projection
                :where [?policy :policy/projection ?projection]]
       :args [(d/db conn) 17592186045425]}
      d/q)

  17592186045443

  (-> {:query '[:find ?policy
                :in $ ?projection
                :where [?policy :effect/projection ?projection]]
       :args [(d/db conn) 17592186045443]}
      
      d/q)

  (require '[clojure.inspector :as i])

  (i/inspect-tree (archive))

  (->> (dm/q '[:find ?id
               :where [_ :projection/id ?id]]
             (dm/db mconn))
       (map first)
       (mapv #(dm/pull (dm/db mconn)
                       [{[:effect/session] "..."}
                        :effect/text
                        :effect/id
                        :effect/impact
                        {[:effect/_policy :as :effects] "..."}


                        [:session/entity]
                        {[:policy/session] "..."}

                        :policy/id
                        :policy/name
                        :policy/text
                        {[:policy/_projection :as :policies] "..."}

                        :projection/id
                        :projection/text
                        :projection/name]

                       [:projection/id %]))
       i/inspect-tree)
  )
