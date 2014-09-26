app.controller('LoginController', function($scope, $location, GoogleService, CLIENT_ID){
	

	 
    $scope.processAuth = function(authResult) {
        if(authResult['status']['signed_in']) {
        	console.log('success');
            GoogleService.getUserInfo($scope.processUserInfo);
        } else if(authResult['error']) {
        	console.log('fail');
        	 sessionStorage.removeItem('signedIn');
        	 alert("Need a aptitud domain to login");
        }
    }
    
    $scope.processUserInfo = function(userInfo) {
        //TODO put this in backend
        if(userInfo['domain'] == 'aptitud.se'){
        	 sessionStorage.setItem('signedIn', true);
        	 $scope.$apply(function() { $location.path("/home"); });
        }else{
        	sessionStorage.removeItem('signedIn');
        	 gapi.auth.signOut();
        }
    }
    
    $scope.renderSigin = function(){
    	gapi.signin.render('signinButton',{
			clientid: CLIENT_ID,
            cookiepolicy: "single_host_origin",
            requestvisibleactions: "http://schemas.google.com/AddActivity",
            scope: "https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/plus.profile.emails.read",
            callback: $scope.processAuth,
            accesstype: 'online',
            width: 'wide'
    	});
    }
    
    $scope.start = function(){
    	$scope.renderSigin();
    }
    $scope.start();
});