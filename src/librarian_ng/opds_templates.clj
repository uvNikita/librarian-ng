(ns librarian-ng.opds-templates
  (:require
    [ring.util.codec :refer [url-encode]]
    [librarian-ng.opds-tags :as opds]))

(opds/defn-feed
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


(opds/defn-feed
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

(opds/defn-feed
  books-results
  [books]
  (map book-entry books))