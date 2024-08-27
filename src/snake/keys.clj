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

(defn toggle-pause
  [state]
  (let [current-state (:game-state state)]
    (cond
      (= current-state "continue") (assoc state :game-state "pause")
      (= current-state "pause") (assoc state :game-state "continue"))))

(defn handle-keys
  [state]
  (let [last-key (:last-key state)
        sd (:snake-body state)
        dir (:snake-dir state)
        game-state (:game-state state)]
    (update-last-key last-key)
    (if @last-key (println (str "last key is " @last-key)))
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
      (= @last-key 32) (toggle-pause state)
      :else state)]
    ;; at the end of snake move resets last-key
    ;; to prevent addition of direction at every move
    (reset! last-key nil)
    new-state)))

(defn is-menu-pressed?
  [state]
  (let [game-state (:game-state state)]
     (and (Raylib/IsMouseButtonPressed 0)
          (not (= game-state "menu"))
          (and (> (Raylib/GetMouseX) 20) (> (Raylib/GetMouseY) 250))
    )))

(defn handle-buttons
  [state]
  (cond (is-menu-pressed? state) (assoc state :game-state "menu")
    :else state))
