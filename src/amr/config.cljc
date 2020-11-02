(ns amr.config
  "Expects the config to be in the (malli) format defined in `config-spec`."
  (:require #?(:clj [clojure.edn :as edn])))

(def config-spec
  [:map
   [:server
    [:port int?]] ; The port we listen to
   [:datomic
    [:map
     [:uri string?]
     [:port int?]
     [:cfg ; a datomic-client-style cfg
      [:map
       [:sever-type :peer-server]
       [:access-key string?]
       [:secret string?]
       [:endpoint string?]
       [:validate-hostnames boolean?]]]]]])

#?(:clj
   (def config
     (-> "resources/edn/config.edn" slurp edn/read-string)))
