app.controller('LoginController', function($scope, $location, $http, GoogleService, AuthService, Loader, CLIENT_ID){
	
    $scope.processAuth = function(authResult) {
    	$scope.changeAccount = false;
        if(authResult['status']['signed_in']) {
        	Loader.start();
            GoogleService.getUserInfo(authUser);
        } else if(authResult['error'] == 'user_signed_out'){
        	$scope.changeAccount = true;
        }else if(authResult['error']) {
        	resetUserSession();
        	console.log(authResult);
        	console.log("google auth fail");
        }
    }
    
    
    function resetUserSession(){
    	sessionStorage.removeItem('signedIn');
    	sessionStorage.removeItem('user');
    	gapi.auth.signOut();
    }
    
    function authUser(userinfo){
    	sessionStorage.setItem('user',JSON.stringify(userinfo));
    	AuthService.auth(userinfo, $scope.applySignIn);
    }

    $scope.applySignIn = function(data){
		if(data.authenticated){
			sessionStorage.setItem('signedIn',true);
			$location.path("/home"); 
			console.log("applySignIn success");
		}else{
			resetUserSession();
			$location.path("/login"); 
			console.log("applySignIn fail");
		}
		Loader.end();
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