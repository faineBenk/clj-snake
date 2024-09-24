(ns rendering
  (:import (com.raylib Jaylib Jaylib$Color Raylib))
  (:require [helpers :as h])
  (:require [constants :as const])
  (:require [client :as client]))

;; custom colors

(def button-inactive-color (Jaylib$Color. 110 100 150 200))
(def screen-semitransparent-color (Jaylib$Color. 60 50 150 100))

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
  ;; why 10?
  [(+ p 10) 0 (+ p 10) (:width const/main-window-scales)])

(defn mesh-points-to-vectors
  [v]
  (cons [10 0 10 (:width const/main-window-scales)] (map #(mesh-point-to-vector %) v)))

(defn draw-continue
  [state]
    (Raylib/ClearBackground Jaylib/DARKPURPLE)
    (doseq [[x1 y1 x2 y2] (mesh-points-to-vectors
                          (create-mesh-points-axis const/init-coordinates-for-mesh))]
      (Raylib/DrawLine x1 y1 x2 y2 Jaylib/GRAY)
      (Raylib/DrawLine y1 x1 y2 x2 Jaylib/GRAY))
    ;; draw-borders [offset-start offset-end [rectangle size]]
    (draw-borders const/border-init-x
                  (- (:width const/main-window-scales) 10)
                  (:a2 const/border-dimensions)
                  (:a1 const/border-dimensions))
    (draw-init-snake state)
    (Raylib/DrawCircle (-> state :apple-posns :x) (-> state :apple-posns :y) 5 Jaylib/RED)
    (Raylib/DrawFPS 30, 30))

(defn draw-button-text
  [b-name {x :x y :y}]
  (Raylib/DrawText b-name ;; offset from right side of Rectangle
                          (+ x 5)
                          ;; offset from upper side of Rectangle
                          (+ y (/ const/unit-length 2))
                           const/text-width
                           Jaylib/LIGHTGRAY))

(defn draw-letters-in-input
  [state]
  (let [player-name (get-in state [:player :player-name])]
    (println "input: "(:input state))
    (println "player-name: " player-name)
  ;; (draw-button-text (get-in state [:player :player-name]) input-name-button)
  (draw-button-text player-name (get-in state [:buttons "input-name"]))))

(defn draw-button
  [state b-name {x :x y :y width :width height :height}]
  (Raylib/DrawRectangle x y width height button-inactive-color)
  (cond
    (= b-name "menu") (draw-button-text "menu" (get-in state [:buttons "menu"]))
    (= b-name "new-game") (draw-button-text "new game" (get-in state [:buttons "new-game"]))
    (= b-name "continue") (draw-button-text "play" (get-in state [:buttons "continue"]))
    (= b-name "input-name") ;;(let [b-input (get-in state [:buttons "input-name"]) player (:player state)]
                                                    ;;(when (k/button-area-active? b-input)
                                 (draw-letters-in-input state)))


(defn draw-init-screen
  [state]
    (Raylib/DrawRectangle 0 0 (:width const/main-window-scales)
                              (:height const/main-window-scales) Jaylib/VIOLET)
    (draw-button state "new-game" (get-in state [:buttons "new-game"]))
    (draw-button state "continue" (get-in state [:buttons "continue"]))
    (when (get-in state [:player :continue-input])
      (let [txt "input your name and press enter: "
            txt-len (* 10 (count txt))]
        (Raylib/DrawText txt (const/set-x-coord-centrified txt-len)
                             (- const/canvas-center (* const/unit-length 2))
                             const/text-width Jaylib/LIGHTGRAY))
        (draw-button state "input-name" (get-in state [:buttons "input-name"]))))

(defn draw-score
  [state]
  (Raylib/DrawText (str "score: " (:devoured state)) const/canvas-center
                                                     (+ const/canvas-center const/unit-length)
                                                     const/text-width Jaylib/YELLOW))

(defn draw-pause
  [state]
  (draw-continue state)
  (Raylib/DrawRectangle 0 0 (:width const/main-window-scales)
                            (:height const/main-window-scales) screen-semitransparent-color)
  (Raylib/DrawText "pause" const/canvas-center
                           const/canvas-center
                           const/text-width Jaylib/LIGHTGRAY)
  (draw-score state)
  (draw-button state "menu" (get-in state [:buttons "menu"])))

(defn draw-rating-table [rating-table]
  (loop [rem rating-table
         acc (+ const/canvas-center (* const/unit-length 4))]
    (when (seq rem)
      (Raylib/DrawText (str (:player (first rem)) " | " (:score (first rem)))
                       const/canvas-center
                       acc
                       const/text-width Jaylib/YELLOW)
      (recur (rest rem) (+ acc const/unit-length)))))

(defn draw-over
  [state]
  (draw-continue state)
  (Raylib/DrawRectangle 0 0 (:width const/main-window-scales)
                            (:height const/main-window-scales) screen-semitransparent-color)
  (Raylib/DrawText "game over" const/canvas-center
                               const/canvas-center
                               const/text-width Jaylib/LIGHTGRAY)
  (draw-score state)
  (draw-button state "menu" (get-in state [:buttons "menu"]))
  (Raylib/DrawText "rating table: " const/canvas-center
                                    (+ const/canvas-center (* const/unit-length 3))
                                    const/text-width Jaylib/LIGHTGRAY)
  (draw-rating-table (client/get-rating-table)))

