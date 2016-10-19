(ns turtlenews.main
  ^{ :author "Anand Muthu"
     :doc "turtlenews API routes"}
 (:require [liberator.core        :refer [resource defresource]]
           [compojure.core        :refer [defroutes GET ANY]]
           [compojure.route       :as route]
           [immutant.web          :as immutant]
           [turtlenews.hackernews :as hn]
           [turtlenews.reddit     :as reddit]
           [clojure.core.memoize  :as memo])
 (:gen-class))

(def ^:const ttl_one_hour (* 1000 60 60))

(def get-best-hn (memo/ttl hn/get-best :ttl/threshold ttl_one_hour))
(def get-top-reddit (memo/ttl reddit/get-best :ttl/threshold ttl_one_hour))

;; (get-best-hn 5)
;; (get-top-reddit "r/programming" 5)

(defn get-top-articles!
  []
  (let [hn (get-best-hn 5)
        rd-prog (get-top-reddit "r/programming" 5)]
       (concat hn rd-prog)))

(defn wrap-exception
  [handler]
  (fn [request]
    (try
      (handler request)
      (catch Exception e
        {:status 500
         :error "Error Occurred"}))))

(defresource get-top-articles
  [req]
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :handle-ok (get-top-articles!))

(defroutes event-routes
  (GET "/" req (get-top-articles req)))

(defroutes app-routes
  event-routes
  (ANY "*" [] (route/not-found {:status "notok"})))

(defn handler
  []
  (-> app-routes
      (wrap-exception)))

(defn -main
  [& args]
  (let [available-threads (.availableProcessors (Runtime/getRuntime))]
   (immutant/run (handler) {:port 9000 :io-threads available-threads :worker-threads available-threads :path "/"})))
