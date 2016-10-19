(defproject turtlenews "0.1"
  :description "News, less is more"
  :url "https://github.com/ananthmv/turtlenews"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 ;; Core platform
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/java.jdbc "0.6.1"]
                 [org.clojure/data.csv "0.1.3"]
                 [org.clojure/core.cache "0.6.5"]
                 [org.clojure/core.memoize "0.5.8"
                  :exclusions [org.clojure/core.cache]]

                 [environ "1.0.0"]

                 ;; Web Server
                 [http-kit "2.2.0"]
                 [org.immutant/web "2.1.5"]

                 ;; API
                 [liberator "0.12.2"]
                 [compojure "1.1.6"]

                 ;; Logging
                 [org.clojure/tools.logging "0.3.1"]
                 [org.slf4j/slf4j-log4j12 "1.7.21"]
                 [log4j/log4j "1.2.17"
                  :exclusions [javax.mail/mail
                               javax.jms/jms
                               com.sun.jmdk/jmxtools
                               com.sun.jmx/jmxri]]]

  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :min-lein-version "2.3.3"
  :main turtlenews.main
  :aot :all
  :warn-on-reflection true
  :test-paths ["test"]
  :aliases {"cc" ["do" "clean," "uberjar"]} ;commit check
  :jvm-opts ["-Dclojure.compiler.disable-locals-clearing=true"
             "-Djava.net.preferIPv4Stack=true"
             "-Dsun.net.inetaddr.ttl=0"
             "-XX:+TieredCompilation"
             "-Xms256m"
             "-Xmx256m"
             "-server"]
  :jar-name "turtlenews.jar"
  :uberjar-name "turtlenews-standalone.jar")
