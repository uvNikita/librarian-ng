(ns librarian-ng.opds-tags)


(defmacro title [title-str]
  {:tag :title :content [title-str]})

(defmacro content [content-str]
  {:tag :content :attrs {:type "text"} :content [content-str]})

(defmacro link [href & {:keys [type] :or {type "application/atom+xml"}}]
  {:tag :link :attrs {:href href
                      :type type}})

(defmacro entry [title-str & entries]
  {:tag     :entry
   :content (into [(title title-str)] entries)})

(defmacro author [name uri]
  {:tag :author
    :content [{:tag :name
               :content [name]}
              {:tag :uri
               :content [uri]}]})

(defmacro defn-feed [name args & body]
  `(defn ~name ~args
     {:tag     :feed
      :attrs   {:xmlns "http://www.w3.org/2005/Atom"}
      :content (into [] (flatten [~@body]))}))
