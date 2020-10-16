(ns amr.server.data
  (:require [amr.util :as util]
            [clojure.data.csv :as csv]
            [clojure.string :as str]))

;;; HELPERS 

(defn csv->m [domain [header & rest]]
  (map zipmap
       (->> header (map (partial keyword (name domain))) repeat)
       rest))

(defn ->lookup-ref [v ref-k]
  [ref-k v])

(defn ->tag [s]
  (keyword "tag" (str/replace s " " "-")))

(defn derived? [policy]
  (:policy/derived policy))

;;; TRANSFORMATION

(defmulti xf-row :domain)

(defmethod xf-row :projection [{:keys [row]}]
  (-> row
      (util/->uuid [:projection/id])
      (update :projection/source #(str/split % #";"))))

(defmethod xf-row :policy [{:keys [row]}]
  (-> row
      (util/->uuid [:policy/id :policy/projection :policy/session])
      (update :policy/projection ->lookup-ref :projection/id)
      (update :policy/session ->lookup-ref :session/id)
      (util/?update :policy/derived util/->UUID)
      (util/?update :policy/derived ->lookup-ref :policy/id)))

(defmethod xf-row :session [{:keys [row]}]
  (-> row
      (util/->uuid [:session/id])
      (update :session/entity util/->entity)))

(defmethod xf-row :effect [{:keys [row]}]
  (-> row
      (util/->uuid [:effect/id :effect/policy :effect/session])
      (update :effect/policy  ->lookup-ref :policy/id)
      (update :effect/session ->lookup-ref :session/id)
      (update :effect/impact #(keyword "impact" %))
      (update :effect/tag #(str/split % #";"))
      (update :effect/tag #(map ->tag %))))

(defn parse-csv
  "`Domain` is a datomic entity ns."
  [csv domain]
  (->> csv
       slurp
       csv/read-csv
       (csv->m domain)
       (map util/remove-empty)
       (map #(xf-row {:domain domain :row %}))))

(comment
  (remove derived? (parse-csv "resources/csv/policies.csv" :policy))
  (parse-csv "resources/csv/projections.csv" :projection)
  (parse-csv "resources/csv/sessions.csv" :session)
  (parse-csv "resources/csv/effects.csv" :effect)
  )
