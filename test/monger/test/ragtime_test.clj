(set! *warn-on-reflection* true)

(ns monger.test.ragtime-test
  (:require [monger.core :as mg]
            [monger.collection  :as mc]
            [monger.test.helper :as helper]
            monger.ragtime)
  (:use clojure.test
        [monger.test.fixtures :only [purge-migrations]]
        ragtime.core))


(helper/connect!)

(use-fixtures :each purge-migrations)


(when-not (get (System/getenv) "CI")
  (deftest test-add-migration-id
    (let [db   (mg/get-db "monger-test")
          coll "meta.migrations"
          key  "1"]
      (mc/remove db coll {})
      (is (not (mc/any? db coll {:_id key})))
      (is (not (contains? (applied-migration-ids db) key)))
      (add-migration-id db key)
      (is (mc/any? db coll {:_id key}))
      (is (contains? (applied-migration-ids db) key))))


  (deftest test-remove-migration-id
    (let [db   (mg/get-db "monger-test")
          coll "meta.migrations"
          key  "1"]
      (mc/remove db coll {})
      (add-migration-id db key)
      (is (mc/any? db coll {:_id key}))
      (is (contains? (applied-migration-ids db) key))
      (remove-migration-id db key)
      (is (not (contains? (applied-migration-ids db) key))))))
