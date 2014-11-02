(ns pet-owners.core-test
  (:require #_[clojure.test :refer :all]
            [pet-owners.core :refer :all]
            [expectations :refer [expect]]
            [datomic.api :as d]))

(defn create-empty-in-memory-db []
  (let [uri "datomic:mem://pet-owner-test"]
    (d/delete-database uri)
    (d/create-database uri)
    (let [conn (d/connect uri)
          schema (load-file "resources/datomic/schema.edn") ]
      (d/transact conn schema)
      conn)))

;; Adding one owner should allow us to find that owner
(expect #{["John"]}
        (with-redefs [conn (create-empty-in-memory-db)]
          (do
            (add-pet-owner "John")
            (find-all-pet-owners))))

;; Adding multiple owners should allow us to find all those owners
(expect #{["John"] ["Paul"] ["George"]}
        (with-redefs [conn (create-empty-in-memory-db)]
          (do
            (add-pet-owner "John")
            (add-pet-owner "Paul")
            (add-pet-owner "George")
            (find-all-pet-owners))))

;; Adding one pet should allow us to find that pet's owner
(expect #{["John"]}
        (with-redefs [conn (create-empty-in-memory-db)]
          (do
            (add-pet-owner "John")
            (add-pet "Sparky" "John")
            (find-owner-for-pet "Sparky"))))



;; Adding multiple pets should allow us to find all those pets
(expect #{["Sparky"] ["Spot"]}
        (with-redefs [conn (create-empty-in-memory-db)]
          (do
            (add-pet-owner "John")
            (add-pet "Sparky" "John")
            (add-pet "Spot" "John")
            (find-all-pets))))

;; Adding one owner with one pet should allow us to find that pet for
;; that owner
#_(expect #{["John"]}
        (with-redefs [conn (create-empty-in-memory-db)]
          (do
            (add-pet-owner "John")
            (add-pet "Salt" "John")
            (find-pets-for-owner "John"))))
