(defproject librarian-ng "0.1.0-SNAPSHOT"
  :description "Books library management system with opds support"
  :url "http://github.com/uvNikita/librarian-ng"
  :license {:name "MIT License"
            :url "http://www.opensource.org/licenses/mit-license.php"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring/ring-core "1.5.0"]
                 [compojure "1.5.1"]
                 [clojurewerkz/elastisch "2.2.1"]
                 [me.raynes/fs "1.4.6"]]
  :main ^:skip-aot librarian-ng.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler librarian-ng.core/app})
