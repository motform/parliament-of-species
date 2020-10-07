(ns amr.server.db
  (:require [amr.config :refer [config]]
            [clojure.edn :as edn]
            [datomic.client.api :as d]))

(def schema (-> "resources/edn/schema.edn" slurp edn/read-string))

(def client (d/client (get-in config [:datomic :cfg])))

(def conn (d/connect client {:db-name "amr"}))

