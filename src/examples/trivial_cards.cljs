(ns examples.trivial-cards
  "A very simple Fulcro application"
  (:require
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.mutations :as m]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro]))

(defsc Demo [this {:keys [:demo/counter] :as _props}]
  {:query         [:demo/counter]
   :ident         (fn [] [:component/id :singleton])
   :initial-state (fn [_]
                    {:demo/counter 0})}
  (dom/div
    (str "Counter accumulator: " counter)
    (dom/button {:onClick #(m/set-value! this :demo/counter (inc counter))} "+")))

(ws/defcard demo-card
  (ct.fulcro/fulcro-card
   {::ct.fulcro/root       Demo
    ::ct.fulcro/wrap-root? true}))
