(ns iclojure.core
  (:require [goog.dom :as gd]))

;;;; TODO:  use Om, castrocauda, or alike

(def KEY-ENTER 13)
(def KEY-BACKSPACE 8)

(def app
  (atom [{:mode    "title"
          :content "Welcome to iClojure Notebook"}
         {:mode    "subtitle"
          :content "Persistent REPL for Clojure"}
         {:mode    "paragraph"
          :content "@ympbyc, @SirSkidmore, @mschmornoff, and @athos0220 started hacking on this project when we met at Clojure/conj 2014. Right now the development is focused on the frontend and it's far from complete."}
         {:mode    "clojure"
          :content "(defn fact [n] (if (= n 0) 1 (* n (fact (dec n)))))"}]))

#_(add-watch app
           (fn [k a app old new]
             (.setItem js/localStorage "iclojure-state"
                       (prn-str new))))

(def mode->node
  {"title"     "h1"
   "clojure"   "pre"
   "subtitle"  "h2"
   "paragraph" "p"})

(defn render-notes [notes]
  (let [note (gd/getElement "note")]
    (set! (.-innerHTML note) "")    ;;clear
    (doseq [{:keys [mode content]} notes]
      (let [node (gd/createElement (mode->node mode))]
        (set! (.-className node) "cell-done")
        (set! (.-innerHTML node) content)
        (gd/append note node)))))

(defn setup []
  (let [cell     (gd/getElement "cell")
        mode-sel (gd/getElement "mode-selection")
        export-edn (gd/getElement "export-edn")]

    (.addEventListener cell "keypress"
                       (fn [e]
                         (cond
                          (= (.-keyCode e) KEY-ENTER)
                          (let [mode (.-value mode-sel)
                                content (.-value cell)]
                            (swap! app conj
                                   {:mode mode
                                    :content (if (= mode "clojure")
                                               (str content "    ;;=> &lt;RESULT&gt;")
                                               content)})
                            (set! (.-value cell) ""))

                          (and (= (.-keyCode e) KEY-BACKSPACE)
                               (= (.-value cell) ""))
                          (swap! app pop))
                         (render-notes @app)))

    (.addEventListener export-edn "click"
                       (fn [e]
                         (swap! app conj
                                {:mode "clojure"
                                 :content (prn-str @app)})
                         (render-notes @app)))

    (.focus cell))
  (render-notes @app))

(.addEventListener js/window "load" setup)
