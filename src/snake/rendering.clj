(ns snake.rendering
  (:import (com.raylib Jaylib Raylib))
  (:require [snake.helpers :as h]))

(defn draw-init-snake
  [state]
  (doseq [posn (get-in state [:snake-posns])]
    (let [{:keys [x y]} posn]
    (Raylib/DrawCircle x y 10 Jaylib/VIOLET))))
