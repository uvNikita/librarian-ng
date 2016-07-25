(ns librarian-ng.core
  (:require
    [clojure.xml :as xml]

    [ring.middleware.params :refer [wrap-params]]
    [ring.util.response :refer [content-type response]]

    [compojure.core :refer [defroutes GET context]]

    [clojurewerkz.elastisch.native :as es]

    [librarian-ng.opds-templates :as tmpl]
    [librarian-ng.library :as lib]
    [clojure.string :as str])
  )

(def es-conn (es/connect [["127.0.0.1" 9300]] {"cluster.name" "librarian"}))

(defn echo-response [& params]
  {:headers {"Content-Type" "text/plain;charset=utf-8"}
   :body    (str/join "\n" params)})


(def opensearch
  {:tag     :OpenSearchDescription
   :attrs   {:xmlns "http://a9.com/-/spec/opensearch/1.1/"}
   :content [{:tag   :Url
              :attrs {:type     "application/atom+xml"
                      :template "/search?searchTerm={searchTerms}"}}]})

(defroutes
  opds
  (GET "/" [] (response (tmpl/index)))

  (GET "/search" [searchTerm]
    (response (tmpl/search searchTerm)))
  (context "/search" []
    (GET "/author/:author" [author]
      (echo-response author))
    (GET "/book/:book" [book]
      (->> book (lib/search-book es-conn) (tmpl/books-results) (response)))
    (GET "/seq/:seq" [seq]
      (echo-response seq)))

  (GET "/seq/:seq" [author seq]
    (str author " " seq))
  (GET "/book/:book-id" [book-id]
    (str book-id))

  (context "/author/:author" [author]
    (GET "/" [] (echo-response author))
    (GET "/seqs" [] (str author "seqs"))
    (GET "/noseq" [] (str author "noseq")))
  #_(GET "/opensearch.xml" [] (response opensearch)))

(defn log-resp
  [handler]
  (fn [req]
    (if-let [resp (handler req)]
      (do (prn resp)
          resp))))

(defn wrap-xml-response [handler]
  (fn [req]
    (let [response (handler req)
          body (:body response)]
      (if (and (coll? body) (contains? body :tag))
        (-> response
            (content-type "text/xml;charset=utf-8")
            (update-in [:body] #(-> % xml/emit with-out-str)))
        response))))

(def app
  (-> opds
      wrap-xml-response
      log-resp
      wrap-params))
