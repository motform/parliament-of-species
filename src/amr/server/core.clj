(ns amr.server.core
  (:gen-class)
  (:require [amr.config :refer [config]]
            [amr.server.routes :as routes]
            [muuntaja.core :as m]
            [reitit.coercion.malli :as malli]
            [reitit.dev.pretty :as pretty]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as coersion]
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

    [""
     ["/api" routes/api]]

    {:exception pretty/exception
     :coercion malli/coercion
     :validate spec/validate
     ;; :reitit.middleware/transform dev/print-request-diffs
     :data {:muuntaja m/instance
            :middleware [[cors/wrap-cors
                          :access-control-allow-origin [#"http://localhost:8022"]
                          :access-control-allow-methods [:get :put :post :delete]]
                         parameters/parameters-middleware
                         muuntaja/format-negotiate-middleware
                         muuntaja/format-response-middleware
                         coersion/coerce-response-middleware
                         exception/exception-middleware
                         coersion/coerce-exceptions-middleware
                         muuntaja/format-request-middleware
                         muuntaja/format-middleware
                         coersion/coerce-request-middleware]}})

   (ring/routes
    (ring/create-resource-handler {:path "/"})
    (ring/redirect-trailing-slash-handler)
    (ring/create-default-handler))))

(defn -main []
  (let [port (or (get-in config [:server :port]) 3000)]
    (jetty/run-jetty #'server {:port port :join? false})
    (println "Server running at" port)))

(comment

  (server {:request-method :get
           :headers {"Accept" "application/transit+json"}
           :uri "/api/random"
           :query-params {"domain" "projection"}})

  (server {:request-method :get
           :headers {"Accept" "application/transit+json"}
           :uri "/api/archive"})

  (server {:request-method :get
           :headers {"Accept" "application/transit+json"}
           :uri "/api/in/projection/a975be9f-6ab6-4df1-8036-57a5be9ecb13"})

  (server {:request-method :get
           :headers {"Accept" "application/transit+json"}
           :uri "/api/stack"
           :query-params {"entity" "aqua"}})

  ;; NOTE actually posts to the db
  #_(server {:request-method :post
             :headers {"Accept" "application/transit+json"}
             :uri "/api/submit/effect"
             :body-params {:id #uuid "dc1a3596-7f60-4567-95da-fa8443178f0c",
                           :policy #uuid "b390fcee-b847-4c18-bb06-558e8157bdec",
                           :session #uuid "df94c6b6-978e-4f98-8aea-7dcaff284692",
                           :impact :impact/positive, :text "", :tag []}})

  )

