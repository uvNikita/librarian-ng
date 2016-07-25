(ns librarian-ng.library
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str]

    [clojurewerkz.elastisch.rest.document :as esd]
    [clojurewerkz.elastisch.query :as q]
    [clojurewerkz.elastisch.rest.response :as esrsp]

    [clojurewerkz.elastisch.rest.index :as esi]
    [me.raynes.fs :refer [extension]])
  (:import
    [java.util.zip ZipFile]
    )
  )

(defrecord Book [id title authors genres seq-title seq-num])

(def mappings
  {"book" {:properties {:title      {:type "string"}
                        :authors    {:type "string"}
                        :annotation {:type "string"}
                        :id         {:type "integer"}
                        :genres     {:type "string"}
                        :seq-title  {:type "string"}
                        :seq-num    {:type "integer"}}}})



(defn init-db [conn]
  (esi/create conn "librarian" :mappings mappings))

(defn parse-inp-entry [entry]
  (let [[authors-raw
         genres
         title
         seq-title
         seq-num
         id
         _ _ _ _
         date
         lang
         _] (str/split entry #"\u0004")
        parse-author #(-> % (str/replace #"," " ") (str/trim))
        authors (into [] (map parse-author (str/split authors-raw #":")))]
    {:authors   authors
     :genres    (str/split genres #":")
     :title     title
     :seq-title seq-title
     :seq-num   (try (Integer. seq-num) (catch Exception _ nil))
     :id        (Integer. id)}))

(defn parse-inp [inp]
  (map parse-inp-entry (line-seq (io/reader inp))))

(defn add-book! [conn book]
  (esd/create conn "librarian" "book" book))

(defn add-inpx! [conn filename]
  (with-open [inpx (ZipFile. filename)]
    (doseq [e (enumeration-seq (.entries inpx))
            :when (->> e (.getName) (extension) (= ".inp"))]
      (let [books (parse-inp (.getInputStream inpx e))]
        (doall (map #(add-book! conn %) books))))))

(defn search-book [conn search-term]
  (let [res (esd/search conn "librarian" "book" :query (q/match :title search-term))]
    (reverse (map #(-> % :_source map->Book)
                  (sort-by :_score (esrsp/hits-from res))))))
