(ns snake.keys
  (:import (com.raylib Jaylib Raylib))
  (:require [snake.snake :as snake]
            [snake.constants :as const]))

(defn update-last-key
  [last-key]
  (let [key-pressed (Raylib/GetKeyPressed)]
    (when (not (= key-pressed 0))
      (reset! last-key key-pressed)
      (recur last-key))))

(defn handle-keys
  [state]
  (let [last-key (:last-key state)
        sd (:snake-body state)
        dir (:snake-dir state)]
    (update-last-key last-key)
    (let [new-state (cond
      ;(let [new-state (assoc state :snake-dir "down")]

      (and (= @last-key 83) (not (= dir "down")))  (assoc state :snake-vel (snake/change-snake-velocity "down")
                                    :snake-dir "down"
                                    :snake-body (snake/insert-direction "down" sd))
      (and (= @last-key 87) (not (= dir "up")))    (assoc state :snake-vel (snake/change-snake-velocity "up")
                                    :snake-dir "up"
                                    :snake-body (snake/insert-direction "up" sd))
      (and (= @last-key 68) (not (= dir "right"))) (assoc state :snake-vel (snake/change-snake-velocity "right")
                                    :snake-dir "right"
                                    :snake-body (snake/insert-direction "right" sd))
      (and (= @last-key 65) (not (= dir "left")))  (assoc state :snake-vel (snake/change-snake-velocity "left")
                                    :snake-dir "left"
                                    :snake-body (snake/insert-direction "left" sd))
      :else state)]
    ;; at the end of snake move resets last-key
    ;; to prevent addition of direction at every move
    (reset! last-key nil)
    new-state)))
