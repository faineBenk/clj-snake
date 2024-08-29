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
    ; (when @last-key (println (str "last key is " @last-key)))
    (let [new-state (cond
      ;(let [new-state (assoc state :snake-dir "down")]

      (and (= @last-key 83) (not (= dir "up")))  (assoc state :snake-vel (snake/change-snake-velocity "down")
                                    :snake-dir "down"
                                    :snake-body (snake/insert-direction "down" sd))
      (and (= @last-key 87) (not (= dir "down")))    (assoc state :snake-vel (snake/change-snake-velocity "up")
                                    :snake-dir "up"
                                    :snake-body (snake/insert-direction "up" sd))
      (and (= @last-key 68) (not (= dir "left"))) (assoc state :snake-vel (snake/change-snake-velocity "right")
                                    :snake-dir "right"
                                    :snake-body (snake/insert-direction "right" sd))
      (and (= @last-key 65) (not (= dir "right")))  (assoc state :snake-vel (snake/change-snake-velocity "left")
                                    :snake-dir "left"
                                    :snake-body (snake/insert-direction "left" sd))
      (= @last-key 32) (toggle-pause state)
      :else state)]
    ;; at the end of snake move resets last-key
    ;; to prevent addition of direction at every move
    (reset! last-key nil)
    new-state)))

(defn button-area-active?
  [button]
    ;;(cond
      ;; menu area
  (let [b-x (:x button)
        b-y (:y button)
        b-w (:width button)
        b-h (:height button)
        m-x (Raylib/GetMouseX)
        m-y (Raylib/GetMouseY)]
      (and
           (>= m-x b-x) (<= m-x (+ b-x b-w))
           (>= m-y b-y) (<= m-y (+ b-y b-h)))))

(defn button-hover
  [buttons]
  (some (fn [[b-name button]]
          (when (button-area-active? button)
            [b-name button]))
        buttons))

(defn falsify-hovers [state]
  (assoc state :buttons (into {} (map (fn [[b-name button]] [b-name (assoc button :hover false)]) (:buttons state)))))

(defn is-current-state-valid?
  [game-state avail-states]
  (some (fn [st] (= game-state st)) avail-states))

(defn handle-buttons
  [state]
  (let [state (falsify-hovers state)
        buttons (:buttons state)
        [b-name button] (button-hover buttons)
        avail-states (:states button)
        game-state (:game-state state)]
  (if (is-current-state-valid? game-state avail-states)
    (if (Raylib/IsMouseButtonPressed 0)
      (cond
        ;; menu
        (= b-name "menu") (assoc state :game-state "menu")
        ;; continue game
        (= b-name "continue") (assoc state :game-state "continue")
        ;; new game
        (= b-name "new-game") (snake/update-init-snake const/init-game-state)
        :else state)
      (assoc-in state [:buttons b-name :hover] true))
    state)))
