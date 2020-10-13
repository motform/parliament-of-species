(ns amr.server.core
  (:require [amr.config :refer [config]]
            [amr.server.routes :as routes]
            [muuntaja.core :as m]
            [reitit.dev.pretty :as pretty]
            [reitit.ring :as ring]
            [reitit.ring.middleware.dev :as dev]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.spec :as spec]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.cors :as cors]))

(def server
  (ring/ring-handler
   (ring/router

    [["/api"
      routes/game]]

    {:exception pretty/exception
     :validate spec/validate
     :reitit.middleware/transform dev/print-request-diffs
     :data {:muuntaja m/instance
            :middleware [[cors/wrap-cors
                          :access-control-allow-origin [#"http://localhost:8022"]
                          :access-control-allow-methods [:get :put :post :delete]]
                         parameters/parameters-middleware
                         muuntaja/format-negotiate-middleware
                         muuntaja/format-response-middleware
                         exception/exception-middleware
                         muuntaja/format-request-middleware]}})

   (ring/create-default-handler)))

(defn -main []
  (let [port (or (get-in config [:server :port]) 3000)]
    (jetty/run-jetty #'server {:port port :join? false})
    (println "Server running at" port)))

(comment

  (server {:request-method :get
           :headers {"Accept" "application/transit+json"}
           :uri "/api/projection/random"})

  (server {:request-method :get
           :headers {"Accept" "application/transit+json"}
           :uri "/api/projection/id/a975be9f-6ab6-4df1-8036-57a5be9ecb13"})

  )

