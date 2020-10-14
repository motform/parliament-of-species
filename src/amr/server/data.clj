(ns amr.server.data
  (:require [amr.utils :as utils]
            [clojure.data.csv :as csv]
            [clojure.string :as str]
            [clojure.edn :as edn])
  (:import [java.util UUID]))

(defn csv->m [domain [header & rest]]
  (map zipmap
       (->> header (map (partial keyword (name domain))) repeat)
       rest))

(defn ->UUID [s]
  (when s (UUID/fromString s)))

(defn nillify [csv]
  (map (partial map #(if (= % "nil") (edn/read-string %) %)) csv))

(defmulti xf-row :domain)

(defmethod xf-row :projection [{:keys [row]}]
  (-> row
      (update :projection/id       ->UUID)
      (update :projection/source #(str/split % #";"))))

(defmethod xf-row :policy [{:keys [row]}]
  (-> row
      (update        :policy/id         ->UUID)
      (update        :policy/projection ->UUID)
      (update        :policy/session    ->UUID)
      (utils/?update :policy/derived    ->UUID)))

(defmethod xf-row :session [{:keys [row]}]
  (-> row
      (update :session/id     ->UUID)
      (update :session/entity utils/->entity)))

(defn parse-csv
  "`Domain` is a datomic entity ns."
  [csv domain]
  (->> csv
       slurp
       csv/read-csv
       nillify
       (csv->m domain)
       (map utils/remove-nil)
       (map #(xf-row {:domain domain :row %}))))

(comment
  (parse-csv "resources/csv/policies.csv"    :policy)
  (parse-csv "resources/csv/projections.csv" :projection)
  (parse-csv "resources/csv/sessions.csv"    :session)
  )
