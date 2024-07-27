(ns snake.helpers)

(defn sfirst
  [l]
  (second (first l)))

(defn get-x-in-first-posn
  [state]
  (get-in
   (-> state
    :snake-posns
    first)
    [:x]))

(defn get-y-in-first-posn
  [state]
  (get-in
   (-> state
    :snake-posns
    first)
    [:y]))
