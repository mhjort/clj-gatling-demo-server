(ns clj-gatling-demo-server.core
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [org.httpkit.server :refer [run-server]])
  (:import [java.util.concurrent TimeUnit ScheduledThreadPoolExecutor]))

(def ongoing-requests (atom 0))

(defn- pong []
  (Thread/sleep (+ 20 (rand-int 80)))
  "pong")

(defn- article-read [id]
  (Thread/sleep (+ 20 (rand-int 30)))
  (str "Read " id))

(defn- program-start [id]
  (Thread/sleep (+ 50 (rand-int 100)))
  (str "Start " id))

(defn- wrapped-request [request]
  (let [ongoing-reqs (swap! ongoing-requests inc)
        start (System/currentTimeMillis)
        result (request)]
    (swap! ongoing-requests dec)
    result))

(defroutes app-routes
  (GET "/ping" [] (wrapped-request pong))
  (GET "/metrics/article/read/:id" [id] (wrapped-request (partial article-read id)))
  (GET "/metrics/program/start/:id" [id] (wrapped-request (partial program-start id))))

(defn print-ongoing-requests []
  (let [requests @ongoing-requests]
    (when (> requests 0)
      (println "Ongoing requests:" requests))))

(defn run [threads-str]
  (let [port (read-string (or (System/getenv "PORT") "8080"))
        threads (if threads-str
                  (read-string threads-str)
                  300)
        executor (ScheduledThreadPoolExecutor. 1)
        stop-server-fn (run-server (handler/api app-routes) {:port port :join? false :thread threads})]
    (.scheduleAtFixedRate executor print-ongoing-requests 0 50 TimeUnit/MILLISECONDS)
    (println "Server started at port" port "with" threads "threads.")
    (fn []
      (stop-server-fn)
      (.shutdownNow executor))))

(defn -main [& args]
  (run (-> args first)))
