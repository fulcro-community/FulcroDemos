(ns app.ui.exampleset-1
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
    [app.application :refer [SPA]]
    [app.util :as u]))




;; IMPORTANT NOTE: when changing the `:initial-state`, please remember to refresh the page, otherwise the state 
;; will not change!


;; ----------------------------
;; ----------------------------  #1
;; ----------------------------



;; comment/un-comment the following line to enable section 1
#_#_#_
    (defsc Person [this {:keys [:person/id
                                  :person/name
                                  :person/age] :as props}]
        {:query [:person/id
                 :person/name
                 :person/age]}
        (dom/div
          (str props)))

    (def ui-person (comp/factory Person {:keyfn :person/id}))


    (defsc Root [this {:root/keys [my-person]}]
      {:query         [:root/my-person]
       :initial-state (fn [_initial-props]
                        {:root/my-person {:person/id   1
                                          :person/name "Jane"
                                          :person/age  "34"}})}
      (dom/div
        "Person UI"
        (ui-person my-person)))


(comment
  (app/current-state app.application/SPA)
  ;=>
  {:root/my-person #:person{:id 1, :name "Jane", :age "34"},
   ...             ...}
  )


;; ----------------------------
;; ----------------------------  #2 
;; ----------------------------





#_#_#_(defsc Person [this {:keys [:person/id
                                  :person/name
                                  :person/age] :as props}]
        {:query         [:person/id
                         :person/name
                         :person/age]
         :initial-state (fn [initial-props]
                          #:person{:id   (u/uuid)
                                   :age  (or (:age initial-props) (rand-int 99))
                                   :name (:name initial-props)})}
        (dom/div
          (str props)))

    (def ui-person (comp/factory Person {:keyfn :person/id}))


    (defsc Root [this {:root/keys [my-people]}]
      {:query         [:root/my-people]
       :initial-state (fn [_initial-props]
                        {:root/my-people (mapv (partial comp/get-initial-state Person)
                                           [{:age 9 :name "Jane"}
                                            {:age 24 :name "Nick"}
                                            {:name "Ron"}])})}
      (dom/div
        "Person UI"
        (map ui-person my-people)))

(comment
  (app/current-state app.application/SPA)
  ;=>
  {:root/my-people [#:person{:id #uuid"5ed8e0be-9579-4700-bebf-15efcf5b7cba", :age 9, :name "Jane"}
                    #:person{:id #uuid"49c911ec-a35c-4f58-ae14-c66a3cbf0819", :age 24, :name "Nick"}
                    #:person{:id #uuid"7a3077d1-ce12-4e28-8045-8480fd0ee381", :age 73, :name "Ron"}],
   ...             ...})



;; ----------------------------
;; ----------------------------  #3
;; ----------------------------



;#_#_#_#_#_
(defsc Person [this {:keys [:person/id
                            :person/name
                            :person/age] :as props}]
  {:query         [:person/id
                   :person/name
                   :person/age]
   :initial-state (fn [initial-props]
                    #:person{:id   (rand-int 1000) ;; usually uuid or something, but for readability just 1-999
                             :age  (or (:age initial-props) (rand-int 99))
                             :name (:name initial-props)})

   ;;             special form, where the args list from defsc (props and this) are in scope
   :ident         (fn [] [:person/id (:person/id props)])
   ;;             no need for the first key in the vector to be the same as the id key (though it is preferable for
   ;;               them to be the same)
   ;:ident         (fn [] [:whatever/id (:person/id props)])
   }
  (dom/div
    (str props)))


(def ui-person (comp/factory Person {:key-fn :person/id}))


(defsc PersonList [this {:keys [:person-list/id
                                :person-list/people] :as props}]
  {:query         [:person-list/id
                   {:person-list/people (comp/get-query Person)}]

   :initial-state (fn [_initial-props]
                    {:person-list/id     ::singleton
                     :person-list/people (mapv (partial comp/get-initial-state Person)
                                           [{:age 9 :name "Jane"}
                                            {:age 24 :name "Nick"}
                                            {:name "Ron"}])})

   ;; special form that translates exactly to  (fn [] [:person-list/id (:person-list/id props)])
   :ident         :person-list/id}
  (dom/div
    "Person List"
    (map ui-person people)))

(def ui-person-list (comp/factory PersonList {:keyfn :person-list/id}))

(defsc Root [this {:root/keys [person-list]}]
  {:query         [{:root/person-list (comp/get-query PersonList)}]
   :initial-state (fn [_initial-props]
                    {:root/person-list (comp/get-initial-state PersonList {})})}
  (dom/div
    (dom/div
      (dom/h3 "Person UI")
      (ui-person-list person-list))
    (dom/hr)
    (dom/pre (u/zpstr person-list))))


(comment

  (comp/get-initial-state Root {})
  {:root/person-list
   {:person-list/id :app.ui.exampleset-1/singleton,
    :person-list/people
                    [{:person/age 9, :person/id 90, :person/name "Jane"}
                     {:person/age 24, :person/id 288, :person/name "Nick"}
                     {:person/age 41, :person/id 459, :person/name "Ron"}]}}

  (comp/get-query Root)
  [{:root/person-list [:person-list/id
                       {:person-list/people [:person/id
                                             :person/name
                                             :person/age]}]}]

  ;; Notice how similar the query and initial state look. 



  ;; this is how the ui rendering tree is created
  (dnz/db->tree
    (comp/get-query Root)
    (app/current-state app.application/SPA)
    (app/current-state app.application/SPA))


  ;; and this is how the initial db is created
  (nz/tree->db
    Root
    (comp/get-initial-state Root)
    true                                ;; merge-idents?
    )
  
  
  ;; -----------------------------------------------------


  ;; the reason for using the Root component as opposed to the query is so Fulcro has access to the :ident
  ;; and is able to put items in the correct tables with the correct references replacing the original
  ;; datum.* Example:

  {:person-list/id {1 {:person-list/people [{:person/id 5 :person/name "Jake"}]}}}
  ; =>
  ;; state-map
  {:person/id      {5 {:person/id 5 :person/name "Jake"}}
   ;;                                      notice that in the list is the *exact* path needed
   ;;                                      to retrieve the original person from the state map via
   ;;                                      (get-in state-map [:person/id 5])
   :person-list/id {1 {:person-list/people [[:person/id 5]]}}}
  
  ;; * In reality components are stored in the metadata of queries when using `comp/get-query`
  (binding [*print-meta* true]
    (println (comp/get-query Root)))
  ; =>
  ^{:component app.ui.exampleset-1/Root,
    :queryid app.ui.exampleset-1/Root} 
  [{:root/person-list ^{:component app.ui.exampleset-1/PersonList, 
                        :queryid app.ui.exampleset-1/PersonList}
                      [:person-list/id 
                       {:person-list/people ^{:component app.ui.exampleset-1/Person, 
                                              :queryid app.ui.exampleset-1/Person}
                                            [:person/id :person/name :person/age]}]}]

  ;; because of this the following works equally well:
  (nz/tree->db
    (comp/get-query Root)
    (comp/get-initial-state Root)
    true                                ;; `merge-idents?`. See docstring
    )
  
  ;; without the metadata of components available, it would not be possible to generate idents
  


  (comp/get-ident Person (comp/get-initial-state Person {:name "Jill"}))
  (app/current-state app.application/SPA)

  )


