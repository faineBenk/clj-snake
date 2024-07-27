(ns snake.apple
  (:import (com.raylib Jaylib Raylib))
  (:require [snake.constants :as const]))

(defn is-eaten?
  [state]
  (let [{ax :x ay :y} (first (:snake-posns state))
        {px :x py :y} (:apple-posns state)]
    (= [ax ay] [px py])))

(defn gen-and-check-coord
  []
  (let [r (rand-int const/available-coord-step)]
  (if (= r 0) (recur) (* r 20))))

(defn generate
  [state]
  (if (is-eaten? state)
      (let [new-apple-x (gen-and-check-coord)
            new-apple-y (gen-and-check-coord)]
      (-> state
          (assoc-in  [:apple-posns :x] new-apple-x)
          (assoc-in  [:apple-posns :y] new-apple-y)
          (update-in [:devoured] inc)))
      state))

