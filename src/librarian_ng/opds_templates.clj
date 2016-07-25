(ns librarian-ng.opds-templates
  (:require
    [ring.util.codec :refer [url-encode]]
    [librarian-ng.opds-tags :as opds]
    [clojure.string :as str]))

(defmacro defn-opds-feed [name args & body]
  `(defn ~name ~args
     {:tag     :feed
      :attrs   {:xmlns "http://www.w3.org/2005/Atom"}
      :content (into [] (flatten [~@body]))}))


(defn-opds-feed
  index
  []
  (opds/title "Librarian")
  #_{:tag :link :attrs {:href "/opensearch.xml"
                        :rel  "search"
                        :type "application/opensearchdescription+xml"}}

  {:tag :link :attrs {:href "/search?searchTerm={searchTerms}"
                      :rel  "search"
                      :type "application/atom+xml"}}
  (opds/entry
    "Test entry"
    (opds/link "/")
    (opds/content "test content")))


(defn-opds-feed
  search
  [search-term]
  (opds/entry
    "Search by author"
    (opds/link (str "/search/author/" (url-encode search-term))))
  (opds/entry
    "Search by book"
    (opds/link (str "/search/book/" (url-encode search-term))))
  (opds/entry
    "Search by sequence"
    (opds/link (str "/search/seq/" (url-encode search-term)))))

(defn author-entry [name]
  (opds/author
    name
    (str "/author/" (url-encode name))))

(defn book-entry [{:keys [id title authors]}]
  (opds/entry
    title
    (map author-entry authors)
    (opds/link (str "/book/" id) :type "application/fb2+zip")))

(defn-opds-feed
  books-results
  [books]
  (map book-entry books))