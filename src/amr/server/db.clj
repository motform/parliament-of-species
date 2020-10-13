(ns amr.server.db
  (:require [amr.config :refer [config]]
            [amr.server.data :as data]
            [clojure.edn :as edn]
            [datomic.client.api :as d]
            [datomic.api :as dm]))

;; TODO figure out how to include the entity-enums

;; Move to integrant?
(def schema (-> "resources/edn/schema.edn" slurp edn/read-string))
(def client (d/client (get-in config [:datomic :cfg])))
(def conn (d/connect client {:db-name "amr"}))
(def db (d/db conn))

;;; QUERIES

(defn id->e [id k]
  (let [ns (keyword (name k) "id")]
    (d/q {:query '[:find (pull ?e [*])
                   :where [?e ?ns ?id]
                   :in $ ?id ?ns]
          :args [db id ns]})))

(defn random [k]
  (let [ns (keyword (name k) "id")]
    (first (rand-nth (d/qseq {:query '[:find (pull ?e [*])
                                       :where [?e ?ns _]
                                       :in $ ?ns]
                              :args [db ns]})))))

(defn projection [id]
  (d/pull db '[*] (id->e id "projection")))

(defn projection->policy [])



(comment

  (d/q {:query '[:find ?n
                 :where [?e :projection/entities :entity/aqua]
                 [?e :projection/name ?n]]
        :args [db]})

  (dm/connect )
  ;; DEV LOCAL
  (d/transact conn {:tx-data schema})

  (d/transact conn {:tx-data [{:db/ident :projection/entities
                               :db/valueType :db.type/ref}]})

  (d/transact conn {:tx-data (data/parse-csv "resources/csv/projections.csv" :projection)})

  (d/delete-database client {:db-name "amr"})

  (id->e #uuid "a975be9f-6ab6-4df1-8036-57a5be9ecb13" :projection)

  ;; MEM
  (def muri "datomic:mem://amr.server/repl")

  (do (dm/delete-database muri)
      (dm/create-database muri)
      (def mconn (dm/connect muri))
      (def mdb (dm/db mconn))
      (dm/transact mconn schema))

  (dm/transact mconn (data/parse-csv "resources/csv/projections.csv" :projection))

  
  (dm/q '[:find ?n
          :where
          [?e :projection/name ?n]
          [?e :projection/entities :entity/homo-sapiens]]
        mdb)

  #{[17592186045428 #uuid "a975be9f-6ab6-4df1-8036-57a5be9ecb13"]
    [17592186045427 #uuid "31d5db74-cd3d-42d3-ad50-cf703b3f4143"]}

  (dm/pull mdb '[*] 17592186045427)

  {:db/id 17592186045427,
   :projection/id #uuid "31d5db74-cd3d-42d3-ad50-cf703b3f4143",
   :projection/source ["Kristoffer" "React"],
   :projection/name "food industry",
   :projection/text "Bacteria can spread direct or indirect from food to humans via the food chain.\nBecause countries have different regulations on the use of antibiotics in the food industry,
 more and more people have started turning to their local farmers,
 boycotting food from abroad.\n",
   :projection/entities [#:db{:id 17592186045417}
                         #:db{:id 17592186045418}
                         #:db{:id 17592186045419}
                         #:db{:id 17592186045420}]}

  )
