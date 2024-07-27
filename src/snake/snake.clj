(ns snake.snake
  (:require [snake.constants :as const])
  (:require [snake.helpers :as h])
  (:require [snake.apple :as apple])
  (:require [clojure.pprint :as p]))

(def unit-length (* 20 const/tick))

; (generate-init-snake
;            (get-in  state [:snake-posns])         ; snake with only head
;            (second (get-in state [:snake-body]))) ; amount of segments in initial snake
; returns initial snake body, headed to "right"
(defn generate-init-snake
  [sp n]
  (if (= n 1) sp
      (let [last-posn (last sp)
            new-sp (conj sp {:x (+ (get-in last-posn [:x]) 20)
                             :y (get-in last-posn [:y])})]
        (generate-init-snake new-sp (- n 1)))))

; [[:right 4] [:left 2]] -> [:right :right :right :right :left :left]
(defn expand-dir-list
  [sdir n]
  (repeat n sdir))

;; concat = foldr (\x acc -> x:acc) [4, 5, 6] [1, 2, 3]
;; foldr concat [] listOfLists
(defn expand-dirs-list
  [sd]
  (vec (reduce concat (map (fn [[dir n]] (expand-dir-list dir n)) sd))))


;; (def sd [[:right 2] [:up 1]])

;;(def sp [{:x 20 :y 40} {:x 40 :y 40} {:x 20 :y 60}])

; gets snake-dir and snake-posn of segment, updates segment velocity
; (update-velocity :left {:x 20 :y 40})
(defn update-velocity
  [sdir sposn]
  (cond
    (= sdir :left)  {:x (- (:x sposn) unit-length) :y (:y sposn)}
    (= sdir :right) {:x (+ (:x sposn) unit-length) :y (:y sposn)}
    (= sdir :up)    {:x (:x sposn) :y (- (:y sposn) unit-length)}
    (= sdir :down)  {:x (:x sposn) :y (+ (:y sposn) unit-length)}))



; snake-dir -> snake-posns
; gets snake-dirs and snake-posns, updates every segment in snake-posns
; map-direction-to-velocity (expand-dir-list sd) snake-posns
(defn map-direction-to-velocity
  [sd sp]
  (map (fn [sdir sposn] (update-velocity sdir sposn)) (expand-dirs-list sd) sp))

(defn move-snake
  [state]
    (assoc state :snake-posns (map-direction-to-velocity   (:snake-body   state)
                                                           (:snake-posns state))))

;(println (move-snake const/init-game-state))

(defn change-snake-direction
  [d]
  (cond (= d "down")  {:x 0   :y -20}
        (= d "left")  {:x -20 :y 0}
        (= d "right") {:x 20  :y 0}
        (= d "up")    {:x 0   :y 20}))

(defn add-apple-to-snake
  [sdir ac-x ac-y]
  (cond
    (= sdir :right) {:x (+ ac-x 20) :y ac-y}
    (= sdir :left) {:x (- ac-x 20) :y ac-y}
    (= sdir :up) {:x ac-x :y (+ ac-y 20)}
    (= sdir :down) {:x ac-x :y (- ac-y 20)}
    ))

;;(get-in const/init-game-state [:apple-posns :x])

(defn increase-snake-head
  [sd]
  (conj [(ffirst sd) (inc (h/sfirst sd))]
        (rest sd)))

;;(increase-snake-head (get-in const/init-game-state [:snake-body]))

(defn update-snake-after-eat
  [state]
    (-> state
        (update :devoured inc) ; +
        (assoc :apple-posns (get-in (apple/generate state) [:apple-posns])) ; +
        (update :snake-body increase-snake-head))) ; +
        ;(update add-apple-to-snake sdir ac-x ac-y)))

;;(get-in (apple/generate const/init-game-state) [:apple-posns])


;;(update-snake-after-eat const/equal-head-apple-game-state)
;(use 'snake.snake :reload)
