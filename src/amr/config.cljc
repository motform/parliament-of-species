(ns amr.config
  (:require #?(:clj [clojure.edn :as edn])))

#?(:clj (def config
          (-> "resources/edn/config.edn" slurp edn/read-string)))
