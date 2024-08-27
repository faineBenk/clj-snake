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

(defn print-state
  [state]
   (pprint state))

(defn update-state
  [state]
  (let [current-state (:game-state state)]
     (cond (= current-state "continue")
         (let [updated (if (confl/gonna-stuck-wall? state)
                      (assoc state :game-state "over")
                      (do
                        (println "----------SNAKE STEP----------")
                        (log/info "Current state before update:" state)
                        (-> state
                                snake/move-snake
                                snake/rebuild-snake-body
                                snake/update-snake-after-eat)))]
           (log/info "Current state after update:" updated)
        updated)
         (= current-state "pause")
         state
         (= current-state "over")
         state
         (= current-state "menu")
         (do
           (println "----------MENU----------")
           (log/info "State in menu:" state)
           state))))

(defn render-state
  [state]
  (let [current-state (:game-state state)]
    (Raylib/BeginDrawing)
    (cond (= current-state "continue") (rd/draw-continue state)
          (= current-state "pause") (rd/draw-pause state)
          (= current-state "over") (rd/draw-over state)
          (= current-state "menu") (rd/draw-menu))
    (Raylib/EndDrawing)))

(defn game-loop
  [old-state]
    (when (not (Raylib/WindowShouldClose))
      ;; 1) (handle-keyboard)
      ;; 2) (let [new-state (update-state old-state)]
      ;; 3) (render new-state)
      (let* [new-state (-> old-state keys/handle-keys keys/handle-buttons update-state)]
        (render-state new-state)
        (recur new-state))))
