(ns snake.keys
  (:import (com.raylib Jaylib Raylib))
  (:require [snake.snake :as snake]))

(defn update-last-key
  [last-key]
  (let [key-pressed (Raylib/GetKeyPressed)]
    (when (not (= key-pressed 0))
      (reset! last-key key-pressed)
      (recur last-key))))

(defn handle-keys
  [state]
  (let [last-key (:last-key state)]
    (update-last-key last-key)
    (cond
      ;(let [new-state (assoc state :snake-dir "down")]
      (= @last-key 83) (assoc state :snake-vel (snake/change-snake-direction "down")
                                    :snake-dir "down")
      (= @last-key 87) (assoc state :snake-vel (snake/change-snake-direction "up")
                                    :snake-dir "up")
      (= @last-key 68) (assoc state :snake-vel (snake/change-snake-direction "right")
                                    :snake-dir "right")
      (= @last-key 65) (assoc state :snake-vel (snake/change-snake-direction "left")
                                    :snake-dir "left")
      :else state)))
