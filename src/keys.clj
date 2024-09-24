(ns keys
  (:import (com.raylib Jaylib Raylib))
  (:require [snake :as snake]
            [constants :as const]))

(defn extract-keys-from-queue []
  (loop [v []]
    (let [new-key (Raylib/GetKeyPressed)]
      (cond
        (= new-key 0) v
        :else
        (recur (conj v new-key))))))

(defn is-alphanumeric? [c]
  (and (>= c 39) (<= c 96)))

(defn seq-into-special-sym [fi si]
  (cond
    (= fi 340) (cond (= si 45) \_
                     :else \!)
    :else
    1))

;; 340 -- shift
;; [65 68 340 45 66] -> [\a \d \_ \b]
(defn tokenize [input output]
  (loop [i input
         o output]
    (cond
      (empty? i) o
      (is-alphanumeric? (first i)) (recur (rest i) (conj o (char (first i))))
      (= (first i) 340) (recur (vec (drop 2 i)) (conj o (seq-into-special-sym (first i) (second i))))
      :else
      o)))

(tokenize [65 68 340 49 66] [])

(vec (drop 2 [340 45 66]))

;; check keycode validity with k. table
(defn fill-with [input keys-happened]
  (loop [acc-input input
         left-keys keys-happened]
    (cond (empty? left-keys) acc-input
          ;; ESC
          (= (first left-keys) 259) (recur (vec (butlast acc-input)) (rest left-keys))
          ;;
          (is-alphanumeric? (first left-keys)) (recur (conj acc-input (first left-keys)) (rest left-keys))
          :else
          left-keys)))

; Current problems:
; 1. Every key except for enter is considered as char, which is wrong. Chars you are interested in should be filtered using the keycode table
; 2. Enter may happen in between of input, not only last
; 3. For some reason punctuation marks is not handled correctly. While it's ok for !?* and etc, it is unacceptable for _. Thus it must be handled.

(defn toggle-pause
  [state]
  (let [current-state (:game-state state)]
    (cond
      (= current-state "continue") (assoc state :game-state "pause")
      (= current-state "pause") (assoc state :game-state "continue"))))

(defn update-player-name
  [state]
  (let [chars (tokenize (:input state))
        player-name (apply str (map char chars))]
    ;(println "codepoints: " codepoints)
    ;(println "input: " (:input state))
    ;(println "player name: " player-name)
    (assoc-in state [:player :player-name] player-name)))

(defn handle-keys
  [state]
  (let [game-state (:game-state state)
        keys-happened (extract-keys-from-queue)]
    (cond
          (= game-state "menu")
              (cond (get-in state [:player :continue-input])
                  (let [[keys-happened _] (partition keys-happened)
                        enter-happened (contains keys-happened 257)
                        state (update state :input (fn [input] (fill-with input keys-happened)))]
                      (assoc-in (update-player-name state) [:player :continue-input] (not enter-happened)))
                  ;; when enter hit, player name sent to server
                  :else
                  (assoc-in state [:player :continue-input] false))
      :else
      (let [last-key (last keys-happened)
            sd (:snake-body state)
            dir (:snake-dir state)]
        (when last-key (println (str "last key is " last-key)))
        (let [new-state (cond
          (and (= last-key 83) (not (= dir "up")))  (assoc state :snake-vel (snake/change-snake-velocity "down")
                                        :snake-dir "down"
                                        :snake-body (snake/insert-direction "down" sd))
          (and (= last-key 87) (not (= dir "down")))    (assoc state :snake-vel (snake/change-snake-velocity "up")
                                        :snake-dir "up"
                                        :snake-body (snake/insert-direction "up" sd))
          (and (= last-key 68) (not (= dir "left"))) (assoc state :snake-vel (snake/change-snake-velocity "right")
                                        :snake-dir "right"
                                        :snake-body (snake/insert-direction "right" sd))
          (and (= last-key 65) (not (= dir "right")))  (assoc state :snake-vel (snake/change-snake-velocity "left")
                                        :snake-dir "left"
                                        :snake-body (snake/insert-direction "left" sd))
          (= last-key 32) (toggle-pause state)
          :else state)]
        new-state)))))

(defn button-area-active?
  [button]
    ;;(cond
      ;; menu area
  (let [b-x (:x button)
        b-y (:y button)
        b-w (:width button)
        b-h (:height button)
        m-x (Raylib/GetMouseX)
        m-y (Raylib/GetMouseY)]
      (and
           (>= m-x b-x) (<= m-x (+ b-x b-w))
           (>= m-y b-y) (<= m-y (+ b-y b-h)))))

(defn button-hover
  [buttons]
  (some (fn [[b-name button]]
          (when (button-area-active? button)
            [b-name button]))
        buttons))

(defn falsify-hovers [state]
  (assoc state :buttons (into {} (map (fn [[b-name button]]
                                          [b-name (assoc button :hover false)]) (:buttons state)))))

(defn is-current-state-valid?
  [game-state avail-states]
  (some (fn [st] (= game-state st)) avail-states))

(defn handle-buttons
  [state]
  (let [state (falsify-hovers state)
        buttons (:buttons state)
        [b-name button] (button-hover buttons)
        avail-states (:states button)
        game-state (:game-state state)]
  (if (is-current-state-valid? game-state avail-states)
    (if (Raylib/IsMouseButtonPressed 0)
      (cond
        ;; menu
        (= b-name "menu") (assoc state :game-state "menu")
        ;; continue game
        (= b-name "continue") (assoc state :game-state "continue")
        ;; new game
        (= b-name "new-game") (snake/update-init-snake const/init-game-state)
        :else state)
      (assoc-in state [:buttons b-name :hover] true))
    state)))

