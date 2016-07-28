(ns librarian-ng.opds-tags-test
  (:require [clojure.test :refer :all]
            [librarian-ng.opds-tags :refer :all]
            [clojure.zip :as zip]))

(deftest entry-tag
  (testing "empty"
    (let [opds-zip (zip/xml-zip (entry "test title"))]
      (is (= (-> opds-zip (zip/root) :tag) :entry) "entry tag root")
      (is (= (-> opds-zip (zip/children) (count)) 1) "one element")
      (is (= (-> opds-zip (zip/down) (zip/node) :tag) :title) "title element")))
  (testing "nested"
    (let [opds-zip (zip/xml-zip (entry "title 1" (entry "title 2")))]
      (is (= (-> opds-zip (zip/children) (count)) 2) "two elements")
      (is (= (-> opds-zip
                 (zip/down)
                 (zip/rightmost)
                 (zip/node)
                 :tag)
             :entry) "innter element is entry")
      (is (= (-> opds-zip
                 (zip/down)
                 (zip/rightmost)
                 (zip/children)
                 (count))
             1) "inner entry one element"))))
