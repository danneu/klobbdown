*Far from production-ready*

# klobbdown

A Markdown implementation for my `klobb` static site generator.

* The grammar is here: https://github.com/danneu/klobbdown/blob/master/src/klobbdown/parse.clj

My only other experience with writing grammar is my Ruby [BBCode parser](https://github.com/danneu/bbcode_parser).

## Why?

I didn't see a Clojure Markdown implementation that expressed a grammar and found them hard to understand.

I'd like klobbdown to become a feature complete Markdown implementation with better defaults than Markdown proper.

## Using Instaparse

I use the incredible [Engelberg/instaparse](https://github.com/Engelberg/instaparse) Clojure library to express my grammar.

Instaparse's README convinced me that I could do this.

## Demo

~~~ clojure
(def sample-markup "# A quick demo

- A
- B
- C

## Grocery list

1. Apricots
2. Bananas

--- clojure
(+ 1 2)
---

")
~~~
    
Parses into this klobbdown tree:
    
~~~ clojure
(markup-to-tree sample-markup)

([:heading "#" "A quick demo"]
 [:unordered-list
  [:unordered-item "A"]
  [:unordered-item "B"]
  [:unordered-item "C"]]
 [:heading "##" "Grocery list"]
 [:ordered-list 
  [:ordered-item "Apricots"] 
  [:ordered-item "Bananas"]]
 [:pre-code 
  [:lang "clojure"] 
  [:codetext "(+ 1 2)"]])
~~~

And then into this Hiccup tree:

~~~ clojure
(tree-to-hiccup (markup-to-tree sample-markup))

([:h1 "A quick demo"]
 [:ul 
  [:li "A"] 
  [:li "B"] 
  [:li "C"]]
 [:h2 "Grocery list"]
 [:ol 
  [:li "Apricots"] 
  [:li "Bananas"]]
 [:pre 
  [:code 
   [:codetext "(+ 1 2)"]]])
~~~
       
And then into this HTML tree:

~~~ clojure
(to-html sample-markup)
    
"<h1>A quick demo</h1>
 <ul>
   <li>A</li>
   <li>B</li>
   <li>C</li>
 </ul>
 <h2>Grocery list</h2>
 <ol>
   <li>Apricots</li>
   <li>Bananas</li>
 </ol>
 <pre>
   <code>
     (+ 1 2)
   </code>
 </pre>"
~~~

## License

Copyright © 2013 Dan Neumann

Distributed under the Eclipse Public License, the same as Clojure.
