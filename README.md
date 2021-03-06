# clj-esearch

Clojure REST client for [Elastic Search](http://www.elasticsearch.org/)

Uses [Aleph](https://github.com/ztellman/aleph) HTTP client under the hood.

The query map corresponds to the JSON query DSL, allowing you to
leverage the full features of elastic search
[Elastic Search Query DSL ref](http://www.elasticsearch.org/guide/reference/query-dsl/)

## Usage


```clojure
(use 'clj-esearch.core)

;; returns a Lamina result channel

(add-doc "http://127.0.0.1:9200"
         "tweets"
         "tweet"
         {:text "foo bar" :author {:name "john"} :posted 123450000000})


(add-doc "http://127.0.0.1:9200"
         "tweets"
         "tweet"
         {:text "foo bar" :author {:name "john"} :posted 123450000000}
         :id 1) ;; specified id


;; If you need the query to block/wait for the response dereference it

@(add-doc ...)

;; Error handling can be done using lamina utilities (same is true for timeouts)

(lamina.core/run-pipeline
  (add-doc "http://127.0.0.1:9200"
           "tweets"
           "tweet"
           {:text "foo bar" :author {:name "john"} :posted 123450000000})
  :error-handler (fn [e] ...)
  #(when (> (:status %) 201) (throw (Exception. "Not good"))))

```

See [tests](https://github.com/mpenet/clj-esearch/blob/master/test/clj_esearch/test/core.clj) for more details.

[Lamina](https://github.com/ztellman/lamina) [Result Channel](https://github.com/ztellman/lamina/wiki/Result-Channels)
[Aleph](https://github.com/ztellman/aleph)


## Installation

clj-esearch is available as a Maven artifact from [Clojars](http://clojars.org/clj-esearch):

    :dependencies
      [[clj-esearch "0.4.0"] ...]

## License

Distributed under the Eclipse Public License, the same as Clojure.
