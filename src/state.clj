(ns state
  (:import (com.raylib Jaylib Raylib))
  (:require [clojure.tools.logging :as log]
            [apple :as apple]
            [snake :as snake]
            [keys :as keys]
            [constants :as const]
            [rendering :as rd]
            [conflicts :as confl]
            [client :as client]))

(use 'clojure.pprint)

(defn print-state
  [state]
   (pprint state))

(defn update-state
  [state]
  (if (= 0 (mod (:frames state) (/ const/fps 5)))
    (let [current-state (:game-state state)]
        (cond (= current-state "continue")
            (let [updated (cond (confl/gonna-stuck-wall? state)
                        (assoc state :game-state "over")
                        (apple/gonna-be-in-apple? state)
                        (snake/update-snake-after-eat state)
                        :else (do
                            (println "----------SNAKE STEP----------")
                            (log/info "Current state before update:" state)
                            (-> state
                                snake/move-snake
                                snake/rebuild-snake-body)))]
            (log/info "Current state after update:" updated)
            updated)
            (= current-state "pause")
            state
            (= current-state "over")
            (let [p-name (get-in state [:player :player-name])
                    score (:devoured state)
                  new-state (if (not (:score-sent state))
                                (do
                                  (println "SEND SCORE")
                                  (client/send-score-to-server p-name score)
                                  (assoc state :score-sent true))
                                state)]
              (println "GET RATING TABLE")
              (client/get-rating-table)
              new-state)
            (= current-state "menu")
            (do
              ;(println "----------MENU----------")
              ;(log/info "State in menu:" state)
              (assoc state :score-sent false))))
    state))

(defn render-state
  [state]
  (let [current-state (:game-state state)]
    (Raylib/BeginDrawing)
    (cond (= current-state "continue") (rd/draw-continue state)
          (= current-state "pause") (rd/draw-pause state)
          (= current-state "over") (rd/draw-over state)
          (= current-state "menu") (rd/draw-init-screen state))
    (Raylib/EndDrawing)))

(defn inc-frames
  [state]
  (update state :frames inc))

(defn game-loop
  [old-state]
    (when (not (Raylib/WindowShouldClose))
      ;; 1) (handle-keyboard)
      ;; 2) (let [new-state (update-state old-state)]
      ;; 3) (render new-state)
      (let* [new-state (-> old-state keys/handle-keys keys/handle-buttons update-state inc-frames)]
        (render-state new-state)
        (recur new-state))))
