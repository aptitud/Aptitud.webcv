angular.module('configuration', ['ui.select'])
       //.constant('API_END_POINT','http://afternoon-river-5325.herokuapp.com')
	   .constant('API_END_POINT','http://localhost:8000')
       .constant('CLIENT_ID','57103926862-ktsb4791lhv5fp42tista6jhocosc3rg.apps.googleusercontent.com');
var app = angular.module('WebCVApplication', ['configuration', 'ngRoute']);

app.config(['$routeProvider', function($routeProvider) {
	$routeProvider.
		when('/home', {
           templateUrl: 'home.html'
       }).
		when('/edit/:id', {
			templateUrl: 'edit.html'
		}).
		when('/new', {
			templateUrl: 'edit.html'
		}).
       when('/login', {
           templateUrl: 'login.html'
       }).
       otherwise({
           redirectTo: '/login'
       });
}]);

app.run( function($rootScope, $location, AuthService) {
    $rootScope.$on( "$routeChangeStart", function(event, next, current) {
    	if (sessionStorage.signedIn) {
    		var user = sessionStorage.user;
    		AuthService.auth(user, function(data){
    			if(!data.authenticated){
   				 	gapi.auth.signOut();
   				 	$location.path("/login"); 
    			}
    		});
    	}else{
		 	$location.path("/login"); 
    	}         
    });
});

app.factory('Loader', function(){
	var loader = {};
	loader.start = function(){
		$("#loader").addClass('overlay');
	}
	loader.end = function(){
		$("#loader").removeClass('overlay');
	}
	return loader;
});