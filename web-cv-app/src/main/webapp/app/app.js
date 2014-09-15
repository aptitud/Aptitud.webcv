angular.module('configuration', [])
       .constant('API_END_POINT','http://localhost:8080/web-cv-rest/');
var app = angular.module('WebCVApplication', ['configuration']);
