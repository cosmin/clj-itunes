(ns clj-itunes.client-test
  (:use clojure.test)
  (:require [clj-itunes.client :as client]
            [clj-http.client :as http]))

(deftest test-normalize-explicit
  (let [normalize-explicit #'client/normalize-explicit]
    (testing "Normalize booleans"
      (is (= (normalize-explicit {:explicit true})
             {:explicit "Yes"}))
      (is (= (normalize-explicit {:explicit false})
             {:explicit "No"})))
    (testing "Leave strings alone when normalizing"
      (is (= (normalize-explicit {:explicit "Yes"})
             {:explicit "Yes"}))
      (is (= (normalize-explicit {:explicit "No"})
             {:explicit "No"})))))

(deftest test-stringify
  (let [stringify #'client/stringify]
    (testing "Handles numbers, strings and keywords"
      (is (= "1" (stringify 1)))
      (is (= "key" (stringify :key)))
      (is (= "foo" (stringify "foo"))))))

(deftest test-params-to-query
  (let [stringify-params #'client/stringify-params]
    (testing "Handles empty query"
      (is (= {} (stringify-params {}))))
    (testing "Handles one key and value"
      (is (= {:key "value"} (stringify-params {:key "value"})))
      (is (= {"n" "1"} (stringify-params {"n" 1}))))
    (testing "Handles multiple keys and values"
      (let [result (stringify-params {:a "b", "c" :d})]
        (is (= {:a "b" "c" "d"} result))))))

(deftest test-search
  (with-redefs [http/get (fn [url & [req]] [url req])]
    (testing "Handles no params"
      (let [[url req] (client/search "Foo")]
        (is (= "http://itunes.apple.com/search" url))
        (is {:term "Foo"} (:query-params req))))
    (testing "Handles params"
      (let [[url req] (client/search "Foo" {:media :ebook})]
        (is (= "http://itunes.apple.com/search" url)
            (= {:term "Foo" :media "ebook"} (:query-params req)))))))

(deftest test-lookup
  (with-redefs [http/get (fn [url & [req]] [url req])]
    (testing "Handles no params"
      (let [[url req] (client/lookup :id "123")]
        (is (= "http://itunes.apple.com/lookup" url))
        (is (= {:id "123"} (:query-params req)))))
    (testing "Handles params"
      (let [[url req] (client/lookup :amgArtistId 543 {:entity :song})]
        (is (= "http://itunes.apple.com/lookup" url))
        (is (= {:amgArtistId "543" :entity "song"} (:query-params req)))))))
