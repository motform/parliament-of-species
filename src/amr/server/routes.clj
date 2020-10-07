(ns amr.server.routes)

(def ping
  [["/ping" {:name ::ping
             :get (fn [{{:keys [e]} :query-params}]
                    {:status 200 :body {:ping e}})}]])

