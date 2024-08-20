(ns snake.constants)

(def fps 5)

(def tick (/ 1 fps))

; upper bound value for generating apple coordinates:
; available-coord-step * 20 < InitWindow width
(def available-coord-step 15)

(def main-window-scales {:width 300 :height 300})

(def init-coordinates-for-mesh (repeat (/ (:width main-window-scales) 20) 20))

(def border-dimensions {:a1 280 :a2 5})

(def init-game-state
  {:snake-posns [{:x 60 :y 60}]
   :snake-vel {:x 0 :y 20}
   :apple-posns {:x 60 :y 60}
   :devoured 0                ; amount of apples eaten
   :last-key (atom 0)         ; last pressed key
   :snake-dir "right"         ; (head snake) direction
   :snake-body '([:right 4]) ; snake directions
   :border-offset {:start 5 :end 290}
   :continue true})
