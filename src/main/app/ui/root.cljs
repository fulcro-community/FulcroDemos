(ns app.ui.root
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
    [taoensso.timbre :as log]
    [app.ui.exampleset-1 :as eg1]))












; Root for exampleset 1

(def Root eg1/Root)

