(ns clj-itunes.client
  (:require [clj-http.client :as http]
            [clojure.string :as string]))

(def ^:dynamic *api-base-url* "http://itunes.apple.com")

(def +entities-for-media+
  {:movie      [:movieArtist, :movie]
   :podcast    [:podcastAuthor, :podcast]
   :music      [:musicArtist, :musicTrack, :album,
                :musicVideo, :mix, :song]
   :musicVideo [:musicArtist :musicVideo]
   :audiobook  [:audiobookAuthor :audiobook]
   :shortFilm  [:shortFilmArtist :shortFilm]
   :tvShow     [:tvEpisode :tvSeason]
   :software   [:software :iPadSoftware :macSoftware]
   :ebook      [:ebook]
   :all        [:movie :album :allArtist :podcast :musicVideo
                :mix :audiobook :tvSeason :allTrack]})

(def +attributes-for-media+
  {:movie      [:actorTerm :genreIndex :artistTerm :shortFilmTerm,
                :producerTerm :ratingTerm :directorTerm :releaseYearTerm,
                :featureFilmTerm :movieArtistTerm :movieTerm :ratingIndex,
                :descriptionTerm]
   :podcast    [:titleTerm :languageTerm :authorTerm :genreIndex :artistTerm,
                :ratingIndex :keywordsTerm :descriptionTerm]
   :music      [:mixTerm :genreIndex :artistTerm :composerTerm :albumTerm,
                :ratingIndex :songTerm :musicTrackTerm]
   :musicVideo [:genreIndex :artistTerm :albumTerm :ratingIndex :songTerm]
   :audiobook  [:titleTerm :authorTerm :genreIndex :ratingIndex]
   :shortFilm  [:genreIndex :artistTerm :shortFilmTerm :ratingIndex,
                :descriptionTerm]
   :software   [:softwareDeveloper]
   :tvShow     [:genreIndex :tvEpisodeTerm :showTerm :tvSeasonTerm,
                :ratingIndex :descriptionTerm]
   :all        [:actorTerm :languageTerm :allArtistTerm :tvEpisodeTerm,
                :shortFilmTerm :directorTerm :releaseYearTerm :titleTerm,
                :featureFilmTerm :ratingIndex :keywordsTerm :descriptionTerm,
                :authorTerm :genreIndex :mixTerm :allTrackTerm :artistTerm,
                :composerTerm :tvSeasonTerm :producerTerm :ratingTerm,
                :songTerm :movieArtistTerm :showTerm :movieTerm :albumTerm]})

(def +media-formats+ (keys +entities-for-media+))

(defn- normalize-explicit [params]
  (if (or (not (contains? params :explicit))
          (string? (:explicit params)))
    params
    (assoc params :explicit (if (:explicit params) "Yes" "No"))))

(defn- stringify [val]
  (cond
   (string? val) val
   (keyword? val) (name val)
   :default (str val)))

(defn- stringify-params
  [params]
  (into {} (map (fn [[k v]] [k (stringify v)])) params))

(defn search
  "Search the iTunes store given a search term and optional parameters.

   Supported parameters are:
     * country - 2 letter country code (default: US)
     * media - media to search for, see +media-formats+. (default: all)
     * entity - type of results, relative to media. see +entities-for-media+
     * attribute - attribute to search for. see +attributes-for-media+ (default: all)
     * limit - number of search results, 1-200. (default: 50)
     * lang - language, using 5 letter code name (default: en_us)
     * version - search result key version, 1 or 2. (default: 2)
     * explicit - whether or not you want explicit results. (default: true)

  See the full docs at http://bit.ly/bGaJt4"
  ([term] (search term {}))
  ([term params]
   (let [params     (assoc params :term term)
         search-url (str *api-base-url* "/search")]
     (http/get search-url {:as          :json
                           :query-params (stringify-params (normalize-explicit params))}))))

(defn lookup
  "Lookup an item in the iTunes store by iTunes IDs, UPC/EAN, and AMG IDs

  * key is one of id, amgArtistId, amgAlbumId, upc, eacn, isbn
  * value is the corresponding value for the key
  * params allows you to further refine the query

  Supported parameters are:
     * country - 2 letter country code (default: US)
     * entity - type of results see +entities-for-media+
     * limit - number of results, 1-200. (default: 50)
     * sort - sort the results (recent seems to work, not sure what else)

  See the full examples at http://bit.ly/KrqhwH"
  ([key value] (lookup key value {}))
  ([key value params]
   (let [lookup-url (str *api-base-url* "/lookup")]
     (http/get lookup-url {:as           :json
                           :query-params (stringify-params (assoc params key value))}))))
