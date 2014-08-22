DOC:
- allow custom imports
-- adde default imports such as javautil


Eclipse:
> automatically add popupmenu to add Panettone Nature to a project
- create your update site
- allow custom version of panettone jar (use the project's own file, if found)


pANETTONE:
- make panettone support .tone.*
- add docs to panettone


 

LATER
> if there is an error in your template, show it there
> use USER provided vraptor-panettone. run the panettone based on the user version and not our own
<% for(News news : newses) use(news.class).render(news); %> ====> show(news) newses.forEach ? <% newses.forEach(use(news.class)::render); %>