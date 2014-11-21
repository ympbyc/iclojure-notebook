(ns iclojure.core
  (:require [goog.dom :as gd]))

(def KEY-ENTER 13)
(def KEY-BACKSPACE 8)

(defn setup []
  (let [cell     (gd/getElement "cell")
        mode-sel (gd/getElement "mode-selection")
        note     (gd/getElement "note")]

    (.addEventListener cell "keypress"
                       (fn [e]
                         (cond
                          (= (.-keyCode e) KEY-ENTER)
                          (let [span (condp = (.-value mode-sel)
                                       "title"     (gd/createElement "h1")
                                       "subtitle"  (gd/createElement "h2")
                                       "paragraph" (gd/createElement "p")
                                       "clojure"   (gd/createElement "pre"))]
                            (set! (.-innerHTML span) (.-value cell))
                            (gd/appendChild note span)
                            (set! (.-value cell) ""))

                          (and (= (.-keyCode e) KEY-BACKSPACE)
                               (= (.-value cell) ""))
                          (gd/removeNode (gd/getLastElementChild note)))))))

(.addEventListener js/window "load" setup)
