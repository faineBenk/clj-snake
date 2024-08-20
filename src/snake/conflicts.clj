(ns snake.conflicts
  (:require [snake.constants :as const]))

(defn gonna-stuck-wall?
  [state]
  ;; [sposn x1 x2 y1 y2]
  (let [{sx :x sy :y} (last (:snake-posns state))
        dir (:snake-dir state)
        next-sx-left  (- sx 20)
        next-sx-right (+ sx 20)
        next-sy-up (- sy 20)
        next-sy-down (+ sy 20)
        max-w (:width const/main-window-scales)
        max-h (:height const/main-window-scales)]
  (or
     (and (= 0 next-sx-left) (= dir "left"))
     (and (= max-w next-sx-right) (= dir "right"))
     (and (= 0 next-sy-up) (= dir "up"))
     (and (= max-h next-sy-down) (= dir "down")))))
