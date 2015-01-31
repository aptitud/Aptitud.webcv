Aptitud.web-cv
=============

The main deployable to heroku is a war file, with both front and- back-end, which is fired up with jetty-runner (downloading it is part of the
gradle build script). You can start it locally in any war, I used jetty. The front end is decoupled from the backend and
could be hosted separately, and to ease front end development one can use 'grunt server' in the applications root directory
to start a watch/auto reload server, run 'grunt server', use http://localhost:8080/?local=true in any browser for development
to run the frontend from the grunt server with auto reload on any file change.


Build projects:
  - gradle build

Deploy to heroku
  - Production in on account cloud@aptitud.se, so get the key put in gradle.properties
  - Production db settings is set by heroku config vars. Don't use the production db for development.
    For example use the hardcoded one in DatabaseConfig for development
  - Then run: 'gradle herokuAppDeploy' (one can also just use the git push to heroku, the plugin does the same in this case)
  - and watch the magic
  - run: 'heroku logs' and watch for any errors and when both nodes are running after deploy
  - check for any error: https://aptitudcvonline.herokuapp.com/


