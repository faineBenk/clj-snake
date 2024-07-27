(ns snake.core
  (:gen-class)
  (:import (com.raylib Jaylib Raylib))
  (:require [clojure.tools.logging :as log]
            [snake.state :as st]
            [snake.constants :as const]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (log/info "----------STARTING SNAKE----------")
  (Raylib/InitWindow (const/main-window-scales :width) (const/main-window-scales :height) "snake-game")
  (Raylib/SetTargetFPS const/fps)
  (let [init const/init-game-state]
    (st/print-state init)
    (st/game-loop init))
  (Raylib/CloseWindow)
  (log/info "----------SNAKE ENDING----------"))
