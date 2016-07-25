(ns librarian-ng.opds-tags)


(defmacro title [title-str]
  {:tag :title :content [title-str]})

(defmacro content [content-str]
  {:tag :content :attrs {:type "text"} :content [content-str]})


(defmacro link [href & {:keys [type] :or {type "application/atom+xml"}}]
  {:tag :link :attrs {:href href
                      :type type}})

(defmacro entry [title-str & entries]
  `{:tag     :entry
    :content [~(title title-str)
              ~@entries]})