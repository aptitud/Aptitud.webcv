angular.module('configuration', [])
       .constant('API_END_POINT','8080')
       .constant('HOST','localhost');
var app = angular.module('WebCVApplication', ['configuration']);
