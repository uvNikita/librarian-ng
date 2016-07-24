(ns librarian-ng.opds-templates
  (:require
    [librarian-ng.opds-tags :as opds]))

(defmacro defn-opds-feed [name args & body]
  `(defn ~name ~args
     {:tag     :feed
      :attrs   {:xmlns "http://www.w3.org/2005/Atom"}
      :content [~@body]}))


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
    (opds/content "test content"))
  )


(defn-opds-feed
  search
  [search-term]
  (opds/entry
    "Search by author"
    (opds/link (str "/search/author/" search-term)))
  (opds/entry
    "Search by book"
    (opds/link (str "/search/book/" search-term)))
  (opds/entry
    "Search by sequence"
    (opds/link (str "/search/seq/" search-term))))

