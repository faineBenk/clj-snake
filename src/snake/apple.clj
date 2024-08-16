(ns snake.apple
  (:import (com.raylib Jaylib Raylib))
  (:require [snake.constants :as const])
  (:require [snake.helpers :as h]))

(defn is-eaten?
  [state]
  (let [{sx :x sy :y} (last (:snake-posns state)) ;; snake head
        {ax :x ay :y} (:apple-posns state)]       ;; current apple position
    (= [sx sy] [ax ay])))

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
  (let [dir (get-in state [:snake-dir])
        sp (get-in state [:snake-posns])
        {ax :x ay :y} (:apple-posns state)]
    (cond
      (= dir "down")  (assoc state :snake-posns (concat sp (vector {:x ax :y (- ay 20)})))
      (= dir "left")  (assoc state :snake-posns (concat sp (vector {:x (- ax 20) :y ay})))
      (= dir "right") (assoc state :snake-posns (concat sp (vector {:x (+ ax 20) :y ay})))
      (= dir "up")    (assoc state :snake-posns (concat sp (vector {:x ax :y (+ ay 20)}))))))

;; (add-apple-to-head const/init-game-state)

;; (concat  '({:x 20, :y 40} {:x 40, :y 40}) (vector {:x 50  :y 50}))

(defn generate
  [state]
  (if (is-eaten? state)
      (let [new-apple-x (gen-and-check-coord)
            new-apple-y (gen-and-check-coord)]
      (-> state
          (update-in [:devoured] inc)
          (add-apple-to-head)
          (assoc-in [:apple-posns :x] new-apple-x)
          (assoc-in  [:apple-posns :y] new-apple-y)
          (update-in [:snake-body] increase-head-after-eat)))
      state))
