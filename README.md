# clj-itunes

A Clojure library for querying the iTunes search and lookup APIs (affiliate)

## Releases and Dependency Information

Latest stable release is `0.1.0`

For leiningen

```clojure
[com.offbytwo/clj-itunes "0.1.0"]
```

For Maven

```xml
<dependency>
  <groupId>com.offbytwo</groupId>
  <artifactId>clj-itunes</groupId>
  <version>0.1.0</version>
</dependency>
```

## Usage

Example

```clojure
(require '[clj-itunes.client :as itunes-client])
```

To search

```clojure
(itunes-client/search "Clojure" {:media :ebook})
```

To lookup a specific item

```clojure
(itunes-client/lookup :id 284910350)
```

## License

Copyright © 2012 Cosmin Stejerean

Distributed under the Eclipse Public License, the same as Clojure.
