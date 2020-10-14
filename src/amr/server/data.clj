(ns amr.server.data
  (:require [amr.utils :as utils]
            [clojure.data.csv :as csv]
            [clojure.string :as str])
  (:import [java.util UUID]))

(defn csv->m [domain [header & rest]]
  (map zipmap
       (->> header (map (partial keyword (name domain))) repeat)
       rest))

(defn ->UUID
  ([s] 
   (when s (UUID/fromString s)))
  ([m ks]
   (utils/update-vals m ks ->UUID)))

(defmulti xf-row :domain)

(defmethod xf-row :projection [{:keys [row]}]
  (-> row
      (->UUID [:projection/id])
      (update :projection/source #(str/split % #";"))))

(defmethod xf-row :policy [{:keys [row]}]
  (-> row
      (->UUID [:policy/id :policy/projection :policy/session])
      (utils/?update :policy/derived    ->UUID)))

(defmethod xf-row :session [{:keys [row]}]
  (-> row
      (->UUID [:session/id])
      (update :session/entity utils/->entity)))

(defn parse-csv
  "`Domain` is a datomic entity ns."
  [csv domain]
  (->> csv
       slurp
       csv/read-csv
       (csv->m domain)
       (map utils/remove-empty)
       (map #(xf-row {:domain domain :row %}))))

(comment
  (parse-csv "resources/csv/policies.csv"    :policy)
  (parse-csv "resources/csv/projections.csv" :projection)
  (parse-csv "resources/csv/sessions.csv"    :session)
  )
