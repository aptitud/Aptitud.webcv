app.controller('NavbarController', function($scope, $rootScope){
	
    $scope.isAuthenticated = !angular.isUndefined(sessionStorage.getItem('signedIn'));

	$rootScope.$on('authenticated', function(event, data) {
		$scope.isAuthenticated = true;
	});

	$rootScope.$on('logout', function(event, data) {
		$scope.isAuthenticated = false;
	});

	function resetUserSession(){
    	sessionStorage.removeItem('signedIn');
    	sessionStorage.removeItem('user');
    	gapi.auth.signOut();
    }
});