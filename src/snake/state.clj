(ns snake.state
  (:import (com.raylib Jaylib Raylib))
  (:require [clojure.tools.logging :as log]
            [snake.apple :as apple]
            [snake.snake :as snake]
            [snake.keys :as keys]
            [snake.constants :as const]
            [snake.helpers :as h]
            [snake.rendering :as rd]))

(use 'clojure.pprint)
(defn print-state [state]
  (pprint state))

(defn update-state [state]
 (log/info "Current state before update:" state)
  ;; apple/generate should be part of update-snake-after-eat
  (let [updated (-> state
                    snake/move-snake
                    snake/update-snake-after-eat
                    snake/rebuild-snake-body )]
    (log/info "Current state after update:" updated)
    updated))

(defn render-state [state]
    (Raylib/BeginDrawing)
    (do
      (Raylib/ClearBackground Jaylib/RAYWHITE)
      (rd/draw-init-snake state)
      (Raylib/DrawCircle (-> state :apple-posns :x) (-> state :apple-posns :y) 10 Jaylib/RED)
      (Raylib/DrawFPS 20, 20))
    (Raylib/EndDrawing))

(defn game-loop [old-state]
  (when (not (Raylib/WindowShouldClose))
    ;; 1) (handle-keyboard)
    ;; 2) (let [new-state (update-state old-state)]
    ;; 3) (render new-state)
    (let* [new-state (-> old-state keys/handle-keys update-state)]
      (render-state new-state)
      (println "----------SNAKE STEP----------")
      (recur new-state))))

(concat (butlast []))
