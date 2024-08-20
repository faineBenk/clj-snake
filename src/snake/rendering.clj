(ns snake.rendering
  (:import (com.raylib Jaylib Raylib))
  (:require [snake.helpers :as h])
  (:require [snake.constants :as const]))

(defn draw-init-snake
  [state]
  (doseq [posn (get-in state [:snake-posns])]
    (let [{:keys [x y]} posn]
    (Raylib/DrawCircle x y 10 Jaylib/PURPLE))))

(defn draw-border
  [x y w h]
  (Raylib/DrawRectangle x y w h Jaylib/BEIGE))

(defn draw-borders
  [a1 a2 w h]
  (draw-border a1 a1 w h)  ;; horizontal line up
  (draw-border a1 a2 w h)  ;; horizontal line down
  (draw-border a1 a1 h w)  ;; vertical line left
  (draw-border a2 a1 h w)) ;; vertical line right

;; [20 40 60 80 100 .. 300]
(defn increase-values
  [l acc len]
  (if (<= acc len)
    (cons (* acc (first l)) (increase-values (rest l) (+ acc 1) len)) l))

(defn create-mesh-points-axis
  [v]
  (increase-values v 1 (count v)))

;; 20 -> [20 0 20 300]
(defn mesh-point-to-vector
  [p]
  [(+ p 10) 0 (+ p 10) 300])

(defn mesh-points-to-vectors
  [v]
  (cons [10 0 10 300] (map #(mesh-point-to-vector %) v)))
