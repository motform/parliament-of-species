(ns amr.app.footer)

(defn footer []
  [:footer
   [:div
    [:p "The Parliament of Species is a gamified citizen tool that aims to create an archive of possible policies based on scientific projections about the future of AMR. It enables citizens to co-create policies by building upon each other’s speculations, reflections and knowledge."]]
   [:div
    [:p "This is a student project form the course " [:em "Design and Social Innovation"] ", a part of the " [:em "Interaction Design"] " Masters Programme at Malmö University."]]
   [:div]
   [:div]
   [:div]
   [:div
    [:p "If you have any thoughts or questions, " [:a {:href "mailto:admin@parliamentofspecies.com"} "email us!"]]
    [:p "The project distributed as free and open source, available " [:a {:href "https://github.com/motform/parliament-of-species"} " here."]]
    [:p "© 2020"]]])
