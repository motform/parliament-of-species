(ns amr.server.data
  (:require [clojure.data.csv :as csv]
            [clojure.string :as str])
  (:import [java.util UUID]))

(defn csv->m [domain [header & rest]]
  (map zipmap
       (->> header (map (partial keyword (name domain))) repeat)
       rest))

(defn m-field->vec [field]
  (let [sep (str/split field #";")]
    (if (= 1 (count sep)) field sep)))

(defn str->entity-enum [row ->domain]
  (update row (->domain  "entities") #(map (partial keyword "entity") %)))

(defn xf-row [domain row]
  (let [->domain (partial keyword (name domain))]
    (-> row
        (update (->domain "id") #(UUID/fromString %))
        (update (->domain "source") #(str/split % #";"))
        (update (->domain "entities") #(str/split % #";"))
        (update (->domain "entities") #(map (partial keyword "entity") %)))))

(defn parse-csv
  "`Domain` is a datomic entity ns."
  [csv domain]
  (->> csv
       slurp
       csv/read-csv
       (csv->m domain)
       (map (partial xf-row domain))))


(comment
  (parse-csv "resources/csv/policies.csv" :policy)
  (parse-csv "resources/csv/projections.csv" :projection)
  )
