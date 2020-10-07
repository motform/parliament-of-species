(ns amr.server.core
  (:require [amr.server.routes :as routes]
            [reitit.dev.pretty :as pretty]
            [reitit.ring :as ring]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.spec :as rs]
            [ring.adapter.jetty :as jetty]
            [muuntaja.core :as m]))

(def server
  (ring/ring-handler
   (ring/router

    [["/api" routes/ping]]

    {:exception pretty/exception
     :validate rs/validate
     :data {:muuntaja m/instance
            :middleware [parameters/parameters-middleware
                         muuntaja/format-negotiate-middleware
                         muuntaja/format-response-middleware
                         exception/exception-middleware
                         muuntaja/format-request-middleware]}})

   (ring/create-default-handler)))

(defn start! []
  (let [port 3000]
    (jetty/run-jetty #'server {:port port :join? false})
    (println "Server running at " port)))

(comment

  (server {:request-method :get
           :uri "/api/ping" :query-params {:e "pong"}})
  )


