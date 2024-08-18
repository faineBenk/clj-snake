(ns snake.snake
  (:require [snake.constants :as const])
  (:require [snake.helpers :as h])
  (:require [snake.apple :as apple]))

;(def unit-length (* 20 const/tick))
(def unit-length 20)


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

(defn increase-segments-amount
  [[dir amount]]
  [[dir (+ amount 1)]])

(defn decrease-segments-amount
  [[dir amount]]
  (if (> amount 1)
    [[dir (- amount 1)]]
    nil))

;; [[:up 1] [:right 4] [:down 2]] -> [[:right 4] [:down 3]]
(defn update-directions
  [sd]
    (concat
      (decrease-segments-amount (first sd))
      (butlast (rest sd))
      (increase-segments-amount (last sd))))

;; direction -> snake-body
;; when dir changed (i.e, by key press), add this dir to list of snake directions
(defn insert-direction
  [d sd]
  (cond (= d "down")
                     ;; take snake-body, decrease amount of segments in last direction,
                     ;; add 1 down segment to result
                      (concat (butlast sd) (decrease-segments-amount (last sd)) (vector [:down 1]))
        (= d "left")  (concat (butlast sd) (decrease-segments-amount (last sd)) (vector [:left 1]))
        (= d "right") (concat (butlast sd) (decrease-segments-amount (last sd)) (vector [:right 1]))
        (= d "up")    (concat (butlast sd) (decrease-segments-amount (last sd)) (vector [:up 1]))))


(insert-direction "down" [[:right 0] [:down 4]])

(def conflicting-posns '({:x 60, :y 100} {:x 80, :y 100} {:x 100, :y 100} {:x 100, :y 80} {:x 100, :y 60} {:x 100 :y 80}))
(defn is-segment-conflict?
  [sp]
  (= '() (filter #(= (last sp) %) (butlast sp))))

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

;; state -> state
;; rebuild body after every move.
;; [[:down 1] [:right 4]] -> [[:right 5]]
(defn rebuild-snake-body
  [state]
  ;; rebuild ends when snake fully rebuilt to current snake-dir
  (let [sd (get-in state [:snake-body])]
    (if (not (= (h/slast sd) (h/sum-of-values-in-map sd)))
    (assoc state :snake-body (update-directions sd))
    state)))

(defn update-snake-after-eat
  [state]
  (apple/generate state))
