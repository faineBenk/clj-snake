(ns snake.constants)

(def fps 1)

(def tick (/ 1 fps))

; upper bound value for generating apple coordinates:
; available-coord-step * 20 < InitWindow width
(def available-coord-step 15)

(def main-window-scales {:width 300 :height 300})

(def init-game-state
  {:snake-posns [{:x 20 :y 40}]
   :snake-vel {:x 0 :y 20}
   :apple-posns {:x 60 :y 60}
   :devoured 0                ; amount of apples eaten
   :last-key (atom 0)         ; last pressed key
   :snake-dir :right         ; (head snake) direction
   :snake-body '([:right 4])}) ; snake directions

; is-eaten? == true
(def equal-head-apple-game-state
  {:snake-posns [{:x 60 :y 60}]
   :snake-vel {:x 0 :y 20}
   :apple-posns {:x 60 :y 60}
   :devoured 0                ; amount of apples eaten
   :last-key (atom 0)         ; last pressed key
   :snake-dir :right         ; (head snake) direction
   :snake-body [[:right 4]]}) ; snake directions
