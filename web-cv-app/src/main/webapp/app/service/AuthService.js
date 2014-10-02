app.service('AuthService', function($http, API_END_POINT){	
	
	this.auth = function(userInfo, callback){
		$http.get(API_END_POINT+"/auth", {
			 params: {
				 userInfo: userInfo
		        }
		 }).success(callback);
	}
	
});