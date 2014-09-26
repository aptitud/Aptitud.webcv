angular.module('configuration', [])
       .constant('API_END_POINT','http://afternoon-river-5325.herokuapp.com')
       .constant('CLIENT_ID','57103926862-ktsb4791lhv5fp42tista6jhocosc3rg.apps.googleusercontent.com');
var app = angular.module('WebCVApplication', ['configuration', 'ngRoute']);

app.config(['$routeProvider', function($routeProvider) {
	$routeProvider.
		when('/home', {
           templateUrl: 'home.html',
       }).
       when('/login', {
           templateUrl: 'login.html',
       }).
       otherwise({
           redirectTo: '/login'
       });
}]);
app.run( function($rootScope, $location) {
    $rootScope.$on( "$routeChangeStart", function(event, next, current) {
    	console.log(sessionStorage.signedIn);
      if (!sessionStorage.signedIn) {
    	  $location.path( "/login" );
      }         
    });
});