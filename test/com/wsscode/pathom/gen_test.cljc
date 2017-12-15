(ns com.wsscode.pathom.gen-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [com.wsscode.pathom.gen :as sgen]
            [fulcro.client.primitives :as fp]))

(s/def ::coll (s/coll-of int?))
(s/def ::not-coll int?)
(s/def ::fixed-number #{42})
(s/def ::fixed-str #{"bla"})

(fp/defsc Component [_ _ _]
  {:ident (fn [] [:fixed "here"])
   :query [::fixed-number ::fixed-str]}
  (identity nil))

(deftest test-coll-spec?
  (is (true? (sgen/coll-spec? ::coll)))
  (is (false? (sgen/coll-spec? ::not-coll)))
  (is (false? (sgen/coll-spec? ::invalid))))

(deftest test-query->props
  (is (= (sgen/query->props [::fixed-number ::fixed-str ::undefined])
         {::fixed-number 42 ::fixed-str "bla"}))

  (is (= (sgen/query->props {::sgen/settings {::number-list {::sgen/coll 10}}}
           [{::number-list [::fixed-number]}])
         {::number-list (repeat 10 {::fixed-number 42})}))

  (is (= (sgen/query->props {::sgen/settings {::fixed-number {::sgen/gen (s/gen #{43})}}}
           [::fixed-number])
         {::fixed-number 43}))

  (is (= (sgen/query->props [[::fixed-number '_]])
         {::fixed-number 42})))

(deftest test-comp->props
  (is (= (sgen/comp->props Component)
         {::fixed-number 42 ::fixed-str "bla"})))

(deftest test-comp->db
  (is (= (sgen/comp->db Component)
         {::fixed-number 42 ::fixed-str "bla"})))
