(ns com.fulcro-community.fulcro-demos.workspaces.basics-ws
  (:require
    [clojure.string :as str]
    [com.fulcrologic.fulcro.dom :as dom]
    [com.fulcrologic.fulcro.dom.html-entities :as ent]
    [com.fulcrologic.fulcro.dom.events :as evt]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro.algorithms.form-state :as fs]
    [com.fulcrologic.fulcro.algorithms.denormalize :as dnz]
    [com.fulcrologic.fulcro.algorithms.normalize :as nz]
    [taoensso.timbre :as log]
    [nubank.workspaces.core :as ws]
    [nubank.workspaces.card-types.fulcro3 :as ct.fulcro]
    [com.fulcro-community.workspace-utils :as wsu]
    [app.util :as u])
  )




(defsc Demo [this {:keys [:demo/counter] :as props}]
  {:query         [:demo/counter]
   :ident         (fn [] [:component/id :singleton])
   :initial-state (fn [_]
                    {:demo/counter 0})}
  (dom/div
    (str "Counter accumulator: " counter)
    (dom/button {:onClick #(m/set-value! this :demo/counter (inc counter))} "+")))

(def ui-demo (comp/factory Demo {:keyfn :demo/counter}))



(def app (wsu/wrap-deref-app
           (ws/defcard demo-card
             (ct.fulcro/fulcro-card
               {::ct.fulcro/root       Demo
                ::ct.fulcro/wrap-root? true}))))

(comment
  (app/current-state @app))