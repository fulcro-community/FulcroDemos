(ns com.fulcro-community.workspace-utils
  (:require [nubank.workspaces.data :as wsd]
            [nubank.workspaces.model :as wsm]
            [nubank.workspaces.card-types.fulcro3 :as wsf3]
            [taoensso.timbre :as log]))

(comment
  ;; must be mounted
  (wsd/active-card 'com.fulcro-community.fulcro-demos.workspaces.basics-ws/demo-card)
  
  (-> 
    (wsd/active-card 'com.fulcro-community.fulcro-demos.workspaces.basics-ws/demo-card)
    ::wsf3/app
    com.fulcrologic.fulcro.application/current-state))


(defn wrap-deref-app 
  "When using `ws/defcard` and `n.w.ct.fulcro3`, you can wrap `defcard` with this function
  to get a `deref`able object that returns the live fulcro-app. 
  should at most be used for comment blocks. requires that the card
  be mounted on screen."
  [card-map]
  ;; card-map is in the form
  #_{card-sym {::wsm/card-form "ast of the settings"
               ::wsm/card-id card-sym}}
  ;; and we need card-sym for wsd/active-card
  (let [card-sym (-> card-map keys first)]
    (log/spy card-sym)
    (log/spy card-map)
    (when card-sym
      (reify IDeref
        (-deref [_]
          (get (wsd/active-card card-sym)
            ::wsf3/app))))))