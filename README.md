Aptitud.webcv
=============

Build projects:
  - cd <path to project>/web-cv-rest 
  - gradle build
  - cd <path to project>/web-cv-app 
  - gradle build

Deploy to heroku (at the moment we are using the war plugin to deploy):
  - Login in to heroku on aptitud account in a terminal.
  - cd <path to root project>
  - Deploy rest service "heroku deploy:war --war web-cv-rest/build/libs/web-cv-rest.war --app aptitudrestcv"
  - Deploy web-app "heroku deploy:war --war web-cv-app/build/libs/web-cv-app.war --app aptitudwebcv"
