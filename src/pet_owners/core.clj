(ns pet-owners.core
  (:require [datomic.api :as d]))

(def conn nil)

(defn add-pet-owner [owner-name]
  @(d/transact conn [{:db/id (d/tempid :db.part/user)
                      :owner/name owner-name}]))

(defn find-all-pet-owners []
  (d/q '[:find ?owner-name
         :where
         #_[?po :owner/pets _]
         [?po :owner/name ?owner-name]]
       (d/db conn)))

(defn find-all-pets []
  (d/q '[:find ?pet-name
         :where [_ :pet/name ?pet-name]]
       (d/db conn)))

(defn get-ownerid-by-name [owner-name]
  (ffirst
   (d/q '[:find ?eid
          :in $ ?owner-name
          :where [?eid :owner/name ?owner-name]]
        (d/db conn)
        owner-name)))

(defn add-pet [pet-name owner-name]
  (let [pet-id (d/tempid :db.part/user)
        owner-id (get-ownerid-by-name owner-name)]
    @(d/transact conn [{:db/id pet-id
                        :pet/name pet-name}
                       {:db/id owner-id
                        :owner/pets pet-id}])))

(defn find-owner-for-pet [pet-name]
  (d/q '[:find ?owner-name
         :in $ ?pet-name
         :where
         [?p :pet/name ?pet-name]
         [?o :owner/pets ?p]
         [?o :owner/name ?owner-name]]
       (d/db conn)
       pet-name))

(defn find-pets-for-owner [owner-name]
  (d/q '[:find ?pet-name
         :in $ ?owner-name
         :where [?eid :owner/name ?owner-name]
                [?eid :owner/pets ?pet]
                [?pet :pet/name ?pet-name]]
       (d/db conn)
       owner-name))


