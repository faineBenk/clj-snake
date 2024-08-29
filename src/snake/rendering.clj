(ns snake.rendering
  (:import (com.raylib Jaylib Jaylib$Color Raylib))
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

(defn draw-continue
  [state]
    (Raylib/ClearBackground Jaylib/DARKPURPLE)
    (doseq [[x1 y1 x2 y2] (mesh-points-to-vectors
                          (create-mesh-points-axis const/init-coordinates-for-mesh))]
      (Raylib/DrawLine x1 y1 x2 y2 Jaylib/GRAY)
      (Raylib/DrawLine y1 x1 y2 x2 Jaylib/GRAY))
    ;; draw-borders [offset-start offset-end [rectangle dimensions]]
    (draw-borders 5 290 (:a1 const/border-dimensions)
                            (:a2 const/border-dimensions))
    (draw-init-snake state)
    (Raylib/DrawCircle (-> state :apple-posns :x) (-> state :apple-posns :y) 5 Jaylib/RED)
    (Raylib/DrawFPS 20, 20))

(defn draw-init-screen-buttons
  []
  (Raylib/DrawRectangle 95 95 100 40 (Jaylib$Color. 110 100 150 200))
  (Raylib/DrawText "new game" 100 105 20 Jaylib/LIGHTGRAY)
  (Raylib/DrawRectangle 95 145 100 40 (Jaylib$Color. 110 100 150 200))
  (Raylib/DrawText "continue" 100 155 20 Jaylib/LIGHTGRAY))

(defn draw-init-screen
  []
    (Raylib/DrawRectangle 0 0 300 300 Jaylib/VIOLET)
    (draw-init-screen-buttons))


(defn draw-menu-button
  []
  (Raylib/DrawRectangle 25 240 55 40 (Jaylib$Color. 110 100 150 200))
  (Raylib/DrawText "menu" 30 250 20 Jaylib/LIGHTGRAY))

(defn draw-score
  [state]
  (Raylib/DrawText (str "score: " (:devoured state)) 100 150 20 Jaylib/YELLOW))

(defn draw-pause
  [state]
  (draw-continue state)
  (Raylib/DrawRectangle 0 0 300 300 (Jaylib$Color. 60 50 150 100 ))
  (Raylib/DrawText "pause" 120 130 20 Jaylib/LIGHTGRAY)
  (draw-score state)
  (draw-menu-button))

(defn draw-over
  [state]
  (draw-continue state)
  (Raylib/DrawRectangle 0 0 300 300 (Jaylib$Color. 60 50 150 100 ))
  (Raylib/DrawText "game over" 100 130 20 Jaylib/LIGHTGRAY)
  (draw-score state)
  (draw-menu-button))

;; buttons
