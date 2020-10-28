(ns amr.server.data
  (:require [amr.util :as util]
            [clojure.data.csv :as csv]
            [clojure.string :as str]))

;;; HELPERS 

(defn csv->m [domain [header & rest]]
  (map zipmap
       (->> header (map (partial keyword (name domain))) repeat)
       rest))

(defn ->tag [s]
  (keyword "tag" (str/replace s " " "-")))

(defn derived? [policy]
  (:policy/derived policy))

;;; TRANSFORMATION

(defmulti xf-row :domain)

(defmethod xf-row :projection [{:keys [row]}]
  (-> row
      (dissoc :projection/year)
      (util/->uuid [:projection/id])
      (update :projection/source #(str/split % #";"))))

(defmethod xf-row :policy [{:keys [row]}]
  (-> row
      (util/->uuid [:policy/id :policy/projection :policy/session])
      (update :policy/projection util/->lookup-ref :projection/id)
      (update :policy/session util/->lookup-ref :session/id)
      (util/?update :policy/derived util/->uuid)
      (util/?update :policy/derived util/->lookup-ref :policy/id)))

(defmethod xf-row :session [{:keys [row]}]
  (-> row
      (util/->uuid [:session/id :session/author])
      (update :session/entity util/->entity)))

(defmethod xf-row :effect [{:keys [row]}]
  (-> row
      (util/->uuid [:effect/id :effect/policy :effect/session])
      (update :effect/policy  util/->lookup-ref :policy/id)
      (update :effect/session util/->lookup-ref :session/id)
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
       (map #(util/remove-vals % str/blank?))
       (map #(xf-row {:domain domain :row %}))))

(comment
  (first (parse-csv "resources/csv/policies.csv" :policy))
  (parse-csv "resources/csv/projections.csv" :projection)
  (parse-csv "resources/csv/sessions.csv" :session)
  (first (parse-csv "resources/csv/effects.csv" :effect))
  )
