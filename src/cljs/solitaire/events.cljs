(ns solitaire.events
  (:require
   [re-frame.core :as rf]
   [solitaire.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]))

(rf/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
            db/default-db))

(rf/reg-event-db
 ::start
 (fn [db _]
   (assoc db
          :board db/initial-board
          :status :in-progress)))

(rf/reg-event-db
 ::select-field
 (fn [db [_ x y]]
   (assoc db :selected-field
          (if (= (:selected-field db) [x y])
            nil
            [x y]))))

(rf/reg-event-db
 ::make-move
 (fn [{:keys [board] :as db} [_ x y]]
   (let [source (:selected-field db)
         target [x y]]
     (if (db/can-move? board source target)
       (let [new-board (db/move board source target)]
         (assoc db
                :board new-board
                :selected-field nil
                :status (if (db/game-over? new-board)
                          :game-over
                          :in-progress)))
       (assoc db :selected-field nil)))))
