(ns amr.app.footer)

;; NOTE the footer is a six column grid, in which items are _manually_ placed
(defn footer []
  [:footer
   [:div {:style {:grid-column "1"}}
    [:p [:strong "The Parliament of Species"] " is a gamified citizen tool that aims to create an archive of possible policies based on scientific projections about the future of antibiotic-resistant bacteria. It enables citizens to co-create policies by building upon each other’s speculations, reflections and knowledge."]]
   [:div {:style {:grid-column "2"}}
    [:p "This is a student project form the course " [:em "Design and Social Innovation"] ", part of the " [:em "Interaction Design Masters Programme"] " at " [:strong "Malmö University."]]]
   [:div.col {:style {:grid-column "6"}}
    [:p "If you have any thoughts or questions, " [:a {:href "mailto:admin@parliamentofspecies.com"} "email us!"]]
    [:p "The project source distributed as free and open source, available " [:a {:target "_blank" :href "https://github.com/motform/parliament-of-species"} "on Github"] " under GPL-3.0."]
    [:p {:style {:margin-top "auto"}} "The content on this website is livened under " [:a {:target "_blank" :href "https://creativecommons.org/licenses/by-nc-sa/4.0/"} "CC BY-NC-SA 4.0"]]]])
