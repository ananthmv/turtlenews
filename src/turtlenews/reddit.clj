(ns turtlenews.reddit
  ^{ :author "Anand Muthu"
     :doc "Reddit articles"}
 (:require [org.httpkit.client :as http]
           [clojure.data.json :as json]
           [clojure.tools.logging :as log]))

(def ^:const *subreddit-url* "https://www.reddit.com/%s/top/.json?sort=top&t=week&limit=%d")

(def ^:private options { :timeout 2000
                         :user-agent "turtlenews app"
                         :headers {"X-Accept" "application/json"}
                                  "Content-Type" "application/x-www-form-urlencoded; charset=UTF-8"})

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
  (let [info (into {} (filter #(some? (#{:title :url :created_utc :id :permalink} (first %))) (:data meta)))
        meta_url (str "https://www.reddit.com" (:permalink info))]
       (dissoc (conj info {:meta_url meta_url}) :permalink)))

(defn get-best
  [subreddit max-articles]
  (let [articles (:data (http-get (format *subreddit-url* subreddit max-articles) options))]
       (map #(transform-link %) (get-in articles [:data :children]))))
;; (get-in (:data (http-get (format *subreddit-url* "r/programming" 5) options)) [:data :children])
;; (http-get *hn-best-news-url* {} options)
