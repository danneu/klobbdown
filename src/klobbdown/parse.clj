(ns klobbdown.parse
  (:require [instaparse.core :as insta]
            [hiccup.core :as hiccup]))


(def markup-to-tree
  (insta/parser
   "<root> = (heading |
              unordered-list |
              ordered-list |
              pre-code |
              inline-code |
              anchor |
              image |
              paragraph
             )+

    heading = #'[#]+' <space> #'[a-zA-Z0-9 ]+' <blankline>?

    paragraph = (inline-code |
                 anchor |
                 strong |
                 emphasis |
                 paragraph-text
                )+ <#'\n\n'>
    <paragraph-text> = #'[^`#*\n{2}]+'

    strong = <'**'> strong-text <'**'> 
    <strong-text> = #'[^\\*\\*]+'
    emphasis =  <'*'> emphasis-text <'*'>
    <emphasis-text> = #'[^\\*]+'

    unordered-list = unordered-item+ <blankline>
    unordered-item = <'- '> #'[a-zA-Z ]+' <newline>?

    ordered-list = ordered-item+ <blankline>
    ordered-item = <ol-item-token> #'[a-zA-Z0-9 ]+' <newline>?
    ol-item-token = #'[0-9]+\\. '

    inline-code = <'`'> #'[^`]+' <'`'>

    pre-code = <'~~~'> lang? <newline>
               codetext
               <'\n~~~'> <blankline>
    lang = <' '> #'[a-zA-Z]+'
    codetext = #'[^\\n~~~]+'

    anchor = auto-anchor | braced-anchor
    <auto-anchor> = <'<'> url <'>'>
    <braced-anchor> = <'['> text <']'> <'('> url <')'>
    <text> = #'[^]]+'
    <url> = #'[^>)]+'

    image = <'!'>
            <'['> alt <']'>
            <'('> path title? <')'>
    <alt> = #'[^]]+'
    <path> = #'[^) ]+'
    <title> = <spaces> #'[^)]+'

    spaces = space+
    space = ' '
    blankline = #'\n\n'
    newline = #'\n'
    "))


;; Transformers ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; [:anchor "example.com"]
;; [:anchor "Click me" "example.com" ]
(defn transform-anchor
  ([url] [:a {:href url} url])
  ([text url] [:a {:href url} text]))

;; [:emphasis "lol"]
(defn transform-emphasis
  [text]
  [:em text])

;; [:strong "lol"]
(defn transform-strong
  [text]
  [:strong text])

;; [:pre-code "(+ 1 2)"]
;; [:pre-code "clojure" "(+ 1 2)"]
(defn transform-pre-code
  ([text] [:pre [:code text]])
  ([lang text] [:pre [:code text]]))

(defn transform-inline-code
  [text]
  [:code text])

;; [:image alt path]
;; [:image alt path title]
(defn transform-image
  ([alt path] [:img {:src path :alt alt}])
  ([alt path title] [:img {:src path :alt alt :title title}]))

(defn transform-unordered-item
  [item]
  [:li item])

(defn transform-unordered-list
  [& items]
  (into [:ul] items))

(defn transform-ordered-item
  [item]
  [:li item])

(defn transform-ordered-list
  [& items]
  (into [:ol] items))

(defn transform-paragraph
  [& items]
  (into [:p] items))

(defn transform-heading
  [octothorpes text]
  (let [level (count octothorpes)
        tag (keyword (str "h" level))]
    [tag text]))


;; Usage ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn tree-to-hiccup
  [tree]
  (let [transformations {:anchor transform-anchor
                         :emphasis transform-emphasis
                         :strong transform-strong
                         :image transform-image
                         :pre-code transform-pre-code
                         :inline-code transform-inline-code
                         :unordered-item transform-unordered-item
                         :unordered-list transform-unordered-list
                         :ordered-item transform-ordered-item
                         :ordered-list transform-ordered-list
                         :heading transform-heading
                         :paragraph transform-paragraph}]
    (insta/transform transformations tree)))

(defn to-html
  "Parses markup into HTML."
  [markup]
  (hiccup/html (tree-to-hiccup (markup-to-tree markup))))
