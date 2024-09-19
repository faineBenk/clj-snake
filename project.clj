(defproject snake "0.1.0-SNAPSHOT"
  :description "snake with parentheses"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/tools.logging "1.2.3"]
                 [clj-http "3.12.3"]
                 [cheshire "5.13.0"]]
  :resource-paths ["lib"]
  :main core
  ;; :main ^:skip-aot core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
