(ns app.client
  (:require
    [app.application :refer [SPA]]
    [app.ui.root :as root]
    [com.fulcrologic.fulcro.application :as app]
    [taoensso.timbre :as log]
    [com.fulcrologic.fulcro.inspect.inspect-client :as inspect]))

(defn ^:export refresh []
  (log/info "Hot code Remount")
  (app/mount! SPA root/Root "app"))

(defn ^:export init []
  (log/info "Application starting.")
  
  (app/set-root! SPA root/Root {:initialize-state? true})
  (app/mount! SPA root/Root "app" {:initialize-state? false}))

(comment
  (inspect/app-started! SPA)
  (app/mounted? SPA)
  (app/set-root! SPA root/Root {:initialize-state? true})
  

  (reset! (::app/state-atom SPA) {})
  (app/current-state SPA))
