(ns client
  (:require [clj-http.client :as client])
  (:require [cheshire.core :as json]))

(defn get-id-by-username [player]
  (let [response (client/get (str "http://localhost:8080/get-user-id/" player))]
        (:body response)))

(defn send-score-to-server [player score]
  (let [response (client/post (str "http://localhost:8080/set-score/" player)
                              {:body (json/generate-string {:score score})
                               :headers {"Content-Type" "application/json"}})]
     (println "Status: " (:status response))
     (println "Body: " (:body response))))

(defn get-rating-table []
  (let [response (client/get "http://localhost:8080/rating-table"
                              {:headers {"Content-Type" "application/json"}})]
    (json/parse-string (:body response) true)))

(def sample-pl [{:player "player1", :score 229} {:player "player38", :score 227}])
