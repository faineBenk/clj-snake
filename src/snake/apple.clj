(ns snake.apple
  (:import (com.raylib Jaylib Raylib))
  (:require [snake.constants :as const])
  (:require [snake.helpers :as h]))

(defn gonna-be-in-apple?
  [state]
  (let [dir (:snake-dir state)
        {ax :x ay :y} (:apple-posns state)
        {sx :x sy :y} (last (:snake-posns state))
        [next-sx next-sy] (cond (= dir "down") [sx (+ sy 20)]
                      (= dir "left") [(- sx 20) sy]
                      (= dir "right") [(+ sx 20) sy]
                      (= dir "up") [sx (- sy 20)])]
    (= [next-sx next-sy] [ax ay])))

(defn is-eaten?
  [state]
  (let [{sx :x sy :y} (last (:snake-posns state)) ;; snake head
        {ax :x ay :y} (:apple-posns state)]       ;; current apple position
    (= [sx sy] [ax ay])))
    ;;(head-in-apple? [sx sy] [ax ay])))

(defn gen-and-check-coord
  []
  (let [r (rand-int const/available-coord-step)]
  (if (= r 0) (recur) (* r 20))))

;; snake-direction -> snake-direction
;; increase snake body on 1 after apple eaten
(defn increase-head-after-eat
  [sd]
  (concat (butlast sd) (vector [(h/flast sd) (inc (h/slast sd))])))

;; state -> snake-positions
(defn add-apple-to-head
  [state]
  (assoc state :snake-posns (concat (:snake-posns state) (vector (:apple-posns state)))))

;; (add-apple-to-head const/init-game-state)

;; (concat  '({:x 20, :y 40} {:x 40, :y 40}) (vector {:x 50  :y 50}))

(defn generate
  [state]
      (let [new-apple-x (gen-and-check-coord)
            new-apple-y (gen-and-check-coord)]
      (-> state
          (update-in [:devoured] inc)
          (add-apple-to-head)
          (assoc-in [:apple-posns :x] new-apple-x)
          (assoc-in  [:apple-posns :y] new-apple-y)
          (update-in [:snake-body] increase-head-after-eat))))
