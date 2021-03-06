(ns clj-esearch.test.core
  (:use [clj-esearch.core]
        [lamina.core :only [wait-for-result]]
        [clojure.test]))

(def test-server "http://127.0.0.1:9200")
(def test-index "titems")
(def test-type "titem")

(defrecord Doc [title posted content])
(def test-doc (Doc. "foo" 12345 "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."))


(use-fixtures :each (fn [atest]
                      @(request {:method :delete
                                 :url (url test-server test-index test-type)})
                      @(request {:method :post
                                 :url (url test-server test-index "_flush")})
                      @(request {:method :post
                                 :url (url test-server "_cache" "clear")})
                      (atest)))

(deftest url-generation-test
  (is (= "http://127.0.0.1:9200/a/b/c") (url test-server "a" "b" "c"))
  (is (= "http://127.0.0.1:9200/a/b/c") (url test-server "a" :b  :c))
  (is (= "http://127.0.0.1:9200/a/b,c") (url test-server "a" ["b" "c"]))
  (is (= "http://127.0.0.1:9200/a/b,c") (url test-server "a" [:b :c]))
  (is (= "http://127.0.0.1:9200/a,b/c") (url test-server ["a" "b"] "c"))
  (is (= "http://127.0.0.1:9200/_all/b/c") (url test-server :_all "b" "c")))

(deftest add-doc-test
  (let [response @(add-doc test-server test-index test-type test-doc)]
    (is (= 201 (:status response)))))

(deftest get-doc-test
  (add-doc test-server
           test-index
           test-type
           test-doc
           :id 1)
  (Thread/sleep 1000)
  (let [response @(get-doc test-server test-index test-type 1)]
    (is (= 200 (:status response)))))

(deftest delete-test
  (let [doc @(add-doc test-server
                      test-index
                      test-type
                      test-doc
                      :id 3)]
    (is (=  200 (:status @(delete-doc test-server test-index test-type 3))))))

(deftest search-doc-test
  (dotimes [i 3]
    @(add-doc test-server
              test-index
              test-type
              test-doc))
  (Thread/sleep 1000)
  (let [response @(search-doc test-server
                              {:query {:term {:title "foo"}}}
                              :index :_all)]
    (is (= 200 (:status response)))
    (is (= 3 (-> response :body :hits :hits count)))))

(deftest percolate-test
  (is (= 200 (:status @(percolate test-server
                                  test-index
                                  "perc-test"
                                  {:query {:term {:field1 "value1" }}})))))

(deftest bulk-test
  (is (= 200 (:status @(bulk test-server
                             [{:index {:_index "test-index" :_type "test-type" :_id "foo"}}
                              {:foo "bar" :lorem "ipsum"}])))))
