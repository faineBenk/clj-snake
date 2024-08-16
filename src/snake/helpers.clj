(ns snake.helpers)

(defn sfirst
  [l]
  (second (first l)))

(defn flast
  [l]
  (first (last l)))

(defn slast
  [l]
  (second (last l)))

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

(defn sum-of-values-in-map
  [m]
  (reduce + (map (fn [x] (second x)) m)))

(defn remove-if-zero
  [v]
  (filter #(not (zero? (second %))) v))

