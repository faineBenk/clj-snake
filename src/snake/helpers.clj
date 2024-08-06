(ns snake.helpers)

(defn sfirst
  [l]
  (second (first l)))

(defn get-x-in-first-posn
  [state]
  (get-in (-> state
              :snake-posns
              first)
          [:x]))

(defn get-y-in-first-posn
  [state]
  (get-in (-> state
              :snake-posns
              first)
          [:y]))

(defn update-vector
  [v-old v-with-updated]
  (mapv (fn [[dir _] new-val]
           [dir new-val])
        v-old v-with-updated))

;; core analog?
(defn sum-of-values-in-map
  [m]
  (reduce + (map (fn [x] (second x)) m)))

;; [[:left 6] [:down 0] :right 3]] -> [[:left 6] [:right 3]]
(defn remove-if-zero
  [v]
  nil)
