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
  (let [params-to-query #'client/params-to-query]
    (testing "Handles empty query"
      (is (= "" (params-to-query {}))))
    (testing "Handles one key and value"
      (is (= "key=value" (params-to-query {:key "value"})))
      (is (= "n=1" (params-to-query {"n" 1}))))
    (testing "Handles multiple keys and values"
      (let [result (params-to-query {:a "b", "c" :d})]
        (is (or (= "a=b&c=d" result)
                (= "c=d&a=b" result)))))))

(deftest test-search
  (with-redefs [http/get (fn [url & [req]] [url req])]
    (testing "Handles no params"
      (let [[url _] (client/search "Foo")]
        (is (= "http://itunes.apple.com/search?term=Foo" url))))
    (testing "Handles params"
      (let [[url _] (client/search "Foo" {:media :ebook})]
        (is (or (= "http://itunes.apple.com/search?term=Foo&media=ebook")
                (= "http://itunes.apple.com/search?media=ebook&term=Foo")))))))

(deftest test-lookup
  (with-redefs [http/get (fn [url & [req]] [url req])]
    (testing "Handles no params"
      (let [[url _] (client/lookup :id "123")]
        (is (= "http://itunes.apple.com/lookup?id=123" url))))
    (testing "Handles params"
      (let [[url _] (client/lookup :amgArtistId 543 {:entity :song})]
        (is (or (= "http://itunes.apple.com/lookup?amgArtistId=543&entity=song")
                (= "http://itunes.apple.com/search?entity=song&amgArtistId=543")))))))