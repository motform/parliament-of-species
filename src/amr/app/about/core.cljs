(ns amr.app.about.core
  (:require [re-frame.core :as rf]))

(defn about []
  [:<> 
   [:section.about.padded 
    [:h1 "About the Parliament"]
    [:p "This is a gamified citizen tool with the aim to create an archive of possible policies based on projections about the future of AMR."]
    [:p "It is a tool accessible to anyone that has access to the internet and wants to contribute to the open science (European paper) archive of what policies could or should be implemented to sustain balance between humans and non-human species."]
    [:p "The outcome of this tool is a platform where citizens co-create policies by building on each other's speculations,  reflections and knowledge."]]])

