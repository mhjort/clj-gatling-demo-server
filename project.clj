(defproject clj-gatling-demo-server "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [http-kit "2.2.0"]
                 [ring/ring-core "1.3.1"]
                 [javax.servlet/servlet-api "2.5"]
                 [compojure "1.2.1"]]
  :main ^:skip-aot clj-gatling-demo-server.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
