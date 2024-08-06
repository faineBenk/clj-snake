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

; expands snake-posns according to init snake-head and init snake-body
(defn update-init-snake
  [state]
  (assoc-in state [:snake-posns]
            (generate-init-snake
             (state :snake-posns)
   (h/sfirst (state :snake-body)))))

; [[:right 4] [:left 2]] -> [:right :right :right :right :left :left]
(defn expand-dir-list
  [sdir n]
  (repeat n sdir))

(defn expand-dirs-list
  [sd]
  (vec (reduce concat (map (fn [[dir n]] (expand-dir-list dir n)) sd))))

; gets snake-dir and snake-posn of segment, updates segment velocity
; (update-velocity :left {:x 20 :y 40})
(defn update-velocity
  [sdir sposn]
  (cond
    (= sdir :left)  {:x (- (:x sposn) unit-length) :y (:y sposn)}
    (= sdir :right) {:x (+ (:x sposn) unit-length) :y (:y sposn)}
    (= sdir :up)    {:x (:x sposn) :y (- (:y sposn) unit-length)}
    (= sdir :down)  {:x (:x sposn) :y (+ (:y sposn) unit-length)}))

;; snake-body -> snake-body
;; (- every value in snake-body map 1)
(defn decrease-tail-directions
  [sd]
  (map (fn [x] (- (second x) 1)) sd))

(defn increase-first
  [[dir amount]]
  [dir (+ amount 1)])

(defn update-last
  [[dir amount]]
  [dir (- amount 1)])

;; [[:up 1] [:right 4] [:down 2]] -> [[:up 2] [:right 4] [:down 1]]
(defn update-directions
  [sd]
  (concat
   (vector (increase-first (first sd)))
   (butlast (rest sd))
   (vector (update-last (last sd)))))

;; (h/update-vector sd (increase-first-direction (first sd))) [:up 2]

;; (increase-first-direction (first sd))

;; (update-irections sd)


;; direction -> snake-body
;; when dir changed (i.e, by key press), add this dir to list of snake directions
(defn insert-direction
  [d sd]
  (cond (= d "down")
        (concat (vector [:down 1]) (butlast sd) (vector (update-last (last sd))))))
                                           ;; take snake-body, decrease amount of segments in every direction,
                                           ;; add 1 down segment to result
        ;(= d "left")  (cons [:left 1] (update-tail-directions (rest sd)))
        ;(= d "right") (cons [:right 1] (update-tail-directions (rest sd)))
        ;(= d "up")  (cons [:up 1] (update-tail-directions (rest sd)))))

; snake-dir -> snake-posns
; gets snake-dirs and snake-posns, updates every segment in snake-posns
; map-direction-to-velocity (expand-dir-list sd) snake-posns
(defn map-direction-to-velocity
  [sd sp]
  (map (fn [sdir sposn] (update-velocity sdir sposn)) (expand-dirs-list sd) sp))

(defn move-snake
  [state]
    (assoc state :snake-posns (map-direction-to-velocity   (:snake-body  state)
                                                           (:snake-posns state))))

(defn change-snake-velocity
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

;; snake-direction -> snake-direction
;; increase snake body on 1 after apple eaten
(defn increase-head-after-eat
  [sd]
  (conj [(ffirst sd) (inc (h/sfirst sd))]
        (rest sd)))

;; (increase-snake-head (get-in const/init-game-state [:snake-body]))

;; state -> state
;; rebuild body after every move.
;; [[:down 1] [:right 4]] -> [[:down 2] [:right 3]]
(defn rebuild-snake-body
  [state]
  ;; rebuild ends when snake fully rebuilt to current snake-dir
  (let [sd (get-in state [:snake-body])]
    (if (not (= (h/sfirst sd) (h/sum-of-values-in-map sd)))
    (update-directions sd)
    sd)))

(rebuild-snake-body [[:left 5] [:down 1] [:right 4]])

(defn update-snake-after-eat
  [state]
    (-> state
        (update :devoured inc) ; +
        (assoc :apple-posns (get-in (apple/generate state) [:apple-posns])) ; +
        (update :snake-body increase-head-after-eat))) ; +
