(ns snake.state
  (:import (com.raylib Jaylib Raylib))
  (:require [clojure.tools.logging :as log]
            [snake.apple :as apple]
            [snake.snake :as snake]
            [snake.keys :as keys]
            [snake.constants :as const]
            [snake.helpers :as h]
            [snake.rendering :as rd]
            [snake.conflicts :as confl]))

(use 'clojure.pprint)
(defn print-state [state]
  (pprint state))

(defn update-state [state]
 (log/info "Current state before update:" state)
  ;; apple/generate should be part of update-snake-after-eat
  (let [updated (if (confl/gonna-stuck-wall? state)
                   (assoc state :continue false)
                   (-> state
                             snake/move-snake
                             snake/rebuild-snake-body
                             snake/update-snake-after-eat))
                    ]
    (log/info "Current state after update:" updated)
    updated))

(defn render-state [state]
    (Raylib/BeginDrawing)
    (do
      (Raylib/ClearBackground Jaylib/DARKPURPLE)
      (doseq [[x1 y1 x2 y2] (rd/mesh-points-to-vectors
                             (rd/create-mesh-points-axis const/init-coordinates-for-mesh))]
        (Raylib/DrawLine x1 y1 x2 y2 Jaylib/GRAY)
        (Raylib/DrawLine y1 x1 y2 x2 Jaylib/GRAY))
      ;; draw-borders [offset-start offset-end [rectangle dimensions]]
      (rd/draw-borders 5 290 (:a1 const/border-dimensions)
                              (:a2 const/border-dimensions))
      (rd/draw-init-snake state)
      (Raylib/DrawCircle (-> state :apple-posns :x) (-> state :apple-posns :y) 5 Jaylib/RED)
      (Raylib/DrawFPS 20, 20))
    (Raylib/EndDrawing))

(defn game-loop [old-state]
  (when (not (Raylib/WindowShouldClose))
    ;; 1) (handle-keyboard)
    ;; 2) (let [new-state (update-state old-state)]
    ;; 3) (render new-state)
    (let* [new-state (if (:continue old-state)
                         (-> old-state keys/handle-keys update-state)
                         (do
                           ;; show score and menu buttons
                           (println "----------GAME OVER----------")
                           old-state))]
      (render-state new-state)
      (println "----------SNAKE STEP----------")
      (recur new-state))))
