(ns snake.constants)

(def fps 30)

(def tick (/ 1 fps))

; upper bound value for generating apple coordinates:
; available-coord-step * 20 < InitWindow width
(def available-coord-step 30)

(def main-window-scales {:width 600 :height 600})

(def canvas-center (int (/ (:width main-window-scales) 2.45)))
; width of one square on canvas
(def unit-length 20)

; positions on x and y axis from where vertical
; and horizontal bars will be drawn
(def init-coordinates-for-mesh (repeat (/ (:width main-window-scales) unit-length) unit-length))

; start position at right of canvas
; from where borders will be drawn
(def border-init-x 5)

(def border-dimensions {:a1 border-init-x :a2 (- (:width main-window-scales) 20)})

(def text-width 20)

(def button-height (* text-width 2))

;; based on text, generates value for setting Rectangle width
(defn set-rectangle-dimensions
  [txt letter-width]
  (+ (* (count txt) letter-width) unit-length))

(def init-game-state
  {:snake-posns [{:x 60 :y 60}]
   :snake-vel {:x 0 :y unit-length}
   :apple-posns {:x 60 :y 60}
   :devoured 0                ; amount of apples eaten
   :last-key (atom 0)         ; last pressed key
   :snake-dir "right"         ; (head snake) direction
   :snake-body '([:right 4]) ; snake directions
   :border-offset {:start 5 :end 290}
   :game-state "continue"
   :frames 0
   :buttons {"menu" {:x unit-length :y (- (:height main-window-scales) 60)
                     :width (set-rectangle-dimensions "menu" 10)
                     :height button-height :hover false :states ["pause" "over"]}
             "new-game" {:x (- canvas-center unit-length) :y (- canvas-center unit-length)
                         :width (set-rectangle-dimensions "new-game" 10)
                         :height button-height :hover false :states ["menu"]}
             "continue" {:x (- canvas-center unit-length) :y (+ canvas-center (* unit-length 2))
                         :width (set-rectangle-dimensions "continue" 10)
                         :height button-height :hover false :states ["menu"]}
             }})
   ;; :active-button {:b-name nil :x nil :y nil :hover false}})  ; start menu : "start"
                             ; game is played : "continue"
                             ; game is paused : "pause"
                             ; game is over : "over"
