(ns clj-gatling-demo-server.core
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [org.httpkit.server :refer [run-server]])
  (:import [java.util.concurrent TimeUnit ScheduledThreadPoolExecutor]))

(def ongoing-requests (atom 0))

(defn- pong []
  (let [ongoing-reqs (swap! ongoing-requests inc)
        start (System/currentTimeMillis)]
    (Thread/sleep (+ 20 (rand-int 80)))
    (swap! ongoing-requests dec)
    "pong"))

(defroutes app-routes
  (GET "/ping" [] (pong)))

(defn print-ongoing-requests []
  (let [requests @ongoing-requests]
    (when (> requests 0)
      (println "Ongoing requests:" requests))))

(defn run [threads-str]
  (let [threads (if threads-str
                  (read-string threads-str)
                  1000)
        executor (ScheduledThreadPoolExecutor. 1)
        stop-server-fn (run-server (handler/api app-routes) {:port 8080 :join? false :thread threads})]
    (.scheduleAtFixedRate executor print-ongoing-requests 0 50 TimeUnit/MILLISECONDS)
    (println "Server started at port 8080 with" threads "threads.")
    (fn []
      (stop-server-fn)
      (.shutdownNow executor))))

(defn -main [& args]
  (run (-> args first)))
