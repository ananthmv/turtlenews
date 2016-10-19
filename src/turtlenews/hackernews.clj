(ns turtlenews.hackernews
  ^{ :author "Anand Muthu"
     :doc "Hackernews best articles"}
 (:require [org.httpkit.client :as http]
           [clojure.data.json :as json]
           [clojure.tools.logging :as log]))

(def ^:const MAX-ITEM-HN-NEWS 5)
(def ^:const hn-best-news-url "https://hacker-news.firebaseio.com/v0/beststories.json")
(def ^:const hn-news-details-url "https://hacker-news.firebaseio.com/v0/item/%s/.json")
(def ^:const hn-meta-url "https://news.ycombinator.com/item?id=%s")

(def ^:private options { :timeout 2000
                         :user-agent "turtlenews app"
                         :headers { "X-Accept" "application/json"
                                    "Content-Type" "application/x-www-form-urlencoded; charset=UTF-8"}})

(defn- http-get
  [url options]
  (let [ data options
         {:keys [error status body headers]} @(http/get url data)]
    { :error error
      :headers headers
      :status status
      :body body
      :data (json/read-str body :key-fn keyword)}))

(defn transform-link
  [meta]
  (let [info (into {} (filter #(some? (#{:title :url :time :id} (first %))) (:data meta)))
        hn_url (format hn-meta-url (:id info))]
       (conj info {:meta_url hn_url :url (or (:url info) hn_url)})))

(defn get-best
  [max-links]
  (let [news (:data (http-get hn-best-news-url options))]
       (map (fn [item-id]
              (let [resp (http-get (format hn-news-details-url item-id) options)]
                   (transform-link resp)))
            (take max-links news))))

;; (http-get hn-best-news-url {} options)
