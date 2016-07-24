(ns librarian-ng.library
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str]

    [clojurewerkz.elastisch.rest :as es]
    [clojurewerkz.elastisch.rest.document :as esd]

    [me.raynes.fs :refer [extension]]
    )
  (:import
    [java.util.zip ZipFile]
    )
  )


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
  (esd/create conn "test" "book" book))

(defn add-inpx! [conn filename]
  (with-open [inpx (ZipFile. filename)]
    (doseq [e (enumeration-seq (.entries inpx))
            :when (->> e (.getName) (extension) (= ".inp"))]
      (let [books (parse-inp (.getInputStream inpx e))]
        (doall (map #(add-book! conn %) books))))))


